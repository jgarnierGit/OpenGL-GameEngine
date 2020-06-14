package inputListeners;

import java.util.logging.Logger;

/**
 * Cache every input state each frame
 * @author chezmoi
 *
 */
class InputHandler {

	private final boolean[] inputs;
	private final int[] activeCounter;
	private final boolean[] releaseIsEnabled;
	private static InputHandler commonInputHandler = null;
	private Logger logger;

	private InputHandler() {
		inputs = new boolean[65535];
		activeCounter = new int[65535];
		releaseIsEnabled = new boolean[65535];
	}
	
	protected void updateInputStatus(int key, boolean active) {
		inputs[key] = active;
	}
	
	protected void updateInputReleasableStatus(int key, boolean releasable) {
		releaseIsEnabled[key] = releasable;
	}

	/**
	 * Lazy-loading singleton is enough as inputListener can only be performed on
	 * main thread
	 * 
	 * @return
	 */
	protected static InputHandler create() {
		if (commonInputHandler == null) {
			commonInputHandler = new InputHandler();
			commonInputHandler.logger = Logger.getLogger("UserInputHandler");
		}
		return commonInputHandler;
	}

	protected void resetCount(int button) {
		activeCounter[button] = 0;
	}

	/**
	 * return true while button is pressed
	 * 
	 * @param button
	 * @return
	 */
	protected boolean activateOnPress(int button) {
		return inputs[button];
	}

	/**
	 * return true when button can be released, then release it.
	 * 
	 * @param button
	 * @return
	 */
	protected boolean activateOnReleaseOnce(int button) {
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
	protected boolean activateOnPressOneTime(int button) {
		if(!inputs[button]) {
			return false;
		}
		activeCounter[button]++;
		return inputs[button] && activeCounter[button] == 1;
	}
}
