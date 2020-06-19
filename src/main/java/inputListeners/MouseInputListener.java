package inputListeners;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import java.util.logging.Level;

import renderEngine.DisplayManager;

/**
 * avoid enum collision values between mouse and keyboard
 * 
 * @author chezmoi
 *
 */
public class MouseInputListener extends InputListeners {

	private float mouseXposition;
	private float mouseYposition;
	private float lastMouseXPos;
	private float lastMouseYPos;
	protected float scrollValue;
	private boolean isScrollingUp;

	public MouseInputListener() {
		mouseXposition = 0;
		mouseYposition = 0;
		lastMouseXPos = 0;
		lastMouseYPos = 0;
		scrollValue = 0;
		isScrollingUp = false;
	}

	public float getScrollValue() {
		return scrollValue;
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

	private void updateMousePosition() {
		glfwSetCursorPosCallback(DisplayManager.WINDOW_ID, (long window, double xpos, double ypos) -> {
			lastMouseXPos = mouseXposition;
			lastMouseYPos = mouseYposition;
			mouseXposition = (float) xpos;
			mouseYposition = (float) ypos;
		});
	}

	@Override
	protected void updateUserInputs() {
		updateMouseInputHandler();
		updateScrollInputHandler();
	}

	private void updateMouseInputHandler() {
		updateMousePosition();
		glfwSetMouseButtonCallback(DisplayManager.WINDOW_ID, (long window, int key, int action, int mods) -> {
			// no GLFW_REPEAT for mouse inputs
			if (action == GLFW_PRESS) {
				this.inputHandler.updateInputStatus(key, true);
				this.inputHandler.updateInputReleasableStatus(key, true);
			} else {
				this.inputHandler.resetCount(key);
				this.inputHandler.updateInputStatus(key, false);
			}
		});
	}

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

	public void resetScrollDelta() {
		scrollValue = 0;
	}

	public void resetMouseXDelta() {
		mouseXposition = 0;
		lastMouseXPos = 0;
	}

	public void resetMouseYDelta() {
		mouseYposition = 0;
		lastMouseYPos = 0;
	}

}
