package toolbox.mousePicker.MouseBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import modelsLibrary.ISimpleGeom;
import modelsLibrary.SimpleGeom;
import modelsLibrary.SimpleGeom2D;
import modelsLibrary.SimpleGeom3D;
import renderEngine.DisplayManager;
import renderEngine.Draw2DRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.Draw3DRenderer;
import toolbox.Maths;
import toolbox.mousePicker.MouseInputListener;

public class MouseLogger implements IMouseBehaviour {
	private List<Entity> entities;
	private Camera camera;
	private Draw3DRenderer draw3DRenderer;
	private ISimpleGeom ray3D;
	private List<ISimpleGeom> boundingBoxes;
	private List<ISimpleGeom> cameraBboxes;
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

	public MouseLogger(Camera camera, Draw3DRenderer draw3DRenderer, Draw2DRenderer draw2DRenderer, Matrix4f projection,
			Loader loader, MouseInputListener mouseInputListener) {
		this.entities = new ArrayList<>();
		this.camera = camera;
		this.draw3DRenderer = draw3DRenderer;
		this.loader = loader;
		this.ray3D = new SimpleGeom3D(this.loader);
		this.raysWorldOrigin = new SimpleGeom3D(loader);
		this.boundingBoxes = new ArrayList<>();
		this.debugPoints = new ArrayList<>();
		this.cameraBboxes = new ArrayList<>();
		this.draw2DRenderer = draw2DRenderer;
		this.projectionMatrix = projection;
		this.mouseInputListener = mouseInputListener;
	}

	@Override
	public void process(Vector3f normalizedRay) {
		this.ray = normalizedRay;
		this.mouseInputListener.addRunner(() -> processPicking());
	}
	
	public void processEntity(Entity entities) {
		this.entities.add(entities);
	}
	
	public void clear() {
		entities.clear();
	}

	private void processPicking() {
		cleanSelected();
		this.ray3D.resetGeom();
		this.raysWorldOrigin.resetGeom();
		for (ISimpleGeom point : debugPoints) {
			point.resetGeom();
		}
		debugPoints.clear();
		
		for(ISimpleGeom point : cameraBboxes) {
			point.resetGeom();
		}
		cameraBboxes.clear();

		this.camPos = camera.getPosition();
		
		this.viewMatrix = Maths.createViewMatrix(camera);
		
		filterEntitiesByCameraClip();
		printCameraBBox();
		generateBoundingBoxes();
		filterByRayPromixity(ray);
		
		//rayCasting(ray);
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
		Vector3f rayPosNormalizedToCam = normalizeFromOrigin(rayFromCamera,camPos);
	
		List<Entity> orderedList = this.entities.stream().sorted((entity1, entity2)-> {
			Vector3f entity1PosNormalizedToCam = normalizeFromOrigin(entity1.getPositions(),camPos);
			Vector3f delta = Vector3f.sub(rayPosNormalizedToCam, entity1PosNormalizedToCam, null);
			
			Vector3f entity2PosNormalizedToCam = normalizeFromOrigin(entity2.getPositions(), camPos);
			Vector3f delta2 = Vector3f.sub(rayPosNormalizedToCam,entity2PosNormalizedToCam, null);
			
			return  delta.length() > delta2.length() ? 1 : delta.length() < delta2.length() ? -1 : 0;
			
		}).collect(Collectors.toList());
		if(!orderedList.isEmpty()) {
			orderedList.get(0).select();
			//printSelectedBboxIn2D(orderedList.get(0));
		}
		logFilterByRayProximity(orderedList, rayPosNormalizedToCam, rayFromCamera);

		List<Entity> filteredList = this.entities.stream().filter(entity -> {
			return false;
		}).collect(Collectors.toList());
		this.entities = filteredList;
	}

	/**
	 * Project bbox worldCoordinates points to the rendering plane. 
	 * Last step (converting to clipSpace) is equals to the cartesian conversion as w is equal to -z.
	 * Doing again cartesian division aka [ (NearPlane * x)/z (NearPlane *y)/z] gives a minimap. (NearPlane can be replaced by any constant.)
	 * @param entity
	 */
	private void printSelectedBboxIn2D(Entity entity) {
		Vector4f outsideColor = new Vector4f(0.85f,0.2f,0.25f,1);
		Vector4f renderedColor = new Vector4f(0.84f,0.56f,0.91f,1);
		entity.getBoundingBox().ifPresent(bbox -> {
			SimpleGeom2D pointToScreenSpace = new SimpleGeom2D(loader);
			SimpleGeom2D pointToCartesianSpace = new SimpleGeom2D(loader);
			SimpleGeom2D nearPlane = new SimpleGeom2D(loader);
			nearPlane.addPoint(new Vector2f(-MasterRenderer.getNearPlane(),-MasterRenderer.getNearPlane()), new Vector4f(0.56f,0.91f,0.84f,1));
			nearPlane.addPoint(new Vector2f(MasterRenderer.getNearPlane(),-MasterRenderer.getNearPlane()));
			nearPlane.addPoint(new Vector2f(MasterRenderer.getNearPlane(),MasterRenderer.getNearPlane()));
			nearPlane.addPoint(new Vector2f(-MasterRenderer.getNearPlane(),MasterRenderer.getNearPlane()));
			bbox.getVertices().forEach(verticeWorldCoord -> {
				Vector3f vtxViewCoord = objectToViewCoord(verticeWorldCoord);
				Vector4f vtxProjCoord = objectToProjectionMatrix(vtxViewCoord);
				Vector3f vtxScreenSpace = objectToClipSpace(vtxProjCoord); 
				if(isInClipSpace(vtxProjCoord)) {
					pointToScreenSpace.addPoint(new Vector2f(vtxScreenSpace.x,vtxScreenSpace.y),renderedColor);
				}
				else {
					pointToScreenSpace.addPoint(new Vector2f(vtxScreenSpace.x,vtxScreenSpace.y), outsideColor);
				}
				
				
				pointToCartesianSpace.addPoint(new Vector2f((MasterRenderer.getNearPlane() * vtxScreenSpace.x) / vtxScreenSpace.z, (MasterRenderer.getNearPlane() * vtxScreenSpace.y) / vtxScreenSpace.z),
						 new Vector4f(0.56f,0.91f,0.84f,1));
			});
			debugPoints.add(pointToScreenSpace);
			debugPoints.add(pointToCartesianSpace);
			debugPoints.add(nearPlane);
			// retry to render on 2D rendered. may compute this by taking care nearLenght while dividing by distance.
			pointToScreenSpace.addRenderMode(GL11.GL_LINE_LOOP);	
			this.draw2DRenderer.reloadAndprocess(pointToScreenSpace);
				pointToCartesianSpace.addRenderMode(GL11.GL_LINE_LOOP);
				this.draw2DRenderer.reloadAndprocess(pointToCartesianSpace);
				nearPlane.addRenderMode(GL11.GL_LINE_LOOP);
				this.draw2DRenderer.reloadAndprocess(nearPlane);
		});
	}

	/**
	 * Normalize vector from specified origin.
	 * @param vector vector to normalize
	 * @param origin translate vector to origin to override default world origin
	 * @return normalized vector
	 */
	private Vector3f normalizeFromOrigin(Vector3f vector, Vector3f origin) {
		Vector3f rayPositionOriginCam = Vector3f.sub(vector, origin, null);
		rayPositionOriginCam.normalise();
		return Vector3f.add(origin, rayPositionOriginCam, null);
	}

	/**
	 * V'.x = V.x * XScale; (PM[10] = PM[20] = PM[30] = 0)
	 * V'.y = V.y * YScale; (PM[01] = PM[21] = PM[31] = 0)
	 * V'.z = V.z * -;		(PM[02] = PM[12] = 0)
	 * V'.w = -V.z;			(PM[03] = PM[13] = PM[33] = 0)
	 * @param vector3f Homogeneous vector point (V[w]=1)
	 * @return [vector] * [projectionMatrix] (Row major column) 
	 */
	private Vector4f objectToProjectionMatrix(Vector3f vector) {

		Vector4f projectionCoords = Matrix4f.transform(this.projectionMatrix,
				new Vector4f(vector.x,vector.y,vector.z,1), null);
		System.out.println("ProjectionCoords "+ projectionCoords);
		return projectionCoords;
	}
	
	/**
	 * @param projectionCoords
	 * @return new Vector3f(x/w,y/w,z/w)
	 */
	private Vector3f objectToClipSpace(Vector4f projectionCoords) {
		return new Vector3f(projectionCoords.x/projectionCoords.w,projectionCoords.y/projectionCoords.w,projectionCoords.z/projectionCoords.w);
	}
	/**
	 * ViewMatrix as w[33] set as constant 1.
	 * Need a 1*4 Vector to apply translation of ViewMatrix.
	 * V[w] = 1 to detect entity that are backward of camera
	 * @param worldPosition
	 * @return [x,y,z] rotated * translated homogeneous vector point
	 */
	private Vector3f objectToViewCoord(Vector3f worldPosition) {
		System.out.println("worldCoord "+ worldPosition);
		Vector4f objectPos4f = new Vector4f(worldPosition.x, worldPosition.y, worldPosition.z, 1f);
		Vector4f objectWorld = Matrix4f.transform(viewMatrix, objectPos4f, null);
		return new Vector3f(objectWorld.x,objectWorld.y,objectWorld.z);
	}
	
	/**
	 * @param eyeCoords
	 * @return
	 */
	private Vector3f viewCoordToWorldCoord(Vector3f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, new Vector4f(eyeCoords.x,eyeCoords.y,-eyeCoords.z,0), null);
		return new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
	}
	
	private Vector3f objectToViewCoordNormalized(Entity entity) {
		Vector3f objectPosition = entity.getPositions();
		// makes position as homogeneous vector to allows applying translation transformation given by viewMatrix.
		Vector4f objectPos4f = new Vector4f(objectPosition.x, objectPosition.y, objectPosition.z, 1f);
		Vector4f objectWorld = Matrix4f.transform(viewMatrix, objectPos4f, null);
		Vector3f objectToWorld = new Vector3f(objectWorld.x, objectWorld.y, objectWorld.z);
		objectToWorld.normalise();
		return objectToWorld;
	}

	/**
	 * Filter entities by testing worldPosition vector relative to clipping environnement.
	 * FIXME some entities whose have worldPosition outside clipping but due to their scaling and shape are rendered will be filtered.
	 * A way to avoid overtesting bounding boxes is to define in Entity a forceBoundingBoxTest param designed for this method.
	 * If not specified, worldOrigin wil be kept.
	 */
	private void filterEntitiesByCameraClip() {
		List<Entity> filteredList = this.entities.stream().filter(entity -> {
			Vector3f viewCoordEntityPos = objectToViewCoord(entity.getPositions());
			Vector4f projectedCoordEntity = objectToProjectionMatrix(viewCoordEntityPos);
			Vector3f clippedVector = objectToClipSpace(projectedCoordEntity);
			return isInClipSpace(projectedCoordEntity);
				// projectedCoordEntity.length() <= MasterRenderer.getFarPlane(); //if i want to use getFarPlane i may want to multiply it by cos(fov)
		}).collect(Collectors.toList());
		this.entities = filteredList;
	}
	
	/**
	 * We could have used a normalized Vector3f, but we can avoid a division.
	 * Instead we can just test if each coordinates are bounded into [-w;w] 
	 * @param position
	 * @return
	 */
	private boolean isInClipSpace(Vector4f position) {
		return position.x >= -position.w && position.x <= position.w && position.y >= -position.w && position.y <= position.w && 
				position.z >= -position.w && position.z <= position.w; // z clipped seems to be twice as it should be.
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
		this.ray3D.addRenderMode(GL11.GL_POINTS);
		this.ray3D.addRenderMode(GL11.GL_LINE_STRIP);
		this.draw3DRenderer.reloadAndprocess(this.ray3D);
	}

	private void generateBoundingBoxes() {
	/**	this.entities.forEach(entity -> {
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
			//entity.setBoundingBox(boundingBox);
		});**/
		printBoundingBoxes();
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
		}).collect(Collectors.toMap(entity -> objectToViewCoordNormalized(entity.getKey()).z, Entry::getKey, (o1, o2) -> o1,
				TreeMap::new));
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

	/********** log methods **********/
	private void logFilterByRayProximity(List<Entity> orderedList, Vector3f rayPosNormalizedToCam, Vector3f rayFromCamera) {
		orderedList.forEach(entity -> {
			Vector4f selectedColor = new Vector4f(0,0,0,1);
			raysWorldOrigin.addPoint(camPos, selectedColor);
			Vector3f entityPosNormalizedToCam = normalizeFromOrigin(entity.getPositions(), camPos);
			raysWorldOrigin.addPoint(entityPosNormalizedToCam, selectedColor);
			if(!entity.isSelected()) {
				selectedColor = new Vector4f(0.5f,0.5f,0.5f,1);
			}
			else {
				selectedColor = new Vector4f(0.4f,0.7f,0.8f,1);
			}
			raysWorldOrigin.addPoint(camPos, selectedColor);
			raysWorldOrigin.addPoint(entity.getPositions(), selectedColor);
		});

		raysWorldOrigin.addPoint(camPos, new Vector4f(0,0,0,1));
		raysWorldOrigin.addPoint(rayPosNormalizedToCam, new Vector4f(0,0,0,1));

		raysWorldOrigin.addPoint(camPos, new Vector4f(1,0.6f,0.5f,1));
		raysWorldOrigin.addPoint(rayFromCamera, new Vector4f(1,0.6f,0.5f,1));
		raysWorldOrigin.addRenderMode(GL11.GL_LINES);
		this.draw3DRenderer.reloadAndprocess(raysWorldOrigin);
	}
	
	/**
	 * print frustrum bbox
	 */
	private void printCameraBBox() {
		/***
		 * Near plane will be [-1,1] plane range
		 * Far plane must be far-near
		 */
		float fovRatio = (float) Math.tan(Math.toRadians(MasterRenderer.getFOV()/ 2f)) * MasterRenderer.getAspectRatio();
		float xCamNearRatio =  MasterRenderer.getNearPlane() ;
		float xCamFarRatio = MasterRenderer.getFarPlane() / fovRatio;
		float yCamNearRatio = xCamNearRatio / MasterRenderer.getAspectRatio();
		float yCamFarRatio = xCamFarRatio / MasterRenderer.getAspectRatio();
		
		Vector3f ltnCam = new Vector3f(-xCamNearRatio,yCamNearRatio,MasterRenderer.getNearPlane());
		Vector3f rtnCam = new Vector3f(xCamNearRatio,yCamNearRatio,MasterRenderer.getNearPlane());
		Vector3f lbnCam = new Vector3f(-xCamNearRatio,-yCamNearRatio,MasterRenderer.getNearPlane());
		Vector3f rbnCam = new Vector3f(xCamNearRatio,-yCamNearRatio,MasterRenderer.getNearPlane());
		Vector3f ltfCam = new Vector3f(-xCamFarRatio,yCamFarRatio,MasterRenderer.getFarPlane());
		Vector3f rtfCam = new Vector3f(xCamFarRatio,yCamFarRatio,MasterRenderer.getFarPlane());
		Vector3f lbfCam = new Vector3f(-xCamFarRatio,-yCamFarRatio,MasterRenderer.getFarPlane());
		Vector3f rbfCam = new Vector3f(xCamFarRatio,-yCamFarRatio,MasterRenderer.getFarPlane());
		

		Vector3f ltfWorldCoord = Vector3f.add( camPos,viewCoordToWorldCoord(ltfCam),null);
		Vector3f rtfWorldCoord = Vector3f.add( camPos,viewCoordToWorldCoord(rtfCam),null);
		Vector3f lbfWorldCoord = Vector3f.add( camPos,viewCoordToWorldCoord(lbfCam),null);
		Vector3f rbfWorldCoord = Vector3f.add( camPos,viewCoordToWorldCoord(rbfCam),null);
		Vector3f ltnWorldCoord = Vector3f.add( camPos,viewCoordToWorldCoord(ltnCam),null);
		Vector3f rtnWorldCoord = Vector3f.add( camPos,viewCoordToWorldCoord(rtnCam),null);
		Vector3f lbnWorldCoord = Vector3f.add( camPos,viewCoordToWorldCoord(lbnCam),null);
		Vector3f rbnWorldCoord = Vector3f.add( camPos,viewCoordToWorldCoord(rbnCam),null);

		SimpleGeom3D frustrum = getFrustrumForLines(ltfWorldCoord,rtfWorldCoord,lbfWorldCoord,rbfWorldCoord,ltnWorldCoord,rtnWorldCoord,lbnWorldCoord,rbnWorldCoord);
		SimpleGeom3D frustrumPlain = getFrustrumForPlainTriangles(ltfWorldCoord,rtfWorldCoord,lbfWorldCoord,rbfWorldCoord,ltnWorldCoord,rtnWorldCoord,lbnWorldCoord,rbnWorldCoord);
		cameraBboxes.add(frustrum);
		frustrumPlain.addGlState(GL11.GL_BLEND,true);
		frustrum.addRenderMode(GL11.GL_LINES);
		frustrumPlain.addRenderMode(GL11.GL_TRIANGLES);
		this.draw3DRenderer.reloadAndprocess(frustrum);
		this.draw3DRenderer.reloadAndprocess(frustrumPlain,100);
	}
	
	private SimpleGeom3D getFrustrumForPlainTriangles(Vector3f ltfWorldCoord, Vector3f rtfWorldCoord,
			Vector3f lbfWorldCoord, Vector3f rbfWorldCoord, Vector3f ltnWorldCoord, Vector3f rtnWorldCoord,
			Vector3f lbnWorldCoord, Vector3f rbnWorldCoord) {
		Vector4f cameraTransparency = new Vector4f(0.5f,0.98f,0.4f,0.41f);
		SimpleGeom3D frustrum = new SimpleGeom3D(loader);
		frustrum.addPoint(ltnWorldCoord,cameraTransparency);
		frustrum.addPoint(lbnWorldCoord,cameraTransparency);
		frustrum.addPoint(rtnWorldCoord,cameraTransparency);//T1 near
		
		frustrum.addPoint(lbnWorldCoord,cameraTransparency);
		frustrum.addPoint(rbnWorldCoord,cameraTransparency);
		frustrum.addPoint(rtnWorldCoord,cameraTransparency);//T2 near
		
		frustrum.addPoint(rtnWorldCoord,cameraTransparency);
		frustrum.addPoint(rbnWorldCoord,cameraTransparency);
		frustrum.addPoint(rbfWorldCoord,cameraTransparency);//T3 right
		
		frustrum.addPoint(rtnWorldCoord,cameraTransparency);
		frustrum.addPoint(rbfWorldCoord,cameraTransparency);
		frustrum.addPoint(rtfWorldCoord,cameraTransparency);//T4 right
		
		frustrum.addPoint(rtnWorldCoord,cameraTransparency);
		frustrum.addPoint(rtfWorldCoord,cameraTransparency);
		frustrum.addPoint(ltnWorldCoord,cameraTransparency);//T5 top
		
		frustrum.addPoint(ltnWorldCoord,cameraTransparency);
		frustrum.addPoint(rtfWorldCoord,cameraTransparency);
		frustrum.addPoint(ltfWorldCoord,cameraTransparency);//T6 top
		
		frustrum.addPoint(lbfWorldCoord,cameraTransparency);
		frustrum.addPoint(ltnWorldCoord,cameraTransparency);
		frustrum.addPoint(ltfWorldCoord,cameraTransparency);//T7 left
		
		frustrum.addPoint(ltnWorldCoord,cameraTransparency);
		frustrum.addPoint(lbfWorldCoord,cameraTransparency);
		frustrum.addPoint(lbnWorldCoord,cameraTransparency);//T8 left
		
		frustrum.addPoint(lbfWorldCoord,cameraTransparency);
		frustrum.addPoint(rbnWorldCoord,cameraTransparency);
		frustrum.addPoint(lbnWorldCoord,cameraTransparency);//T9 bottom
		
		frustrum.addPoint(rbnWorldCoord,cameraTransparency);
		frustrum.addPoint(lbfWorldCoord,cameraTransparency);
		frustrum.addPoint(rbfWorldCoord,cameraTransparency);//T10 bottom
		
		frustrum.addPoint(lbfWorldCoord,cameraTransparency);
		frustrum.addPoint(rbfWorldCoord,cameraTransparency);
		frustrum.addPoint(ltfWorldCoord,cameraTransparency);//T11 far
		
		frustrum.addPoint(ltfWorldCoord,cameraTransparency);
		frustrum.addPoint(rbfWorldCoord,cameraTransparency);
		frustrum.addPoint(rtfWorldCoord,cameraTransparency);//T11 far
		return frustrum;
	}

	private SimpleGeom3D getFrustrumForLines(Vector3f ltfWorldCoord, Vector3f rtfWorldCoord, Vector3f lbfWorldCoord, Vector3f rbfWorldCoord, Vector3f ltnWorldCoord, Vector3f rtnWorldCoord, Vector3f lbnWorldCoord, Vector3f rbnWorldCoord) {
		SimpleGeom3D frustrum = new SimpleGeom3D(loader);
		frustrum.addPoint(lbfWorldCoord,frustrum.getDefaultColor());
		frustrum.addPoint(ltfWorldCoord,frustrum.getDefaultColor());
		
		frustrum.addPoint(ltfWorldCoord,frustrum.getDefaultColor());
		frustrum.addPoint(rtfWorldCoord,frustrum.getDefaultColor());

		frustrum.addPoint(rtfWorldCoord,frustrum.getDefaultColor());
		frustrum.addPoint(rbfWorldCoord,frustrum.getDefaultColor());

		frustrum.addPoint(rbfWorldCoord,frustrum.getDefaultColor());
		frustrum.addPoint(lbfWorldCoord,frustrum.getDefaultColor());

		frustrum.addPoint(lbfWorldCoord,frustrum.getDefaultColor());
		frustrum.addPoint(lbnWorldCoord,BOUNDING_BOX_COLOR);

		frustrum.addPoint(lbnWorldCoord,BOUNDING_BOX_COLOR);
		frustrum.addPoint(rbnWorldCoord,BOUNDING_BOX_COLOR);

		frustrum.addPoint(rbnWorldCoord,BOUNDING_BOX_COLOR);
		frustrum.addPoint(rbfWorldCoord,frustrum.getDefaultColor());

		frustrum.addPoint(rbnWorldCoord,BOUNDING_BOX_COLOR);
		frustrum.addPoint(rtnWorldCoord,BOUNDING_BOX_COLOR);

		frustrum.addPoint(rtnWorldCoord,BOUNDING_BOX_COLOR);
		frustrum.addPoint(rtfWorldCoord,frustrum.getDefaultColor());

		frustrum.addPoint(rtnWorldCoord,BOUNDING_BOX_COLOR);
		frustrum.addPoint(ltnWorldCoord,BOUNDING_BOX_COLOR);

		frustrum.addPoint(ltnWorldCoord,BOUNDING_BOX_COLOR);
		frustrum.addPoint(ltfWorldCoord,frustrum.getDefaultColor());

		frustrum.addPoint(ltnWorldCoord,BOUNDING_BOX_COLOR);
		frustrum.addPoint(lbnWorldCoord,BOUNDING_BOX_COLOR);
		return frustrum;
	}

	private void printBoundingBoxes() {
		for (ISimpleGeom boundingBox : boundingBoxes) {
			boundingBox.resetGeom();
		}
		this.boundingBoxes.clear();
		Vector4f outsideColor = new Vector4f(0.85f,0.2f,0.25f,1);
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
			
			int indexPoint= 0;
			for(Vector point : boundingBox.getVertices()){
				Vector3f viewCoordPoint = objectToViewCoord((Vector3f) point);
				Vector4f projectedPoint = objectToProjectionMatrix(viewCoordPoint);
				if(!isInClipSpace(projectedPoint)) {
					boundingBox.updateColor(indexPoint, outsideColor);
				}
				indexPoint+=4;
			}
			boundingBoxes.add(boundingBox);
			entity.setBoundingBox((SimpleGeom3D)boundingBox);

		});
		for (ISimpleGeom bbox : boundingBoxes) {
			bbox.addRenderMode(GL11.GL_LINES);
			this.draw3DRenderer.reloadAndprocess(bbox);
		}
	}
}
