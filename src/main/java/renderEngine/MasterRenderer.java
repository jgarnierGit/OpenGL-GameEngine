package renderEngine;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Vector4f;

import camera.CameraEntity;
import entities.Entity;
import entities.GeomContainer;
import entities.Light;
import shadows.ShadowMapMasterRenderer;

public class MasterRenderer {
	public static final float RED = 0.55f;
	public static final float BLUE = 0.64f;
	public static final float GREEN = 0.75f;

	private Draw3DRenderer defaultDraw3DRenderer;
	private Draw2DRenderer defaultDraw2DRenderer;
	private CameraEntity camera;
	private Loader loader;

	private Set<DrawRenderer> renderers;
	private Set<DrawRenderer> activeRenderers;
	private ShadowMapMasterRenderer shadowMasterRenderer;

	private MasterRenderer(Loader loader, CameraEntity camera,
			Draw3DRenderer draw3DRenderer, Draw2DRenderer draw2DRenderer, ShadowMapMasterRenderer shadowMasterRenderer) {
		this.loader = loader;
		this.camera = camera;
		this.defaultDraw3DRenderer = draw3DRenderer;
		this.defaultDraw2DRenderer = draw2DRenderer;
		this.renderers = new HashSet<>();
		this.activeRenderers = new HashSet<>();
		this.shadowMasterRenderer = shadowMasterRenderer;
	}

	public static MasterRenderer create(CameraEntity camera) throws IOException {
		enableCulling();
		Draw3DRenderer draw3DRenderer = new Draw3DRenderer(camera);
		Draw2DRenderer draw2DRenderer = new Draw2DRenderer();
		Loader loader = new Loader();
		ShadowMapMasterRenderer shadowMasterRenderer = new ShadowMapMasterRenderer(camera);
		return new MasterRenderer(loader, camera, draw3DRenderer, draw2DRenderer, shadowMasterRenderer);
	}

	public Draw3DRenderer getDefault3DRenderer() {
		return this.defaultDraw3DRenderer;
	}

	public Draw2DRenderer getDefault2DRenderer() {
		return this.defaultDraw2DRenderer;
	}

	public Loader getLoader() {
		return this.loader;
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK); // do not render hidden vertices.
	}

	/**
	 * Allow to render geometries defined in anticlockwise turn. Clockwise detection
	 * is useful to not render faces oriented backward to camera. This way plane can
	 * be rendered front and back
	 */
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	/**
	 * load datas in each renderers
	 * @param arrayList
	 * @param toRender
	 * @param clipPlane
	 */
	public void reloadRenderingDatas(List<Light> arrayList, List<GeomContainer> geomToRender, Vector4f clipPlane) {
		//add geom to its renderer queue for rendering
		this.activeRenderers.clear();
		geomToRender.forEach(geom -> {
			if(activeRenderers.add(geom.getRenderableGeom().getRenderer())) {
				geom.getRenderableGeom().getRenderer().clearGeom();
			}
			geom.getRenderableGeom().updateRenderer();
		});
		updateForRendering(activeRenderers);
		//TODO extract it to specific shader? or not can be general setter
		defaultDraw3DRenderer.setClipPlane(clipPlane); 
	}

	public void render() {
		camera.updateViewMatrix();
		prepare();
		for (DrawRenderer drawRenderer : activeRenderers) {
			drawRenderer.render();
		}
	}
	
	public void renderShadowMap(List<GeomContainer> geomToRender, Light sun) {
		shadowMasterRenderer.render(geomToRender, sun);
	}
	
	public int getShadowMapTexture() {
		return shadowMasterRenderer.getShadowMap();
	}

	public void cleanUp() {
		for (DrawRenderer drawRenderer : this.renderers) {
			drawRenderer.cleanUp();
		}
		loader.cleanUp();
		shadowMasterRenderer.cleanUp();
	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST); // test the depth priority
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer
		GL11.glClearColor(RED, GREEN, BLUE, 1);
	}

	/**
	 * TODO ugly solution to make it static... find another way
	 * 
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

	private void updateForRendering(Set<DrawRenderer> renderers) {
		for (DrawRenderer drawRenderer : renderers) {
			drawRenderer.updateForRendering();
		}
	}
	
	public void registerRenderer(DrawRenderer renderer) {
			this.renderers.add(renderer);
		}
}
