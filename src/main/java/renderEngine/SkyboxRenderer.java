package renderEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import camera.CameraEntity;
import modelsLibrary.CubeTexture;
import renderEngine.Loader.VBOIndex;
import shaderManager.SkyboxShader;

public class SkyboxRenderer extends DrawRenderer {
	//TODO extract in abstract class specific for 3D
	private CameraEntity camera;
	
	private int dayTexture;
	private int nightTexture;
	private float time = 0;
	
	
	private SkyboxRenderer(CameraEntity camera, int dayTexture, int nightTexture) {
		this.camera = camera;
		this.dayTexture = dayTexture;
		this.nightTexture = nightTexture;
	}
	
	//TODO extract CubeTexture from DayNight logic
	public static SkyboxRenderer create(SkyboxShader skyboxShader,CameraEntity camera, CubeTexture skyboxTextureDay, CubeTexture skyboxTextureNight) {
		SkyboxRenderer renderer = new SkyboxRenderer(camera, skyboxTextureDay.getTextureId(), skyboxTextureNight.getTextureId());
		skyboxShader.start();
		skyboxShader.connectTextureUnits();
		skyboxShader.stop();
		return renderer;
	}
	
	@Override
	public void render() {
		// must find a way to not allow multi rendering for Skybox.
		for (RenderingParameters params : renderingParams) {
			SkyboxShader shader = (SkyboxShader) params.getShader();
			shader.start();
			shader.loadViewMatrix(camera.getViewMatrix());
			shader.loadFogColour(MasterRenderer.RED, MasterRenderer.GREEN, MasterRenderer.BLUE);
			
			//TODO really.really.really.really ugly
			prepare(params.getGeom().getRawGeom().getVaoId());
			dayNightCycle(shader);
			params.getEntities().forEach(entity -> {
				genericDrawRender(params);
			});
			unbindGeom();
			shader.stop();
		}
	}
	
	private void dayNightCycle(SkyboxShader shader){
		time += DisplayManager.getFrameTimeSeconds() * 1000;
		time %= 24000; // affecte le reste de la division.
		int texture1;
		int texture2;
		float blendFactor;		
		if(time >= 0 && time < 5000){
			texture1 = nightTexture;
			texture2 = nightTexture;
			blendFactor = (time - 0)/(5000 - 0);
		}else if(time >= 5000 && time < 8000){
			texture1 = nightTexture;
			texture2 = dayTexture;
			blendFactor = (time - 5000)/(8000 - 5000);
		}else if(time >= 8000 && time < 21000){
			texture1 = dayTexture;
			texture2 = dayTexture;
			blendFactor = (time - 8000)/(21000 - 8000);
		}else{
			texture1 = dayTexture;
			texture2 = nightTexture;
			blendFactor = (time - 21000)/(24000 - 21000);
		}

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
		shader.loadBlendFactor(blendFactor);
	}

	@Override
	protected void prepare(int vaoId) {
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(VBOIndex.POSITION_INDEX);
	}

	@Override
	protected void unbindGeom() {
		GL20.glDisableVertexAttribArray(VBOIndex.POSITION_INDEX);
		GL30.glBindVertexArray(0);
	}
	
}
