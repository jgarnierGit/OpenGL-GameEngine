package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.SimpleEntity;
import modelsLibrary.ISimpleGeom;

public class RenderingParameters implements IRenderingParameters{
	private Optional<Integer> glRenderMode;
	private HashMap<Integer, Boolean> glStatesRendering;
	private ISimpleGeom simpleGeom;
	private List<SimpleEntity> entities;
	private String alias;
	private String destinationOrderAlias;
	private boolean renderAfter;
	
	private Optional<Vector4f> overridedColors;
	private HashMap<Vector, Vector4f> overrideColorsAtIndex;

	/**
	 * hide this constructor, only way to get a RenderingParameters is by SimpleGeom
	 * @param simpleGeom
	 */
	public RenderingParameters(ISimpleGeom simpleGeom, String alias) {
		this.simpleGeom = simpleGeom;
		this.glStatesRendering = new HashMap<>();
		this.entities = new ArrayList<>();
		this.glRenderMode = Optional.empty();
		this.overridedColors = Optional.empty();
		this.overrideColorsAtIndex = new HashMap<>();
		
		this.renderAfter = false;
		this.destinationOrderAlias = "";
		this.alias= alias; 
	}
	
	/**
	 * Prepare a renderingParameter with same presets as given renderingParameter.
	 * Does not apply overridedColors, overrideColorsAtIndex.
	 * @param toClone
	 * @param geomToApply
	 * @param alias
	 */
	public RenderingParameters(RenderingParameters toClone, ISimpleGeom geomToApply, String alias) {
		this.alias= alias; 
		this.simpleGeom = geomToApply;
		this.destinationOrderAlias = toClone.destinationOrderAlias;
		if(toClone.glRenderMode.isPresent()) {
			 this.glRenderMode = Optional.ofNullable(toClone.glRenderMode.get());
		}else {
			this.glRenderMode = Optional.empty();
		}
		this.glStatesRendering = new HashMap<>();
		this.glStatesRendering.putAll(toClone.glStatesRendering);
		this.renderAfter = toClone.renderAfter;
		this.entities = new ArrayList<>();
		this.overridedColors = Optional.empty();
		this.overrideColorsAtIndex = new HashMap<>();
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
	public boolean isDestinationPositionAfter() {
		return this.renderAfter;
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
	 * add world transformation (position/rotation/scale) to apply for geom.
	 * @param positions
	 * @param rotX
	 * @param rotY
	 * @param rotZ
	 * @param scale
	 */
	public void addEntity(Vector3f positions, float rotX, float rotY, float rotZ, float scale) {
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
	
	
}
