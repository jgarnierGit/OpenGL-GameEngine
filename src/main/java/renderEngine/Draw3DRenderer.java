package renderEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Matrix4f;

import entities.Camera;
import modelsLibrary.ISimpleGeom;
import renderEngine.Loader.VBOIndex;
import shaderManager.Draw3DShader;
import toolbox.Maths;

/**
 * Render using ViewMatrix transformation
 * @author chezmoi
 *
 */
public class Draw3DRenderer  extends DrawRenderer{
	
	private Draw3DShader draw3DShader;
	private Camera camera;


	public Draw3DRenderer(Camera camera, Matrix4f projectionMatrix) throws IOException {
		super();
		this.draw3DShader = new Draw3DShader();
		this.camera = camera;
		draw3DShader.start();
		draw3DShader.loadProjectionMatrix(projectionMatrix);
		draw3DShader.stop();
	}

	@Override
	public void render() {
		for (ISimpleGeom geom : geoms) {
			draw3DShader.start();
			prepare(geom,VBOIndex.POSITION_INDEX, Draw3DShader.COLOR_INDEX);
			Matrix4f viewMatrix = Maths.createViewMatrix(camera);
			draw3DShader.loadViewMatrix(viewMatrix);

			// Disable distance filtering.
			GL11.glDisable(GL11.GL_DEPTH);
			geom.enableRenderOptions();
			renderByMode(geom);
			unbindGeom(VBOIndex.POSITION_INDEX, Draw3DShader.COLOR_INDEX);
			// GL11.glLineWidth(1);
			geom.disableRenderOptions();
			GL11.glEnable(GL11.GL_DEPTH);
			draw3DShader.stop();
		}
	}

	@Override
	public void cleanUp() {
		draw3DShader.cleanUp();
	}
	
	@Override
	public void reloadAndprocess(ISimpleGeom geom, int renderingIndex) {
		geom.reloadPositions(Draw3DShader.COLOR_INDEX);
		process(geom, renderingIndex);
	}
	
	/**
	 * No need to update transformModel Matrix because coordinates are in world
	 * coordinate already. So we alter viewMatrix in java instead of in shader
	 * because simplier.
	 * 
	 * @param model
	 */
/**
	private void updateViewModelMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(model.getPositionVector3f(), 0, 0, 0, 1);
		rayShader.loadTransformationMatrix(transformationMatrix);
		Matrix4f modelMatrix = new Matrix4f();
		Matrix4f.translate(position, modelMatrix, modelMatrix);
		modelMatrix.m00 = viewMatrix.m00;
		modelMatrix.m01 = viewMatrix.m10;
		modelMatrix.m02 = viewMatrix.m20;
		modelMatrix.m10 = viewMatrix.m01;
		modelMatrix.m11 = viewMatrix.m11;
		modelMatrix.m12 = viewMatrix.m21;
		modelMatrix.m20 = viewMatrix.m02;
		modelMatrix.m21 = viewMatrix.m12;
		modelMatrix.m22 = viewMatrix.m22;
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 0, 1), modelMatrix, modelMatrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), modelMatrix, modelMatrix);
		Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, modelMatrix, null);
		rayShader.loadModelViewMatrix(modelViewMatrix);
	}**/
}
