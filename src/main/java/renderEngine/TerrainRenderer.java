package renderEngine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;

import com.mokiat.data.front.parser.MTLMaterial;

import models.MTLUtils;
import models.MaterialMapper;
import models.Model3D;
import renderEngine.Loader.VBOIndex;
import shaderManager.TerrainShader;
import terrains.Terrain;
import toolbox.GLTextureIDIncrementer;
import toolbox.Maths;

/**
 * TODO can refactor terrainRenderer and EntityRenderer using Visitor, as there is only MTLLibrary parsing changing.
 * if there are other things changing do visitor for those points too.
 * @author chezmoi
 *
 */
public class TerrainRenderer{
	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}
	
	public void render(List<Model3D> terrains) {
		for(Model3D terrain : terrains) {
			prepareTerrain(terrain);
			loadTerrain(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, MasterRenderer.storeDataInIntBuffer(terrain.getObjUtils().getOBJUtils().getIndices()));
			unbindTerrain();
		}
	}
	
	 /** 
	 * Before we can render a VAO it needs to be made active, and we can do this
	 * by binding it. We also need to enable the relevant attributes of the VAO,
	 * which in this case is just attribute 0 where we stored the position data.
	 */
	private void prepareTerrain(Model3D model) {
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(VBOIndex.POSITION_INDEX);
		GL20.glEnableVertexAttribArray(VBOIndex.TEXTURE_INDEX);
		GL20.glEnableVertexAttribArray(VBOIndex.NORMAL_INDEX);
		if(!model.getObjUtils().getMtlUtils().getMaterials().isEmpty() && model.getObjUtils().getMtlUtils().isMaterialValid("")) { //TODO delete second check when FIXME on MTLUtils is done test with one texture ok and many not.
			bindTextures(model.getObjUtils().getMtlUtils());
		}else {
			useNoTexture(0);
		}

		shader.loadShineVariables(1, 0);
	}
	
	private void useNoTexture(int id) {
		GL13.glActiveTexture(GLTextureIDIncrementer.GL_TEXTURE_IDS.get(id));
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}
	
	// TODO code may be duplicated with entityRenderer
	private void bindTextures(MTLUtils mtlUtils) {
		List<MaterialMapper> materialList = mtlUtils.getMaterials();
		for(int i =0; i< materialList.size() && i<33; i++) {
			if(mtlUtils.isMaterialValid(materialList.get(i).getMaterial().getName())) {
				GL13.glActiveTexture(GLTextureIDIncrementer.GL_TEXTURE_IDS.get(i));
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, mtlUtils.getTexturesIndexes().get(i));
			}
			else {
				useNoTexture(i);
			}
		}
	}

	/**
	 * Rendering active {Entity}
	 * @param entity
	 */
	private void loadTerrain(Model3D modelTerrain) {
		Terrain terrain = (Terrain) modelTerrain;
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
