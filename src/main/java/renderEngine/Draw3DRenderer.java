package renderEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector4f;

import camera.CameraEntity;
import models.RenderableGeom;
import renderEngine.Loader.VBOIndex;
import shaderManager.Draw3DShader;
import shaderManager.IShader3D;
import toolbox.Maths;

/**
 * Render using ViewMatrix transformation
 * 
 * @author chezmoi
 *
 */
public class Draw3DRenderer extends DrawRendererCommon {
	private CameraEntity camera;
	private Vector4f clipPlane;

	public Draw3DRenderer(CameraEntity camera) {
		super();
		this.camera = camera;
	}

	public void setClipPlane(Vector4f clipPlane) {
		this.clipPlane = clipPlane;
	}

	@Override
	public void render() {
		for (RenderableGeom geom : geoms) {
			RenderingParameters params = geom.getRenderingParameters();
			IShader3D draw3DShader = (IShader3D) params.getShader();
			draw3DShader.start();
			draw3DShader.loadClipPlane(clipPlane); // TODO extract this is not generic at all
			// FIXME : this form is binding many times same VAOID potentially
			prepare(geom.getVAOGeom().getVaoId());
			Matrix4f viewMatrix = camera.getViewMatrix();
			draw3DShader.loadViewMatrix(viewMatrix);
			draw3DShader.setUseImage(!geom.getVAOGeom().getTextures().isEmpty());
			int numberOfRows = geom.getObjContent().getMaterials().getNumberOfRows();
			draw3DShader.loadNumberOfRows(numberOfRows);
			// Disable distance filtering.
			GL11.glDisable(GL11.GL_DEPTH);
			params.enableRenderOptions();
			if (params.getEntities() == null || params.getEntities().isEmpty()) { // not good at all
				Matrix4f transformationM = new Matrix4f();
				draw3DShader.loadTransformationMatrix(transformationM);
				genericDrawRender(geom);
			} else {
				params.getEntities().forEach(entity -> {
					draw3DShader.loadOffset(getTextureXOffset(entity.getTextureAtlasIndex(), numberOfRows),
							getTExtureYOffset(entity.getTextureAtlasIndex(), numberOfRows));
					Matrix4f transformationM = Maths.createTransformationMatrix(entity.getPositions(), entity.getRotX(),
							entity.getRotY(), entity.getRotZ(), entity.getScale());
					draw3DShader.loadTransformationMatrix(transformationM);
					genericDrawRender(geom);
				});
			}
			unbindGeom();
			// GL11.glLineWidth(1);
			params.disableRenderOptions();
			GL11.glEnable(GL11.GL_DEPTH);
			draw3DShader.stop();
		}
	}

	private float getTextureXOffset(int textureIndex, int numberOfRows) {
		if (numberOfRows == 0) {
			return 0;
		}
		float column = textureIndex % numberOfRows;
		return column / numberOfRows;
	}

	private float getTExtureYOffset(int textureIndex, int numberOfRows) {
		if (numberOfRows == 0) {
			return 0;
		}
		float row = textureIndex / numberOfRows;
		return row / numberOfRows;
	}

	@Override
	protected void prepare(int vaoId) {
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(VBOIndex.POSITION_INDEX);
		GL20.glEnableVertexAttribArray(Draw3DShader.COLOR_INDEX);
		GL20.glEnableVertexAttribArray(Draw3DShader.TEXTURE_INDEX);
	}

	@Override
	protected void unbindGeom() {
		GL20.glDisableVertexAttribArray(Draw3DShader.TEXTURE_INDEX);
		GL20.glDisableVertexAttribArray(Draw3DShader.COLOR_INDEX);
		GL20.glDisableVertexAttribArray(VBOIndex.POSITION_INDEX);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
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
	 * scale, Matrix4f viewMatrix) { Matrix4f transformationMatrix =
	 * Maths.createTransformationMatrix(model.getPositionVector3f(), 0, 0, 0, 1);
	 * rayShader.loadTransformationMatrix(transformationMatrix); Matrix4f
	 * modelMatrix = new Matrix4f(); Matrix4f.translate(position, modelMatrix,
	 * modelMatrix); modelMatrix.m00 = viewMatrix.m00; modelMatrix.m01 =
	 * viewMatrix.m10; modelMatrix.m02 = viewMatrix.m20; modelMatrix.m10 =
	 * viewMatrix.m01; modelMatrix.m11 = viewMatrix.m11; modelMatrix.m12 =
	 * viewMatrix.m21; modelMatrix.m20 = viewMatrix.m02; modelMatrix.m21 =
	 * viewMatrix.m12; modelMatrix.m22 = viewMatrix.m22; Matrix4f.rotate((float)
	 * Math.toRadians(rotation), new Vector3f(0, 0, 1), modelMatrix, modelMatrix);
	 * Matrix4f.scale(new Vector3f(scale, scale, scale), modelMatrix, modelMatrix);
	 * Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, modelMatrix, null);
	 * rayShader.loadModelViewMatrix(modelViewMatrix); }
	 **/
}
