package modelsLibrary;

import org.lwjglx.util.vector.Vector3f;

import modelsManager.Model3D;
import renderEngine.Loader;

public class Ray extends Model3D{
	private Vector3f worldPosition;
	private Loader loader;

	public Ray (Vector3f orig, Vector3f destination, Loader loader) {
		worldPosition = orig;
		this.loader = loader;
	}
	
	public void setWorldPosition(Vector3f orig) {
		worldPosition = orig;
	}
	
/**	public void setRayEndPosition(Vector3f end) {
		//Vector3f length = Vector3f.sub(worldPosition, end, null);
		geom.setEndPosition(end); //= new LineGeom(end, this.loader);
	} **/

	public Vector3f getWorldPositionVector3f() {
		return worldPosition;
	}
	
	/**public int getPointsLength() {
		return this.geom.getPoints().length;
	}**/
}
