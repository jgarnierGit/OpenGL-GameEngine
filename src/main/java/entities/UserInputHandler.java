package entities;

import static org.lwjgl.glfw.GLFW.*;

import renderEngine.DisplayManager;

public class UserInputHandler {

	private static final boolean[] inputs = new boolean[65535];
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
			/**	if(setOrigins) {
					originXPos = (float) xpos;
					originYPos =(float) ypos;
					System.out.println("set origin mouse");
				}**/
				// mouseXposition = (float) (DisplayManager.WIDTH / 2 + originXPos - xpos);
				// mouseYposition = (float) (DisplayManager.HEIGHT / 2 + originYPos - ypos);
			lastMouseXPos = mouseXposition;
			lastMouseYPos = mouseYposition;
			mouseXposition = (float) xpos;
			mouseYposition = (float) ypos;
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
}
