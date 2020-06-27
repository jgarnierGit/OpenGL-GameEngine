package renderEngine;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector4f;

import camera.CameraEntity;
import entities.EntityTutos;
import entities.Light;
import modelsLibrary.ISimpleGeom;
import modelsManager.Model3D;
import shaderManager.StaticShader;
import shaderManager.TerrainShader;
import toolbox.CoordinatesSystemManager;

public class MasterRenderer {
	public static final float RED = 0.55f;
	public static final float BLUE = 0.64f;
	public static final float GREEN = 0.75f;
	private float time = 0;

	private StaticShader shader;
	private EntityRenderer renderer;
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader;
	private Draw3DRenderer draw3DRenderer;
	private Draw2DRenderer draw2DRenderer;
	private List<Model3D> terrains = new ArrayList<>();
	private CameraEntity camera;
	private Loader loader;
	
	private List<DrawRenderer> specificRenderers;
	
	private HashMap<Model3D, List<EntityTutos>> entities = new HashMap<>();
	
	private MasterRenderer(Loader loader, CameraEntity camera, StaticShader shader, EntityRenderer renderer,Draw3DRenderer draw3DRenderer, Draw2DRenderer draw2DRenderer, TerrainShader terrainShader) {
		this.loader =loader;
		this.camera = camera;
		this.draw3DRenderer = draw3DRenderer;
		this.draw2DRenderer = draw2DRenderer;
		this.renderer = renderer;
		this.shader = shader;
		this.terrainShader = terrainShader;
		this.specificRenderers = new ArrayList<>();
	}
	
	public static MasterRenderer create(CameraEntity camera) throws IOException {
		StaticShader shader = new StaticShader();
		enableCulling();
		EntityRenderer renderer = new EntityRenderer(shader, CoordinatesSystemManager.getProjectionMatrix());
		//terrainRenderer = new TerrainRenderer(terrainShader, CoordinatesSystemManager.getProjectionMatrix());//TODO extract
		Draw3DRenderer draw3DRenderer = new Draw3DRenderer(camera);
		Draw2DRenderer draw2DRenderer = new Draw2DRenderer();
		TerrainShader terrainShader = new TerrainShader();
		Loader loader = new Loader();
		
		return new MasterRenderer(loader, camera, shader, renderer, draw3DRenderer, draw2DRenderer, terrainShader);
	}
	
	public Draw3DRenderer get3DRenderer() {
		return this.draw3DRenderer;
	}
	
	public Draw2DRenderer get2DRenderer() {
		return this.draw2DRenderer;
	}
	
	public Loader getLoader() {
		return this.loader;
	}


	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK); // do not render hidden vertices.
	}
	
	/**
	 * Allow to render geometries defined in anticlockwise turn.
	 * Clockwise detection is useful to not render faces oriented backward to camera.
	 * This way plane can be rendered front and back
	 */
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	private void updateProjectionInTime() {
		time += DisplayManager.getFrameTimeSeconds() * 50;
		time %= 180;
		float aspectRatio = (float) DisplayManager.WIDTH / (float) DisplayManager.HEIGHT;
		float y_scale = (float)  ((1f / Math.tan(Math.toRadians(time/ 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		Matrix4f projectionMatrix = CoordinatesSystemManager.getProjectionMatrix();

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		renderer.setProjectionMatrix(projectionMatrix);
		
		//float y_scale2 = (float)  ((1f / Math.tan(Math.toRadians(-time/ 2f))) * aspectRatio);
		//float x_scale2 = y_scale / aspectRatio;
		//projectionMatrix.m00 = y_scale2;
		//projectionMatrix.m11 = x_scale2;
		terrainShader.start();
		terrainShader.loadProjectionMatrix(projectionMatrix);
		terrainShader.stop();
		
	}
	
	public void render(List<Light> lights, Vector4f clipPlane) {
		//updateProjectionInTime();
		camera.updateViewMatrix();
		prepare();
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColour(RED, GREEN, BLUE);
		shader.loadViewMatrix(camera);
		shader.loadLightsColor(lights);
		renderer.render(entities);
		shader.stop();
	/**	terrainShader.start();
	 * terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadSkyColour(RED, GREEN, BLUE);
		terrainShader.loadViewMatrix(camera);
		terrainShader.loadLightsColor(lights);
		terrainRenderer.render(terrains);
		terrainShader.stop();**/
		draw3DRenderer.setClipPlane(clipPlane);
		draw3DRenderer.render();
		draw2DRenderer.render();
		for(DrawRenderer drawRenderer : this.specificRenderers) {
			drawRenderer.render();
		}
	}
	
	public void clean() {
		terrains.clear();
		entities.clear();
	}
	
	public void processTerrain(Model3D terrain) {
		terrains.add(terrain);
	}
	
	/**
	 * TODO adapt logic to process list based on same texture but different models?
	 * @param entity
	 */
	public void processEntity(EntityTutos entity) {
		Model3D entityModel = entity.getModel();
		List<EntityTutos> batch = entities.getOrDefault(entityModel, new ArrayList<>());
		if(batch.isEmpty()) {
			entities.put(entityModel, new ArrayList<EntityTutos>(Arrays.asList(entity)));
		}
		else {
			batch.add(entity);
		}
	}
	
	public void cleanUp() {
		shader.cleanUp();
	//	terrainShader.cleanUp();
		draw3DRenderer.cleanUp();
		draw2DRenderer.cleanUp();
		for(DrawRenderer drawRenderer : this.specificRenderers) {
			drawRenderer.cleanUp();
		}
		loader.cleanUp();
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST); // test the depth priority
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer
		GL11.glClearColor(RED, GREEN, BLUE, 1);

	}
	
	
	/**
	 * TODO ugly solution to make it static... find another way
	 * @Duplicated from {Loader.storeDataInIntBuffer}
	 * @param data
	 * @return
	 */
	public static IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		((Buffer) buffer).flip();
		return buffer;
	}

	/**
	 * @deprecated use reloadAndprocess from specified renderer
	 * One more optimization must be done in future to not reload unchanged geometries (vertices + colors)
	 * @param geom
	 */
	public void reloadAndprocess(ISimpleGeom geom) {
		geom.reloadVao();
		geom.updateRenderer();
	}

	//TODO maybe find another way for those 2 methods. delegate to another class to keep this one clean.
	public void sendForRendering() {
		this.draw2DRenderer.sendForRendering();
		this.draw3DRenderer.sendForRendering();
		for(DrawRenderer drawRenderer : this.specificRenderers) {
			drawRenderer.sendForRendering();
		}
	}

	public void addRenderer(DrawRenderer renderer) {
		this.specificRenderers.add(renderer);
	}
}
