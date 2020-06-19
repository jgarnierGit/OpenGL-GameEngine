package camera.behavior;

import org.lwjglx.util.vector.Vector3f;

import camera.Camera;
import camera.CameraEntity;
import inputListeners.MouseInputListener;
import inputListeners.PlayerInputListener;
import renderEngine.DisplayManager;
import toolbox.Maths;

public class CameraFreeFly extends Camera {
	private float speed;
	private int rotateInput;
	private Runnable rotateRunnable;
	private Runnable translateRunnable;
	private int translateInput;
	
	private CameraFreeFly(PlayerInputListener inputListener, CameraEntity camera, int glfwRotateInput, int glfwDeltaTranslation) {
		super(inputListener, camera);
		speed = 0;
		rotateInput= glfwRotateInput;
		translateInput = glfwDeltaTranslation;
		rotateRunnable = null;
		translateRunnable = null;
	}
	
	public static CameraFreeFly create(PlayerInputListener inputListener, CameraEntity camera, int glfwRotateInput, int glfwDeltaTranslation) {
		CameraFreeFly cameraBehavior = new CameraFreeFly(inputListener, camera, glfwRotateInput, glfwDeltaTranslation);
		cameraBehavior.bindInputHanlder();
		return cameraBehavior;
	}
	
	@Override
	public void stopMoving() {
		speed= 0;
		inputListener.getMouse().ifPresent(mouseListener -> {
			mouseListener.resetScrollDelta();
			mouseListener.resetMouseXDelta();
			mouseListener.resetMouseYDelta();
		});
	}
	
	//TODO find a way to pass mouseListener as param. maybe builder.
	@Override
	public void bindInputHanlder() {
		inputListener.getMouse().ifPresent(mouseListener -> {
			initMouseRunnable(mouseListener);
			mouseListener.addRunnerOnPress(rotateInput, rotateRunnable);
			mouseListener.addRunnerOnPress(translateInput, translateRunnable);
		});
	}
	
	private void initMouseRunnable(MouseInputListener mouseListener) {
		if(rotateRunnable == null) {
			rotateRunnable = () -> rotate(mouseListener);
		}
		if(translateRunnable == null) {
			translateRunnable = () -> translate(mouseListener);
		}
	}
	
	@Override
	public void unbindInputHanlder() {
		inputListener.getMouse().ifPresent(mouseListener -> {
			mouseListener.removeRunnerOnPress(rotateInput, rotateRunnable);
			mouseListener.removeRunnerOnPress(translateInput, translateRunnable);
		});
	}

	/**
	 * cos(0) = 1; cos(1) = 0 sin(0) = 0; sin(1) = 1
	 * @param terrain
	 */
	@Override
	public void update() {
		Vector3f lookAtunitVector = Maths.degreesToCartesianUnitVector(cameraEntity.getPitch(), cameraEntity.getYaw());
		calculateSpeed();
		lookAtunitVector.scale(this.speed);
		Vector3f newPosition = Vector3f.add(cameraEntity.getPosition(), lookAtunitVector, null);
		cameraEntity.setPosition(newPosition);
	}
	
	private void rotate(MouseInputListener mouseListener) {
		calculatePitch(mouseListener);
		calculateYaw(mouseListener);
	}
	
	private void translate(MouseInputListener mouseListener) {
		translateX(mouseListener);
		translateY(mouseListener);
	}
	
	private void translateX(MouseInputListener mouseListener) {
		float deltaX = (mouseListener.getMouseXpos() - DisplayManager.WIDTH/2) * 0.005f;
		Vector3f worldCoordTranslationX = this.cameraEntity.getCoordinatesSystemManager().viewCoordToWorldCoord(this.cameraEntity.getViewMatrix(), new Vector3f(deltaX,0,0));
		Vector3f newPosition = Vector3f.add(cameraEntity.getPosition(),worldCoordTranslationX,null);
		cameraEntity.setPosition(newPosition);
	}
	
	private void translateY(MouseInputListener mouseListener) {
		float deltaY = - (mouseListener.getMouseYpos() - DisplayManager.HEIGHT/2) * 0.005f;
		Vector3f worldCoordTranslationX = this.cameraEntity.getCoordinatesSystemManager().viewCoordToWorldCoord(this.cameraEntity.getViewMatrix(), new Vector3f(0,deltaY,0));
		Vector3f newPosition = Vector3f.add(cameraEntity.getPosition(),worldCoordTranslationX,null);
		cameraEntity.setPosition(newPosition);
	}

	private void calculatePitch(MouseInputListener mouseListener) {
		float ypos = mouseListener.getMouseDeltaY();
		float pitch = cameraEntity.getPitch() - (-ypos * 0.5f);
		cameraEntity.setPitch(pitch);
	}

	private void calculateYaw(MouseInputListener mouseListener) {
		float xpos = mouseListener.getMouseDeltaX();
		float yaw = cameraEntity.getYaw() + xpos * 0.5f;
		cameraEntity.setYaw(yaw);
	}
	

	
	private void calculateSpeed() {
		this.inputListener.getMouse().ifPresent(mouseListener -> {
			float inputScroll = mouseListener.getScrollValue();
			if (inputScroll == 0) {
				this.speed = 0;
			} else {
				if (Math.abs(this.speed) < Math.abs(inputScroll) / 10) {
					float speed = inputScroll * 0.05f;
					this.speed += speed;
				}
			}
		});
	}
}
