package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.EntityTutos;
import entities.SimpleEntity;
import modelsLibrary.ISimpleGeom;

public class RenderingParameters implements IRenderingParameters{
	private Optional<Integer> glRenderMode;
	private HashMap<Integer, Boolean> glStatesRendering;
	private ISimpleGeom simpleGeom;
	private List<SimpleEntity> entities;
	private boolean skipEntities;
	private String alias;
	private String destinationOrderAlias;
	private Boolean renderAfter;
	protected Logger logger;
	
	private Optional<Vector4f> overridedColors;
	private HashMap<Vector, Vector4f> overrideColorsAtIndex;

	private RenderingParameters() {
		this.glStatesRendering = new HashMap<>();
		this.entities = new ArrayList<>();
		this.glRenderMode = Optional.empty();
		this.overridedColors = Optional.empty();
		this.overrideColorsAtIndex = new HashMap<>();
		this.skipEntities = false;
		this.renderAfter = null;
		this.destinationOrderAlias = "";
		this.logger = Logger.getLogger("RenderingParameters");
	}
	/**
	 * hide this constructor, only way to get a RenderingParameters is by SimpleGeom
	 * @param simpleGeom
	 */
	public RenderingParameters(ISimpleGeom simpleGeom, String alias) {
		this();
		this.simpleGeom = simpleGeom;
		this.alias= alias; 
	}
	
	/**
	 * Use direct vertices coordinates to render.
	 * This must be used only for unique objects.
	 */
	public void doNotUseEntities() {
		skipEntities = true;
	}
	
	
	public boolean isNotUsingEntities() {
		return this.skipEntities;
	}
	
	/**
	 * Prepare a renderingParameter with same presets as given renderingParameter.
	 * Does not apply overridedColors, overrideColorsAtIndex.
	 * @param toClone
	 * @param geomToApply
	 * @param alias
	 */
	public RenderingParameters(RenderingParameters toClone, ISimpleGeom geomToApply, String alias) {
		this();
		this.alias= alias; 
		this.simpleGeom = geomToApply;
		this.destinationOrderAlias = toClone.destinationOrderAlias;
		if(toClone.glRenderMode.isPresent()) {
			 this.glRenderMode = Optional.ofNullable(toClone.glRenderMode.get());
		}
		
		this.glStatesRendering.putAll(toClone.glStatesRendering);
		this.renderAfter = toClone.renderAfter;
	}
	
	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}
	@Override
	public String getAlias() {
		return this.alias;
	}
	@Override
	public String getDestinationOrderAlias() {
		return this.destinationOrderAlias;
	}
	@Override
	public Optional<Boolean> isDestinationPositionAfter() {
		return Optional.ofNullable(this.renderAfter);
	}
	@Override
	public void renderBefore(String alias) {
		this.destinationOrderAlias=alias;
		this.renderAfter = false;
	}
	@Override
	public void renderAfter(String alias) {
		this.destinationOrderAlias=alias;
		this.renderAfter = true;
	}

	public ISimpleGeom getGeom() {
		return this.simpleGeom;
	}
	
	/**
	 * Set new color to apply for entire geom.
	 * @param color
	 */
	public void overrideEachColor(Vector4f color) {
		this.overridedColors = Optional.ofNullable(color);
	}
	
	/**
	 * first applies global color changes, then unitary color changes.
	 */
	public void applyColorOverriding() {
		this.overridedColors.ifPresent(color -> {
			simpleGeom.setColor(color);
		});
		
		this.overrideColorsAtIndex.forEach((position, color) -> {
			simpleGeom.updateColorByPosition(position, color);
		});
		simpleGeom.reloadVao();
	}
	
	public Optional<Vector4f> getOverridedColors(){
		return this.overridedColors;
	}
	/**
	 * Set color replacement for specified vertice index.
	 * Replace color if index was already set.
	 * @param position
	 * @param color
	 */
	public void overrideColorAtIndex(Vector position, Vector4f color) {
		this.overrideColorsAtIndex.put(position, color);
	}
	
	/**
	 * add same world transformation (position/rotation/scale) as entity to apply for geom.
	 * @param entity
	 */
	public void addEntity(EntityTutos entity) {
		this.addEntity(entity,entity.getPositions(),entity.getRotX(),entity.getRotY(),entity.getRotZ(),entity.getScale());
	}

	/**
	 * add entity and override its world transformation (position/rotation/scale) to apply for geom.
	 * @param entity 
	 * @param positions override position given by entity
	 * @param rotX override rotX given by entity
	 * @param rotY override rotY given by entity
	 * @param rotZ override rotZ given by entity
	 * @param scale override scale given by entity
	 */
	public void addEntity(EntityTutos entity, Vector3f positions, float rotX, float rotY, float rotZ, float scale) {
		entity.addRenderingParameters(this);
		entities.add(new SimpleEntity(positions, rotX, rotY, rotZ, scale));
	}
	
	public List<SimpleEntity> getEntities() {
		return this.entities;
	}

	/**
	 * returns GL constant for rendering Mode (GL11.GL_POINTS / GL_LINES /
	 * GL_TRIANGLES and subtypes)
	 * 
	 * @return GL constant for rendering Mode (GL11.GL_POINTS / GL_LINES /
	 *         GL_TRIANGLES and subtypes)
	 */
	public Optional<Integer> getRenderMode() {
		return this.glRenderMode;
	}

	/**
	 * add GL constant to render current object. Each renderMode added are rendered
	 * simultaneously.
	 * 
	 * TODO only GL_POINTS is compatibles with other ones. Transfer from GL_LINES to GL_TRIANGLES needs new points ordering.
	 * 
	 * @param glRenderMode (GL11.GL_POINTS / GL_LINES / GL_TRIANGLES and subtypes)
	 */
	public void setRenderMode(int glRenderMode) {
		this.glRenderMode = Optional.ofNullable(glRenderMode);
	}
	
	public HashMap<Integer, Boolean> getStatesRendering(){
		return this.glStatesRendering;
	}

	/**
	 * Set each Gl_State to apply for active Geom.
	 */
	public void enableRenderOptions() {
		glStatesRendering.forEach((glState, doActivate) -> {
			if (Boolean.TRUE.equals(doActivate)) {
				GL11.glEnable(glState);
			} else {
				GL11.glDisable(glState);
			}
		});
	}

	/**
	 * TODO hide from this interface.
	 * Unset each Gl_State when rendering is done.
	 */
	public void disableRenderOptions() {
		glStatesRendering.forEach((glState, doActivate) -> {
			if (Boolean.TRUE.equals(doActivate)) {
				GL11.glDisable(glState);
			} else {
				GL11.glEnable(glState);
			}
		});
	}

	/**
	 * add Gl_State rule for rendering
	 * 
	 * @param glBlend
	 * @param b       true to enable before rendering, false to disable before
	 *                rendering
	 */
	public void addGlState(int glBlend, boolean b) {
		glStatesRendering.put(glBlend, b);
	}

	@Override
	public String toString() {
		return "RenderingParameters [alias=" + alias + "]";
	}

	public void reset() {
		this.entities = new ArrayList<>();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		return result;
	}

	//FIXME don't know if this is really helpful. To retreive a RenderingParam I still need to compare based on an alias.
	@Override
	public boolean equals(Object obj) { 
		if (this == obj)
			return true;
		if (!(obj instanceof RenderingParameters))
			return false;
		RenderingParameters other = (RenderingParameters) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		return true;
	}

	public void removeEntity(EntityTutos entity) {
		boolean removed = this.entities.removeIf(simpleEntity -> {
			Vector3f posA = simpleEntity.getPositions();
			Vector3f posB = entity.getPositions();
			return posA.x == posB.x && posA.y == posB.y && posA.z == posB.z;
		});
	/**	if(removed) {
			if(this.logger.isLoggable(Level.INFO)){
				this.logger.info("some entities updated from "+ entity.getModel());
			}
		}**/
	}

	public void overrideGlobalTransparency(float transparency) {
		this.overridedColors = Optional.of(new Vector4f(-1f,-1f,-1f,transparency));
	}
	public void renderLast() {
		this.renderAfter=true;
		this.destinationOrderAlias="";
	}
	public void renderFirst() {
		this.renderAfter=false;
		this.destinationOrderAlias="";
	}
	public void resetRenderingOrder() {
		this.renderAfter=null;
		this.destinationOrderAlias="";
	}
	
	
}
