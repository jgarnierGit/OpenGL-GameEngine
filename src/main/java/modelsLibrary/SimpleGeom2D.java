package modelsLibrary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector4f;

import renderEngine.Loader;

public class SimpleGeom2D extends SimpleGeom {
	public SimpleGeom2D(Loader loader) {
		super(loader, 2);
	}

	@Override
	public void addPoint(Vector point) {
		duplicateLastColor();
		addPoint2f(point);
	}
	
	@Override
	public void addPoint(Vector point, Vector4f color) {
		addColor(color);
		addPoint2f(point);
	}
	
	private void addPoint2f(Vector point) {
		if (!(point instanceof Vector2f)) {
			throw new IllegalArgumentException("Vector2f excepted, got " + point.getClass());
		}
		Vector2f v2f = (Vector2f) point;
		float[] newPoints = ArrayUtils.addAll(points, v2f.x, v2f.y);
		points = newPoints;
	}

	@Override
	public List<Vector2f> getVertices() {
		List<Vector2f> vectors = new ArrayList<>();
		for (int i = 0; i < points.length; i += 2) {
			vectors.add(new Vector2f(points[i], points[i + 1]));
		}
		return vectors;
	}

	@Override
	public void updateColor(Vector point, Vector4f color) {
		throw new UnsupportedOperationException("need to keep an internal List<Vector> to retreive point index in float index and update color.");
	}
}
