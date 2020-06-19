package inputListeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Stores executables mapped to input and execute when input is triggered
 * @author chezmoi
 *
 */
public abstract class InputListeners {
	protected HashMap<Integer, Set<Runnable>> runnersOnPress;
	protected HashMap<Integer, Set<Runnable>> runnersOnUiquePress;
	protected HashMap<Integer, Set<Runnable>> runnersOnRelease;
	protected InputHandler inputHandler;
	protected Logger logger;

	protected InputListeners() {
		inputHandler = InputHandler.create();
		this.runnersOnPress = new HashMap<>();
		this.runnersOnUiquePress = new HashMap<>();
		this.runnersOnRelease = new HashMap<>();
		this.logger = Logger.getLogger("GenericInputListeners");
	}
	
	/**
	 * Update inputs states received every frame 
	 */
	protected abstract void updateUserInputs();

	/**
	 * Add a function to be executed while input is pressed.
	 * @param glfwInput input to listen
	 * @param function to execute
	 */
	public void addRunnerOnPress(Integer glfwInput, Runnable function) {
		add(this.runnersOnPress, glfwInput, function);
	}
	
	/**
	 * Remove function binding from On pressed event.
	 * @param glfwInput GLFW.* input to listen
	 * @param function to execute
	 */
	public void removeRunnerOnPress(Integer glfwInput, Runnable function) {
		remove(this.runnersOnPress, glfwInput, function);
	}
	
	/**
	 * Add a function to be executed just once when input is pressed.
	 * @param glfwInput GLFW.* input to listen
	 * @param function to execute
	 */
	public void addRunnerOnUniquePress(Integer glfwInput, Runnable function) {
		add(this.runnersOnUiquePress, glfwInput, function);
	}
	
	/**
	 * Remove function binding from On unique pressed event.
	 * @param glfwInput GLFW.* input to listen
	 * @param function to execute
	 */
	public void removeRunnerOnUniquePress(Integer glfwInput, Runnable function) {
		remove(this.runnersOnUiquePress, glfwInput, function);
	}

	/**
	 * Add a function to be executed just once when input is released.
	 * @param glfwInput GLFW.* input to listen
	 * @param function to execute
	 */
	public void addRunnerOnRelease(Integer glfwInput, Runnable function) {
		add(this.runnersOnRelease, glfwInput, function);
	}
	
	/**
	 * Remove function binding from On release event.
	 * @param glfwInput GLFW.* input to listen
	 * @param function to execute
	 */
	public void removeRunnerOnRelease(Integer glfwInput, Runnable function) {
		remove(this.runnersOnRelease, glfwInput, function);
	}
	
	private void remove(HashMap<Integer, Set<Runnable>> runners, Integer glfwInput, Runnable function) {
		Set<Runnable> runnables = runners.get(glfwInput);
		if(runnables == null) {
			return;
		}
		boolean removed = runnables.remove(function);
	}
	
	private void add(HashMap<Integer, Set<Runnable>> runners, Integer glfwInput, Runnable function) {
		Set<Runnable> runnables = new HashSet<>();
		runnables.add(function);
		Set<Runnable> runnable = runners.putIfAbsent(glfwInput, runnables);
		if (runnable != null) {
			boolean added = runners.get(glfwInput).add(function);
		}
	}

	public void clear() {
		this.runnersOnPress.clear();
		this.runnersOnUiquePress.clear();
		this.runnersOnRelease.clear();
	}

	protected void execute() {
		for (Integer glfwInput : this.runnersOnPress.keySet()) {
			if (inputHandler.activateOnPress(glfwInput)) {
				run(this.runnersOnPress.getOrDefault(glfwInput, new HashSet<>()));
			}
		}

		for (Integer glfwInput : this.runnersOnUiquePress.keySet()) {
			if (inputHandler.activateOnPressOneTime(glfwInput)) {
				run(this.runnersOnUiquePress.getOrDefault(glfwInput, new HashSet<>()));
			}
		}
		
		for (Integer glfwInput : this.runnersOnRelease.keySet()) {
			if (inputHandler.activateOnReleaseOnce(glfwInput)) {
				run(this.runnersOnRelease.getOrDefault(glfwInput, new HashSet<>()));
			}
		}
	}

	private void run(Set<Runnable> runnables) {
		for (Runnable runner : runnables) {
			runner.run();
		}
	}
}
