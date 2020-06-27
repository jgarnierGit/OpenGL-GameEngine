package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.Entity;
import entities.SimpleEntity;
import modelsLibrary.ISimpleGeom;
import shaderManager.Draw3DShader;
import shaderManager.IShader;
import shaderManager.ShaderProgram;

public class RenderingParameters implements IRenderingParameters {
	private IShader shader;
	private Optional<Integer> glRenderMode;
	private HashMap<Integer, Boolean> glStatesRendering;
	private ISimpleGeom simpleGeom;
	private List<Entity> entities;
	private boolean skipEntities;
	private String alias;
	private String destinationOrderAlias;
	private Boolean renderAfter;
	protected Logger logger;

	private Optional<Vector4f> overridedColors;
	private HashMap<Vector, Vector4f> overrideColorsAtIndex;

	RenderingParameters(IShader shader, ISimpleGeom simpleGeom, String alias, Entity entity) {
		this.simpleGeom = simpleGeom;
		this.shader = shader;
		this.alias = alias;
		this.glStatesRendering = new HashMap<>();
		this.entities = new ArrayList<>();
		this.entities.add(entity);
		this.glRenderMode = Optional.empty();
		this.overridedColors = Optional.empty();
		this.overrideColorsAtIndex = new HashMap<>();
		this.skipEntities = false;
		this.renderAfter = null;
		this.destinationOrderAlias = "";
	}

	public static RenderingParameters create(IShader shader, ISimpleGeom simpleGeom, String alias,
			Entity entity) {
		RenderingParameters param = new RenderingParameters(shader, simpleGeom, alias, entity);
		param.logger = Logger.getLogger("RenderingParameters");
		return param;
	}

	/**
	 * Prepare a renderingParameter with same presets as given renderingParameter.
	 * Does not apply overridedColors, overrideColorsAtIndex.
	 * 
	 * @param toClone
	 * @param geomToApply
	 * @param alias
	 */
	public static RenderingParameters copy(RenderingParameters toClone, ISimpleGeom geomToApply, String alias,
			Entity entity) {
		RenderingParameters cloned = new RenderingParameters(toClone.shader, geomToApply, alias, entity);
		cloned.destinationOrderAlias = toClone.destinationOrderAlias;
		if (toClone.glRenderMode.isPresent()) {
			cloned.glRenderMode = Optional.ofNullable(toClone.glRenderMode.get());
		}
		cloned.glStatesRendering.putAll(toClone.glStatesRendering);
		cloned.renderAfter = toClone.renderAfter;
		return cloned;
	}

	/**
	 * Use direct vertices coordinates to render. This must be used only for unique
	 * objects.
	 */
	public void doNotUseEntities() {
		skipEntities = true;
	}

	public boolean isNotUsingEntities() {
		return this.skipEntities;
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
		this.destinationOrderAlias = alias;
		this.renderAfter = false;
	}

	@Override
	public void renderAfter(String alias) {
		this.destinationOrderAlias = alias;
		this.renderAfter = true;
	}

	public ISimpleGeom getGeom() {
		return this.simpleGeom;
	}

	/**
	 * Set new color to apply for entire geom.
	 * 
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

	public Optional<Vector4f> getOverridedColors() {
		return this.overridedColors;
	}

	/**
	 * Set color replacement for specified vertice index. Replace color if index was
	 * already set.
	 * 
	 * @param position
	 * @param color
	 */
	public void overrideColorAtIndex(Vector position, Vector4f color) {
		this.overrideColorsAtIndex.put(position, color);
	}

	/**
	 * Add same world transformation (position/rotation/scale) as entity to apply
	 * for geom.
	 * 
	 * @param entity
	 */
	public void addEntity(Entity entity) {
		this.addEntity(entity.getPositions(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
	}

	/**
	 * Create entity with world transformation (position/rotation/scale) to apply
	 * for geom.
	 * 
	 * @param positions override position given by entity
	 * @param rotX      override rotX given by entity
	 * @param rotY      override rotY given by entity
	 * @param rotZ      override rotZ given by entity
	 * @param scale     override scale given by entity
	 */
	public void addEntity(Vector3f positions, float rotX, float rotY, float rotZ, float scale) {
		entities.add(new SimpleEntity(positions, rotX, rotY, rotZ, scale));
	}

	public List<Entity> getEntities() {
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
	 * TODO only GL_POINTS is compatibles with other ones. Transfer from GL_LINES to
	 * GL_TRIANGLES needs new points ordering.
	 * 
	 * @param glRenderMode (GL11.GL_POINTS / GL_LINES / GL_TRIANGLES and subtypes)
	 */
	public void setRenderMode(int glRenderMode) {
		this.glRenderMode = Optional.ofNullable(glRenderMode);
	}

	public Map<Integer, Boolean> getStatesRendering() {
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
	 * TODO hide from this interface. Unset each Gl_State when rendering is done.
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

	// FIXME don't know if this is really helpful. To retreive a RenderingParam I
	// still need to compare based on an alias.
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

	public void removeEntity(Entity entity) {
		this.entities.removeIf(simpleEntity -> {
			Vector3f posA = simpleEntity.getPositions();
			Vector3f posB = entity.getPositions();
			return posA.x == posB.x && posA.y == posB.y && posA.z == posB.z;
		});
	}

	public void overrideGlobalTransparency(float transparency) {
		this.overridedColors = Optional.of(new Vector4f(-1f, -1f, -1f, transparency));
	}

	public void renderLast() {
		this.renderAfter = true;
		this.destinationOrderAlias = "";
	}

	public void renderFirst() {
		this.renderAfter = false;
		this.destinationOrderAlias = "";
	}

	public void resetRenderingOrder() {
		this.renderAfter = null;
		this.destinationOrderAlias = "";
	}

	public IShader getShader() {
		return this.shader;
	}

}
