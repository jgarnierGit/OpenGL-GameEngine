package entities;

import static org.lwjgl.glfw.GLFW.*;

import renderEngine.DisplayManager;

public class UserInputHandler {

	private static final boolean[] inputs = new boolean[65535];
	private static final int[] activeCounter = new int[65535];
	private static boolean isButtonPressedNow = false;
	private static float mouseXposition = 0;
	private static float mouseYposition = 0;
	private static float lastMouseXPos = 0;
	private static float lastMouseYPos = 0;
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
		updateMousePosition();
		glfwSetMouseButtonCallback(DisplayManager.WINDOW_ID, (long window, int button, int action, int mods) -> {
			// no GLFW_REPEAT for mouse inputs
			if(action == GLFW_PRESS) {
				inputs[button] = true;
				isButtonPressedNow = true;				
			}
			else {
				resetCount(button);
				inputs[button] = false;
				isButtonPressedNow = false;
			}
		});
	}

	private static void updateKeyboardInputHandler() {
		glfwSetKeyCallback(DisplayManager.WINDOW_ID,(long window, int key, int scancode, int action, int mods) -> {
			// GLFW_REPEAT returned after a small delay.	
			if(action != GLFW_RELEASE) {
					inputs[key] = true;
					isButtonPressedNow = action == GLFW_PRESS;
				}
				else {
					resetCount(key);
					inputs[key] = false;
					isButtonPressedNow = false;
				}
		});
	}
	
	private static void incrementCount(int button) {
		if(!inputs[button]) {
			activeCounter[button] = 1;
		}
		else {
			activeCounter[button] += 1;
		}
	}
	
	private static void resetCount(int button) {
		activeCounter[button] =  0;
	}
	
	public static boolean isPressed() {
		return isButtonPressedNow;
	}
	
	/**
	 * return true while button is pressed
	 * @param button
	 * @return
	 */
	public static boolean activateOnPress(int button) {
		return inputs[button];
	}
	
	/**
	 * @param button
	 * @return code{true} only once when button is pressed
	 */
	public static boolean activateOnPressOneTime(int button) {
		if(isButtonPressedNow) {
			incrementCount(button);
			return inputs[button] && activeCounter[button] == 1;
		}
		return false;
	}

	public static float getMouseXpos() {
		return mouseXposition;
	}
	
	public static float getMouseYpos() {
		return mouseYposition;
	}
	
	public static float getMouseDeltaX() {
		return mouseXposition - lastMouseXPos;
	}
	
	public static float getMouseDeltaY() {
		return mouseYposition - lastMouseYPos;
	}
	
	public static float getScrollValue() {
		return scrollValue;
	}

	public static void updateMousePosition() {
		glfwSetCursorPosCallback(DisplayManager.WINDOW_ID, (long window, double xpos, double ypos) -> {
			lastMouseXPos = mouseXposition;
			lastMouseYPos = mouseYposition;
			mouseXposition = (float) xpos;
			mouseYposition = (float) ypos;
		});
	}
}
