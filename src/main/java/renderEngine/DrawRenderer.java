package renderEngine;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import modelsLibrary.ISimpleGeom;

public abstract class DrawRenderer implements IDrawRenderer {
	protected List<ISimpleGeom> geoms;
	
	public DrawRenderer() {
		this.geoms = new LinkedList<>();
	}
	
	@Override
	public void process(ISimpleGeom geom) {
		process(geom, this.geoms.size());
	}
	
	@Override
	public void process(ISimpleGeom geom, int renderingIndex) {
		// apply this logic while rendering
		renderingIndex = renderingIndex > this.geoms.size() ? this.geoms.size() : renderingIndex;
		renderingIndex = renderingIndex < 0 ? 0 : renderingIndex;
		this.geoms.add(renderingIndex, geom);
	}

	@Override
	public void reloadAndprocess(ISimpleGeom geom) {
		reloadAndprocess(geom, this.geoms.size());
	}
	
	/**
	 * Before we can render a VAO it needs to be made active, and we can do this by binding it. We also
	 * need to enable the relevant attributes of the VAO, which in this case is just
	 * attribute 0 where we stored the position data.
	 * 
	 * @param geom Geom to render
	 * @param positionIndex GL vertex Attrib array position in VBO?
	 * @param colorIndex GL vertex Attrib array position in VBO?
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
	
	protected void renderByMode(ISimpleGeom geom) {
		int dataLength = 0;
		// cf https://www.khronos.org/opengl/wiki/Primitive => internal gl logic, hidden for DrawArrays usage;
		int verticesCount = geom.getPoints().length / geom.getDimension();
		for(int glRenderMode : geom.getRenderModes()) {
			// GL11.glEnable(GL11.GL_POINT_SMOOTH);
			GL11.glLineWidth(2); //seems to have a max cap unlike PointSize. for GL_LINES
			GL11.glPointSize(5); // GL_POINTS
			// GL11.drawArrays can draw points with GL_POINTS, not GL_POINT
			GL11.glDrawArrays(glRenderMode, 0, verticesCount);
			GL11.glPointSize(1);
			GL11.glLineWidth(1);
		}
	}
}
