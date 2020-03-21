package modelsLibrary;

import org.lwjglx.util.vector.Vector3f;

import modelsManager.Model3D;
import renderEngine.Loader;

//TODO class not using Model3D logic...
public class Ray{
	private int vaoId;
	private Loader loader;
	private float[] points = new float[] { -0f, 0f, -0f, 1f,1f,1f};

	public Ray (Loader loader) {
		this.loader = loader;
		vaoId = loader.loadToVAO(points,3);
	}
	
	
	public int getVaoId() {
		return vaoId;
	}
	
	public float[] getPoints() {
		return points;
	}

	public void setEndPosition(Vector3f end) {
		points[3] = end.x;
		points[4] = end.y;
		points[5] = end.z;
		loader.reloadVAOPosition(vaoId, points,3);
	}
	
/**	public void setRayEndPosition(Vector3f end) {
		//Vector3f length = Vector3f.sub(worldPosition, end, null);
		geom.setEndPosition(end); //= new LineGeom(end, this.loader);
	} **/
}
