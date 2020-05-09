package toolbox.mousePicker.MouseBehaviour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.Entity;
import modelsLibrary.ISimpleGeom;
import modelsLibrary.SimpleGeom;
import modelsLibrary.SimpleGeom2D;
import modelsLibrary.SimpleGeom3D;
import renderEngine.Draw2DRenderer;
import renderEngine.Draw3DRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.RenderingParameters;
import toolbox.CoordinatesSystemManager;
import toolbox.Maths;

public class MouserLoggerPrinter {
	private CoordinatesSystemManager coordSysManager;
	private Draw3DRenderer draw3DRenderer;
	private Draw2DRenderer draw2DRenderer;
	private List<SimpleGeom> boundingBoxes;
	private List<SimpleGeom> cameraBboxes;
	private SimpleGeom3D raysWorldOrigin;
	private List<SimpleGeom> debugPoints;
	private SimpleGeom ray3D;
	private Loader loader;
	private Vector3f camPos;
	private static final Vector4f BOUNDING_BOX_COLOR = new Vector4f(0.5f, 1.0f, 0.5f, 1.0f);
	public static final float BOUNDING_BOX = 4f;

	public MouserLoggerPrinter(Loader loader, Draw3DRenderer draw3DRenderer, Draw2DRenderer draw2DRenderer, CoordinatesSystemManager coordSysManager) {
		this.boundingBoxes = new ArrayList<>();
		this.debugPoints = new ArrayList<>();
		this.cameraBboxes = new ArrayList<>();
		this.loader = loader;
		this.draw3DRenderer = draw3DRenderer;
		this.draw2DRenderer = draw2DRenderer;
		this.coordSysManager = coordSysManager;
		this.raysWorldOrigin = new SimpleGeom3D(this.loader);
		this.ray3D = new SimpleGeom3D(this.loader);
	}
	
	public ISimpleGeom getRay3D() {
		return this.ray3D;
	}
	
	public void setCameraPosition(Vector3f camPos) {
		this.camPos = camPos;
	}
	
	public void clear() {
		draw2DRenderer.clearGeom();
		draw3DRenderer.clearGeom();
	}
	
	public void prepareRendering() {
		this.draw2DRenderer.sendForRendering();
		this.draw3DRenderer.sendForRendering();
	}
	
	public void printFilterByRayProximity(List<Entity> orderedList, Vector3f rayPosNormalizedToCam,
			Vector3f rayFromCamera) {
		this.raysWorldOrigin.resetGeom();
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

		raysWorldOrigin.addPoint(camPos, new Vector4f(0, 0, 0, 1));
		raysWorldOrigin.addPoint(rayPosNormalizedToCam, new Vector4f(0, 0, 0, 1));

		raysWorldOrigin.addPoint(camPos, new Vector4f(1, 0.6f, 0.5f, 1));
		raysWorldOrigin.addPoint(rayFromCamera, new Vector4f(1, 0.6f, 0.5f, 1));
		int indexRayParam = raysWorldOrigin.createRenderingPamater();
		RenderingParameters rayParams = raysWorldOrigin.getRenderingParameters().get(indexRayParam);
		rayParams.setRenderMode(GL11.GL_LINES);
		rayParams.setAlias("ray");
		this.draw3DRenderer.reloadAndprocess(raysWorldOrigin);
	}

	/**
	 * print frustrum bbox
	 */
	public void printCameraBBox() {
		for (SimpleGeom point : cameraBboxes) {
			point.resetGeom();
		}
		cameraBboxes.clear();
		/***
		 * Near plane will be [-1,1] plane range Far plane must be far-near
		 */
		float fovRatio = (float) Math.tan(Math.toRadians(MasterRenderer.getFOV() / 2f))
				* MasterRenderer.getAspectRatio();
		float xCamNearRatio = MasterRenderer.getNearPlane();
		float xCamFarRatio = MasterRenderer.getFarPlane() / fovRatio;
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
		SimpleGeom3D frustrumPlain = getFrustrumForPlainTriangles(ltfWorldCoord, rtfWorldCoord, lbfWorldCoord,
				rbfWorldCoord, ltnWorldCoord, rtnWorldCoord, lbnWorldCoord, rbnWorldCoord);
		cameraBboxes.add(frustrum);
		cameraBboxes.add(frustrumPlain);
		int indexPlainParam = frustrumPlain.createRenderingPamater();
		int indexFrustrumParam = frustrum.createRenderingPamater();
		RenderingParameters frustrumPlainParams = frustrumPlain.getRenderingParameters().get(indexPlainParam);
		RenderingParameters frustrumParams = frustrum.getRenderingParameters().get(indexFrustrumParam);

		frustrumPlainParams.addGlState(GL11.GL_BLEND, true);
		frustrumPlainParams.setAlias("frustrumPlain");
		frustrumParams.setRenderMode(GL11.GL_LINES);
		frustrumParams.setAlias("frustrumLines");
		frustrumPlainParams.setRenderMode(GL11.GL_TRIANGLES);
		frustrumPlainParams.renderBefore("bboxEntities"); 
		frustrumPlain.invertNormals();
		for(SimpleGeom cameraFrustrum : cameraBboxes) {
			this.draw3DRenderer.reloadAndprocess(cameraFrustrum);
		}
	}


	/**
	 * loading many little objects (lines forming cube) is faster than combining each lines to one unique geometry.
	 * order of 100 000 took like 10sec
	 */
	public void printBoundingBoxes(List<Entity> entities) {
		for (SimpleGeom boundingBox : boundingBoxes) {
			boundingBox.resetGeom();
		}
		this.boundingBoxes.clear();
		SimpleGeom boundingBox = new SimpleGeom3D(this.loader);
		Vector4f outsideColor = new Vector4f(0.85f, 0.2f, 0.25f, 1);
		entities.forEach(entity -> {
			Vector3f worldPositionEntity = entity.getPositions();
			
			Vector3f ltn = new Vector3f(worldPositionEntity.x - BOUNDING_BOX,worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX);
			Vector3f rtn = new Vector3f(worldPositionEntity.x + BOUNDING_BOX,worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX);
			Vector3f lbn = new Vector3f(worldPositionEntity.x - BOUNDING_BOX,worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX);
			Vector3f rbn = new Vector3f(worldPositionEntity.x + BOUNDING_BOX,worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z - BOUNDING_BOX);
			Vector3f ltf = new Vector3f(worldPositionEntity.x - BOUNDING_BOX,worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX);
			Vector3f rtf = new Vector3f(worldPositionEntity.x + BOUNDING_BOX,worldPositionEntity.y + BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX);
			Vector3f lbf = new Vector3f(worldPositionEntity.x - BOUNDING_BOX,worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX);
			Vector3f rbf = new Vector3f(worldPositionEntity.x + BOUNDING_BOX,worldPositionEntity.y - BOUNDING_BOX, worldPositionEntity.z + BOUNDING_BOX);
			List<Vector3f> bbox = Arrays.asList(ltn,rtn,lbn,rbn,ltf,rtf,lbf,rbf);
			boundingBox.addPoint(new Vector3f(lbf), BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(ltf), BOUNDING_BOX_COLOR);// LEFT-FAR

			boundingBox.addPoint(new Vector3f(ltf), BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(rtf), BOUNDING_BOX_COLOR);//TOP-FAR

			boundingBox.addPoint(new Vector3f(rtf), BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(rbf), BOUNDING_BOX_COLOR);//RIGHT-FAR

			boundingBox.addPoint(new Vector3f(rbf), BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(lbf), BOUNDING_BOX_COLOR);//BOTTOM-FAR

			boundingBox.addPoint(new Vector3f(lbf), BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(lbn), BOUNDING_BOX_COLOR);//LEFT-BOTTOM

			boundingBox.addPoint(new Vector3f(lbn), BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(rbn), BOUNDING_BOX_COLOR);//BOTTOM-NEAR

			boundingBox.addPoint(new Vector3f(rbn), BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(rbf), BOUNDING_BOX_COLOR);//RIGH-BOTTOM

			boundingBox.addPoint(new Vector3f(rbn), BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(rtn), BOUNDING_BOX_COLOR);//RIGHT-NEAR

			boundingBox.addPoint(new Vector3f(rtn), BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(rtf), BOUNDING_BOX_COLOR);//RIGHT-TOP

			boundingBox.addPoint(new Vector3f(rtn), BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(ltn), BOUNDING_BOX_COLOR);//TOP-NEAR

			boundingBox.addPoint(new Vector3f(ltn), BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(ltf), BOUNDING_BOX_COLOR);//LEFT-TOP

			boundingBox.addPoint(new Vector3f(ltn), BOUNDING_BOX_COLOR);
			boundingBox.addPoint(new Vector3f(lbn), BOUNDING_BOX_COLOR);//LEFT-NEAR


			int indexPoint = 0;
			for (Vector3f point : bbox) {
				Vector3f viewCoordPoint = this.coordSysManager.objectToViewCoord(point);
				Vector4f projectedPoint = this.coordSysManager.objectToProjectionMatrix(viewCoordPoint);
				if (!this.coordSysManager.isInClipSpace(projectedPoint)) {
					boundingBox.updateColor(indexPoint, outsideColor);
				}
				indexPoint += 4;
			}
			entity.setBoundingBox(bbox);
		});
		boundingBoxes.add(boundingBox);

		for (SimpleGeom bbox : boundingBoxes) {
			int index = bbox.createRenderingPamater();
			RenderingParameters bboxParam = bbox.getRenderingParameters().get(index);
			bboxParam.setRenderMode(GL11.GL_LINES);
			bboxParam.setAlias("bboxEntities");
			this.draw3DRenderer.reloadAndprocess(bbox);
		}
	}
	
	public void printRay() {
		this.ray3D.resetGeom();
		int renderingParamIndex = this.ray3D.createRenderingPamater();
		RenderingParameters rayParamPoints = this.ray3D.getRenderingParameters().get(renderingParamIndex);
		rayParamPoints.setRenderMode(GL11.GL_POINTS);
		rayParamPoints.setAlias("RayPoints");
		int renderingParamIndexLines = this.ray3D.createRenderingPamater();
		RenderingParameters rayParamLines = this.ray3D.getRenderingParameters().get(renderingParamIndexLines);
		rayParamLines.setRenderMode(GL11.GL_LINE_STRIP);
		rayParamLines.setAlias("rayLines");
		this.draw3DRenderer.reloadAndprocess(this.ray3D);
	}
	
	/**
	 * Project bbox worldCoordinates points to the rendering plane. Last step
	 * (converting to clipSpace) is equals to the cartesian conversion as w is equal
	 * to -z. Doing again cartesian division aka [ (NearPlane * x)/z (NearPlane
	 * *y)/z] gives a minimap. (NearPlane can be replaced by any constant.)
	 * 
	 * @param entity
	 */
	public void printSelectedBboxIn2D(Entity entity) {
		for (SimpleGeom point : debugPoints) {
			point.resetGeom();
		}
		debugPoints.clear();
		Vector4f outsideColor = new Vector4f(0.85f, 0.2f, 0.25f, 1);
		Vector4f renderedColor = new Vector4f(0.84f, 0.56f, 0.91f, 1);
		if (entity.getBoundingBox().isEmpty()) {
			return;
		}
		SimpleGeom2D pointToScreenSpace = new SimpleGeom2D(loader);
		SimpleGeom2D pointToCartesianSpace = new SimpleGeom2D(loader);
		SimpleGeom2D nearPlane = new SimpleGeom2D(loader);
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
		debugPoints.add(pointToScreenSpace);
		debugPoints.add(pointToCartesianSpace);
		debugPoints.add(nearPlane);
		// retry to render on 2D rendered. may compute this by taking care nearLenght
		// while dividing by distance.
		int indexScreenSpaceParam = pointToScreenSpace.createRenderingPamater();
		RenderingParameters pointScreenSpaceParams = pointToScreenSpace.getRenderingParameters().get(indexScreenSpaceParam);
		pointScreenSpaceParams.setRenderMode(GL11.GL_LINE_LOOP);
		pointScreenSpaceParams.setAlias("pointScreenSpace");
		this.draw2DRenderer.reloadAndprocess(pointToScreenSpace);

		int indexcartesianParam = pointToCartesianSpace.createRenderingPamater();
		RenderingParameters pointCartesiansParams = pointToCartesianSpace.getRenderingParameters().get(indexcartesianParam);
		pointCartesiansParams.setRenderMode(GL11.GL_LINE_LOOP);
		pointCartesiansParams.setAlias("pointCartesian");
		this.draw2DRenderer.reloadAndprocess(pointToCartesianSpace);

		int indexNearParam = nearPlane.createRenderingPamater();
		RenderingParameters pointNearParams =nearPlane.getRenderingParameters().get(indexNearParam);
		pointNearParams.setAlias("pointNear");
		pointNearParams.setRenderMode(GL11.GL_LINE_LOOP);
		this.draw2DRenderer.reloadAndprocess(nearPlane);
	}
	
	private SimpleGeom3D getFrustrumForPlainTriangles(Vector3f ltfWorldCoord, Vector3f rtfWorldCoord,
			Vector3f lbfWorldCoord, Vector3f rbfWorldCoord, Vector3f ltnWorldCoord, Vector3f rtnWorldCoord,
			Vector3f lbnWorldCoord, Vector3f rbnWorldCoord) {
		Vector4f cameraTransparency = new Vector4f(0.5f, 0.98f, 0.4f, 0.41f);
		SimpleGeom3D frustrum = new SimpleGeom3D(loader);
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
		SimpleGeom3D frustrum = new SimpleGeom3D(loader);
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
