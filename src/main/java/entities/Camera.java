package entities;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjglx.util.vector.Vector3f;

import renderEngine.DisplayManager;

public class Camera {

	private float distanceFromPlayer = 10;
	private float angleAroundPlayer;

	private Vector3f position = new Vector3f(0,0,-20);
	private float pitch = 20;
	private float yaw = 50;
	private float roll = 0;

	private Player player;


	public Camera(Player player) {
		this.player = player;
	}

	public Vector3f getPosition() {
		return position;
	}

	/**
	 * get the pitch angle.
	 * Pitch angle is the angle that makes "yes" head movement.
	 * @return
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * get the yaw angle.
	 * Yaw angle is the angle that makes "No" head movement.
	 * @return
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * get the Roll angle.
	 * Roll angle is the angle that makes the "Meh" head movement.
	 * @return
	 */
	public float getRoll() {
		return roll;
	}

	private void calculateCameraPosition(float horizontalDist, float verticalDist) {
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDist * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDist * Math.cos(Math.toRadians(theta)));
		position.x = player.getPositions().x - offsetX;
		position.z = player.getPositions().z - offsetZ;
		position.y = player.getPositions().y + verticalDist;
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {
		glfwSetScrollCallback(DisplayManager.WINDOW_ID, new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				float zoomLevel = (float) yoffset * 0.5f;
				distanceFromPlayer -= zoomLevel;
			}
		});
	}

	private void calculatePitch() {
		if (UserInputHandler.isPressed() && UserInputHandler.isActive(GLFW_MOUSE_BUTTON_MIDDLE)) {
			System.out.println("updating origin");
			UserInputHandler.setYOrigin();
		}
		if(UserInputHandler.isActive(GLFW_MOUSE_BUTTON_MIDDLE)){
			float ypos =  UserInputHandler.getMouseYpos();
			//float pitchChange += yposMouse;
			pitch -= ypos *0.01f;
		}
		else {

		}
	}

	private void calculateAngleAroundPlayer() {
		if (UserInputHandler.isActive(GLFW_MOUSE_BUTTON_MIDDLE)) {
			if(UserInputHandler.isPressed()) {
				System.out.println("updating origin");
				UserInputHandler.setXOrigin();
			}
			float xpos = UserInputHandler.getMouseXpos();
			float angleChange = xpos * 0.1f;
			angleAroundPlayer -= angleChange;
		}else {

		}
	}

	/**
	 * Do not use Keyboard library, (lwjgl 2 compatible). use GLFW instead
	 * Tip : english keyboard.
	 */
	public void move() {
		calculateAngleAroundPlayer();
		calculatePitch();


		calculateZoom();
		float horizontalDist = calculateHorizontalDistance();
		float verticalDist = calculateVerticalDistance();
		calculateCameraPosition(horizontalDist,verticalDist);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
	}
}
