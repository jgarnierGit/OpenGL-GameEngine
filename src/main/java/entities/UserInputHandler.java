package entities;

import static org.lwjgl.glfw.GLFW.*;

import renderEngine.DisplayManager;
import toolbox.mousePicker.MouseInputListener;

public class UserInputHandler {

	private final boolean[] inputs;
	private final int[] activeCounter;
	private boolean isButtonPressedNow;
	private float mouseXposition;
	private float mouseYposition;
	private float lastMouseXPos;
	private float lastMouseYPos;
	private float scrollValue;
	private boolean isScrollingUp;
	private static UserInputHandler userInputHandler = null;

	private UserInputHandler() {
		inputs = new boolean[65535];
		activeCounter = new int[65535];
		isButtonPressedNow = false;
		mouseXposition = 0;
		mouseYposition = 0;
		lastMouseXPos = 0;
		lastMouseYPos = 0;
		scrollValue = 0;
		isScrollingUp = false;
	}

	/**
	 * Lazy-loading singleton is enough as inputListener can only be performed on main thread
	 * @return
	 */
	public static UserInputHandler create() {
		if(userInputHandler == null) {
			userInputHandler = new UserInputHandler();
			userInputHandler.initKeyboardInputHandler();
			userInputHandler.initMouseInputHandler();
			userInputHandler.initScrollInputHandler();
		}
		return userInputHandler;
	}

	private void initScrollInputHandler() {
		glfwSetScrollCallback(DisplayManager.WINDOW_ID, (long window, double xoffset, double yoffset) -> {
			isScrollingUp = yoffset > 0;
			if (scrollValue != 0 && isScrollingUp != (scrollValue > 0)) {
				scrollValue = 0;
			} else {
				scrollValue += (float) yoffset;
			}
		});
	}

	private void initMouseInputHandler() {
		updateMousePosition();
		glfwSetMouseButtonCallback(DisplayManager.WINDOW_ID, (long window, int button, int action, int mods) -> {
			// no GLFW_REPEAT for mouse inputs
			if (action == GLFW_PRESS) {
				inputs[button] = true;
				isButtonPressedNow = true;
			} else {
				resetCount(button);
				inputs[button] = false;
				isButtonPressedNow = false;
			}
		});
	}

	private void initKeyboardInputHandler() {
		glfwSetKeyCallback(DisplayManager.WINDOW_ID, (long window, int key, int scancode, int action, int mods) -> {
			// GLFW_REPEAT returned after a small delay.
			if (action != GLFW_RELEASE) {
				inputs[key] = true;
				isButtonPressedNow = action == GLFW_PRESS;
			} else {
				resetCount(key);
				inputs[key] = false;
				isButtonPressedNow = false;
			}
		});
	}

	private void incrementCount(int button) {
		if (!inputs[button]) {
			activeCounter[button] = 1;
		} else {
			activeCounter[button] += 1;
		}
	}

	private void resetCount(int button) {
		activeCounter[button] = 0;
	}

	public boolean isPressed() {
		return isButtonPressedNow;
	}

	/**
	 * return true while button is pressed
	 * 
	 * @param button
	 * @return
	 */
	public boolean activateOnPress(int button) {
		return inputs[button];
	}

	/**
	 * @param button
	 * @return code{true} only once when button is pressed
	 */
	public boolean activateOnPressOneTime(int button) {
		if (isButtonPressedNow) {
			incrementCount(button);
			return inputs[button] && activeCounter[button] == 1;
		}
		return false;
	}

	public float getMouseXpos() {
		return mouseXposition;
	}

	public float getMouseYpos() {
		return mouseYposition;
	}

	public float getMouseDeltaX() {
		return mouseXposition - lastMouseXPos;
	}

	public float getMouseDeltaY() {
		return mouseYposition - lastMouseYPos;
	}

	public float getScrollValue() {
		return scrollValue;
	}

	public void updateMousePosition() {
		glfwSetCursorPosCallback(DisplayManager.WINDOW_ID, (long window, double xpos, double ypos) -> {
			lastMouseXPos = mouseXposition;
			lastMouseYPos = mouseYposition;
			mouseXposition = (float) xpos;
			mouseYposition = (float) ypos;
		});
	}
}
