package renderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Matrix4f;

import entities.Entity;
import models.Model3D;
import models.TextureContainer;
import models.TextureData;
import renderEngine.Loader.VBOIndex;
import shaderManager.StaticShader;
import toolbox.GLTextureIDIncrementer;
import toolbox.Maths;

/**
 * Handles the rendering of a model to the screen.
 * 
 * @author Karl
 *
 */
public class EntityRenderer{
	private StaticShader shader;
	

	/**
	 * This method must be called each frame, before any rendering is carried
	 * out. 
	 */
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start(); //this shader will never change because it is specifying projection matrix (camera)
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	/**
	 * Renders models to the screen.
	 * 
	 * 
	 * The VAO can then be rendered to the screen using glDrawArrays(). We tell
	 * it what type of shapes to render and the number of vertices that it needs
	 * to render.
	 * 
	 * @param rawModel
	 *            - The model to be rendered.
	 */
	public void render(Map<Model3D,List<Entity>> entities) {
		for(Model3D model : entities.keySet()) {
			prepareTextureModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, MasterRenderer.storeDataInIntBuffer(model.getContainer3D().getFlatIndices()));
			}
			unbindTextureModel();
		}
	}

	 /** 
	  * TODO do same thing as in TerrainRenderer and maybe refactor to merge the 2 renderer ?
	 * Before we can render a VAO it needs to be made active, and we can do this
	 * by binding it. We also need to enable the relevant attributes of the VAO,
	 * which in this case is just attribute 0 where we stored the position data.
	 */
	private void prepareTextureModel(Model3D model) {
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(VBOIndex.POSITION_INDEX);
		GL20.glEnableVertexAttribArray(VBOIndex.TEXTURE_INDEX);
		GL20.glEnableVertexAttribArray(VBOIndex.NORMAL_INDEX);
		TextureData texture = model.getTextureContainer().getTextures().get(0);
		if(texture.isHasTransparency()) {
			MasterRenderer.disableCulling();
		}
		shader.loadFakeLighting(texture.isUseFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GLTextureIDIncrementer.GL_TEXTURE_IDS.get(0));
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
	}
	
	/**
	 * Rendering active {Entity}
	 * @param entity
	 */
	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPositions(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	/** 
	 * After rendering we unbind the VAO and disable the attribute.
	 */
	private void unbindTextureModel() {
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(VBOIndex.NORMAL_INDEX);
		GL20.glDisableVertexAttribArray(VBOIndex.TEXTURE_INDEX);
		GL20.glDisableVertexAttribArray(VBOIndex.POSITION_INDEX);
		GL30.glBindVertexArray(0);
	}

}
