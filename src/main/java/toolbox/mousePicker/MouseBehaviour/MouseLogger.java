package toolbox.mousePicker.MouseBehaviour;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.UserInputHandler;
import modelsLibrary.Ray;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.RayRenderer;
import toolbox.Maths;

public class MouseLogger implements IMouseBehaviour {
	private List<Entity> entities;
	private Camera camera;
	private RayRenderer rayRenderer;
	private Ray ray;
	private Matrix4f viewMatrix;
	private Vector3f camPos;
	private Loader loader;
	private static final int BOUNDING_BOX = 4;

	public MouseLogger(Camera camera, RayRenderer rayRenderer, Loader loader) {
		this.entities = new ArrayList<>();
		this.camera = camera;
		this.rayRenderer = rayRenderer; //TODO I want to render via a DrawGeomRenderer using logic as simple as RayRenderer, but I want to specify what I render (GL_POINTS, TRIANGLES, LINES) dynamically. same logic will fit with others Renderer. 
		this.viewMatrix = Maths.createViewMatrix(camera);
		this.loader = loader;
		this.ray = new Ray(this.loader);
	}

	@Override
	public void process(Vector3f ray) {
		// FIXME why need many click to be interpreted
		if (UserInputHandler.activateOnPressOneTime(GLFW_MOUSE_BUTTON_LEFT)) {
			cleanSelected();
			this.camPos = camera.getPosition();
			// ray is with world origin.
			rayCasting(ray);
		}
	}

	private void cleanSelected() {
		for (Entity entity : entities) {
			entity.unselect();
		}
	}
	
	/**
	 * Apply distance given by distance and orientation from cursorRay to camera position
	 * @param cursorRay always starts from mouse coordinates.
	 * @param distance
	 * @return Vector3f 3D position of cursor click + distance transformation.
	 */
	private Vector3f getPointOnRay(Vector3f cursorRay, float distance) {
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(cursorRay.x * distance, cursorRay.y * distance, cursorRay.z * distance);
		return Vector3f.add(start, scaledRay, null);
	}

	private void rayCasting(Vector3f mouseCoord) {
		System.out.println("mouseCoord");
		System.out.println(mouseCoord);
		Float distance = 5f;
		Vector3f origRay = getPointOnRay(mouseCoord, 0);
		Vector3f endRay = getPointOnRay(mouseCoord, distance);
		/**System.out.println("origRay");
		System.out.println(origRay);
		System.out.println("endRay");
		System.out.println(endRay);**/
		this.ray.addPoint(origRay); 
		
		/**System.out.println(this.entities);
		this.entities.forEach(entity -> {
			System.out.println("entity");
			System.out.println(entity.getPositions());
		});**/
		Optional<Entity> selectedEntity = rayMarching(mouseCoord, 0f, distance);
		if (selectedEntity.isPresent()) {
			Entity entity = selectedEntity.get();
			System.out.println(entity.getModel().getClass() + " is selected");
			entity.select();
			//Vector3f objectWorld = objectToWorldCoord(selectedEntity.getPositions());
			System.out.println(entity.getPositions());
		} else {
			System.out.println("nothing selected");
		}
		System.out.println(ray);
		this.ray.reloadPositions();
		this.rayRenderer.process(this.ray, GL11.GL_POINTS);
	}
	
	private Optional<Entity> rayMarching(Vector3f mouseCoord, Float startPointDistance, Float distance) {
		Vector3f beginRay = getPointOnRay(mouseCoord, startPointDistance);
		if(distance > MasterRenderer.getFarPlane()) {
			distance =  MasterRenderer.getFarPlane();
			Vector3f endRay = getPointOnRay(mouseCoord, distance);
			return getMatchingEntities(beginRay, endRay);
		}
		Vector3f endRay = getPointOnRay(mouseCoord, distance);
		this.ray.addPoint(endRay);
		Optional<Entity> matchedEntity = getMatchingEntities(beginRay, endRay);
		if(matchedEntity.isPresent()) {
			return matchedEntity;
		}
		return rayMarching(mouseCoord, distance, distance * 2);
	}

	/**
	 * TODO transform entity position using viewMatrix so we can easily order by distance using z coordinate.
	 * find each Entity between beginRay and endRay
	 * @param endRay
	 * @return nearest Entity if any
	 */
	private Optional<Entity> getMatchingEntities(Vector3f beginRay, Vector3f endRay) {
		Map<Entity, Vector3f> entitiesViewPosition = this.entities.stream().collect(Collectors.toMap(Function.identity(), Entity::getPositions)); 
		TreeMap<Float, Entity> result = entitiesViewPosition.entrySet().stream().filter(entry -> {
			Entity entity = entry.getKey();
			Vector3f worldPositionEntity = entity.getPositions(); //TODO print entity position point.
			if((worldPositionEntity.x + BOUNDING_BOX <= endRay.x && worldPositionEntity.x + BOUNDING_BOX  >= beginRay.x) ||
					(worldPositionEntity.x - BOUNDING_BOX <= endRay.x && worldPositionEntity.x - BOUNDING_BOX  >= beginRay.x) ||
					(worldPositionEntity.y + BOUNDING_BOX <= endRay.y && worldPositionEntity.y + BOUNDING_BOX  >= beginRay.y) ||
					(worldPositionEntity.y - BOUNDING_BOX <= endRay.y && worldPositionEntity.y - BOUNDING_BOX  >= beginRay.y) ||
					(worldPositionEntity.z + BOUNDING_BOX <= endRay.z && worldPositionEntity.z + BOUNDING_BOX  >= beginRay.z) ||
					(worldPositionEntity.z - BOUNDING_BOX <= endRay.z && worldPositionEntity.z - BOUNDING_BOX  >= beginRay.z)) {
				return true;
			}
			return false;
		}).collect(Collectors.toMap(entity -> objectToWorldCoord(entity.getKey()).z, Entry::getKey, (o1,o2) -> o1, TreeMap::new));
		if(result.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(result.firstEntry().getValue());
	}

	private Vector3f objectToWorldCoord(Entity entity) {
		System.out.println(entity.getModel());
		Vector3f objectPosition = entity.getPositions();
		System.out.println("worldCoord");
		System.out.println(objectPosition);
		Vector4f objectPos4f = new Vector4f(objectPosition.x, objectPosition.y, objectPosition.z, 1f);
		Vector4f objectWorld = Matrix4f.transform(viewMatrix, objectPos4f, null);
		Vector3f objectToWorld = new Vector3f(objectWorld.x, objectWorld.y, objectWorld.z);
		objectToWorld.normalise();
		System.out.println("viewWorld");
		System.out.println(objectToWorld);
		return objectToWorld;
	}

	public void processEntity(Entity entities) {
		this.entities.add(entities);
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
			Vector3f objectWorld = entity.getPositions(); //objectToWorldCoord(
			//System.out.println(entity.getPositions());
			return objectWorld.z < maxRayCast.z && objectWorld.z > minRayCast.z;
		}).collect(Collectors.toList());

		if (filteredInZ.size() == 1) {
			return filteredInZ.get(0);
		} else if (iteration >= RAY_RANGE) { // might never be reach but better safe than infinite
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
			if (maxZ > MasterRenderer.getFarPlane()) { //TODO cannot be just a coordinate. as to be a distance calculated by vector.
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
	
	
	public void clear() {
		entities.clear();
	}

}
