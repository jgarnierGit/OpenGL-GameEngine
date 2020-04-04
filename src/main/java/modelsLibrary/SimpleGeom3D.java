package modelsLibrary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector3f;

import renderEngine.Loader;

public class SimpleGeom3D extends SimpleGeom {

	public SimpleGeom3D(Loader loader) {
		super(loader, 3);
	}

	@Override
	public void addPoint(Vector vector) {
		if (!(vector instanceof Vector3f)) {
			throw new IllegalArgumentException("Vector3f excepted, got " + vector.getClass());
		}
		Vector3f v3f = (Vector3f) vector;
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
}
