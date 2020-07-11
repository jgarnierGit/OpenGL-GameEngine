package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.Entity;
import entities.SimpleEntity;
import models.data.Face;
import models.data.SimpleGeom;
import models.data.VAOGeom;
import renderEngine.DrawRenderer;
import renderEngine.MasterRenderer;
import renderEngine.RenderingParameters;
import shaderManager.Draw3DShader;
import shaderManager.IShader3D;

/**
 * SimpleGeom3D can also render 2D as the z component is only used when
 * transformed by projectionMatrix. This is the vertexShader attach which make
 * the difference.
 * 
 * @author chezmoi
 *
 */
public class SimpleGeom3D extends SimpleGeom {

	private SimpleGeom3D() {
		// hidden
	}

	protected static SimpleGeom3D create(MasterRenderer masterRenderer, DrawRenderer draw3DRenderer, IShader3D shader,
			String alias, Entity entity) {
		SimpleGeom3D simpleGeom3D = new SimpleGeom3D();
		masterRenderer.registerRenderer(draw3DRenderer);
		simpleGeom3D.vaoGeom = VAOGeom.create(masterRenderer.getLoader(), draw3DRenderer, 3);
		simpleGeom3D.geomEditor = GeomEditor.create(simpleGeom3D);
		simpleGeom3D.renderingParameters = RenderingParameters.create(shader, simpleGeom3D.vaoGeom, alias, entity);
		return simpleGeom3D;
	}

	protected static SimpleGeom3D createWithDefaultEntity(MasterRenderer masterRenderer, DrawRenderer draw3DRenderer,
			IShader3D shader, String alias) {
		SimpleGeom3D simpleGeom3D = new SimpleGeom3D();
		masterRenderer.registerRenderer(draw3DRenderer);
		simpleGeom3D.vaoGeom = VAOGeom.create(masterRenderer.getLoader(), draw3DRenderer, 3);
		simpleGeom3D.geomEditor = GeomEditor.create(simpleGeom3D);
		simpleGeom3D.renderingParameters = RenderingParameters.create(shader, simpleGeom3D.vaoGeom, alias,
				SimpleEntity.createDefaultEntity());
		return simpleGeom3D;
	}

	@Override
	public SimpleGeom3D copy(String alias) {
		SimpleGeom3D copy = new SimpleGeom3D();
		copy.copyValues(this, alias);
		return copy;
	}

	@Override
	public void addPoint(Vector point) {
		addPoint3f(point);
	}

	@Override
	public void addPoint(Vector point, Vector4f color) {
		addPoint3f(point);
	}

	@Override
	public List<Integer> getPositionsToUpdate(Vector ref) {
		ArrayList<Integer> positionsToUpdate = new ArrayList<>();
		Vector3f v3f = getVector3f(ref);
		int i = 0;
		for (Vector3f vertice : this.getVertices()) {
			if (vertice.x == v3f.x && vertice.y == v3f.y && vertice.z == v3f.z) {
				positionsToUpdate.add(i);
			}
			i++;
		}
		return positionsToUpdate;
	}

	@Override
	public void invertNormals() {
		boolean configurationIncorrect = true;
		RenderingParameters param = this.getRenderingParameters();
		if (param.getRenderMode().isPresent() && GL11.GL_TRIANGLES == param.getRenderMode().get().intValue()) {
			configurationIncorrect = false;
		}

		if (configurationIncorrect) {
			throw new IllegalStateException(
					"invert normals is only available for GL_TRIANGLES, please consider specify a renderMode for this geom");
		}
		Iterator<Vector3f> vertices = this.getVertices().iterator();
		ArrayList<Vector3f> invertedVertices = new ArrayList<>();

		while (vertices.hasNext()) {
			// TODO extract
			// hardcoded logic for GL_TRIANGLES
			Vector3f vert0 = vertices.next();
			Vector3f vert1 = vertices.next();
			Vector3f vert2 = vertices.next();
			invertedVertices.add(vert0);
			invertedVertices.add(vert2);
			invertedVertices.add(vert1);
		}

		List<Float> temp = invertedVertices.stream().map(vertice -> Arrays.asList(vertice.x, vertice.y, vertice.z))
				.flatMap(Collection::stream).collect(Collectors.toList());
		this.vaoGeom.getPositions().setContent(temp);
	}

	private void addPoint3f(Vector point) {
		Vector3f v3f = getVector3f(point);
		List<Float> pointsContent = this.vaoGeom.getPositions().getContent();
		pointsContent.add(v3f.x);
		pointsContent.add(v3f.y);
		pointsContent.add(v3f.z);
	}

	@Override
	public List<Vector3f> getVertices() {
		List<Vector3f> vectors = new ArrayList<>();
		List<Float> content = this.vaoGeom.getPositions().getContent();
		for (int i = 0; i < content.size(); i += 3) {
			vectors.add(new Vector3f(content.get(i), content.get(i + 1), content.get(i + 2)));
		}
		return vectors;
	}

	private Vector3f getVector3f(Vector point) {
		if (!(point instanceof Vector3f)) {
			throw new IllegalArgumentException("Vector3f excepted, got " + point.getClass());
		}
		return (Vector3f) point;
	}

	public List<Face> getFaces() {
		if (!this.getRenderingParameters().getRenderMode().isPresent()) {
			throw new IllegalStateException(
					"No rendering mode set, cannot construct faces. Please specify one of the GL11.GL_TRIANGLES* mode.");
		}
		int mode = this.getRenderingParameters().getRenderMode().get();
		List<Face> faces = new ArrayList<>();
		List<Vector3f> vertices = this.getVertices();
		switch (mode) {
		case GL11.GL_TRIANGLES:
			for (int i = 0; i < vertices.size(); i += 3) {
				faces.add(new Face(vertices.get(i), vertices.get(i + 1), vertices.get(i + 2)));
			}
			break;
		case GL11.GL_TRIANGLE_STRIP:
			for (int i = 0; i < vertices.size() - 2; i++) {
				faces.add(new Face(vertices.get(i), vertices.get(i + 1), vertices.get(i + 2)));
			}
			break;
		case GL11.GL_TRIANGLE_FAN:
			throw new NotImplementedException();
		default:
			throw new IllegalStateException(mode
					+ ": Rendering mode not allowed to construct faces. Please specify one of the GL11.GL_TRIANGLES* mode.");
		}
		return faces;
	}

	public Draw3DShader getShader() {
		return (Draw3DShader) this.renderingParameters.getShader();
	}
}
