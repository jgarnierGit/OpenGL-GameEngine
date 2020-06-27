package modelsLibrary;

import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Vector3f;

import camera.CameraEntity;
import entities.GeomContainer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.SkyboxRenderer;
import shaderManager.SkyboxShader;

public class SkyboxDayNight implements GeomContainer {
	SimpleGeom3D skyboxGeom;
	SkyboxRenderer renderer;
	CubeTexture skyboxTextureDay;
	CubeTexture skyboxTextureNight;
	Loader loader;
	private static final float SIZE = 500f;
	
	private SkyboxDayNight(Loader loader) {
		skyboxTextureDay = null;
		skyboxTextureNight = null;
		this.loader = loader;
	}
	
	public static SkyboxDayNight create(MasterRenderer masterRenderer, CameraEntity camera) throws IOException {
		SkyboxDayNight skybox = new SkyboxDayNight(masterRenderer.getLoader());
		SkyboxShader skyboxShader = new SkyboxShader();
		skybox.useDefaultTemplate(); //TODO extract to have a more generic Skybox.
		skybox.renderer = SkyboxRenderer.create(skyboxShader, camera, skybox.skyboxTextureDay, skybox.skyboxTextureNight);
		//TODO automate this binding.
		masterRenderer.addRenderer(skybox.renderer);
		skybox.skyboxGeom = SimpleGeom3DBuilder.create(masterRenderer.getLoader(), skybox.renderer, "skybox").withShader(skyboxShader).build();
		initGeom(skybox);
		skybox.renderer.reloadAndprocess(skybox.skyboxGeom);
		skybox.renderer.sendForRendering();
		return skybox;
	}

	private static void initGeom(SkyboxDayNight skybox) {
		Vector3f lbn = new Vector3f(-SIZE,  -SIZE, -SIZE);
		Vector3f rbn = new Vector3f(SIZE,  -SIZE, -SIZE);
		Vector3f ltn = new Vector3f(-SIZE,  SIZE, -SIZE);
		Vector3f rtn = new Vector3f(SIZE,  SIZE, -SIZE);
		Vector3f lbf = new Vector3f(-SIZE,  -SIZE, SIZE);
		Vector3f rbf = new Vector3f(SIZE,  -SIZE, SIZE);
		Vector3f ltf = new Vector3f(-SIZE,  SIZE, SIZE);
		Vector3f rtf = new Vector3f(SIZE,  SIZE, SIZE);
		
		skybox.skyboxGeom.addPoint(ltn); 
		skybox.skyboxGeom.addPoint(lbn);
		skybox.skyboxGeom.addPoint(rbn);
		
		skybox.skyboxGeom.addPoint(rbn);
		skybox.skyboxGeom.addPoint(rtn);
		skybox.skyboxGeom.addPoint(ltn);
				
		skybox.skyboxGeom.addPoint(lbf);
		skybox.skyboxGeom.addPoint(lbn);
		skybox.skyboxGeom.addPoint(ltn);
		
		skybox.skyboxGeom.addPoint(ltn);
		skybox.skyboxGeom.addPoint(ltf);
		skybox.skyboxGeom.addPoint(lbf);
				
		skybox.skyboxGeom.addPoint(rbn);
		skybox.skyboxGeom.addPoint(rbf);
		skybox.skyboxGeom.addPoint(rtf);
		
		skybox.skyboxGeom.addPoint(rtf);
		skybox.skyboxGeom.addPoint(rtn);
		skybox.skyboxGeom.addPoint(rbn);
				
		skybox.skyboxGeom.addPoint(lbf);
		skybox.skyboxGeom.addPoint(ltf);
		skybox.skyboxGeom.addPoint(rtf);
		
		skybox.skyboxGeom.addPoint(rtf);
		skybox.skyboxGeom.addPoint(rbf);
		skybox.skyboxGeom.addPoint(lbf);
				
		skybox.skyboxGeom.addPoint(ltn);
		skybox.skyboxGeom.addPoint(rtn);
		skybox.skyboxGeom.addPoint(rtf);
		
		skybox.skyboxGeom.addPoint(rtf);
		skybox.skyboxGeom.addPoint(ltf);
		skybox.skyboxGeom.addPoint(ltn);
				
		skybox.skyboxGeom.addPoint(lbn);
		skybox.skyboxGeom.addPoint(lbf);
		skybox.skyboxGeom.addPoint(rbn);
		
		skybox.skyboxGeom.addPoint(rbn);
		skybox.skyboxGeom.addPoint(lbf);
		skybox.skyboxGeom.addPoint(rbf);
		skybox.skyboxGeom.getRenderingParameters().setRenderMode(GL11.GL_TRIANGLES);
	}
	
	public void useDefaultTemplate() {
		CubeTexture cubeDay = new CubeTexture(DefaultSkybox.TEXTURE_FILES[1], DefaultSkybox.TEXTURE_FILES[0], DefaultSkybox.TEXTURE_FILES[2], DefaultSkybox.TEXTURE_FILES[3], DefaultSkybox.TEXTURE_FILES[5], DefaultSkybox.TEXTURE_FILES[4]);
		CubeTexture cubeNight = new CubeTexture(DefaultSkybox.NIGHT_TEXTURE_FILES[1], DefaultSkybox.NIGHT_TEXTURE_FILES[0], DefaultSkybox.NIGHT_TEXTURE_FILES[2], DefaultSkybox.NIGHT_TEXTURE_FILES[3], DefaultSkybox.NIGHT_TEXTURE_FILES[5], DefaultSkybox.NIGHT_TEXTURE_FILES[4]);
		this.bindCubeTextureDay(cubeDay);
		this.bindCubeTextureNight(cubeNight);
	}
	
	public void bindCubeTextureDay(CubeTexture texture) {
		this.skyboxTextureDay = texture;
		int textureId= this.loader.loadCubeMap(this.skyboxTextureDay);
		this.skyboxTextureDay.setTextureId(textureId);
	}
	
	public void bindCubeTextureNight(CubeTexture texture) {
		this.skyboxTextureNight = texture;
		int textureId= this.loader.loadCubeMap(this.skyboxTextureNight);
		this.skyboxTextureNight.setTextureId(textureId);
	}
	

	@Override
	public List<ISimpleGeom> getGeoms() {
		// TODO Auto-generated method stub
		return null;
	}
}
