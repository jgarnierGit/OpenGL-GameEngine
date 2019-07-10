package entities;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import renderEngine.DisplayManager;

public class UserInputHandler {

	private static final boolean[] inputs = new boolean[65535];
	private static boolean isButtonPressedNow = false;
	private static float mouseXposition = 0;
	private static float mouseYposition = 0;
	private static float originXPos = 0;
	private static float originYPos = 0;

	public static void updateMouseInputHandler() {
		updateMousePosition(false);
		glfwSetMouseButtonCallback(DisplayManager.WINDOW_ID, new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				if(action != GLFW_RELEASE) {
					inputs[button] = true;
				}
				else {
					inputs[button] = false;
				}
			}
		});
	}

	public static void updateKeyboardInputHandler() {
		glfwSetKeyCallback(DisplayManager.WINDOW_ID, new GLFWKeyCallback() {	
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if(action != GLFW_RELEASE) {
					inputs[key] = true;
					if(action == GLFW_PRESS) {
						isButtonPressedNow = true;
					}
					else {
						isButtonPressedNow = false;
					}
				}
				else {
					inputs[key] = false;
				}
			}
		});
	}
	public static boolean isPressed() {
		return isButtonPressedNow;
	}
	public static boolean isActive(int button) {
		return inputs[button];
	}

	public static float getMouseXpos() {
		return mouseXposition;
	}

	public static void updateMousePosition(boolean setOrigins) {
		glfwSetCursorPosCallback(DisplayManager.WINDOW_ID, new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				if(setOrigins) {
					originXPos = (float) xpos;
					originYPos =(float) ypos;
					System.out.println("set origin mouse");
				}
				mouseXposition = (float) (DisplayManager.WIDTH / 2 + originXPos - xpos);
				mouseYposition = (float) (DisplayManager.HEIGHT / 2 + originYPos - ypos);		
			}
		});
	}

	/**	public static void updateMouseXPosition() {
		glfwSetCursorPosCallback(DisplayManager.WINDOW_ID, new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				mouseXposition = (float) xpos;
			}
		});
	}

	public static void updateMouseYPosition() {
		glfwSetCursorPosCallback(DisplayManager.WINDOW_ID, new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				mouseYposition = (float) (DisplayManager.HEIGHT / 2 - ypos);
			}
		});
	}**/

	public static float getMouseYpos() {
		return mouseYposition;
	}

	public static void setYOrigin() {
		updateMousePosition(true);
	}

	public static void setXOrigin() {
		updateMousePosition(true);
	}
}
