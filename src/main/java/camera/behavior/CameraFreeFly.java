package camera.behavior;

import java.util.logging.Level;

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
	private int translateInput;
	
	private CameraFreeFly(PlayerInputListener inputListener, CameraEntity camera, int glfwRotateInput, int glfwDeltaTranslation) {
		super(inputListener, camera);
		speed = 0;
		rotateInput= glfwRotateInput;
		translateInput = glfwDeltaTranslation;
		this.camera = camera;
	}
	
	public static CameraFreeFly create(PlayerInputListener inputListener, CameraEntity camera, int glfwRotateInput, int glfwDeltaTranslation) {
		CameraFreeFly cameraBehavior = new CameraFreeFly(inputListener, camera, glfwRotateInput, glfwDeltaTranslation);
		cameraBehavior.bindInputHanlder();
		return cameraBehavior;
	}
	
	//TODO find a way to pass mouseListener as param. maybe builder.
	@Override
	public void bindInputHanlder() {
		inputListener.getMouse().ifPresent(mouseListener -> {
			mouseListener.addRunnerOnPress(rotateInput, () -> rotate(mouseListener));
			mouseListener.addRunnerOnPress(translateInput, () -> translate(mouseListener));
		});
	}

	/**
	 * cos(0) = 1; cos(1) = 0 sin(0) = 0; sin(1) = 1
	 * @param terrain
	 */
	@Override
	public void update() {
		Vector3f lookAtunitVector = Maths.degreesToCartesianUnitVector(camera.getPitch(), camera.getYaw());
		calculateSpeed();
		lookAtunitVector.scale(this.speed);
		Vector3f newPosition = Vector3f.add(camera.getPosition(), lookAtunitVector, null);
		camera.setPosition(newPosition);
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
		Vector3f worldCoordTranslationX = this.camera.getCoordinatesSystemManager().viewCoordToWorldCoord(this.camera.getViewMatrix(), new Vector3f(deltaX,0,0));
		Vector3f newPosition = Vector3f.add(camera.getPosition(),worldCoordTranslationX,null);
		camera.setPosition(newPosition);
	}
	
	private void translateY(MouseInputListener mouseListener) {
		float deltaY = - (mouseListener.getMouseYpos() - DisplayManager.HEIGHT/2) * 0.005f;
		Vector3f worldCoordTranslationX = this.camera.getCoordinatesSystemManager().viewCoordToWorldCoord(this.camera.getViewMatrix(), new Vector3f(0,deltaY,0));
		Vector3f newPosition = Vector3f.add(camera.getPosition(),worldCoordTranslationX,null);
		camera.setPosition(newPosition);
	}

	private void calculatePitch(MouseInputListener mouseListener) {
		float ypos = mouseListener.getMouseDeltaY();
		float pitch = camera.getPitch() - (-ypos * 0.5f);
		camera.setPitch(pitch);
	}

	private void calculateYaw(MouseInputListener mouseListener) {
		float xpos = mouseListener.getMouseDeltaX();
		float yaw = camera.getYaw() + xpos * 0.5f;
		camera.setYaw(yaw);
	}
	

	
	private void calculateSpeed() {
		this.inputListener.getMouse().ifPresent(mouseListener -> {
			float inputScroll = mouseListener.getScrollValue();
			if (inputScroll == 0) {
				this.speed = 0;
			} else {
				if (Math.abs(this.speed) < Math.abs(inputScroll) / 10) {
					logger.log(Level.INFO, " " + this.speed + " " + inputScroll);
					float speed = inputScroll * 0.05f;
					this.speed += speed;
				}
			}
		});
	}

}
