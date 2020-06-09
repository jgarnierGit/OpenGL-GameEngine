package inputListeners;

import java.util.logging.Logger;

public abstract class InputInteractable implements IInputInteractable{
	protected InputListeners inputListener;
	protected Logger logger;
	
	public InputInteractable(InputListeners inputListener) {
		this.inputListener = inputListener;
		this.logger = Logger.getLogger("InputInteractable");
		this.bindInputHanlder();
	}
}
