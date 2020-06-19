package camera.behavior;

import org.lwjglx.util.vector.Vector3f;

import camera.Camera;
import camera.CameraEntity;
import entities.Entity;
import inputListeners.MouseInputListener;
import inputListeners.PlayerInputListener;

public class CameraLockedToEntity extends Camera {
	private int rotateInput;
	private float distanceToEntity;
	private final float distanceToEntityRef;
	private float angleAroundPlayer;
	private final float angleAroundPlayerRef;
	private Runnable rotateRunnable;
	private Entity entity;

	private CameraLockedToEntity(PlayerInputListener inputListener, CameraEntity camera, int glfwRotateInput,
			float distanceToEntity, float angleAroundPlayer, Entity entity) {
		super(inputListener, camera);
		rotateInput = glfwRotateInput;
		this.distanceToEntity = distanceToEntity;
		this.distanceToEntityRef = distanceToEntity;
		this.angleAroundPlayer = angleAroundPlayer;
		this.angleAroundPlayerRef = angleAroundPlayer;
		this.entity = entity;
		rotateRunnable = null;
	}

	/**
	 * 
	 * @param inputListener
	 * @param camera
	 * @param glfwRotateInput
	 * @param distanceToEntity
	 * @param angleAroundPlayer default yaw angle 
	 * @param entity	lock to entity
	 * @return
	 */
	public static CameraLockedToEntity create(PlayerInputListener inputListener, CameraEntity camera,
			int glfwRotateInput, float distanceToEntity, float angleAroundPlayer, Entity entity) {
		CameraLockedToEntity cameraEntity = new CameraLockedToEntity(inputListener, camera, glfwRotateInput,
				distanceToEntity, angleAroundPlayer, entity);
		cameraEntity.bindInputHanlder();
		return cameraEntity;
	}
	
	public void lockToEntity(Entity entity) {
		this.entity = entity;
	}

	@Override
	public void update() {
		calculateZoom();
		updatePosition();
	}
	
	@Override
	public void stopMoving() {
		inputListener.getMouse().ifPresent(mouseListener -> {
			mouseListener.resetScrollDelta();
			mouseListener.resetMouseXDelta();
			mouseListener.resetMouseYDelta();
		});
		this.distanceToEntity = this.distanceToEntityRef;
		this.angleAroundPlayer = this.angleAroundPlayerRef;
	}

	private void updatePosition() {
		float horizontalDist = calculateHorizontalDistance();
		float verticalDist = calculateVerticalDistance();
		calculateCameraPosition(horizontalDist, verticalDist);
		this.cameraEntity.setYaw(180 - (entity.getRotY() + angleAroundPlayer));
	}

	private void calculateCameraPosition(float horizontalDist, float verticalDist) {
		float theta = entity.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDist * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDist * Math.cos(Math.toRadians(theta)));
		float xUpdated = entity.getPositions().x - offsetX;
		float zUpdated = entity.getPositions().z - offsetZ;
		float yUpdated = entity.getPositions().y + verticalDist;
		this.cameraEntity.setPosition(new Vector3f(xUpdated, yUpdated, zUpdated));
	}

	@Override
	public void bindInputHanlder() {
		inputListener.getMouse().ifPresent(mouseListener -> {
			initMouseRunnable(mouseListener);
			mouseListener.addRunnerOnPress(rotateInput, rotateRunnable);
		});
	}
	
	/**
	 * TODO try to extract to higher level to avoid duplication.
	 * @param mouseListener
	 */
	private void initMouseRunnable(MouseInputListener mouseListener) {
		if(rotateRunnable == null) {
			rotateRunnable = () -> rotate(mouseListener);
		}
	}

	@Override
	public void unbindInputHanlder() {
		inputListener.getMouse().ifPresent(mouseListener -> {
			mouseListener.removeRunnerOnPress(rotateInput, rotateRunnable);
		});
	}

	private void rotate(MouseInputListener mouseListener) {
		calculatePitch(mouseListener);
		calculateAngleAroundPlayer(mouseListener);
	}

	private void calculateAngleAroundPlayer(MouseInputListener mouseListener) {
		float xpos = mouseListener.getMouseDeltaX();
		angleAroundPlayer -= xpos * 0.5f;
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceToEntity * Math.cos(Math.toRadians(this.cameraEntity.getPitch())));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceToEntity * Math.sin(Math.toRadians(this.cameraEntity.getPitch())));
	}

	private void calculatePitch(MouseInputListener mouseListener) {
		float ypos = mouseListener.getMouseDeltaY();
		this.cameraEntity.increasePitch(ypos * 0.5f);
	}

	private void calculateZoom() {
		this.inputListener.getMouse().ifPresent(mouseListener -> {
			float zoomLevel = mouseListener.getScrollValue() * 0.5f;
			this.distanceToEntity -= zoomLevel;
		});
	}
}
