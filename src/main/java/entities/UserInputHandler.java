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
	private static float scrollValue = 0;
	private static boolean isScrollingUp = false;

	public static void updateInputHandler() {
		updateKeyboardInputHandler();
		updateMouseInputHandler();
		updateScrollInputHandler();
	}
	
	private static void updateScrollInputHandler() {
		glfwSetScrollCallback(DisplayManager.WINDOW_ID, (long window, double xoffset, double yoffset) -> {
			isScrollingUp = yoffset > 0;
			if(scrollValue != 0 && isScrollingUp != (scrollValue > 0)) {
				scrollValue = 0;
			}
			else {
				scrollValue +=  (float) yoffset;
			}
		});
	}
	
	private static void updateMouseInputHandler() {
		updateMousePosition(false);
		glfwSetMouseButtonCallback(DisplayManager.WINDOW_ID, (long window, int button, int action, int mods) -> {
			inputs[button] = action != GLFW_RELEASE;
		});
	}

	private static void updateKeyboardInputHandler() {
		glfwSetKeyCallback(DisplayManager.WINDOW_ID,(long window, int key, int scancode, int action, int mods) -> {
				if(action != GLFW_RELEASE) {
					inputs[key] = true;
					isButtonPressedNow = action == GLFW_PRESS;
				}
				else {
					inputs[key] = false;
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
	
	public static float getMouseYpos() {
		return mouseYposition;
	}
	
	public static float getScrollValue() {
		return scrollValue;
	}

	public static void updateMousePosition(boolean setOrigins) {
		glfwSetCursorPosCallback(DisplayManager.WINDOW_ID, (long window, double xpos, double ypos) -> {
			/**	if(setOrigins) {
					originXPos = (float) xpos;
					originYPos =(float) ypos;
					System.out.println("set origin mouse");
				}**/
				mouseXposition = (float) (DisplayManager.WIDTH / 2 + originXPos - xpos);
				mouseYposition = (float) (DisplayManager.HEIGHT / 2 + originYPos - ypos);		
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

	public static void setYOrigin() {
		updateMousePosition(true);
	}

	public static void setXOrigin() {
		updateMousePosition(true);
	}
}
