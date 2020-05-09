package renderEngine;

import java.util.HashMap;
import java.util.Optional;

import org.lwjgl.opengl.GL11;

import modelsLibrary.ISimpleGeom;

public class RenderingParameters {

	private Optional<Integer> glRenderMode;
	private HashMap<Integer, Boolean> glStatesRendering;
	private ISimpleGeom simpleGeom;
	private String alias;
	private String destinationOrderAlias;
	private boolean renderAfter;

	public RenderingParameters(ISimpleGeom simpleGeom) {
		this.simpleGeom = simpleGeom;
		this.glStatesRendering = new HashMap<>();
		this.glRenderMode = Optional.empty();
		this.renderAfter = false;
		this.destinationOrderAlias = "";
	}
	
	public RenderingParameters(RenderingParameters toClone) {
		this.simpleGeom = toClone.simpleGeom;
		this.alias = toClone.alias;
		this.destinationOrderAlias = toClone.destinationOrderAlias;
		 this.glRenderMode = toClone.glRenderMode;
		this.glStatesRendering = new HashMap<>();
		this.glStatesRendering.putAll(toClone.glStatesRendering);
		this.renderAfter = toClone.renderAfter;
	}

	public ISimpleGeom getGeom() {
		return this.simpleGeom;
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

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getAlias() {
		return this.alias;
	}
	
	public String getDestinationOrderAlias() {
		return this.destinationOrderAlias;
	}
	
	public boolean isDestinationPositionAfter() {
		return this.renderAfter;
	}

	public void renderBefore(String alias) {
		this.destinationOrderAlias=alias;
		this.renderAfter = false;
	}
	
	public void renderAfter(String alias) {
		this.destinationOrderAlias=alias;
		this.renderAfter = true;
	}

	@Override
	public String toString() {
		return "RenderingParameters [alias=" + alias + "]";
	}
	
	
}
