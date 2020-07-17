package models.library;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Vector3f;

import camera.CameraEntity;
import entities.GeomContainer;
import models.GeomEditor;
import models.EditableGeom;
import models.RenderableGeom;
import models.SimpleGeom3D;
import models.SimpleGeom3DBuilder;
import models.data.CubeTexture;
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
		skybox.useDefaultTemplate(); // TODO extract to have a more generic Skybox.
		skybox.renderer = SkyboxRenderer.create(skyboxShader, camera, skybox.skyboxTextureDay,
				skybox.skyboxTextureNight);
		skybox.skyboxGeom = SimpleGeom3DBuilder.create(masterRenderer, skybox.renderer, "skybox")
				.withShader(skyboxShader).build();
		skybox.initGeom();
		//TODO merge what is better between this way and monkey way
		skybox.renderer.reloadGeomToVAO(skybox.skyboxGeom);
		return skybox;
	}

	private void initGeom() {
		if (!this.skyboxGeom.getVertices().isEmpty()) {
			return;
		}
		Vector3f lbn = new Vector3f(-SIZE, -SIZE, -SIZE);
		Vector3f rbn = new Vector3f(SIZE, -SIZE, -SIZE);
		Vector3f ltn = new Vector3f(-SIZE, SIZE, -SIZE);
		Vector3f rtn = new Vector3f(SIZE, SIZE, -SIZE);
		Vector3f lbf = new Vector3f(-SIZE, -SIZE, SIZE);
		Vector3f rbf = new Vector3f(SIZE, -SIZE, SIZE);
		Vector3f ltf = new Vector3f(-SIZE, SIZE, SIZE);
		Vector3f rtf = new Vector3f(SIZE, SIZE, SIZE);

		this.skyboxGeom.addPoint(ltn);
		this.skyboxGeom.addPoint(lbn);
		this.skyboxGeom.addPoint(rbn);

		this.skyboxGeom.addPoint(rbn);
		this.skyboxGeom.addPoint(rtn);
		this.skyboxGeom.addPoint(ltn);

		this.skyboxGeom.addPoint(lbf);
		this.skyboxGeom.addPoint(lbn);
		this.skyboxGeom.addPoint(ltn);

		this.skyboxGeom.addPoint(ltn);
		this.skyboxGeom.addPoint(ltf);
		this.skyboxGeom.addPoint(lbf);

		this.skyboxGeom.addPoint(rbn);
		this.skyboxGeom.addPoint(rbf);
		this.skyboxGeom.addPoint(rtf);

		this.skyboxGeom.addPoint(rtf);
		this.skyboxGeom.addPoint(rtn);
		this.skyboxGeom.addPoint(rbn);

		this.skyboxGeom.addPoint(lbf);
		this.skyboxGeom.addPoint(ltf);
		this.skyboxGeom.addPoint(rtf);

		this.skyboxGeom.addPoint(rtf);
		this.skyboxGeom.addPoint(rbf);
		this.skyboxGeom.addPoint(lbf);

		this.skyboxGeom.addPoint(ltn);
		this.skyboxGeom.addPoint(rtn);
		this.skyboxGeom.addPoint(rtf);

		this.skyboxGeom.addPoint(rtf);
		this.skyboxGeom.addPoint(ltf);
		this.skyboxGeom.addPoint(ltn);

		this.skyboxGeom.addPoint(lbn);
		this.skyboxGeom.addPoint(lbf);
		this.skyboxGeom.addPoint(rbn);

		this.skyboxGeom.addPoint(rbn);
		this.skyboxGeom.addPoint(lbf);
		this.skyboxGeom.addPoint(rbf);
		this.skyboxGeom.getRenderingParameters().setRenderMode(GL11.GL_TRIANGLES);
	}

	public void useDefaultTemplate() {
		CubeTexture cubeDay = new CubeTexture(DefaultSkybox.TEXTURE_FILES[1], DefaultSkybox.TEXTURE_FILES[0],
				DefaultSkybox.TEXTURE_FILES[2], DefaultSkybox.TEXTURE_FILES[3], DefaultSkybox.TEXTURE_FILES[5],
				DefaultSkybox.TEXTURE_FILES[4]);
		CubeTexture cubeNight = new CubeTexture(DefaultSkybox.NIGHT_TEXTURE_FILES[1],
				DefaultSkybox.NIGHT_TEXTURE_FILES[0], DefaultSkybox.NIGHT_TEXTURE_FILES[2],
				DefaultSkybox.NIGHT_TEXTURE_FILES[3], DefaultSkybox.NIGHT_TEXTURE_FILES[5],
				DefaultSkybox.NIGHT_TEXTURE_FILES[4]);
		this.bindCubeTextureDay(cubeDay);
		this.bindCubeTextureNight(cubeNight);
	}

	public void bindCubeTextureDay(CubeTexture texture) {
		this.skyboxTextureDay = texture;
		int textureId = this.loader.loadAndBindCubeMap(this.skyboxTextureDay);
		this.skyboxTextureDay.setTextureId(textureId);
	}

	public void bindCubeTextureNight(CubeTexture texture) {
		this.skyboxTextureNight = texture;
		int textureId = this.loader.loadAndBindCubeMap(this.skyboxTextureNight);
		this.skyboxTextureNight.setTextureId(textureId);
	}

	@Override
	public EditableGeom getEditableGeom() {
		return skyboxGeom;
	}

	@Override
	public RenderableGeom getRenderableGeom() {
		return skyboxGeom;
	}

	@Override
	public GeomEditor getGeomEditor() {
		return skyboxGeom.getGeomEditor();
	}
}
