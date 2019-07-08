package entities;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjglx.input.Mouse;
import org.lwjglx.util.vector.Vector3f;

import renderEngine.DisplayManager;

public class Camera {
	
	private float distanceFromPlayer;
	private float angleAroundPlayer;
	
	private Vector3f position = new Vector3f(0,10,0);
	private float pitch = 20;
	private float yaw = 0;
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
				float zoomLevel = (float) yoffset * 0.1f;
				distanceFromPlayer -= zoomLevel;
			}
		});
	}
	
	private void calculatePitch() {
		glfwSetMouseButtonCallback(DisplayManager.WINDOW_ID, new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				if (button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS) {
					glfwSetCursorPosCallback(DisplayManager.WINDOW_ID, new GLFWCursorPosCallback() {
						@Override
						public void invoke(long window, double xpos, double ypos) {
							float pitchChange = (float) ypos * 0.1f;
							pitch -= pitchChange;
						}
					});
				}
			}
		});
	}
	
	private void calculateAngleAroundPlayer() {
		glfwSetMouseButtonCallback(DisplayManager.WINDOW_ID, new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
					glfwSetCursorPosCallback(DisplayManager.WINDOW_ID, new GLFWCursorPosCallback() {
						@Override
						public void invoke(long window, double xpos, double ypos) {
							float angleChange = (float) xpos * 0.1f;
							angleAroundPlayer -= angleChange;
						}
					});
				}
			}
		});
	}
	
	/**
	 * Do not use Keyboard library, (lwjgl 2 compatible). use GLFW instead
	 * Tip : english keyboard.
	 */
	public void move() {
		calculatePitch();
		calculateAngleAroundPlayer();
		
		calculateZoom();
		float horizontalDist = calculateHorizontalDistance();
		float verticalDist = calculateVerticalDistance();
		calculateCameraPosition(horizontalDist,verticalDist);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
	}
}
