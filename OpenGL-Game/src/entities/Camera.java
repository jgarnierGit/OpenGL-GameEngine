package entities;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjglx.util.vector.Vector3f;

import renderEngine.DisplayManager;

public class Camera {
	private Vector3f position = new Vector3f(0,0,3);
	private float pitch = 0;
	private float yaw = 0;
	private float roll = 0;
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
	public Camera() {
	}
	
	/**
	 * Do not use Keyboard library, (lwjgl 2 compatible). use GLFW instead
	 * Tip : english keyboard.
	 */
	public void move() {
		glfwSetKeyCallback(DisplayManager.WINDOW_ID, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_W && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
				position.z-=1f;
			}
			if ( key == GLFW_KEY_Q && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
				position.x+=1f;
			}
			if(key == GLFW_KEY_D && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
				position.x-=1f;
			}
			if(key == GLFW_KEY_Z && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
				position.y+=1f;
			}
			if(key == GLFW_KEY_X && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
				position.y-=1f;
			}
			if(key == GLFW_KEY_Y && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
				yaw+=1f;
			}
			if(key == GLFW_KEY_R && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
				roll+=1f;
			}
			if(key == GLFW_KEY_T && (action == GLFW_PRESS || action == GLFW_REPEAT)) {
				pitch+=1f;
			}
		});
	}
}
