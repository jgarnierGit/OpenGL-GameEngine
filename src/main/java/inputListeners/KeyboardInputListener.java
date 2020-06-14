package inputListeners;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import renderEngine.DisplayManager;

/**
 * Do not use Keyboard library, (lwjgl 2 compatible). use GLFW instead 
 * english keyboard.
 * avoid enum collision values between mouse and keyboard
 * @author chezmoi
 *
 */
public class KeyboardInputListener extends InputListeners{

	@Override
	public void updateUserInputs() {
		updateKeyboardInputHandler();
	}
	
	private void updateKeyboardInputHandler() {
		glfwSetKeyCallback(DisplayManager.WINDOW_ID, (long window, int key, int scancode, int action, int mods) -> {
			// GLFW_REPEAT returned after a small delay.
			if (action != GLFW_RELEASE) {
				this.inputHandler.updateInputStatus(key, true);
				this.inputHandler.updateInputReleasableStatus(key,true);
			} else {
				this.inputHandler.resetCount(key);
				this.inputHandler.updateInputStatus(key, false);
			}
		});
	}

}
