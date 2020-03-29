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
	private List<Integer> glRenderModes;
	private float[] points = new float[] {};

	public Ray(Loader loader) {
		this.loader = loader;
		vaoId = loader.loadToVAO(points, 3);
		this.glRenderModes = new ArrayList<>();
	}

	public int getVaoId() {
		return vaoId;
	}

	public float[] getPoints() {
		return points;
	}

	public void reloadPositions() {
		/**Arrays.asList(rayPoints.get(0).x, rayPoints.get(0).y, rayPoints.get(0).z);
		List<Float> temp = rayPoints.stream().map(ray -> Arrays.asList(ray.x, ray.y, ray.z)).flatMap(Collection::stream)
				.collect(Collectors.toList());
		points = ArrayUtils.toPrimitive(temp.toArray(new Float[rayPoints.size() * 3]));**/
		System.out.println(Arrays.toString(points));
		loader.reloadVAOPosition(vaoId, points, 3);
	}

	public void addPoint(Vector3f endRay) {
		float[] newPoints = ArrayUtils.addAll(points, endRay.x,endRay.y,endRay.z);//points
		points = newPoints;
	}

	@Override
	public String toString() {
		return "Ray " + vaoId + " [points=" + Arrays.toString(points) + "]";
	}

	public void resetRay() {
		points = new float[] {};
	}

	public static Ray copy(Ray ray2) {
		Ray ray = new Ray(ray2.loader);
		ray.points = ray.getPoints();
		return ray;
	}

	public List<Integer> getRenderModes() {
		return this.glRenderModes;
	}

	public void addRenderMode(int glRenderMode2) {
		this.glRenderModes.add(glRenderMode2);
	}
}
