package modelsLibrary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import renderEngine.Loader;

/**
 * SimpleGeom3D can also render 2D as the z component is only used when transformed by projectionMatrix.
 * This is the vertexShader attach which make the difference.
 * @author chezmoi
 *
 */
public class SimpleGeom3D extends SimpleGeom {

	public SimpleGeom3D(Loader loader) {
		super(loader, 3);
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
	
	private void addPoint3f(Vector point) {
		if (!(point instanceof Vector3f)) {
			throw new IllegalArgumentException("Vector3f excepted, got " + point.getClass());
		}
		Vector3f v3f = (Vector3f) point;
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

	@Override
	public void updateColor(Vector point, Vector4f color) {
		throw new UnsupportedOperationException("need to keep an internal List<Vector> to retreive point index in float index and update color.");
	}

}
