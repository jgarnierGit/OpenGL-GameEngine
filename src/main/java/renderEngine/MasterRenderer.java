package renderEngine;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Matrix4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.Model3D;
import shaderManager.StaticShader;
import shaderManager.TerrainShader;

public class MasterRenderer {
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	private static final float RED = 0.49f;
	private static final float BLUE = 0.89f;
	private static final float GREEN = 0.98f;
	
	
	private StaticShader shader;
	private EntityRenderer renderer;
	private TerrainRenderer terrainRenderer;
	//TODO FIXME instanciation + composition  is this a good idea?
	private TerrainShader terrainShader = new TerrainShader();
	private List<Model3D> terrains = new ArrayList<>();
	
	private SkyboxRenderer skyboxRender;
	
	private Matrix4f projectionMatrix;
	
	private HashMap<Model3D, List<Entity>> entities = new HashMap<>();
	
	public MasterRenderer(Loader loader) throws IOException {
		shader = new StaticShader();
		enableCulling();
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRender = new SkyboxRenderer(loader, projectionMatrix);
	}
	
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}


	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK); // do not render hidden vertices.
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void render(List<Light> lights, Camera camera) {
		prepare();
		shader.start();
		shader.loadSkyColour(RED, GREEN, BLUE);
		shader.loadViewMatrix(camera);
		shader.loadLightsColor(lights);
		renderer.render(entities);
		shader.stop();
		terrainShader.start();
		terrainShader.loadSkyColour(RED, GREEN, BLUE);
		terrainShader.loadViewMatrix(camera);
		terrainShader.loadLightsColor(lights);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		skyboxRender.render(camera,RED, GREEN, BLUE);
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
	public void processEntity(Entity entity) { //TODO review this method, weird logic
		Model3D entityModel = entity.getModel();
		List<Entity> batch = entities.getOrDefault(entityModel, new ArrayList<>());
		if(batch.isEmpty()) {
			entities.put(entityModel, new ArrayList<Entity>(Arrays.asList(entity)));
		}
		else {
			batch.add(entity);
		}
	}
	
	public void cleanUp() {
		shader.cleanUp();
		terrainShader.cleanUp();
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST); // test the depth priority
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer
		GL11.glClearColor(RED, GREEN, BLUE, 1);

	}
	
	private void createProjectionMatrix() {
		float aspectRatio = (float) DisplayManager.WIDTH / (float) DisplayManager.HEIGHT;
		float y_scale = (float)  ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 =  -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
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
		buffer.flip();
		return buffer;
	}
}
