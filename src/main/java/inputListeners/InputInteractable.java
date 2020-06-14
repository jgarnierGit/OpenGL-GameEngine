package inputListeners;

import java.util.logging.Logger;

public abstract class InputInteractable implements IInputInteractable{
	protected PlayerInputListener inputListener;
	protected Logger logger;
	
	public InputInteractable(PlayerInputListener inputListener) {
		this.inputListener = inputListener;
		this.logger = Logger.getLogger("InputInteractable");
	}
}
