package renderEngine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;

import models.Model3D;
import models.TextureContainer;
import renderEngine.Loader.VBOIndex;
import shaderManager.TerrainShader;
import terrains.Terrain;
import toolbox.Maths;

public class TerrainRenderer{
	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}
	
	public void render(List<Terrain> terrains) {
		for(Terrain terrain : terrains) {
			prepareTerrain(terrain);
			loadTerrain(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, MasterRenderer.storeDataInIntBuffer(terrain.getContainer3D().getFlatIndices()));
			unbindTerrain();
		}
	}
	
	 /** 
	 * Before we can render a VAO it needs to be made active, and we can do this
	 * by binding it. We also need to enable the relevant attributes of the VAO,
	 * which in this case is just attribute 0 where we stored the position data.
	 */
	private void prepareTerrain(Terrain model) {
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(VBOIndex.POSITION_INDEX);
		GL20.glEnableVertexAttribArray(VBOIndex.TEXTURE_INDEX);
		GL20.glEnableVertexAttribArray(VBOIndex.NORMAL_INDEX);
		bindTextures(model);
		shader.loadShineVariables(1, 0);
	}
	
	private void bindTextures(Terrain model) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getBackgroundTexture().getTextureID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getrTexture().getTextureID());

		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getgTexture().getTextureID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getbTexture().getTextureID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getBlendMap().getTextureID());
	}

	/**
	 * Rendering active {Entity}
	 * @param entity
	 */
	private void loadTerrain(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(),0, terrain.getZ()), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	/** 
	 * After rendering we unbind the VAO and disable the attribute.
	 */
	private void unbindTerrain() {
		GL20.glDisableVertexAttribArray(VBOIndex.NORMAL_INDEX);
		GL20.glDisableVertexAttribArray(VBOIndex.TEXTURE_INDEX);
		GL20.glDisableVertexAttribArray(VBOIndex.POSITION_INDEX);
		GL30.glBindVertexArray(0);
	}

}
