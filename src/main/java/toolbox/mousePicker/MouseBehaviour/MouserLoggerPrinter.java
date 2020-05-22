package toolbox.mousePicker.MouseBehaviour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.EntityTutos;
import modelsLibrary.ISimpleGeom;
import modelsLibrary.SimpleGeom;
import modelsLibrary.SimpleGeom2D;
import modelsLibrary.SimpleGeom3D;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.RenderingParameters;
import toolbox.CoordinatesSystemManager;
import toolbox.Maths;

public class MouserLoggerPrinter {
	private CoordinatesSystemManager coordSysManager;
	private MasterRenderer masterRenderer;
	 //TODO maybe I can hide this too inside an interface
	
	private List<SimpleGeom> geoms;
	private List<SimpleGeom> cameraBboxes; //keep this one to allow cumulation
	private SimpleGeom3D raysWorldOrigin;
	private List<SimpleGeom> debugPoints;
	private SimpleGeom ray3D;
	private Loader loader;
	private Vector3f camPos;
	private static final Vector4f BOUNDING_BOX_COLOR = new Vector4f(0.5f, 1.0f, 0.5f, 1.0f);
	private static final Vector4f BOUNDING_BOX_INSIDE_COLOR = new Vector4f(1.0f, 0.5f, 0.5f, 0.6f);
	private static final Vector4f FRUSTRUM_PLAIN_COLOR = new Vector4f(0.5f, 0.98f, 0.4f, 0.41f);
	private static final Vector4f MASK_COLOR = new Vector4f(0.5f, 0.98f, 0.4f, 0.0f);
	public static final float BOUNDING_BOX = 4f;

	public MouserLoggerPrinter(Loader loader, MasterRenderer masterRenderer, CoordinatesSystemManager coordSysManager) {
		this.geoms = new ArrayList<>();
		this.debugPoints = new ArrayList<>();
		this.cameraBboxes = new ArrayList<>();
		this.loader = loader;
		this.coordSysManager = coordSysManager;
		this.masterRenderer = masterRenderer;
		this.raysWorldOrigin = new SimpleGeom3D(this.loader,this.masterRenderer.get3DRenderer(), "ray");
		this.ray3D = new SimpleGeom3D(this.loader, this.masterRenderer.get3DRenderer(), "RayPoints");
	}

	public ISimpleGeom getRay3D() {
		return this.ray3D;
	}

	public void setCameraPosition(Vector3f camPos) {
		this.camPos = camPos;
	}

	public void clear() {
		for (SimpleGeom geom : geoms) {
			geom.reset();
		}
		geoms.clear();
		this.ray3D.reset();
		
		this.masterRenderer.clearGeom();
	}

	public void prepareRendering() {
		for(SimpleGeom geom : geoms) {
			this.masterRenderer.reloadAndprocess(geom);
		}
		this.masterRenderer.sendForRendering();
	}

	public void printFilterByRayProximity(List<EntityTutos> orderedList, Vector3f rayPosNormalizedToCam,
			Vector3f rayFromCamera) {
		this.raysWorldOrigin.reset();
		RenderingParameters rayParams = raysWorldOrigin.getRenderingParameters();
		rayParams.setRenderMode(GL11.GL_LINES);

		buildRaysByAddingPoints(orderedList);

		// buildRaysByEntity(orderedList,rayParams);

		/**
		 * raysWorldOrigin.addPoint(camPos, new Vector4f(0, 0, 0, 1));
		 * raysWorldOrigin.addPoint(rayPosNormalizedToCam, new Vector4f(0, 0, 0, 1));
		 * 
		 * raysWorldOrigin.addPoint(camPos, new Vector4f(1, 0.6f, 0.5f, 1));
		 * raysWorldOrigin.addPoint(rayFromCamera, new Vector4f(1, 0.6f, 0.5f, 1));
		 **/
		geoms.add(raysWorldOrigin);
	}

	private void buildRaysByEntity(List<EntityTutos> orderedList, RenderingParameters rayParams) {
		Vector4f selectedColor = new Vector4f(0, 0, 0, 1);
		Vector3f originV = new Vector3f(0, 0, 0);
		Vector3f destV = new Vector3f(1, 1, 1);
		// Vector3f viewCoordDestPoint = this.coordSysManager.objectToViewCoord(destV);
		// position of destination influence final rotation
		this.raysWorldOrigin.addPoint(originV, selectedColor);
		this.raysWorldOrigin.addPoint(destV, selectedColor);
		Vector3f xAxis = new Vector3f(1, 0, 0);
		Vector3f yAxis = new Vector3f(0, 1, 0);
		Vector3f zAxis = new Vector3f(0, 0, 1);
		destV.normalise();
		// destV.normalise();
		orderedList.forEach(entity -> {
			Vector3f rayPositionOriginCam = Vector3f.sub(entity.getPositions(), camPos, null);
			// Vector3f rayPositionOriginCam =
			// this.coordSysManager.objectToViewCoord(entity.getPositions());
			rayPositionOriginCam.normalise();

			float objRotx = Vector3f.angle(xAxis, destV);
			float objRoty = Vector3f.angle(yAxis, destV);
			float objRotz = Vector3f.angle(zAxis, destV);
			Vector3f cam = new Vector3f(camPos.x, camPos.y, camPos.z);
			float rotX = Vector3f.angle(xAxis, rayPositionOriginCam);
			float rotY = Vector3f.angle(yAxis, rayPositionOriginCam);
			float rotZ = Vector3f.angle(zAxis, rayPositionOriginCam);

			// try to get 3 angles from normalized rayPositionOriginCam, to complete rotX,
			// rotY, rotZ for entity values.
			// but seems to be meaningless.
			Vector3f entityPosNormalizedToCam = Maths.normalizeFromOrigin(entity.getPositions(), camPos);
			// System.out.println("length by point translation : "+
			// entityPosNormalizedToCam.length());
			// System.out.println("length of dest from camera : "+
			// rayPositionOriginCam.length());

			float length = rayPositionOriginCam.length();
			float scaleRatio = -(length + destV.length()) / (destV.length() - length);
			// System.out.println("ratio size applied :"+ scaleRatio);
			// destV.scale(scaleRatio);
			// System.out.println("scale destV length :"+ destV.length());
			// rayPositionOriginCam.normalise();
			// camPos.x,camPos.y,camPos.z (float)-(Math.toDegrees(Math.acos(destV.x)))
			// rayParams.addEntity(new Vector3f(0,0,0),(float) Math.toDegrees(rotX -
			// objRotx),(float) Math.toDegrees(rotY - objRoty),(float) Math.toDegrees(rotZ -
			// objRotz), 1);//(float) Math.toDegrees(Math.acos(rayPositionOriginCam.x)),
			// (float) Math.toDegrees(Math.acos(rayPositionOriginCam.y)), (float)
			// Math.toDegrees(Math.acos(rayPositionOriginCam.z))
			rayParams.addEntity(new Vector3f(0, 0, 0), 0, 0, 0, 5);
			Vector4f transformedOrigin = applyTransformationMatrix(originV, new Vector3f(0, 0, 0), 0, 0, 0, 5);
			Vector4f transformedDest = applyTransformationMatrix(destV, new Vector3f(0, 0, 0), 0, 0, 0, 5);
			Vector4f finalDest = new Vector4f();
			Vector4f.sub(transformedDest, transformedOrigin, finalDest);
			// System.out.println("length 1 : "+ finalDest.length());
			// length is ok when applied with rot and transl
			rayParams.addEntity(new Vector3f(50, 100, 1000), (float) 90, (float) 90, (float) 90, 5);
			Vector4f transformedOrigin2 = applyTransformationMatrix(originV, new Vector3f(50, 100, 1000), (float) 90,
					(float) 90, (float) 90, 5);
			Vector4f transformedDest2 = applyTransformationMatrix(destV, new Vector3f(50, 100, 1000), 90, 90, 90, 5);
			Vector4f finalDest2 = new Vector4f();
			Vector4f.sub(transformedDest2, transformedOrigin2, finalDest2);
			// System.out.println("length 2 : "+ finalDest2.length());
		});

	}

	private Vector4f applyTransformationMatrix(Vector3f point, Vector3f translation, float rotX, float rotY,
			float rotZ, float scale) {
		Matrix4f transformationM = Maths.createTransformationMatrix(translation, rotX, rotY, rotZ, scale);
		Vector4f pointTransformed = new Vector4f();
		Matrix4f.transform(transformationM, new Vector4f(point.x, point.y, point.z, 1), pointTransformed);
		return pointTransformed;
	}

	private void buildRaysByAddingPoints(List<EntityTutos> orderedList) {
		orderedList.forEach(entity -> {
			Vector4f selectedColor = new Vector4f(0, 0, 0, 1);
			raysWorldOrigin.addPoint(camPos, selectedColor);
			Vector3f entityPosNormalizedToCam = Maths.normalizeFromOrigin(entity.getPositions(), camPos);
			raysWorldOrigin.addPoint(entityPosNormalizedToCam, selectedColor);
			if (!entity.isSelected()) {
				selectedColor = new Vector4f(0.5f, 0.5f, 0.5f, 1);
			} else {
				selectedColor = new Vector4f(0.4f, 0.7f, 0.8f, 1);
			}
			raysWorldOrigin.addPoint(camPos, selectedColor);
			raysWorldOrigin.addPoint(entity.getPositions(), selectedColor);
		});

	}

	/**
	 * print frustrum bbox
	 */
	public void printCameraBBox() {
		for (SimpleGeom point : cameraBboxes) {
			point.reset();
		}
		cameraBboxes.clear();
		/***
		 * Near plane will be [-1,1] plane range Far plane must be far-near
		 */
		float fovRatio = (float) Math.tan(Math.toRadians(MasterRenderer.getFOV() / 2f));
		float xCamNearRatio = MasterRenderer.getNearPlane() * fovRatio;
		float xCamFarRatio = MasterRenderer.getFarPlane() * fovRatio;
		float yCamNearRatio = xCamNearRatio / MasterRenderer.getAspectRatio();
		float yCamFarRatio = xCamFarRatio / MasterRenderer.getAspectRatio();

		Vector3f ltnCam = new Vector3f(-xCamNearRatio, yCamNearRatio, MasterRenderer.getNearPlane());
		Vector3f rtnCam = new Vector3f(xCamNearRatio, yCamNearRatio, MasterRenderer.getNearPlane());
		Vector3f lbnCam = new Vector3f(-xCamNearRatio, -yCamNearRatio, MasterRenderer.getNearPlane());
		Vector3f rbnCam = new Vector3f(xCamNearRatio, -yCamNearRatio, MasterRenderer.getNearPlane());
		Vector3f ltfCam = new Vector3f(-xCamFarRatio, yCamFarRatio, MasterRenderer.getFarPlane());
		Vector3f rtfCam = new Vector3f(xCamFarRatio, yCamFarRatio, MasterRenderer.getFarPlane());
		Vector3f lbfCam = new Vector3f(-xCamFarRatio, -yCamFarRatio, MasterRenderer.getFarPlane());
		Vector3f rbfCam = new Vector3f(xCamFarRatio, -yCamFarRatio, MasterRenderer.getFarPlane());

		Vector3f ltfWorldCoord = Vector3f.add(camPos, this.coordSysManager.viewCoordToWorldCoord(ltfCam), null);
		Vector3f rtfWorldCoord = Vector3f.add(camPos, this.coordSysManager.viewCoordToWorldCoord(rtfCam), null);
		Vector3f lbfWorldCoord = Vector3f.add(camPos, this.coordSysManager.viewCoordToWorldCoord(lbfCam), null);
		Vector3f rbfWorldCoord = Vector3f.add(camPos, this.coordSysManager.viewCoordToWorldCoord(rbfCam), null);
		Vector3f ltnWorldCoord = Vector3f.add(camPos, this.coordSysManager.viewCoordToWorldCoord(ltnCam), null);
		Vector3f rtnWorldCoord = Vector3f.add(camPos, this.coordSysManager.viewCoordToWorldCoord(rtnCam), null);
		Vector3f lbnWorldCoord = Vector3f.add(camPos, this.coordSysManager.viewCoordToWorldCoord(lbnCam), null);
		Vector3f rbnWorldCoord = Vector3f.add(camPos, this.coordSysManager.viewCoordToWorldCoord(rbnCam), null);

		SimpleGeom3D frustrum = getFrustrumForLines(ltfWorldCoord, rtfWorldCoord, lbfWorldCoord, rbfWorldCoord,
				ltnWorldCoord, rtnWorldCoord, lbnWorldCoord, rbnWorldCoord);
		RenderingParameters frustrumParams = frustrum.getRenderingParameters();
		frustrumParams.setAlias("frustrumLines");
		
		SimpleGeom3D frustrumPlain = getFrustrumForPlainTriangles(ltfWorldCoord, rtfWorldCoord, lbfWorldCoord,
				rbfWorldCoord, ltnWorldCoord, rtnWorldCoord, lbnWorldCoord, rbnWorldCoord);
		RenderingParameters frustrumPlainParams = frustrumPlain.getRenderingParameters();
		frustrumPlainParams.setAlias("frustrumPlain");
		
		cameraBboxes.add(frustrum);
		cameraBboxes.add(frustrumPlain);

		frustrumPlainParams.addGlState(GL11.GL_BLEND, true);
		frustrumParams.setRenderMode(GL11.GL_LINES);
		frustrumPlainParams.setRenderMode(GL11.GL_TRIANGLES);
		frustrumPlainParams.renderBefore("bboxEntitiesPlainCategColor");
		frustrumPlain.setColor(MASK_COLOR);

		SimpleGeom3D frustrumPlainInside = frustrumPlain.copy("frustrumPlainInside");
		cameraBboxes.add(frustrumPlainInside);
		frustrumPlainInside.setColor(MASK_COLOR);// );BOUNDING_BOX_INSIDE_COLOR
		RenderingParameters frustrumPlainInsideParams = frustrumPlainInside.getRenderingParameters();
		frustrumPlainInsideParams.renderBefore("frustrumPlain");// frustrumPlain");
		frustrumPlainInsideParams.addGlState(GL11.GL_BLEND, true);
		frustrumPlainInside.invertNormals();
		geoms.addAll(cameraBboxes);
	}

	/**
	 * loading many little objects (lines forming cube) is faster than combining
	 * each lines to one unique geometry. order of 100 000 took like 10sec
	 */
	public void printBoundingBoxes(List<EntityTutos> entities) {
		HashMap<Integer, SimpleGeom3D> generatedGeomLineConfiguration = new HashMap<>();
		HashMap<Integer, SimpleGeom3D> generatedGeomPlainConfiguration= new HashMap<>();
		Vector3f ltn = new Vector3f(-BOUNDING_BOX, BOUNDING_BOX, -BOUNDING_BOX);
		Vector3f rtn = new Vector3f(BOUNDING_BOX, BOUNDING_BOX, -BOUNDING_BOX);
		Vector3f lbn = new Vector3f(-BOUNDING_BOX, -BOUNDING_BOX, -BOUNDING_BOX);
		Vector3f rbn = new Vector3f(BOUNDING_BOX, -BOUNDING_BOX, -BOUNDING_BOX);
		Vector3f ltf = new Vector3f(-BOUNDING_BOX, BOUNDING_BOX, +BOUNDING_BOX);
		Vector3f rtf = new Vector3f(BOUNDING_BOX, BOUNDING_BOX, BOUNDING_BOX);
		Vector3f lbf = new Vector3f(-BOUNDING_BOX, -BOUNDING_BOX, BOUNDING_BOX);
		Vector3f rbf = new Vector3f(BOUNDING_BOX, -BOUNDING_BOX, BOUNDING_BOX);
		List<Vector3f> bboxUniquePoints = Arrays.asList(ltn, rtn, lbn, rbn, ltf, rtf, lbf, rbf);

		SimpleGeom boundingBox = createBboxGeom(ltf, rtf, lbf, rbf, ltn, rtn, lbn, rbn);
		RenderingParameters bboxParam = boundingBox.getRenderingParameters();
		bboxParam.setAlias("bboxEntities");
		bboxParam.setRenderMode(GL11.GL_LINES);
		bboxParam.renderBefore("frustrumPlain");
		//looks like left and right need to be inverted
		SimpleGeom bboxPlain = getFrustrumForPlainTriangles(ltf, rtf, lbf, rbf, ltn, rtn, lbn, rbn);
		RenderingParameters bboxParamPlain = bboxPlain.getRenderingParameters();
		bboxParamPlain.setAlias("bboxEntitiesPlainCategColor");
		bboxParamPlain.setRenderMode(GL11.GL_TRIANGLES);
		bboxParamPlain.addGlState(GL11.GL_BLEND, true);
		
		//bboxPlain.invertNormals();

		Vector4f outsideColor = new Vector4f(0.85f, 0.2f, 0.25f, 1);
		Random random = new Random();
		System.out.println("process over "+ entities.size() +" entities");
		for(EntityTutos entity : entities) {
			Vector3f worldPositionEntity = entity.getPositions();
			int indexPoint = 0;
			int hash = 0;
			int indexHash = 0;
			// detect configuration to apply (mix between inside/outside vertices)
			for (Vector3f point : bboxUniquePoints) {
				Vector3f worldCoordPoint = Vector3f.add(worldPositionEntity, point,null);
				Vector3f viewCoordPoint = this.coordSysManager.objectToViewCoord((Vector3f) worldCoordPoint);
				Vector4f projectedPoint = this.coordSysManager.objectToProjectionMatrix(viewCoordPoint);
				if (!this.coordSysManager.isInClipSpace(projectedPoint)) {
					hash += Math.pow(2, indexHash);
				}
				indexHash++;
			}
			// get or create new configuration and add it an entity
			SimpleGeom3D boundingBoxOutside = generatedGeomLineConfiguration.get(hash);
			if (boundingBoxOutside == null) {
				System.out.println("generate hash : "+ hash);
				boundingBoxOutside = (SimpleGeom3D) boundingBox.copy("bboxEntities");
				generatedGeomLineConfiguration.put(hash, boundingBoxOutside);
				int pointHashIndex = 128;
				int pointIndex = 7;
				while (hash > 0) {
					if (hash >= pointHashIndex) {
						boundingBoxOutside.updateColorByPosition(bboxUniquePoints.get(pointIndex), outsideColor);
						hash -= pointHashIndex;
					}
					pointIndex--;
					pointHashIndex = (int) Math.pow(2, pointIndex);
				}
				geoms.add(boundingBoxOutside);
			}
			// TODO FIXME generating classes over a param affect every Geoms, geoms generation is not good either.
			SimpleGeom3D boundingBoxPlaincateg = generatedGeomPlainConfiguration.get(hash);
			if (boundingBoxPlaincateg == null) {
				boundingBoxPlaincateg = (SimpleGeom3D) bboxPlain.copy("bboxEntitiesPlainCategColor");
				
				generatedGeomPlainConfiguration.put(hash, boundingBoxPlaincateg);
				geoms.add(boundingBoxPlaincateg);
				//TODO debug, rendering params not set.
				RenderingParameters plainBboxParam = boundingBoxPlaincateg.getRenderingParameters();
				//plainBboxParam.renderAfter("bboxEntities");
				boundingBoxPlaincateg.invertNormals();
				float r = random.nextFloat()%100;
				float g = random.nextFloat()%100;
				float b = random.nextFloat()%100;
				plainBboxParam.overrideEachColor(new Vector4f(r, g, b,1));
				System.out.println("colorOverride :"+ hash +" "+ plainBboxParam.getOverridedColors());
			}
			
			RenderingParameters renderParam= boundingBoxOutside.getRenderingParameters();
			renderParam.addEntity(entity.getPositions(), 0f, 0f, 0f, 1);
			RenderingParameters renderParamPlain = boundingBoxPlaincateg.getRenderingParameters();
			renderParamPlain.addEntity(entity.getPositions(), 0f, 0f, 0f, 1);
		}
		
		generatedGeomLineConfiguration.forEach((hash, geom)-> {
			System.out.println(hash +" : "+ geom.getRenderingParameters().getEntities().size() +" "+ geom.getRenderingParameters().getAlias());
		});
	}

	private SimpleGeom createBboxGeom(Vector3f ltf, Vector3f rtf, Vector3f lbf, Vector3f rbf, Vector3f ltn, Vector3f rtn, Vector3f lbn, Vector3f rbn) {
		SimpleGeom3D boundingBox = new SimpleGeom3D(this.loader,this.masterRenderer.get3DRenderer(), "");

		boundingBox.addPoint(new Vector3f(lbf), BOUNDING_BOX_COLOR);
		boundingBox.addPoint(new Vector3f(ltf), BOUNDING_BOX_COLOR);// LEFT-FAR

		boundingBox.addPoint(new Vector3f(ltf), BOUNDING_BOX_COLOR);
		boundingBox.addPoint(new Vector3f(rtf), BOUNDING_BOX_COLOR);// TOP-FAR

		boundingBox.addPoint(new Vector3f(rtf), BOUNDING_BOX_COLOR);
		boundingBox.addPoint(new Vector3f(rbf), BOUNDING_BOX_COLOR);// RIGHT-FAR

		boundingBox.addPoint(new Vector3f(rbf), BOUNDING_BOX_COLOR);
		boundingBox.addPoint(new Vector3f(lbf), BOUNDING_BOX_COLOR);// BOTTOM-FAR

		boundingBox.addPoint(new Vector3f(lbf), BOUNDING_BOX_COLOR);
		boundingBox.addPoint(new Vector3f(lbn), BOUNDING_BOX_COLOR);// LEFT-BOTTOM

		boundingBox.addPoint(new Vector3f(lbn), BOUNDING_BOX_COLOR);
		boundingBox.addPoint(new Vector3f(rbn), BOUNDING_BOX_COLOR);// BOTTOM-NEAR

		boundingBox.addPoint(new Vector3f(rbn), BOUNDING_BOX_COLOR);
		boundingBox.addPoint(new Vector3f(rbf), BOUNDING_BOX_COLOR);// RIGH-BOTTOM

		boundingBox.addPoint(new Vector3f(rbn), BOUNDING_BOX_COLOR);
		boundingBox.addPoint(new Vector3f(rtn), BOUNDING_BOX_COLOR);// RIGHT-NEAR

		boundingBox.addPoint(new Vector3f(rtn), BOUNDING_BOX_COLOR);
		boundingBox.addPoint(new Vector3f(rtf), BOUNDING_BOX_COLOR);// RIGHT-TOP

		boundingBox.addPoint(new Vector3f(rtn), BOUNDING_BOX_COLOR);
		boundingBox.addPoint(new Vector3f(ltn), BOUNDING_BOX_COLOR);// TOP-NEAR

		boundingBox.addPoint(new Vector3f(ltn), BOUNDING_BOX_COLOR);
		boundingBox.addPoint(new Vector3f(ltf), BOUNDING_BOX_COLOR);// LEFT-TOP

		boundingBox.addPoint(new Vector3f(ltn), BOUNDING_BOX_COLOR);
		boundingBox.addPoint(new Vector3f(lbn), BOUNDING_BOX_COLOR);// LEFT-NEAR

		return boundingBox;
	}

	public void printRay() {
		
		RenderingParameters rayParamPoints = this.ray3D.getRenderingParameters();
		rayParamPoints.setRenderMode(GL11.GL_POINTS);
		SimpleGeom3D rayLine = (SimpleGeom3D) this.ray3D.copy("rayLines");
		RenderingParameters rayParamLines = rayLine.getRenderingParameters();
		rayParamLines.setRenderMode(GL11.GL_LINE_STRIP);
		geoms.add(ray3D);
		geoms.add(rayLine);
	}

	/**
	 * Project bbox worldCoordinates points to the rendering plane. Last step
	 * (converting to clipSpace) is equals to the cartesian conversion as w is equal
	 * to -z. Doing again cartesian division aka [ (NearPlane * x)/z (NearPlane
	 * *y)/z] gives a minimap. (NearPlane can be replaced by any constant.)
	 * 
	 * @param entity
	 */
	public void printSelectedBboxIn2D(EntityTutos entity) {
		Vector4f outsideColor = new Vector4f(0.85f, 0.2f, 0.25f, 1);
		Vector4f renderedColor = new Vector4f(0.84f, 0.56f, 0.91f, 1);
		if (entity.getBoundingBox().isEmpty()) {
			return;
		}
		SimpleGeom2D pointToScreenSpace = new SimpleGeom2D(loader, this.masterRenderer.get2DRenderer(), "pointScreenSpace");
		SimpleGeom2D pointToCartesianSpace = new SimpleGeom2D(loader, this.masterRenderer.get2DRenderer(), "pointCartesian");
		SimpleGeom2D nearPlane = new SimpleGeom2D(loader, this.masterRenderer.get2DRenderer(), "pointNear");
		nearPlane.addPoint(new Vector2f(-MasterRenderer.getNearPlane(), -MasterRenderer.getNearPlane()),
				new Vector4f(0.56f, 0.91f, 0.84f, 1));
		nearPlane.addPoint(new Vector2f(MasterRenderer.getNearPlane(), -MasterRenderer.getNearPlane()));
		nearPlane.addPoint(new Vector2f(MasterRenderer.getNearPlane(), MasterRenderer.getNearPlane()));
		nearPlane.addPoint(new Vector2f(-MasterRenderer.getNearPlane(), MasterRenderer.getNearPlane()));
		entity.getBoundingBox().forEach(verticeWorldCoord -> {
			Vector3f vtxViewCoord = this.coordSysManager.objectToViewCoord(verticeWorldCoord);
			Vector4f vtxProjCoord = this.coordSysManager.objectToProjectionMatrix(vtxViewCoord);
			Vector3f vtxScreenSpace = this.coordSysManager.objectToClipSpace(vtxProjCoord);
			if (this.coordSysManager.isInClipSpace(vtxProjCoord)) {
				pointToScreenSpace.addPoint(new Vector2f(vtxScreenSpace.x, vtxScreenSpace.y), renderedColor);
			} else {
				pointToScreenSpace.addPoint(new Vector2f(vtxScreenSpace.x, vtxScreenSpace.y), outsideColor);
			}

			pointToCartesianSpace.addPoint(
					new Vector2f((MasterRenderer.getNearPlane() * vtxScreenSpace.x) / vtxScreenSpace.z,
							(MasterRenderer.getNearPlane() * vtxScreenSpace.y) / vtxScreenSpace.z),
					new Vector4f(0.56f, 0.91f, 0.84f, 1));
		});
		geoms.add(pointToScreenSpace);
		geoms.add(pointToCartesianSpace);
		geoms.add(nearPlane);
		// retry to render on 2D rendered. may compute this by taking care nearLenght
		// while dividing by distance.
		RenderingParameters pointScreenSpaceParams = pointToScreenSpace.getRenderingParameters();
		pointScreenSpaceParams.setRenderMode(GL11.GL_LINE_LOOP);

		RenderingParameters pointCartesiansParams = pointToCartesianSpace.getRenderingParameters();
		pointCartesiansParams.setRenderMode(GL11.GL_LINE_LOOP);

		RenderingParameters pointNearParams = nearPlane.getRenderingParameters();
		pointNearParams.setRenderMode(GL11.GL_LINE_LOOP);
	}

	private SimpleGeom3D getFrustrumForPlainTriangles(Vector3f ltfWorldCoord, Vector3f rtfWorldCoord,
			Vector3f lbfWorldCoord, Vector3f rbfWorldCoord, Vector3f ltnWorldCoord, Vector3f rtnWorldCoord,
			Vector3f lbnWorldCoord, Vector3f rbnWorldCoord) {
		Vector4f cameraTransparency = FRUSTRUM_PLAIN_COLOR;
		SimpleGeom3D frustrum = new SimpleGeom3D(loader,this.masterRenderer.get3DRenderer(), "");
		frustrum.addPoint(ltnWorldCoord, cameraTransparency);
		frustrum.addPoint(lbnWorldCoord, cameraTransparency);
		frustrum.addPoint(rtnWorldCoord, cameraTransparency);// T1 near

		frustrum.addPoint(lbnWorldCoord, cameraTransparency);
		frustrum.addPoint(rbnWorldCoord, cameraTransparency);
		frustrum.addPoint(rtnWorldCoord, cameraTransparency);// T2 near

		frustrum.addPoint(rtnWorldCoord, cameraTransparency);
		frustrum.addPoint(rbnWorldCoord, cameraTransparency);
		frustrum.addPoint(rbfWorldCoord, cameraTransparency);// T3 right

		frustrum.addPoint(rtnWorldCoord, cameraTransparency);
		frustrum.addPoint(rbfWorldCoord, cameraTransparency);
		frustrum.addPoint(rtfWorldCoord, cameraTransparency);// T4 right

		frustrum.addPoint(rtnWorldCoord, cameraTransparency);
		frustrum.addPoint(rtfWorldCoord, cameraTransparency);
		frustrum.addPoint(ltnWorldCoord, cameraTransparency);// T5 top

		frustrum.addPoint(ltnWorldCoord, cameraTransparency);
		frustrum.addPoint(rtfWorldCoord, cameraTransparency);
		frustrum.addPoint(ltfWorldCoord, cameraTransparency);// T6 top

		frustrum.addPoint(lbfWorldCoord, cameraTransparency);
		frustrum.addPoint(ltnWorldCoord, cameraTransparency);
		frustrum.addPoint(ltfWorldCoord, cameraTransparency);// T7 left

		frustrum.addPoint(ltnWorldCoord, cameraTransparency);
		frustrum.addPoint(lbfWorldCoord, cameraTransparency);
		frustrum.addPoint(lbnWorldCoord, cameraTransparency);// T8 left

		frustrum.addPoint(lbfWorldCoord, cameraTransparency);
		frustrum.addPoint(rbnWorldCoord, cameraTransparency);
		frustrum.addPoint(lbnWorldCoord, cameraTransparency);// T9 bottom

		frustrum.addPoint(rbnWorldCoord, cameraTransparency);
		frustrum.addPoint(lbfWorldCoord, cameraTransparency);
		frustrum.addPoint(rbfWorldCoord, cameraTransparency);// T10 bottom

		frustrum.addPoint(lbfWorldCoord, cameraTransparency);
		frustrum.addPoint(rbfWorldCoord, cameraTransparency);
		frustrum.addPoint(ltfWorldCoord, cameraTransparency);// T11 far

		frustrum.addPoint(ltfWorldCoord, cameraTransparency);
		frustrum.addPoint(rbfWorldCoord, cameraTransparency);
		frustrum.addPoint(rtfWorldCoord, cameraTransparency);// T11 far
		return frustrum;
	}

	private SimpleGeom3D getFrustrumForLines(Vector3f ltfWorldCoord, Vector3f rtfWorldCoord, Vector3f lbfWorldCoord,
			Vector3f rbfWorldCoord, Vector3f ltnWorldCoord, Vector3f rtnWorldCoord, Vector3f lbnWorldCoord,
			Vector3f rbnWorldCoord) {
		SimpleGeom3D frustrum = new SimpleGeom3D(loader,this.masterRenderer.get3DRenderer(), "");
		frustrum.addPoint(lbfWorldCoord, frustrum.getDefaultColor());
		frustrum.addPoint(ltfWorldCoord, frustrum.getDefaultColor());

		frustrum.addPoint(ltfWorldCoord, frustrum.getDefaultColor());
		frustrum.addPoint(rtfWorldCoord, frustrum.getDefaultColor());

		frustrum.addPoint(rtfWorldCoord, frustrum.getDefaultColor());
		frustrum.addPoint(rbfWorldCoord, frustrum.getDefaultColor());

		frustrum.addPoint(rbfWorldCoord, frustrum.getDefaultColor());
		frustrum.addPoint(lbfWorldCoord, frustrum.getDefaultColor());

		frustrum.addPoint(lbfWorldCoord, frustrum.getDefaultColor());
		frustrum.addPoint(lbnWorldCoord, BOUNDING_BOX_COLOR);

		frustrum.addPoint(lbnWorldCoord, BOUNDING_BOX_COLOR);
		frustrum.addPoint(rbnWorldCoord, BOUNDING_BOX_COLOR);

		frustrum.addPoint(rbnWorldCoord, BOUNDING_BOX_COLOR);
		frustrum.addPoint(rbfWorldCoord, frustrum.getDefaultColor());

		frustrum.addPoint(rbnWorldCoord, BOUNDING_BOX_COLOR);
		frustrum.addPoint(rtnWorldCoord, BOUNDING_BOX_COLOR);

		frustrum.addPoint(rtnWorldCoord, BOUNDING_BOX_COLOR);
		frustrum.addPoint(rtfWorldCoord, frustrum.getDefaultColor());

		frustrum.addPoint(rtnWorldCoord, BOUNDING_BOX_COLOR);
		frustrum.addPoint(ltnWorldCoord, BOUNDING_BOX_COLOR);

		frustrum.addPoint(ltnWorldCoord, BOUNDING_BOX_COLOR);
		frustrum.addPoint(ltfWorldCoord, frustrum.getDefaultColor());

		frustrum.addPoint(ltnWorldCoord, BOUNDING_BOX_COLOR);
		frustrum.addPoint(lbnWorldCoord, BOUNDING_BOX_COLOR);
		return frustrum;
	}

}
