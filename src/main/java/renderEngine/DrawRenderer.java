package renderEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import modelsLibrary.ISimpleGeom;

public abstract class DrawRenderer implements IDrawRenderer {
	protected List<ISimpleGeom> geoms;
	protected List<RenderingParameters> renderingParams;

	public DrawRenderer() {
		this.geoms = new ArrayList<>();
		this.renderingParams = new ArrayList<>();
	}

	@Override
	public void process(ISimpleGeom geom) {
		this.geoms.add(geom);
	}

	@Override
	public void sendForRendering() {
		renderingParams = getOrderedRenderingParameters();
	}
	
	@Override
	public void clearGeom() {
		this.geoms.clear();
	}

	/**
	 * Before we can render a VAO it needs to be made active, and we can do this by
	 * binding it. We also need to enable the relevant attributes of the VAO, which
	 * in this case is just attribute 0 where we stored the position data.
	 * 
	 * @param geom          Geom to render
	 * @param positionIndex GL vertex Attrib array position in VBO?
	 * @param colorIndex    GL vertex Attrib array position in VBO?
	 */
	protected void prepare(ISimpleGeom geom, int positionIndex, int colorIndex) {
		GL30.glBindVertexArray(geom.getVaoId());
		GL20.glEnableVertexAttribArray(positionIndex);
		GL20.glEnableVertexAttribArray(colorIndex);
	}

	protected void unbindGeom(int positionIndex, int colorIndex) {
		GL20.glDisableVertexAttribArray(colorIndex);
		GL20.glDisableVertexAttribArray(positionIndex);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	protected void genericDrawRender(RenderingParameters params) {
		int dataLength = 0;
		// cf https://www.khronos.org/opengl/wiki/Primitive => internal gl logic, hidden
		// for DrawArrays usage;
		ISimpleGeom geom = params.getGeom();
		int verticesCount = geom.getPoints().length / geom.getDimension();
		// Add default lineLoop rendering.
		renderForMode(params.getRenderMode().orElse(GL11.GL_LINE_LOOP), verticesCount);
	}

	private void renderForMode(int glRenderMode, int verticesCount) {
		// GL11.glEnable(GL11.GL_POINT_SMOOTH);
		GL11.glLineWidth(2); // seems to have a max cap unlike PointSize. for GL_LINES
		GL11.glPointSize(5); // GL_POINTS
		// GL11.drawArrays can draw points with GL_POINTS, not GL_POINT
		GL11.glDrawArrays(glRenderMode, 0, verticesCount);
		GL11.glPointSize(1);
		GL11.glLineWidth(1);
	}

	protected List<RenderingParameters> getOrderedRenderingParameters() {
		List<RenderingParameters> sortedParams = new ArrayList<>();
		for (ISimpleGeom simpleGeom : this.geoms) {
			sortedParams.addAll(simpleGeom.getRenderingParameters());
		}
		Collections.sort(sortedParams);
		return sortedParams;
	}
}
