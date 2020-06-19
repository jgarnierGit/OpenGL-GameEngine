package inputListeners;

public interface IInputInteractable {
	/**
	 * TODO Implement a constructor pattern to make binding automatic
	 * Register in appropriate InputListener commands to execute for dedicated input
	 * i.e :
	 * inputListener.addRunner(GLFW_KEY_W, () -> myMethod());
	 */
	public void bindInputHanlder();
	
	/**
	 * force unbind inputHandler to avoid conflict behavior in game.
	 */
	public void unbindInputHanlder();
}
