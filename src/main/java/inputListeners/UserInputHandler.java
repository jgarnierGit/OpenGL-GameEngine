package inputListeners;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import java.util.logging.Logger;

import renderEngine.DisplayManager;

public class UserInputHandler {

	private final boolean[] inputs;
	private final int[] activeCounter;
	private final boolean[] releaseIsEnabled;
	private float mouseXposition;
	private float mouseYposition;
	private float lastMouseXPos;
	private float lastMouseYPos;
	private float scrollValue;
	private boolean isScrollingUp;
	private static UserInputHandler userInputHandler = null;
	private Logger logger;

	private UserInputHandler() {
		inputs = new boolean[65535];
		activeCounter = new int[65535];
		releaseIsEnabled = new boolean[65535];
		mouseXposition = 0;
		mouseYposition = 0;
		lastMouseXPos = 0;
		lastMouseYPos = 0;
		scrollValue = 0;
		isScrollingUp = false;
	}

	/**
	 * Lazy-loading singleton is enough as inputListener can only be performed on
	 * main thread
	 * 
	 * @return
	 */
	public static UserInputHandler create() {
		if (userInputHandler == null) {
			userInputHandler = new UserInputHandler();
			userInputHandler.logger = Logger.getLogger("UserInputHandler");
		}
		return userInputHandler;
	}

	/**
	 * Those 3 update methods as to be called every frame to update inputs states
	 */
	private void updateScrollInputHandler() {
		glfwSetScrollCallback(DisplayManager.WINDOW_ID, (long window, double xoffset, double yoffset) -> {
			isScrollingUp = yoffset > 0;
			if (scrollValue != 0 && isScrollingUp != (scrollValue > 0)) {
				scrollValue = 0;
			} else {
				scrollValue += (float) yoffset;
			}
		});
	}

	private void updateMouseInputHandler() {
		updateMousePosition();
		glfwSetMouseButtonCallback(DisplayManager.WINDOW_ID, (long window, int button, int action, int mods) -> {
			// no GLFW_REPEAT for mouse inputs
			if (action == GLFW_PRESS) {
				inputs[button] = true;
			} else {
				resetCount(button);
				inputs[button] = false;
			}
		});
	}

	private void updateKeyboardInputHandler() {
		glfwSetKeyCallback(DisplayManager.WINDOW_ID, (long window, int key, int scancode, int action, int mods) -> {
			// GLFW_REPEAT returned after a small delay.
			if (action != GLFW_RELEASE) {
				inputs[key] = true;
				releaseIsEnabled[key] = true;
			} else {
				resetCount(key);
				inputs[key] = false;
			}
		});
	}

	private void resetCount(int button) {
		activeCounter[button] = 0;
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
	 * return true when button can be released, then release it.
	 * 
	 * @param button
	 * @return
	 */
	public boolean activateOnReleaseOnce(int button) {
		if (!inputs[button] && releaseIsEnabled[button]) {
			releaseIsEnabled[button] = false;
			return true;
		}
		return false;
	}

	/**
	 * @param button
	 * @return code{true} only once when button is pressed
	 */
	public boolean activateOnPressOneTime(int button) {
		if(!inputs[button]) {
			return false;
		}
		activeCounter[button]++;
		return inputs[button] && activeCounter[button] == 1;
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

	public void updateUserInputs() {
		userInputHandler.updateKeyboardInputHandler();
		userInputHandler.updateMouseInputHandler();
		userInputHandler.updateScrollInputHandler();
	}
}
