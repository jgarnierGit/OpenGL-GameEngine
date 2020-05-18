package toolbox.mousePicker.MouseBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.Camera;
import entities.EntityTutos;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import toolbox.CoordinatesSystemManager;
import toolbox.Maths;
import toolbox.mousePicker.MouseInputListener;

public class MouseLogger implements IMouseBehaviour {
	private List<EntityTutos> entities;
	private Camera camera;
	private MouserLoggerPrinter mouserLoggerPrinter;
	private Matrix4f viewMatrix;
	private Vector3f camPos;
	private CoordinatesSystemManager coordSysManager;
	private MouseInputListener mouseInputListener;
	private Vector3f ray;

	public MouseLogger(Camera camera, MasterRenderer masterRenderer, Loader loader, MouseInputListener mouseInputListener) {
		this.entities = new ArrayList<>();
		this.camera = camera;
		this.coordSysManager = new CoordinatesSystemManager(masterRenderer.getProjectionMatrix());
		this.mouseInputListener = mouseInputListener;
		this.mouserLoggerPrinter = new MouserLoggerPrinter(loader,masterRenderer, this.coordSysManager);
	}

	@Override
	public void process(Vector3f normalizedRay) {
		this.ray = normalizedRay;
		this.mouseInputListener.addRunner(() -> processPicking());
	}

	public void processEntity(EntityTutos entities) {
		this.entities.add(entities);
	}

	public void clear() {
		entities.clear();
	}

	private void processPicking() {
		cleanSelected(); //TODO try to put it in clear()

		this.camPos = camera.getPosition();
		this.mouserLoggerPrinter.setCameraPosition(this.camPos);
		this.viewMatrix = Maths.createViewMatrix(camera);
		this.coordSysManager.setViewMatrix(this.viewMatrix);

		filterEntitiesByCameraClip();
		this.mouserLoggerPrinter.printCameraBBox();
		generateBoundingBoxes();
		filterByRayPromixity(ray);

		// rayCasting(ray);
		this.mouserLoggerPrinter.prepareRendering();
	}

	/**
	 * Filtering by viewCoord is okay but request a different bounding box; may pist
	 * to analyse. bounding box with relative rotation to camera is simply to
	 * obtain. Mask intersection : * can calculate a mask that generate a 2D texture
	 * and test where ray intersect on mask. what are the perfs and implem? *
	 * firstly try to generate a plan with proportions linked to global shape of
	 * object. => can be extracted automatically from obj coordinates (min & max x &
	 * y & z) + setters for internal construction * with thoses params no need of a
	 * virtual bounding box, just transform coordinates to projectionMatrix using
	 * camera rotation.
	 * 
	 * @param ray2
	 */
	private void filterByRayPromixity(Vector3f normalizedRay) {
		Vector3f rayFromCamera = getPointOnRay(normalizedRay, 1);
		Vector3f normalizedrayFromMouse = new Vector3f();
		rayFromCamera.normalise(normalizedrayFromMouse);
		Vector3f rayPosNormalizedToCam = Maths.normalizeFromOrigin(rayFromCamera, camPos);

		List<EntityTutos> orderedList = this.entities.stream().sorted((entity1, entity2) -> {
			Vector3f entity1PosNormalizedToCam = Maths.normalizeFromOrigin(entity1.getPositions(), camPos);
			Vector3f delta = Vector3f.sub(rayPosNormalizedToCam, entity1PosNormalizedToCam, null);

			Vector3f entity2PosNormalizedToCam = Maths.normalizeFromOrigin(entity2.getPositions(), camPos);
			Vector3f delta2 = Vector3f.sub(rayPosNormalizedToCam, entity2PosNormalizedToCam, null);

			return delta.length() > delta2.length() ? 1 : delta.length() < delta2.length() ? -1 : 0;

		}).collect(Collectors.toList());
		if (!orderedList.isEmpty()) {
			orderedList.get(0).select();
			// printSelectedBboxIn2D(orderedList.get(0));
		}
		this.mouserLoggerPrinter.printFilterByRayProximity(orderedList, rayPosNormalizedToCam, rayFromCamera);

		List<EntityTutos> filteredList = this.entities.stream().filter(entity -> {
			return false;
		}).collect(Collectors.toList());
		this.entities = filteredList;
	}



	private Vector3f objectToViewCoordNormalized(EntityTutos entity) {
		Vector3f objectPosition = entity.getPositions();
		// makes position as homogeneous vector to allows applying translation
		// transformation given by viewMatrix.
		Vector4f objectPos4f = new Vector4f(objectPosition.x, objectPosition.y, objectPosition.z, 1f);
		Vector4f objectWorld = Matrix4f.transform(viewMatrix, objectPos4f, null);
		Vector3f objectToWorld = new Vector3f(objectWorld.x, objectWorld.y, objectWorld.z);
		objectToWorld.normalise();
		return objectToWorld;
	}

	/**
	 * Filter entities by testing worldPosition vector relative to clipping
	 * environnement. FIXME some entities whose have worldPosition outside clipping
	 * but due to their scaling and shape are rendered will be filtered. A way to
	 * avoid overtesting bounding boxes is to define in Entity a
	 * forceBoundingBoxTest param designed for this method. If not specified,
	 * worldOrigin wil be kept.
	 */
	private void filterEntitiesByCameraClip() {
		List<EntityTutos> filteredList = this.entities.stream().filter(entity -> {
			Vector3f viewCoordEntityPos = coordSysManager.objectToViewCoord(entity.getPositions());
			Vector4f projectedCoordEntity = coordSysManager.objectToProjectionMatrix(viewCoordEntityPos);
			Vector3f clippedVector = coordSysManager.objectToClipSpace(projectedCoordEntity);
			return coordSysManager.isInClipSpace(projectedCoordEntity);
			// projectedCoordEntity.length() <= MasterRenderer.getFarPlane(); //if i want to
			// use getFarPlane i may want to multiply it by cos(fov)
		}).collect(Collectors.toList());
		this.entities = filteredList;
	}

	private void cleanSelected() {
		for (EntityTutos entity : entities) {
			entity.unselect();
		}
		this.mouserLoggerPrinter.clear();
	}

	/**
	 * Apply distance given by distance and orientation from cursorRay to camera
	 * position
	 * 
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
		Float distance = 5f;
		Optional<EntityTutos> selectedEntity = rayMarching(mouseCoord, 0f, distance);
		if (selectedEntity.isPresent()) {
			EntityTutos entity = selectedEntity.get();
			System.out.println(entity.getModel().getClass() + " is selected");
			entity.select();
			// Vector3f objectWorld = objectToWorldCoord(selectedEntity.getPositions());
			System.out.println(entity.getPositions());
		} else {
			System.out.println("nothing selected");
		}
		this.mouserLoggerPrinter.printRay();
	}

	private void generateBoundingBoxes() {
		/**
		 * this.entities.forEach(entity -> { SimpleGeom3D boundingBox = new
		 * SimpleGeom3D(this.loader); Vector3f worldPositionEntity =
		 * entity.getPositions(); boundingBox.addPoint(new
		 * Vector3f(worldPositionEntity.x - BOUNDING_BOX, worldPositionEntity.y -
		 * BOUNDING_BOX, worldPositionEntity.z)); boundingBox.addPoint(new
		 * Vector3f(worldPositionEntity.x - BOUNDING_BOX, worldPositionEntity.y +
		 * BOUNDING_BOX, worldPositionEntity.z)); boundingBox.addPoint(new
		 * Vector3f(worldPositionEntity.x + BOUNDING_BOX, worldPositionEntity.y +
		 * BOUNDING_BOX, worldPositionEntity.z)); boundingBox.addPoint(new
		 * Vector3f(worldPositionEntity.x + BOUNDING_BOX, worldPositionEntity.y -
		 * BOUNDING_BOX, worldPositionEntity.z)); //entity.setBoundingBox(boundingBox);
		 * });
		 **/
		this.mouserLoggerPrinter.printBoundingBoxes(this.entities);
	}

	private Optional<EntityTutos> rayMarching(Vector3f mouseCoord, Float startPointDistance, Float distance) {
		Vector3f beginRay = getPointOnRay(mouseCoord, startPointDistance);
		if (distance > MasterRenderer.getFarPlane()) {
			distance = MasterRenderer.getFarPlane();
			Vector3f endRay = getPointOnRay(mouseCoord, distance);
			this.mouserLoggerPrinter.getRay3D().addPoint(endRay);
			return getMatchingEntities(beginRay, endRay);
		}
		Vector3f endRay = getPointOnRay(mouseCoord, distance);
		this.mouserLoggerPrinter.getRay3D().addPoint(endRay);

		Optional<EntityTutos> matchedEntity = getMatchingEntities(beginRay, endRay);
		if (matchedEntity.isPresent()) {
			return matchedEntity;
		}
		return rayMarching(mouseCoord, distance, distance * 2);
	}

	/**
	 * TODO transform entity position using viewMatrix so we can easily order by
	 * distance using z coordinate. find each Entity between beginRay and endRay
	 * 
	 * @param endRay
	 * @return nearest Entity if any
	 */
	private Optional<EntityTutos> getMatchingEntities(Vector3f beginRay, Vector3f endRay) {
		Map<EntityTutos, Vector3f> entitiesViewPosition = this.entities.stream()
				.collect(Collectors.toMap(Function.identity(), EntityTutos::getPositions));
		TreeMap<Float, EntityTutos> result = entitiesViewPosition.entrySet().stream().filter(entry -> {
			EntityTutos entity = entry.getKey();
			Vector3f worldPositionEntity = entity.getPositions();
			if ((worldPositionEntity.x + MouserLoggerPrinter.BOUNDING_BOX <= endRay.x && worldPositionEntity.x + MouserLoggerPrinter.BOUNDING_BOX >= beginRay.x)
					|| (worldPositionEntity.x - MouserLoggerPrinter.BOUNDING_BOX <= endRay.x
							&& worldPositionEntity.x - MouserLoggerPrinter.BOUNDING_BOX >= beginRay.x)
					|| (worldPositionEntity.y + MouserLoggerPrinter.BOUNDING_BOX <= endRay.y
							&& worldPositionEntity.y + MouserLoggerPrinter.BOUNDING_BOX >= beginRay.y)
					|| (worldPositionEntity.y - MouserLoggerPrinter.BOUNDING_BOX <= endRay.y
							&& worldPositionEntity.y - MouserLoggerPrinter.BOUNDING_BOX >= beginRay.y)
					|| (worldPositionEntity.z + MouserLoggerPrinter.BOUNDING_BOX <= endRay.z
							&& worldPositionEntity.z + MouserLoggerPrinter.BOUNDING_BOX >= beginRay.z)
					|| (worldPositionEntity.z - MouserLoggerPrinter.BOUNDING_BOX <= endRay.z
							&& worldPositionEntity.z - MouserLoggerPrinter.BOUNDING_BOX >= beginRay.z)) {
				return true;
			}
			return false;
		}).collect(Collectors.toMap(entity -> objectToViewCoordNormalized(entity.getKey()).z, Entry::getKey,
				(o1, o2) -> o1, TreeMap::new));
		if (result.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(result.firstEntry().getValue());
	}

	/**
	 * TODO use a start vector and a end vector : to avoid infinite loop if many
	 * entities have a too small range to be isolated
	 * 
	 * @param filteredEntities
	 * @param rayCasting
	 * @return
	 */
	private EntityTutos filterInDistance(List<EntityTutos> filteredEntities, Vector3f minRayCast, Vector3f maxRayCast,
			int iteration, boolean gotResult) {
		// TODO update filtering.
		List<EntityTutos> filteredInZ = filteredEntities.stream().filter(entity -> {
			Vector3f objectWorld = entity.getPositions(); // objectToWorldCoord(
			// System.out.println(entity.getPositions());
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
			if (maxZ > MasterRenderer.getFarPlane()) { // TODO cannot be just a coordinate. as to be a distance
														// calculated by vector.
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
}
