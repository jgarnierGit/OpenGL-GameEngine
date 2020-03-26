package modelsLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector3f;

import modelsManager.Model3D;
import renderEngine.Loader;

//TODO class not using Model3D logic...
public class Ray {
	private int vaoId;
	private Loader loader;
	List<Vector3f> rayPoints;
	private float[] points = new float[] { -0f, 0f, -0f, 1f, 1f, 1f };

	public Ray(Loader loader) {
		this.loader = loader;
		vaoId = loader.loadToVAO(points, 3);
		rayPoints = new ArrayList<>();
	}

	public int getVaoId() {
		return vaoId;
	}

	public float[] getPoints() {
		return points;
	}
/**
	public void setEndPosition(Vector3f end) {
		points[3] = end.x;
		points[4] = end.y;
		points[5] = end.z;
		loader.reloadVAOPosition(vaoId, points, 3);
	}

	public void setStartPosition(Vector3f start) {
		points[0] = start.x;
		points[1] = start.y;
		points[2] = start.z;
		loader.reloadVAOPosition(vaoId, points, 3);
	}*/

	public void reloadPositions() {
		Arrays.asList(rayPoints.get(0).x, rayPoints.get(0).y, rayPoints.get(0).z);
		List<Float> temp = rayPoints.stream().map(ray -> Arrays.asList(ray.x, ray.y, ray.z)).flatMap(Collection::stream)
				.collect(Collectors.toList());
		points = ArrayUtils.toPrimitive(temp.toArray(new Float[rayPoints.size() * 3]));
		loader.reloadVAOPosition(vaoId, points, 3);
	}

	public void addPoint(Vector3f endRay) {
		rayPoints.add(endRay);
	}

	@Override
	public String toString() {
		return "Ray " + vaoId + " [points=" + Arrays.toString(points) + "]";
	}

	/**
	 * public void setRayEndPosition(Vector3f end) { //Vector3f length =
	 * Vector3f.sub(worldPosition, end, null); geom.setEndPosition(end); //= new
	 * LineGeom(end, this.loader); }
	 **/
}
