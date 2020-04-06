package toolbox.mousePicker.MouseBehaviour;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
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
import modelsLibrary.ISimpleGeom;
import modelsLibrary.SimpleGeom;
import modelsLibrary.SimpleGeom2D;
import modelsLibrary.SimpleGeom3D;
import renderEngine.DisplayManager;
import renderEngine.Draw2DRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.RayRenderer;
import toolbox.Maths;
import toolbox.mousePicker.MouseInputListener;

public class MouseLogger implements IMouseBehaviour {
	private List<Entity> entities;
	private Camera camera;
	private RayRenderer rayRenderer;
	private ISimpleGeom ray3D;
	private List<ISimpleGeom> boundingBoxes;
	private List<ISimpleGeom> debugPoints;
	private Draw2DRenderer draw2DRenderer;
	private SimpleGeom3D raysWorldOrigin;
	private Matrix4f viewMatrix;
	private Vector3f camPos;
	private Loader loader;
	private Matrix4f projectionMatrix;
	private MouseInputListener mouseInputListener;
	private Vector3f ray;
	private static final float BOUNDING_BOX = 4f;
	private static final Vector4f BOUNDING_BOX_COLOR = new Vector4f(0.5f,1.0f,0.5f,1.0f);

	public MouseLogger(Camera camera, RayRenderer rayRenderer, Draw2DRenderer draw2DRenderer, Matrix4f projection,
			Loader loader, MouseInputListener mouseInputListener) {
		this.entities = new ArrayList<>();
		this.camera = camera;
		this.rayRenderer = rayRenderer; // TODO I want to render via a DrawGeomRenderer using logic as simple as
										// RayRenderer, but I want to specify what I render (GL_POINTS, TRIANGLES,
										// LINES) dynamically. same logic will fit with others Renderer.
		this.loader = loader;
		this.ray3D = new SimpleGeom3D(this.loader);
		this.raysWorldOrigin = new SimpleGeom3D(loader);
		this.boundingBoxes = new ArrayList<>();
		this.debugPoints = new ArrayList<>();
		this.draw2DRenderer = draw2DRenderer;
		this.projectionMatrix = projection;
		this.mouseInputListener = mouseInputListener;
	}

	@Override
	public void process(Vector3f normalizedRay) {
		this.ray = normalizedRay;
		this.mouseInputListener.addRunner(() -> processPicking());
	}

	private void processPicking() {
		cleanSelected();
		this.ray3D.resetGeom();
		this.raysWorldOrigin.resetGeom();
		for (ISimpleGeom point : debugPoints) {
			point.resetGeom();
		}
		debugPoints.clear();

		this.camPos = camera.getPosition();
		this.viewMatrix = Maths.createViewMatrix(camera);
		filterEntitiesBackwardToCamera();
		generateBoundingBoxes();
		filterByRayPromixity(ray);
		// ray is with world origin.
		//rayCasting(ray);
	}

	private void temp() {
		SimpleGeom ViewCoordPoint = new SimpleGeom2D(this.loader);
		Vector3f bboxmXmY = objectToProjectionMatrix(new Vector3f(-0.9f, -0.9f, 0));
		ViewCoordPoint.addPoint(new Vector2f(bboxmXmY.x, bboxmXmY.y));
		this.draw2DRenderer.reloadAndprocess(ViewCoordPoint, GL11.GL_POINTS);
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
		// move ray from world origin to view origin;
		System.out.println("normalized mouseRay "+ normalizedRay);
		//Vector3f rayFromCamera = getPointOnRay(normalizedRay, 1);
		// a normaliser après conversion en view matrix
		//Vector3f normalizedrayFromMouse = (Vector3f) normalizedRay.normalise();
		
		//Vector3f rayFromMouseToViewCoord = objectToProjectionMatrix(rayFromMouse);//objectToProjectionMatrix(normalizedRay);
		List<Entity> orderedList = this.entities.stream().sorted((entity1, entity2)-> {
			Vector3f normalizedPosition1 = new Vector3f();
			entity1.getPositions().normalise(normalizedPosition1);
			Vector3f delta = Vector3f.sub(normalizedRay, normalizedPosition1, null);
			
			Vector3f normalizedPosition2 = new Vector3f();
			entity2.getPositions().normalise(normalizedPosition2);
			Vector3f delta2 = Vector3f.sub(normalizedRay, normalizedPosition2, null);
			return  delta.length() > delta2.length() ? 1 : delta.length() < delta2.length() ? -1 : 0;
			
		}).collect(Collectors.toList());
		orderedList.get(0).select();
		orderedList.forEach(entity -> {
			Vector4f selectedColor = new Vector4f(0,0,0,1);
			if(entity.isSelected()) {
				selectedColor = new Vector4f(0.4f,0.7f,0.8f,1);
			}
			raysWorldOrigin.addPoint(new Vector3f(0,0,0), selectedColor);
			Vector3f normalizedPosition1 = new Vector3f();
			entity.getPositions().normalise(normalizedPosition1);
			raysWorldOrigin.addPoint(normalizedPosition1, selectedColor);
		});
		raysWorldOrigin.addPoint(new Vector3f(0,0,0), new Vector4f(1,0.6f,0.5f,1));
		raysWorldOrigin.addPoint(normalizedRay, new Vector4f(1,0.6f,0.5f,1));
		this.rayRenderer.reloadAndprocess(raysWorldOrigin, GL11.GL_LINES);
		List<Entity> filteredList = this.entities.stream().filter(entity -> {
			/**if (entity.getBoundingBox().isPresent()) {
				SimpleGeom3D bboxEntity = entity.getBoundingBox().get();
				Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPositions(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());

				float entityZ = entity.getPositions().getZ();
				List<Vector3f> bboxVertices = bboxEntity.getVertices();
				Optional<Vector3f> minXVec = bboxVertices.stream()
						.sorted((vec1, vec2) -> vec1.getX() > vec2.getX() ? 1 : vec1.getX() < vec2.getX() ? -1 : 0)
						.findFirst();
				Optional<Vector3f> maxXVec = bboxVertices.stream()
						.sorted((vec1, vec2) -> vec1.getX() < vec2.getX() ? 1 : vec1.getX() > vec2.getX() ? -1 : 0)
						.findFirst();
				Optional<Vector3f> minYVec = bboxVertices.stream()
						.sorted((vec1, vec2) -> vec1.getY() > vec2.getY() ? 1 : vec1.getY() < vec2.getY() ? -1 : 0)
						.findFirst();
				Optional<Vector3f> maxYVec = bboxVertices.stream()
						.sorted((vec1, vec2) -> vec1.getY() < vec2.getY() ? 1 : vec1.getY() > vec2.getY() ? -1 : 0)
						.findFirst();
				if (minXVec.isPresent() && maxXVec.isPresent() && minYVec.isPresent() && maxYVec.isPresent()) {
					float minX = minXVec.get().x;
					float maxX = maxXVec.get().x;
					float minY = minYVec.get().y;
					float maxY = maxYVec.get().y;
					Vector3f bboxmXmY = objectToProjectionMatrix(new Vector3f(minX, minY, entityZ)); //
					Vector3f bboxMXmY = objectToProjectionMatrix(new Vector3f(maxX, minY, entityZ));
					Vector3f bboxmXMY = objectToProjectionMatrix(new Vector3f(minX, maxY, entityZ));
					Vector3f bboxMXMY = objectToProjectionMatrix(new Vector3f(maxX, maxY, entityZ));
					// TODO FIXME : problem is that y coord is influenced by x position.
					// have to do the test at least in world coords, better way would be to test in
					// in view coords.
					// so find a way to calculate a min/max x and y...
					// once projected x seems correct but y from mouse never match...
					// broke what was available with local bbox

					SimpleGeom ViewCoordPoint = new SimpleGeom2D(this.loader);
					System.out.println(minX + " " + maxX + " " + minY + " " + maxY);
					System.out.println("boundingBox_mXmY");
					System.out.println(bboxmXmY);
					System.out.println("boundingBox_MXmY");
					System.out.println(bboxMXmY);
					System.out.println("boundingBox_mXMY");
					System.out.println(bboxmXMY);
					System.out.println("boundingBox_MXMY");
					System.out.println(bboxMXMY);
					System.out.println("rayFromMouseToViewCoord");
					System.out.println(rayFromMouseToViewCoord);
					ViewCoordPoint.addPoint(new Vector2f(bboxmXmY.x, bboxmXmY.y));
					ViewCoordPoint.addPoint(new Vector2f(bboxMXmY.x, bboxMXmY.y));
					ViewCoordPoint.addPoint(new Vector2f(bboxmXMY.x, bboxmXMY.y));
					ViewCoordPoint.addPoint(new Vector2f(bboxMXMY.x, bboxMXMY.y));
					debugPoints.add(ViewCoordPoint);
					ViewCoordPoint.reloadPositions();

					return (bboxmXmY.x <= rayFromMouseToViewCoord.x && bboxMXmY.x >= rayFromMouseToViewCoord.x)
							&& (bboxMXmY.y <= rayFromMouseToViewCoord.y && bboxmXMY.y >= rayFromMouseToViewCoord.y);
				}
			}**/
			return false;
		}).collect(Collectors.toList());
		this.entities = filteredList;
		for (ISimpleGeom point : debugPoints) {
			this.draw2DRenderer.reloadAndprocess(point, GL11.GL_LINE_LOOP);
		}
	//	this.draw2DRenderer.reloadAndprocess(rayFromMousePoint, GL11.GL_POINTS);
	}

	/**
	 * Don't need perspective division by w (in vec4) because ray have no intrinsic depth
	 * @param vector
	 * @return
	 */
	private Vector3f objectToProjectionMatrix(Vector3f vector) {
		System.out.println("input vector3f "+ vector);
		Vector3f vectorToViewCoord = objectToViewCoord(vector);
		Vector4f eyeCoords = Matrix4f.transform(this.projectionMatrix,
				new Vector4f(vectorToViewCoord.x, vectorToViewCoord.y, vectorToViewCoord.z, -1), null);
		return new Vector3f(eyeCoords.x, eyeCoords.y, eyeCoords.z);
	}

	/**
	 * just to test direct 2d print. not usefull to debug ray ratring
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	private Vector2f getNormalizedDeviceCoords(Vector3f vector) {
		Vector3f projectedCoord = objectToProjectionMatrix(vector);
		float xcoord = projectedCoord.x / 2f * DisplayManager.WIDTH + 1;
		float ycoord = -1 + projectedCoord.y / 2f * DisplayManager.HEIGHT;
		return new Vector2f(xcoord, ycoord);
	}

	private void filterEntitiesBackwardToCamera() {
		List<Entity> filteredList = this.entities.stream().filter(entity -> {
			return objectToViewCoord(entity).z <= 0;
		}).collect(Collectors.toList());
		this.entities = filteredList;
	}

	private void cleanSelected() {
		for (Entity entity : entities) {
			entity.unselect();
		}
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
		Optional<Entity> selectedEntity = rayMarching(mouseCoord, 0f, distance);
		if (selectedEntity.isPresent()) {
			Entity entity = selectedEntity.get();
			System.out.println(entity.getModel().getClass() + " is selected");
			entity.select();
			// Vector3f objectWorld = objectToWorldCoord(selectedEntity.getPositions());
			System.out.println(entity.getPositions());
		} else {
			System.out.println("nothing selected");
		}

		this.rayRenderer.reloadAndprocess(this.ray3D, GL11.GL_POINTS);
		this.rayRenderer.process(this.ray3D, GL11.GL_LINE_STRIP);
	}

	private void generateBoundingBoxes() {
		this.entities.forEach(entity -> {
			SimpleGeom3D boundingBox = new SimpleGeom3D(this.loader);
			Vector3f worldPositionEntity = entity.getPositions();
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z));
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z));
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z));
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z));
			entity.setBoundingBox(boundingBox);
		});
		printBoundingBoxes();
	}

	private void printBoundingBoxes() {
		for (ISimpleGeom boundingBox : boundingBoxes) {
			boundingBox.resetGeom();
		}
		this.boundingBoxes.clear();
		this.entities.forEach(entity -> {
			SimpleGeom boundingBox = new SimpleGeom3D(this.loader);
			Vector3f worldPositionEntity = entity.getPositions();
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX),BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX),BOUNDING_BOX_COLOR);

			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX),BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX),BOUNDING_BOX_COLOR);

			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX),BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX),BOUNDING_BOX_COLOR);

			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX),BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX),BOUNDING_BOX_COLOR);

			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX),BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX),BOUNDING_BOX_COLOR);

			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX),BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX),BOUNDING_BOX_COLOR);

			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX),BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX),BOUNDING_BOX_COLOR);

			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX),BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX),BOUNDING_BOX_COLOR);

			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX),BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX),BOUNDING_BOX_COLOR);

			boundingBox.addPoint(new Vector3f(worldPositionEntity.x + BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX),BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX),BOUNDING_BOX_COLOR);

			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX),BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX),BOUNDING_BOX_COLOR);

			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX),BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(worldPositionEntity.x - BOUNDING_BOX,
					worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX),BOUNDING_BOX_COLOR);
			boundingBoxes.add(boundingBox);

		});
		for (ISimpleGeom bbox : boundingBoxes) {
			this.rayRenderer.reloadAndprocess(bbox, GL11.GL_LINES);
		}
	}

	private Optional<Entity> rayMarching(Vector3f mouseCoord, Float startPointDistance, Float distance) {
		Vector3f beginRay = getPointOnRay(mouseCoord, startPointDistance);
		if (distance > MasterRenderer.getFarPlane()) {
			distance = MasterRenderer.getFarPlane();
			Vector3f endRay = getPointOnRay(mouseCoord, distance);
			this.ray3D.addPoint(endRay);
			return getMatchingEntities(beginRay, endRay);
		}
		Vector3f endRay = getPointOnRay(mouseCoord, distance);
		this.ray3D.addPoint(endRay);
		if (startPointDistance > 0) {
			//System.out.println("beginRay - ViewCoord");
			//System.out.println(objectToViewCoord(beginRay));
		}

		//System.out.println("endRay - ViewCoord");
		//System.out.println(objectToViewCoord(endRay));
		Optional<Entity> matchedEntity = getMatchingEntities(beginRay, endRay);
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
	private Optional<Entity> getMatchingEntities(Vector3f beginRay, Vector3f endRay) {
		Map<Entity, Vector3f> entitiesViewPosition = this.entities.stream()
				.collect(Collectors.toMap(Function.identity(), Entity::getPositions));
		TreeMap<Float, Entity> result = entitiesViewPosition.entrySet().stream().filter(entry -> {
			Entity entity = entry.getKey();
			Vector3f worldPositionEntity = entity.getPositions();
			if ((worldPositionEntity.x + BOUNDING_BOX <= endRay.x && worldPositionEntity.x + BOUNDING_BOX >= beginRay.x)
					|| (worldPositionEntity.x - BOUNDING_BOX <= endRay.x
							&& worldPositionEntity.x - BOUNDING_BOX >= beginRay.x)
					|| (worldPositionEntity.y + BOUNDING_BOX <= endRay.y
							&& worldPositionEntity.y + BOUNDING_BOX >= beginRay.y)
					|| (worldPositionEntity.y - BOUNDING_BOX <= endRay.y
							&& worldPositionEntity.y - BOUNDING_BOX >= beginRay.y)
					|| (worldPositionEntity.z + BOUNDING_BOX <= endRay.z
							&& worldPositionEntity.z + BOUNDING_BOX >= beginRay.z)
					|| (worldPositionEntity.z - BOUNDING_BOX <= endRay.z
							&& worldPositionEntity.z - BOUNDING_BOX >= beginRay.z)) {
				return true;
			}
			return false;
		}).collect(Collectors.toMap(entity -> objectToViewCoord(entity.getKey()).z, Entry::getKey, (o1, o2) -> o1,
				TreeMap::new));
		if (result.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(result.firstEntry().getValue());
	}

	private Vector3f objectToViewCoord(Entity entity) {
		// System.out.println(entity.getModel());
		Vector3f objectPosition = entity.getPositions();
		// System.out.println("worldCoord");
		// System.out.println(objectPosition);
		Vector4f objectPos4f = new Vector4f(objectPosition.x, objectPosition.y, objectPosition.z, 1f);
		Vector4f objectWorld = Matrix4f.transform(viewMatrix, objectPos4f, null);
		Vector3f objectToWorld = new Vector3f(objectWorld.x, objectWorld.y, objectWorld.z);
		objectToWorld.normalise();
		// System.out.println("viewWorld");
		// System.out.println(objectToWorld);
		return objectToWorld;
	}

	private Vector3f objectToViewCoord(Vector3f worldPosition) {
		System.out.println("worldCoord "+ worldPosition);
		Vector4f objectPos4f = new Vector4f(worldPosition.x, worldPosition.y, worldPosition.z, 1f);
		Vector4f objectWorld = Matrix4f.transform(viewMatrix, objectPos4f, null);
		Vector3f objectToWorld = new Vector3f(objectWorld.x, objectWorld.y, objectWorld.z);
		objectToWorld.normalise();
		System.out.println("Eye space viewWorld "+ objectToWorld);
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

	public void clear() {
		entities.clear();
	}

}
