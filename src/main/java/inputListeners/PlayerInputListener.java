package inputListeners;

import java.util.Optional;

/**
 * Manage every input listener device.
 * @author chezmoi
 *
 */
public class PlayerInputListener {
	protected MouseInputListener mouseInputHandler;
	protected KeyboardInputListener keyboardInputHandler;
	
	protected PlayerInputListener() {
		// accessed by PlayerInputListenerBuilder
	}

	public void update() {
		if (mouseInputHandler != null) {
			mouseInputHandler.updateUserInputs();
			mouseInputHandler.execute();
		}
		if (keyboardInputHandler != null) {
			keyboardInputHandler.updateUserInputs();
			keyboardInputHandler.execute();
		}
	}

	public void clear() {
		if (mouseInputHandler != null) {
			mouseInputHandler.clear();
		}
		if (keyboardInputHandler != null) {
			keyboardInputHandler.clear();
		}
	}
	
	public Optional<KeyboardInputListener> getKeyboard() {
		return Optional.ofNullable(this.keyboardInputHandler);
	}
	
	public  Optional<MouseInputListener> getMouse() {
		return Optional.ofNullable(this.mouseInputHandler);
	}
}
