package renderEngine;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import modelsLibrary.PointGeom;
import modelsLibrary.Ray;
import renderEngine.Loader.VBOIndex;
import shaderManager.Draw2DShader;
import shaderManager.RayShader;
import toolbox.GLTextureIDIncrementer;
import toolbox.Maths;

public class Draw2DRenderer {
	private List<PointGeom> geoms;
	private Draw2DShader draw2DShader;

	public Draw2DRenderer() throws IOException {
		this.draw2DShader = new Draw2DShader();
		this.geoms = new ArrayList<>();
	}

	public void render() {
		for(PointGeom geom : geoms) {
			prepare(geom);
			
			// Disable distance filtering.
			GL11.glDisable(GL11.GL_DEPTH);
			renderByMode(geom);	
			unbindGeom();
			// GL11.glLineWidth(1);
			GL11.glEnable(GL11.GL_DEPTH);
			draw2DShader.stop();
		}
	}

	private void renderByMode(PointGeom geom) {
		int dataLength = 0;
		// cf https://www.khronos.org/opengl/wiki/Primitive => internal gl logic, hidden for DrawArrays usage;
		int verticesCount = geom.getPoints().length / 2;
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

	/**
	 * TODO refactor to facilitate renderer file creation. Before we can render a
	 * VAO it needs to be made active, and we can do this by binding it. We also
	 * need to enable the relevant attributes of the VAO, which in this case is just
	 * attribute 0 where we stored the position data.
	 * 
	 * @param geom
	 */
	private void prepare(PointGeom geom) {
		draw2DShader.start();
		GL30.glBindVertexArray(geom.getVaoId());
		GL20.glEnableVertexAttribArray(VBOIndex.POSITION_INDEX);
	}

	/**
	 * TODO refactor to facilitate renderer file creation. After rendering we unbind
	 * the VAO and disable the attribute.
	 */
	private void unbindGeom() {
		GL20.glDisableVertexAttribArray(VBOIndex.POSITION_INDEX);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public void cleanUp() {
		draw2DShader.cleanUp();
	}

	public void process(PointGeom geom, int glRenderMode) {
		geom.addRenderMode(glRenderMode);
		this.geoms.add(geom);
	}
}
