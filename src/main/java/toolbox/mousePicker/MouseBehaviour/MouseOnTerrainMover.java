package toolbox.mousePicker.MouseBehaviour;

import org.lwjglx.util.vector.Vector3f;

import camera.CameraEntity;
import modelsLibrary.Terrain;
import renderEngine.MasterRenderer;

/**
 * Ray casting start from max distance : if not subterrain positionned, return, else divide by 2 and affine position until RECURSION_COUNT is reached.
 * @author chezmoi
 *
 */
public class MouseOnTerrainMover implements IMouseBehaviour  {
	private Terrain terrain;
	private CameraEntity camera;
	private Vector3f currentTerrainPoint;
	private Vector3f camPos;
	
	public MouseOnTerrainMover(Terrain terrain, CameraEntity camera) {
		this.terrain = terrain;
		this.camera = camera;
	}
	
	
	//find a way to expose in interface.
	public Vector3f getCurrentTerrainPoint() {
		return currentTerrainPoint;
	}


	@Override
	public void process(Vector3f ray) {
		this.camPos = camera.getPosition();
		if (intersectionInRange(0, MasterRenderer.getFarPlane(), ray)) {
			currentTerrainPoint = binarySearch(0, 0, MasterRenderer.getFarPlane(), ray);
		} else {
			currentTerrainPoint = null;
		}
	}
	
	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
		float half = start + ((finish - start) / 2f);
		if (count >= RECURSION_COUNT) {
			Vector3f endPoint = getPointOnRay(ray, half);
			Terrain terrain = getTerrain(endPoint.getX(), endPoint.getZ());
			if (terrain != null) {
				return endPoint;
			} else {
				return null;
			}
		}
		if (intersectionInRange(start, half, ray)) {
			return binarySearch(count + 1, start, half, ray);
		} else {
			return binarySearch(count + 1, half, finish, ray);
		}
	}
	
	private Vector3f getPointOnRay(Vector3f ray, float distance) {
		
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return Vector3f.add(start, scaledRay, null);
	}

	private boolean intersectionInRange(float start, float finish, Vector3f ray) {
		Vector3f startPoint = getPointOnRay(ray, start);
		Vector3f endPoint = getPointOnRay(ray, finish);
		if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isUnderGround(Vector3f testPoint) {
		Terrain activeTerrain = getTerrain(testPoint.getX(), testPoint.getZ());
		float height = 0;
		if (activeTerrain != null) {
			height = activeTerrain.getHeight(testPoint.getX(), testPoint.getZ());
		}
		if (testPoint.y < height) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * parameters usefull if many terrains are known.
	 * @param worldX	world x position
	 * @param worldZ	world z position.
	 * @return terrain active on (x,z) position
	 */
	private Terrain getTerrain(float worldX, float worldZ) {
		return terrain;
	}
}
