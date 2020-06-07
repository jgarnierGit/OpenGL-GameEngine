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

import entities.Camera;
import entities.EntityTutos;
import entities.Light;
import modelsLibrary.SimpleGeom;
import modelsManager.Model3D;
import shaderManager.StaticShader;
import shaderManager.TerrainShader;

public class MasterRenderer {
	private static final float FOV = 70f;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	private static final float RED = 0.55f;
	private static final float BLUE = 0.64f;
	private static final float GREEN = 0.75f;
	private static final float ASPECT_RATIO = (float) DisplayManager.WIDTH / (float) DisplayManager.HEIGHT;
	private float time = 0;
	
	
	public static float getNearPlane() {
		return NEAR_PLANE;
	}

	public static float getFarPlane() {
		return FAR_PLANE;
	}
	
	public static float getFOV() {
		return FOV;
	}
	
	public static float getAspectRatio() {
		return ASPECT_RATIO;
	}

	private StaticShader shader;
	private EntityRenderer renderer;
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader;
	private Draw3DRenderer draw3DRenderer;
	private Draw2DRenderer draw2DRenderer;
	private List<Model3D> terrains = new ArrayList<>();
	private  Camera camera;
	private Loader loader;
	
	private SkyboxRenderer skyboxRender;
	
	private Matrix4f projectionMatrix;
	
	private HashMap<Model3D, List<EntityTutos>> entities = new HashMap<>();
	
	private MasterRenderer(Loader loader, Camera camera, StaticShader shader, EntityRenderer renderer,Draw3DRenderer draw3DRenderer, Draw2DRenderer draw2DRenderer, TerrainShader terrainShader) {
		this.loader =loader;
		this.camera = camera;
		this.draw3DRenderer = draw3DRenderer;
		this.draw2DRenderer = draw2DRenderer;
		this.renderer = renderer;
		this.shader = shader;
		this.terrainShader = terrainShader;
	}
	
	public static MasterRenderer create(Camera camera) throws IOException {
		StaticShader shader = new StaticShader();
		enableCulling();
		Matrix4f projectionMatrix = createProjectionMatrix();
		EntityRenderer renderer = new EntityRenderer(shader, projectionMatrix);
		//terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		//skyboxRender = new SkyboxRenderer(loader, projectionMatrix); TODO extract
		Draw3DRenderer draw3DRenderer = new Draw3DRenderer(camera, projectionMatrix);
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
	
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
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
	
	public void render(List<Light> lights) {
		//updateProjectionInTime();
		prepare();
		shader.start();
		shader.loadSkyColour(RED, GREEN, BLUE);
		shader.loadViewMatrix(camera);
		shader.loadLightsColor(lights);
		renderer.render(entities);
		shader.stop();
	/**	terrainShader.start();
		terrainShader.loadSkyColour(RED, GREEN, BLUE);
		terrainShader.loadViewMatrix(camera);
		terrainShader.loadLightsColor(lights);
		terrainRenderer.render(terrains);
		terrainShader.stop();**/
		//skyboxRender.render(camera,RED, GREEN, BLUE);
		draw3DRenderer.render();
		draw2DRenderer.render();
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
		loader.cleanUp();
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST); // test the depth priority
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer
		GL11.glClearColor(RED, GREEN, BLUE, 1);

	}
	
	private static Matrix4f createProjectionMatrix() {
		float y_scale = (float)  ((1f / Math.tan(Math.toRadians(FOV/ 2f))) * ASPECT_RATIO);
		float x_scale = y_scale / ASPECT_RATIO;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 =  -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
		return projectionMatrix;
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
	 * One more optimization must be done in future to not reload unchanged geometries (vertices + colors)
	 * @param geom
	 */
	public void reloadAndprocess(SimpleGeom geom) {
		geom.reloadVao();
		geom.updateRenderer();
	}

	//TODO maybe find another way for those 2 methods. delegate to another class to keep this one clean.
	public void sendForRendering() {
		this.draw2DRenderer.sendForRendering();
		this.draw3DRenderer.sendForRendering();
	}

	public void clearGeom() {
		this.draw2DRenderer.clearGeom();
		this.draw3DRenderer.clearGeom();
	}
}
