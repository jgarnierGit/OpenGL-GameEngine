package toolbox;

import static org.lwjgl.glfw.GLFW.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.GuiTexture;
import entities.UserInputHandler;
import modelsLibrary.Ray;
import modelsLibrary.Terrain;
import renderEngine.DisplayManager;
import renderEngine.GuiRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.RayRenderer;

public class MousePicker {
	private static final int MAX_ITERATION = 200;
	private static final int RECURSION_COUNT = 200;
	private static final float RAY_RANGE = 600;
	
	private Vector3f currentRay;
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	private Logger logger;
	private List<Entity> entities;
	private Loader loader;
	private RayRenderer rayRenderer;
	
	private Terrain terrain;
	private Vector3f currentTerrainPoint;

	public MousePicker(Camera cam, Matrix4f projection, Loader loader, RayRenderer rayRenderer, Terrain terrain) {
		this.camera = cam;
		this.projectionMatrix = projection;
		this.viewMatrix = Maths.createViewMatrix(cam);
		this.logger = Logger.getLogger("MousePicker");
		this.entities = new ArrayList<>();
		this.loader = loader;
		this.rayRenderer = rayRenderer;
		this.terrain = terrain;
	}
	
	public Vector3f getCurrentTerrainPoint() {
		return currentTerrainPoint;
	}

	public Vector3f getCurrentRay() {
		return currentRay;

	}

	public void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
		if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
		} else {
			currentTerrainPoint = null;
		}
		log();
		entities.clear();
		
	}

	private void cleanSelected() {
		for (Entity entity : entities) {
			entity.unselect();
		}
	}

	private void log() {
		if (UserInputHandler.activateOnPressOneTime(GLFW_MOUSE_BUTTON_LEFT)) {
			cleanSelected();
			float mouseX = UserInputHandler.getMouseXpos();
			float mouseY = UserInputHandler.getMouseYpos();
			System.out.println("ViewPort Space [" + mouseX + ", " + mouseY + "]");
			Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);
			System.out.println("Normalized device Space [" + normalizedCoords.x + ", " + normalizedCoords.y + "]");
			Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f);
			System.out.println("Homogeneous clip Space [" + clipCoords.x + ", " + clipCoords.y + ", " + clipCoords.z
					+ ", " + clipCoords.w + "]");
			Vector4f eyeCoords = toEyeCoords(clipCoords);
			System.out.println(
					"Eye Space [" + eyeCoords.x + ", " + eyeCoords.y + ", " + eyeCoords.z + ", " + eyeCoords.w + "]");
			Vector3f worldCoords = toWorldCoords(eyeCoords);
			System.out.println("World Space [" + worldCoords.x + ", " + worldCoords.y + ", " + worldCoords.z + "]");
			System.out.println("-----");
			rayCasting(worldCoords);
		}
	}

	private void rayCasting(Vector3f worldCoords) {
		Vector3f rayCasting = new Vector3f(worldCoords.x * 5, worldCoords.y * 5, worldCoords.z * 5);
		Vector3f orig = new Vector3f(this.camera.getPosition().x, this.camera.getPosition().y, this.camera.getPosition().z);
		rayRenderer.reloadPositions(orig,rayCasting);
		/**
		 * List<Entity> filteredEntities = this.entities.stream().filter(entity -> {
		 * return (entity.getPositions().x - 0.01 < worldCoords.x &&
		 * entity.getPositions().x + 0.01 > worldCoords.x) && (entity.getPositions().y -
		 * 0.01 < worldCoords.y && entity.getPositions().y + 0.01 > worldCoords.y);
		 * }).collect(Collectors.toList()); TODO decomment filtering when debug is done.
		 **/
		Entity selectedEntity = filterInDistance(this.entities, orig, rayCasting, 0, false);
		if (selectedEntity != null) {
			System.out.println(selectedEntity.getModel().getClass() + " is selected");
			selectedEntity.select();
			Vector3f objectWorld = objectToWorldCoord(selectedEntity.getPositions());
			System.out.println(objectWorld);
		} else {
			System.out.println("nothing selected");
		}
	}

	/**
	 * TODO use a start vector and a end vector : to avoid infinite loop if many
	 * entities have a too small range to be isolated
	 * 
	 * @param filteredEntities
	 * @param rayCasting
	 * @return
	 */
	private Entity filterInDistance(List<Entity> filteredEntities, Vector3f minRayCast, Vector3f maxRayCast,
			int iteration, boolean gotResult) {
		// TODO update filtering.
		List<Entity> filteredInZ = filteredEntities.stream().filter(entity -> {
			Vector3f objectWorld = objectToWorldCoord(entity.getPositions());
			return objectWorld.z < maxRayCast.z && objectWorld.z > minRayCast.z;
		}).collect(Collectors.toList());

		if (filteredInZ.size() == 1) {
			return filteredInZ.get(0);
		} else if (iteration >= MAX_ITERATION) { // might never be reach but better safe than infinite
			return null;
		} else if (filteredInZ.isEmpty()) {
			// new max based on twice the distance of current vector.
			// while no match, simple double length of search
			float maxZ = maxRayCast.z * 4;
			float maxX = maxRayCast.x * 4;
			float maxY = maxRayCast.y * 4;
			if (gotResult) {
				maxZ = maxRayCast.z + (maxRayCast.z - minRayCast.z);
				maxX = maxRayCast.x + (maxRayCast.x - minRayCast.x);
				maxY = maxRayCast.y + (maxRayCast.y - minRayCast.y);
			}
			// cap to max rendered distance.
			if (maxZ > MasterRenderer.getFarPlane()) {
				maxZ = MasterRenderer.getFarPlane();
				maxX = (maxRayCast.x * (MasterRenderer.getFarPlane() / maxRayCast.z));
				maxY = (maxRayCast.y * (MasterRenderer.getFarPlane() / maxRayCast.z));
			}
			Vector3f rayCastingLonger = new Vector3f(maxX, maxY, maxZ);
			return filterInDistance(filteredEntities, maxRayCast, rayCastingLonger, ++iteration, gotResult);
		} else {
			float maxZ = minRayCast.z + (maxRayCast.z - minRayCast.z) / 2;
			float maxX = minRayCast.x + (maxRayCast.x - minRayCast.x) / 2;
			float maxY = minRayCast.y + (maxRayCast.y - minRayCast.y) / 2;
			Vector3f rayCastingShorter = new Vector3f(maxX, maxY, maxZ);
			return filterInDistance(filteredEntities, minRayCast, rayCastingShorter, ++iteration, true);
		}
	}

	private Vector3f calculateMouseRay() {
		float mouseX = UserInputHandler.getMouseXpos();
		float mouseY = UserInputHandler.getMouseYpos();
		Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f); //pointing into the screen
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		return toWorldCoords(eyeCoords);
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();//just want to be a direction
		return mouseRay;
	}

	private Vector3f objectToWorldCoord(Vector3f objectPosition) {
		Vector4f objectPos4f = new Vector4f(objectPosition.x, objectPosition.y, objectPosition.z, 1f);
		Vector4f objectWorld = Matrix4f.transform(viewMatrix, objectPos4f, null);
		Vector3f objectToWorld = new Vector3f(objectWorld.x, objectWorld.y, objectWorld.z);
		objectToWorld.normalise();
		return objectToWorld;
	}

	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private Vector2f getNormalizedDeviceCoords(float mouseX, float mouseY) {
		float x = (2f * mouseX) / DisplayManager.WIDTH - 1f;
		float y = 1f - (2f * mouseY) / DisplayManager.HEIGHT;
		return new Vector2f(x, y);
	}

	public void processEntity(Entity entityGrass) {
		this.entities.add(entityGrass);
	}
	
	//**********************************************************
	
		private Vector3f getPointOnRay(Vector3f ray, float distance) {
			Vector3f camPos = camera.getPosition();
			Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
			Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
			return Vector3f.add(start, scaledRay, null);
		}
		
		private void logPointOnRay(Vector3f start, Vector3f endPoint) {
			if (UserInputHandler.activateOnPressOneTime(GLFW_MOUSE_BUTTON_LEFT)) {
			System.out.println("start");
			System.out.println(start);
			System.out.println("end");
			System.out.println(endPoint);
			rayRenderer.reloadPositions(start,endPoint);
			}
		}

		private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
			float half = start + ((finish - start) / 2f);
			if (count >= RECURSION_COUNT) {
				Vector3f endPoint = getPointOnRay(ray, half);
				//logPointOnRay(camera.getPosition(),endPoint);
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
			Terrain terrain = getTerrain(testPoint.getX(), testPoint.getZ());
			float height = 0;
			if (terrain != null) {
				height = terrain.getHeight(testPoint.getX(), testPoint.getZ());
			}
			if (testPoint.y < height) {
				return true;
			} else {
				return false;
			}
		}

		private Terrain getTerrain(float worldX, float worldZ) {
			return terrain;
		}
}
