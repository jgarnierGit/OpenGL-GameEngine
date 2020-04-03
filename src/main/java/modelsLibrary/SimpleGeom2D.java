package modelsLibrary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector2f;

import renderEngine.Loader;

public class SimpleGeom2D extends SimpleGeom {
	public SimpleGeom2D(Loader loader) {
		super(loader, 2);
	}

	@Override
	public void addPoint(Vector vector) {
		if (!(vector instanceof Vector2f)) {
			throw new IllegalArgumentException("Vector3f excepted, got " + vector.getClass());
		}
		Vector2f v2f = (Vector2f) vector;
		float[] newPoints = ArrayUtils.addAll(points, v2f.x, v2f.y);
		points = newPoints;
	}

	@Override
	public List<Vector2f> getVertices() {
		List<Vector2f> vectors = new ArrayList<>();
		for (int i = 0; i < points.length / 2; i += 2) {
			vectors.add(new Vector2f(points[i], points[i + 1]));
		}
		return vectors;
	}
}
