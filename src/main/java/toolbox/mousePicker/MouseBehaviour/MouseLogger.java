package toolbox.mousePicker.MouseBehaviour;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import camera.CameraEntity;
import entities.Entity;
import entities.EntityTutos;
import inputListeners.MouseInputListener;
import models.RenderableGeom;
import models.data.Face;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import toolbox.CoordinatesSystemManager;
import toolbox.Maths;
import utils.IntersectionResult;
import utils.SpatialComparator;

/**
 * see to use gl_clipdistance
 * @author chezmoi
 *
 */
public class MouseLogger implements IMouseBehaviour {
	private Map<RenderableGeom, List<Entity>> entitiesByGeom;
	private CameraEntity camera;
	private MouserLoggerPrinter mouserLoggerPrinter;
	private Matrix4f viewMatrix;
	private Vector3f camPos;
	private CoordinatesSystemManager coordSysManager;
	private MouseInputListener mouseInputListener;
	private Vector3f ray;

	public MouseLogger(CameraEntity camera, MasterRenderer masterRenderer, Loader loader,
			MouseInputListener mouseInputListener) {
		this.entitiesByGeom = new HashMap<>();
		this.camera = camera;
		this.coordSysManager = camera.getCoordinatesSystemManager();
		this.mouseInputListener = mouseInputListener;
		this.mouserLoggerPrinter = new MouserLoggerPrinter(loader, masterRenderer, this.coordSysManager);
	}

	@Override
	public void process(Vector3f normalizedRay) {
		this.ray = normalizedRay;
		this.mouseInputListener.addRunnerOnUniquePress(GLFW_MOUSE_BUTTON_LEFT, () -> processPicking());
	}

	public void processEntity(RenderableGeom geom) {
		this.entitiesByGeom.put(geom, new ArrayList<>(geom.getRenderingParameters().getEntities()));
	}

	public void clear() {
		entitiesByGeom.clear();
	}

	private void processPicking() {
		cleanSelected(); // TODO try to put it in clear()

		this.camPos = camera.getPosition();
		this.mouserLoggerPrinter.setCameraPosition(this.camPos);
		this.viewMatrix = camera.getViewMatrix();

		filterEntitiesByCameraClip();
		generateBoundingBoxes();
		for (List<Entity> entities : this.entitiesByGeom.values()) {
			this.mouserLoggerPrinter.printBoundingBoxes(entities);
		}

		Vector3f rayFromCamera = getPointOnRay(ray, 1);
		Vector3f largeRay = getPointOnRay(ray, 1000);
		for (List<Entity> entities : this.entitiesByGeom.values()) {
			this.mouserLoggerPrinter.printFilterByRayProximity(entities, rayFromCamera, largeRay);
		}
		filterEntitiesByBboxIntersection();
		RenderableGeom bboxPlain = null;
		for (RenderableGeom entity : this.entitiesByGeom.keySet()) {
			if ("bboxEntitiesPlainCategColor".equals(entity.getRenderingParameters().getAlias())) {
				bboxPlain = entity;
			}
		}
		this.mouserLoggerPrinter.drawBboxNormals(bboxPlain, 3);

		this.mouserLoggerPrinter.printCameraBBox();
		List<String> aliases = Arrays.asList("bboxEntities", "bboxEntitiesPlainCategColor");
		for (Entry<RenderableGeom, List<Entity>> entry : this.entitiesByGeom.entrySet()) {
			if (aliases.contains(entry.getKey().getRenderingParameters().getAlias())) {
				this.mouserLoggerPrinter.updateTransparency(entry.getKey(), entry.getValue());
			}
		}
		if (!this.entitiesByGeom.isEmpty()) {
			// TODO if I want to override color from an entity, it means it must process set
			// separation and creation.
			List<String> aliasesToSelect = Arrays.asList("bboxEntitiesPlainCategColor");
			for (Entry<RenderableGeom, List<Entity>> entry : this.entitiesByGeom.entrySet()) {
				if (aliasesToSelect.contains(entry.getKey().getRenderingParameters().getAlias())) {
					this.mouserLoggerPrinter.flemme(entry.getKey(), Arrays.asList(entry.getValue().get(0)));
				}
			}
		}

		filterByRayPromixity(ray);

		// rayCasting(ray);
	}

	private void filterEntitiesByBboxIntersection() {
		Vector3f MouseRayWorldCoord = Vector3f.add(camPos, this.ray, null);
		TreeMap<Float, Entity> filteredByDistanceEntities = new TreeMap<>();
		ArrayList<Entity> filteredEntities = new ArrayList<>();
		for (RenderableGeom geom : this.entitiesByGeom) {

			for (Entity entity : geom.getRenderingParameters().getEntities()) {
				List<Vector3f> bbox = entity.getBoundingBox();
				Vector3f ltnWorld = Vector3f.add(entity.getPositions(), bbox.get(0), null);
				Vector3f rtnWorld = Vector3f.add(entity.getPositions(), bbox.get(1), null);
				Vector3f lbnWorld = Vector3f.add(entity.getPositions(), bbox.get(2), null);
				Vector3f rbnWorld = Vector3f.add(entity.getPositions(), bbox.get(3), null);
				Vector3f ltfWorld = Vector3f.add(entity.getPositions(), bbox.get(4), null);
				Vector3f rtfWorld = Vector3f.add(entity.getPositions(), bbox.get(5), null);
				Vector3f lbfWorld = Vector3f.add(entity.getPositions(), bbox.get(6), null);
				Vector3f rbfWorld = Vector3f.add(entity.getPositions(), bbox.get(7), null);
				List<Face> vectorsAsFaces = Arrays.asList(new Face(ltnWorld, lbnWorld, rbnWorld),
						new Face(ltfWorld, lbfWorld, lbnWorld), new Face(rtnWorld, rbnWorld, rbfWorld),
						new Face(rtfWorld, rbfWorld, lbfWorld), new Face(ltfWorld, ltnWorld, rtnWorld),
						new Face(rbnWorld, rbfWorld, lbfWorld));

				boolean added = false;

				List<IntersectionResult> intersectResult = SpatialComparator.getProjectionOverEntity(camPos, this.ray,
						vectorsAsFaces, new Vector3f(0, 0, 0));
				intersectResult.forEach(result -> {
					this.mouserLoggerPrinter.print3DVectors("3DClipped",
							Arrays.asList(camPos, result.getProjectedPosition()), null, GL11.GL_POINTS);
					this.mouserLoggerPrinter.print3DVectors("3DClippedRef",
							Arrays.asList(result.getFace().p1, result.getProjectedPosition()), new Vector4f(0, 0, 0, 1),
							GL11.GL_LINES);
					if (!added) {
						added = true;
						filteredByDistanceEntities.put(
								Vector3f.sub(result.getFace().p1, result.getProjectedPosition(), null).length(),
								entity);
					}
				});
				// end

				// filteredEntities = oldway(entity, MouseRayWorldCoord, ltnWorld, rtnWorld,
				// lbnWorld, rbnWorld, ltfWorld,
				// rtfWorld, lbfWorld, rbfWorld);
			}

		}
		filteredEntities.addAll(filteredByDistanceEntities.values());
		this.entitiesByGeom = filteredEntities;

	}

	private ArrayList<EntityTutos> oldway(EntityTutos entity, Vector3f MouseRayWorldCoord, Vector3f ltnWorld,
			Vector3f rtnWorld, Vector3f lbnWorld, Vector3f rbnWorld, Vector3f ltfWorld, Vector3f rtfWorld,
			Vector3f lbfWorld, Vector3f rbfWorld) {
		ArrayList<EntityTutos> filteredEntities = new ArrayList<>();
		// TODO try to define a new Vector3f which provides methods to transform point
		// to different coordinates system.
		Vector3f ltnClipped = coordSysManager.objectToClipSpace(
				coordSysManager.objectToProjectionMatrix(coordSysManager.objectToViewCoord(this.viewMatrix, ltnWorld)));
		Vector3f rtnClipped = coordSysManager.objectToClipSpace(
				coordSysManager.objectToProjectionMatrix(coordSysManager.objectToViewCoord(this.viewMatrix, rtnWorld)));
		Vector3f lbnClipped = coordSysManager.objectToClipSpace(
				coordSysManager.objectToProjectionMatrix(coordSysManager.objectToViewCoord(this.viewMatrix, lbnWorld)));
		Vector3f rbnClipped = coordSysManager.objectToClipSpace(
				coordSysManager.objectToProjectionMatrix(coordSysManager.objectToViewCoord(this.viewMatrix, rbnWorld)));
		Vector3f ltfClipped = coordSysManager.objectToClipSpace(
				coordSysManager.objectToProjectionMatrix(coordSysManager.objectToViewCoord(this.viewMatrix, ltfWorld)));
		Vector3f rtfClipped = coordSysManager.objectToClipSpace(
				coordSysManager.objectToProjectionMatrix(coordSysManager.objectToViewCoord(this.viewMatrix, rtfWorld)));
		Vector3f lbfClipped = coordSysManager.objectToClipSpace(
				coordSysManager.objectToProjectionMatrix(coordSysManager.objectToViewCoord(this.viewMatrix, lbfWorld)));
		Vector3f rbfClipped = coordSysManager.objectToClipSpace(
				coordSysManager.objectToProjectionMatrix(coordSysManager.objectToViewCoord(this.viewMatrix, rbfWorld)));
		Vector3f mouseClipped = coordSysManager.objectToClipSpace(coordSysManager
				.objectToProjectionMatrix(coordSysManager.objectToViewCoord(this.viewMatrix, MouseRayWorldCoord)));
		List<Vector3f> clippedCoords = Arrays.asList(ltnClipped, rtnClipped, lbnClipped, rbnClipped, ltfClipped,
				rtfClipped, lbfClipped, rbfClipped, mouseClipped);

		// this.mouserLoggerPrinter.print2DVectors(clippedCoords);
		ArrayList<Vector3f> localCoordinates = new ArrayList<>();
		Vector3f nearPlaneU = Vector3f.sub(ltnClipped, lbnClipped, null);
		Vector3f nearPlaneV = Vector3f.sub(rbnClipped, lbnClipped, null);
		Vector3f nearPlaneM = Vector3f.sub(mouseClipped, lbnClipped, null);
		localCoordinates.add(nearPlaneU);
		localCoordinates.add(nearPlaneV);
		localCoordinates.add(nearPlaneM);

		Vector3f leftPlaneU = Vector3f.sub(ltfClipped, lbfClipped, null);
		Vector3f leftPlaneV = Vector3f.sub(lbnClipped, lbfClipped, null);
		Vector3f leftPlaneM = Vector3f.sub(mouseClipped, lbfClipped, null);
		localCoordinates.add(leftPlaneU);
		localCoordinates.add(leftPlaneV);
		localCoordinates.add(leftPlaneM);

		Vector3f rightPlaneU = Vector3f.sub(rtnClipped, rbnClipped, null);
		Vector3f rightPlaneV = Vector3f.sub(rbfClipped, rbnClipped, null);
		Vector3f rightPlaneM = Vector3f.sub(mouseClipped, rbnClipped, null);
		localCoordinates.add(rightPlaneU);
		localCoordinates.add(rightPlaneV);
		localCoordinates.add(rightPlaneM);

		Vector3f farPlaneU = Vector3f.sub(rtfClipped, rbfClipped, null);
		Vector3f farPlaneV = Vector3f.sub(lbfClipped, rbfClipped, null);
		Vector3f farPlaneM = Vector3f.sub(mouseClipped, rbfClipped, null);
		localCoordinates.add(farPlaneU);
		localCoordinates.add(farPlaneV);
		localCoordinates.add(farPlaneM);

		Vector3f topPlaneU = Vector3f.sub(ltfClipped, ltnClipped, null);
		Vector3f topPlaneV = Vector3f.sub(rtnClipped, ltnClipped, null);
		Vector3f topPlaneM = Vector3f.sub(mouseClipped, ltnClipped, null);
		localCoordinates.add(topPlaneU);
		localCoordinates.add(topPlaneV);
		localCoordinates.add(topPlaneM);

		Vector3f bottomPlaneU = Vector3f.sub(rbnClipped, rbfClipped, null);
		Vector3f bottomPlaneV = Vector3f.sub(lbfClipped, ltnClipped, null);
		Vector3f bottomPlaneM = Vector3f.sub(mouseClipped, ltnClipped, null);
		localCoordinates.add(bottomPlaneU);
		localCoordinates.add(bottomPlaneV);
		localCoordinates.add(bottomPlaneM);
		// System.out.println("testing "+ entity.getModel());

		for (int i = 0; i < localCoordinates.size(); i += 3) {
			Vector3f u = localCoordinates.get(i);
			Vector3f v = localCoordinates.get(i + 1);
			Vector3f mouse = localCoordinates.get(i + 2);
			Vector3f max = Vector3f.add(u, v, null);
			// System.out.println("mouse: "+ mouse +", u: "+ u +", v: "+ v +", max : "+
			// max);

			float absxMouse = Math.abs(mouse.x);
			float absyMouse = Math.abs(mouse.y);
			float absxMax = Math.abs(max.x);
			float absyMax = Math.abs(max.y);
			// System.out.println("absxMouse: "+ absxMouse +", absyMouse: "+ absyMouse +",
			// absxMax: "+ absxMax +", absyMax: "+ absyMax);
			boolean isInsideX = max.x < 0 ? mouse.x <= 0 && mouse.x >= max.x : mouse.x >= 0 && mouse.x <= max.x;
			boolean isInsideY = max.y < 0 ? mouse.y <= 0 && mouse.y >= max.y : mouse.y >= 0 && mouse.y <= max.y;
			// System.out.println("isInsideX "+ isInsideX +", isInsideY "+ isInsideY);
			if (isInsideX && isInsideY) {
				// if(absxMouse <= absxMax && absyMouse <= absyMax) {
				// System.out.println("matched");
				filteredEntities.add(entity);
				break;
			}

		}
		return filteredEntities;
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
		Vector3f rayPosNormalizedToCam = Maths.normalizeFromOrigin(rayFromCamera, camPos); // equals rayFromCamera

		List<EntityTutos> orderedList = this.entitiesByGeom.stream().sorted((entity1, entity2) -> {
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

		List<EntityTutos> filteredList = this.entitiesByGeom.stream().filter(entity -> {
			return false;
		}).collect(Collectors.toList());
		this.entitiesByGeom = filteredList;
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
		List<EntityTutos> filteredList = this.entitiesByGeom.stream().filter(entity -> {
			Vector3f viewCoordEntityPos = coordSysManager.objectToViewCoord(entity.getPositions());
			Vector4f projectedCoordEntity = coordSysManager.objectToProjectionMatrix(viewCoordEntityPos);
			Vector3f clippedVector = coordSysManager.objectToClipSpace(projectedCoordEntity);
			return coordSysManager.isInClipSpace(projectedCoordEntity);
			// projectedCoordEntity.length() <= MasterRenderer.getFarPlane(); //if i want to
			// use getFarPlane i may want to multiply it by cos(fov)
		}).collect(Collectors.toList());
		this.entitiesByGeom = filteredList;
	}

	private void cleanSelected() {
		for (EntityTutos entity : entitiesByGeom) {
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
		for (EntityTutos entity : this.entitiesByGeom) {
			entity.setBoundingBox(this.mouserLoggerPrinter.bboxUniquePoints);
		}

	}

	private Optional<EntityTutos> rayMarching(Vector3f mouseCoord, Float startPointDistance, Float distance) {
		Vector3f beginRay = getPointOnRay(mouseCoord, startPointDistance);
		if (distance > CoordinatesSystemManager.getFarPlane()) {
			distance = CoordinatesSystemManager.getFarPlane();
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
		Map<EntityTutos, Vector3f> entitiesViewPosition = this.entitiesByGeom.stream()
				.collect(Collectors.toMap(Function.identity(), EntityTutos::getPositions));
		TreeMap<Float, EntityTutos> result = entitiesViewPosition.entrySet().stream().filter(entry -> {
			EntityTutos entity = entry.getKey();
			Vector3f worldPositionEntity = entity.getPositions();
			if ((worldPositionEntity.x + MouserLoggerPrinter.BOUNDING_BOX <= endRay.x
					&& worldPositionEntity.x + MouserLoggerPrinter.BOUNDING_BOX >= beginRay.x)
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
			if (maxZ > CoordinatesSystemManager.getFarPlane()) { // TODO cannot be just a coordinate. as to be a
																	// distance
				// calculated by vector.
				maxZ = CoordinatesSystemManager.getFarPlane();
				maxX = (maxRayCast.x * (CoordinatesSystemManager.getFarPlane() / maxRayCast.z));
				maxY = (maxRayCast.y * (CoordinatesSystemManager.getFarPlane() / maxRayCast.z));
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
