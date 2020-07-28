package shadows;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Matrix4f;

import entities.Entity;
import models.RenderableGeom;
import renderEngine.DrawRendererCommon;
import renderEngine.RenderingParameters;
import toolbox.Maths;

public class ShadowMapEntityRenderer  extends DrawRendererCommon{

	private Matrix4f projectionViewMatrix;
	private ShadowShader shader;

	/**
	 * @param shader
	 *            - the simple shader program being used for the shadow render
	 *            pass.
	 * @param projectionViewMatrix
	 *            - the orthographic projection matrix multiplied by the light's
	 *            "view" matrix.
	 */
	protected ShadowMapEntityRenderer(ShadowShader shader, Matrix4f projectionViewMatrix) {
		this.shader = shader;
		this.projectionViewMatrix = projectionViewMatrix;
	}

	/**
	 * Renders entities to the shadow map. Each model is first bound and then all
	 * of the entities using that model are rendered to the shadow map.
	 * 
	 * @param entities
	 *            - the entities to be rendered to the shadow map.
	 */
	@Override
	public void render() {
		for (RenderableGeom geom : geoms) {
			RenderingParameters params = geom.getRenderingParameters();
			prepare(geom.getVAOGeom().getVaoId());
			params.getEntities().forEach(entity -> {
				prepareInstance(entity);
				renderByVertices(geom);
			});
			
			unbindGeom();
		}
	}

	/**
	 * Prepares an entity to be rendered. The model matrix is created in the
	 * usual way and then multiplied with the projection and view matrix (often
	 * in the past we've done this in the vertex shader) to create the
	 * mvp-matrix. This is then loaded to the vertex shader as a uniform.
	 * 
	 * @param entity
	 *            - the entity to be prepared for rendering.
	 */
	private void prepareInstance(Entity entity) {
		Matrix4f modelMatrix = Maths.createTransformationMatrix(entity.getPositions(),
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, modelMatrix, null);
		shader.loadMvpMatrix(mvpMatrix);
	}

	/**
	 * Binds a raw model before rendering. Only the attribute 0 is enabled here
	 * because that is where the positions are stored in the VAO, and only the
	 * positions are required in the vertex shader.
	 * 
	 */
	@Override
	protected void prepare(int vaoId) {
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0);
	}

	@Override
	protected void unbindGeom() {
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

}
