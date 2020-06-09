package inputListeners;

public interface IInputInteractable {
	/**
	 * Register in appropriate InputListener commands to execute for dedicated input
	 * i.e :
	 * inputListener.addRunner(GLFW_KEY_W, () -> myMethod());
	 */
	public void bindInputHanlder();
}
