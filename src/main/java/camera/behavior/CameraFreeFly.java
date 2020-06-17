package camera.behavior;

import java.util.logging.Level;

import org.lwjglx.util.vector.Vector3f;

import camera.Camera;
import camera.CameraEntity;
import inputListeners.MouseInputListener;
import inputListeners.PlayerInputListener;

public class CameraFreeFly extends Camera {
	private float speed;
	private int pitchInput;
	private int yawInput;
	
	private CameraFreeFly(PlayerInputListener inputListener, CameraEntity camera, int glfwPitch, int glfwYaw) {
		super(inputListener, camera);
		speed = 0;
		pitchInput= glfwPitch;
		yawInput = glfwYaw;
		this.camera = camera;
	}
	
	public static CameraFreeFly create(PlayerInputListener inputListener, CameraEntity camera, int glfwPitch, int glfwYaw) {
		CameraFreeFly cameraBehavior = new CameraFreeFly(inputListener, camera, glfwPitch,glfwYaw);
		cameraBehavior.bindInputHanlder();
		return cameraBehavior;
	}
	
	//TODO find a way to pass mouseListener as param. maybe builder.
	@Override
	public void bindInputHanlder() {
		inputListener.getMouse().ifPresent(mouseListener -> {
			mouseListener.addRunnerOnPress(pitchInput, () -> calculatePitch(mouseListener));
			mouseListener.addRunnerOnPress(yawInput, () -> calculateYaw(mouseListener));
		});
	}
	
	/**
	 * cos(0) = 1; cos(1) = 0 sin(0) = 0; sin(1) = 1 yaw anti-counter clockwise (0 =
	 * z-forward / x-left) pitch counter clockwise (0 = horizontal)
	 * 
	 * @param terrain
	 */
	@Override
	public void update() {
		float cameraXDirection = (float) (Math.sin(-Math.toRadians(camera.getYaw()))
				* Math.cos(Math.toRadians(camera.getPitch())));
		float cameraZDirection = (float) (Math.cos(-Math.toRadians(camera.getYaw()))
				* Math.cos(Math.toRadians(camera.getPitch())));
		float cameraYDirection = (float) (Math.sin(Math.toRadians(camera.getPitch())));

		Vector3f unitVectorCamera = new Vector3f(cameraXDirection, cameraYDirection, cameraZDirection);
		calculateSpeed();
		unitVectorCamera.scale(this.speed);
		Vector3f newPosition = Vector3f.add(camera.getPosition(), unitVectorCamera, null);
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
