package renderEngine;

import java.util.HashMap;
import java.util.Optional;

import org.lwjgl.opengl.GL11;

import modelsLibrary.ISimpleGeom;

public class RenderingParameters implements Comparable<RenderingParameters> {

	private Optional<Integer> glRenderMode;
	private HashMap<Integer, Boolean> glStatesRendering;
	private Optional<Integer> renderingIndex;
	private ISimpleGeom simpleGeom;

	public RenderingParameters(ISimpleGeom simpleGeom) {
		this.simpleGeom = simpleGeom;
		this.glStatesRendering = new HashMap<>();
		this.renderingIndex = Optional.empty();
		this.glRenderMode = Optional.empty();
	}

	/**
	 * return rendering index
	 * 
	 * @return rendering index
	 */
	public Optional<Integer> getRenderingIndex() {
		return this.renderingIndex;
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

	/**
	 * force index rendering order for transparency interactions
	 * 
	 * @param renderingIndex higher value will render later.
	 */
	public void setRenderingIndex(int renderingIndex) {
		this.renderingIndex = Optional.ofNullable(renderingIndex);
	}

	@Override
	public int compareTo(RenderingParameters params) {
		return this.getRenderingIndex().orElse(0).compareTo(params.getRenderingIndex().orElse(0));
	}
}
