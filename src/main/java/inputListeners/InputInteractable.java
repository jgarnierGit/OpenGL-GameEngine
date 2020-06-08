package inputListeners;

import java.util.logging.Logger;

public abstract class InputInteractable {
	protected InputListeners inputListener;
	protected Logger logger;
	
	public InputInteractable(InputListeners inputListener) {
		this.inputListener = inputListener;
		this.logger = Logger.getLogger("InputInteractable");
	}
	/**
	 * calls inputListener.addRunner
	 */
	public abstract void bindInputHanlder();
}
