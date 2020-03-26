package renderEngine;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

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
import shaderManager.RayShader;
import toolbox.GLTextureIDIncrementer;
import toolbox.Maths;

public class RayRenderer {
	private Ray ray;
	private int glRenderMode;
	private RayShader rayShader;
	private Camera camera;

	public RayRenderer(Camera camera, Matrix4f projectionMatrix, Loader loader) throws IOException {
		this.rayShader = new RayShader();
		this.camera = camera;
		rayShader.start();

		rayShader.loadProjectionMatrix(projectionMatrix);
		rayShader.stop();
	}

	public void render() {
		if(ray == null) {
			return;
		}
		int dataLength = 0;
		switch(glRenderMode) {
		case GL11.GL_POINTS:
			dataLength = ray.getPoints().length;
			break;
		case GL11.GL_LINES:
			dataLength = ray.getPoints().length /2;
			break;
		default :
			System.err.println("unsupported render mode: "+ glRenderMode);
			return;
		}
		
		prepare();
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);

		// Disable distance filtering.
		GL11.glDisable(GL11.GL_DEPTH);
		// GL11.glEnable(GL11.GL_POINT_SMOOTH);
		// GL11.glLineWidth(2); //seems to have a max cap unlike PointSize. for GL_LINES
		GL11.glPointSize(5); // GL_POINTS
		//GL11.drawArrays can draw points with GL_POINTS, not GL_POINT
		rayShader.loadViewMatrix(viewMatrix);

		GL11.glDrawArrays(glRenderMode, 0,dataLength);
		unbindTerrain();
		GL11.glPointSize(1);
		// GL11.glLineWidth(1);
		GL11.glEnable(GL11.GL_DEPTH);
		rayShader.stop();
	}

	/**
	 * TODO refactor to facilitate renderer file creation. Before we can render a
	 * VAO it needs to be made active, and we can do this by binding it. We also
	 * need to enable the relevant attributes of the VAO, which in this case is just
	 * attribute 0 where we stored the position data.
	 * @param ray2 
	 */
	private void prepare() {
		rayShader.start();
		GL30.glBindVertexArray(ray.getVaoId());
		GL20.glEnableVertexAttribArray(VBOIndex.POSITION_INDEX);
	}

	/**
	 * No need to update transformModel Matrix because coordinates are in world
	 * coordinate already. So we alter viewMatrix in java instead of in shader
	 * because simplier.
	 * 
	 * @param model
	 */
	/**
	 * private void updateViewModelMatrix(Vector3f position, float rotation, float
	 * scale, Matrix4f viewMatrix) { /** Matrix4f transformationMatrix =
	 * Maths.createTransformationMatrix(model.getPositionVector3f(), 0, 0, 0, 1);
	 * rayShader.loadTransformationMatrix(transformationMatrix);** / Matrix4f
	 * modelMatrix = new Matrix4f(); Matrix4f.translate(position, modelMatrix,
	 * modelMatrix); modelMatrix.m00 = viewMatrix.m00; modelMatrix.m01 =
	 * viewMatrix.m10; modelMatrix.m02 = viewMatrix.m20; modelMatrix.m10 =
	 * viewMatrix.m01; modelMatrix.m11 = viewMatrix.m11; modelMatrix.m12 =
	 * viewMatrix.m21; modelMatrix.m20 = viewMatrix.m02; modelMatrix.m21 =
	 * viewMatrix.m12; modelMatrix.m22 = viewMatrix.m22; Matrix4f.rotate((float)
	 * Math.toRadians(rotation), new Vector3f(0,0,1), modelMatrix, modelMatrix);
	 * Matrix4f.scale(new Vector3f(scale, scale, scale), modelMatrix, modelMatrix);
	 * Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, modelMatrix, null);
	 * rayShader.loadModelViewMatrix(modelViewMatrix); }
	 **/

	/**
	 * TODO refactor to facilitate renderer file creation. After rendering we unbind
	 * the VAO and disable the attribute.
	 */
	private void unbindTerrain() {
		GL20.glDisableVertexAttribArray(VBOIndex.POSITION_INDEX);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public void cleanUp() {
		rayShader.cleanUp();
	}

	public void process(Ray ray2, int glRenderMode) {
		this.ray = ray2;
		this.glRenderMode = glRenderMode;
	}
}
