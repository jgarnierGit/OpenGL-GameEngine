package modelsLibrary;

import org.lwjglx.util.vector.Vector3f;

import renderEngine.Loader;

public class LineGeom {
	private final int vaoId;
	private Vector3f origine;
	private Vector3f destination;
	private float[] points = new float[] { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
	
	public LineGeom(Vector3f destination, Loader loader) {
		origine = new Vector3f(0f,0f,0f);
		this.destination = destination;
		System.out.println("destination");
		System.out.println(destination);
		//points = new float[] { origine.x, origine.y, origine.z, destination.x, destination.y, destination.z }; //why not two points printed
		vaoId = loader.loadToVAO(points,3); // load needs to be in renderer for some reason to get appropriate index.
	}
	
	public int getVaoId() {
		return vaoId;
	}
	
	public float[] getPoints() {
		return points;
	}

	public void setEndPosition(Vector3f end) {
		points[3] = end.x;
		points[4] = end.x;
		points[5] = end.x;
	}
}
