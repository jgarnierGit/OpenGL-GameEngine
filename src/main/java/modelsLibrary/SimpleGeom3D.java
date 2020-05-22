package modelsLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import renderEngine.Draw3DRenderer;
import renderEngine.Loader;
import renderEngine.RenderingParameters;
import shaderManager.Draw3DShader;

/**
 * SimpleGeom3D can also render 2D as the z component is only used when
 * transformed by projectionMatrix. This is the vertexShader attach which make
 * the difference.
 * 
 * @author chezmoi
 *
 */
public class SimpleGeom3D extends SimpleGeom {

	public SimpleGeom3D(Loader loader, Draw3DRenderer draw3DRenderer, String alias) {
		super(loader, 3, alias);
		drawRenderer = draw3DRenderer;
	}
	
	private SimpleGeom3D(Loader loader, String alias) {
		super(loader, 3, alias);
	}

	@Override
	public SimpleGeom3D copy(String alias) {
		SimpleGeom3D copy = new SimpleGeom3D(this.loader, alias);
		copy.copy(this, alias); // TODO maybe improve to not set twice renderingParameters?
		return copy;
	}

	@Override
	public void addPoint(Vector point) {
		duplicateLastColor();
		addPoint3f(point);
	}

	@Override
	public void addPoint(Vector point, Vector4f color) {
		addColor(color);
		addPoint3f(point);
	}

	@Override
	public void updateColorByPosition(Vector ref, Vector4f color) {
		Vector3f v3f = getVector3f(ref);
		int i = 0;
		for (Vector3f vertice : this.getVertices()) {
			if (vertice.x == v3f.x && vertice.y == v3f.y && vertice.z == v3f.z) {
				super.updateColor(i, color);
			}
			i++;
		}
	}

	@Override
	public void reloadVao() {
		super.reloadVao(Draw3DShader.COLOR_INDEX);
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
		points = ArrayUtils.toPrimitive(temp.toArray(new Float[invertedVertices.size() * 3]));
	}

	private void addPoint3f(Vector point) {
		Vector3f v3f = getVector3f(point);
		float[] newPoints = ArrayUtils.addAll(points, v3f.x, v3f.y, v3f.z);
		points = newPoints;
	}

	@Override
	public List<Vector3f> getVertices() {
		List<Vector3f> vectors = new ArrayList<>();
		for (int i = 0; i < points.length; i += 3) {
			vectors.add(new Vector3f(points[i], points[i + 1], points[i + 2]));
		}
		return vectors;
	}

	private Vector3f getVector3f(Vector point) {
		if (!(point instanceof Vector3f)) {
			throw new IllegalArgumentException("Vector3f excepted, got " + point.getClass());
		}
		return (Vector3f) point;
	}
}
