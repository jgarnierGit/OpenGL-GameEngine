package inputListeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public abstract class InputListeners {
	protected HashMap<Integer, List<Runnable>> runnersOnPress;
	protected HashMap<Integer, List<Runnable>> runnersOnUiquePress;
	protected HashMap<Integer, List<Runnable>> runnersOnRelease;
	protected UserInputHandler userInputListener;
	protected Logger logger;

	protected InputListeners() {
		userInputListener = UserInputHandler.create();
		this.runnersOnPress = new HashMap<>();
		this.runnersOnUiquePress = new HashMap<>();
		this.runnersOnRelease = new HashMap<>();
		this.logger = Logger.getLogger("GenericInputListeners");
	}

	/**
	 * Add a function to be executed while input is pressed.
	 * @param function
	 */
	public void addRunnerOnPress(Integer glfwInput, Runnable function) {
		ArrayList<Runnable> runnables = new ArrayList<>();
		runnables.add(function);
		List<Runnable> runnable = this.runnersOnPress.putIfAbsent(glfwInput, runnables);
		if (runnable != null) {
			this.runnersOnPress.get(glfwInput).add(function);
		}
	}
	
	/**
	 * Add a function to be executed just once when input is pressed.
	 * @param function
	 */
	public void addRunnerOnUniquePress(Integer glfwInput, Runnable function) {
		ArrayList<Runnable> runnables = new ArrayList<>();
		runnables.add(function);
		List<Runnable> runnable = this.runnersOnUiquePress.putIfAbsent(glfwInput, runnables);
		if (runnable != null) {
			this.runnersOnUiquePress.get(glfwInput).add(function);
		}
	}
	
	/**
	 * Add a function to be executed just once when input is released.
	 * @param function
	 */
	public void addRunnerOnRelease(Integer glfwInput, Runnable function) {
		ArrayList<Runnable> runnables = new ArrayList<>();
		runnables.add(function);
		List<Runnable> runnable = this.runnersOnRelease.putIfAbsent(glfwInput, runnables);
		if (runnable != null) {
			this.runnersOnRelease.get(glfwInput).add(function);
		}
	}

	public void clear() {
		this.runnersOnPress.clear();
		this.runnersOnUiquePress.clear();
		this.runnersOnRelease.clear();
	}

	public UserInputHandler getUserInputHandler() {
		return this.userInputListener;
	}

	public void listen() {
		for (Integer glfwInput : this.runnersOnPress.keySet()) {
			if (userInputListener.activateOnPress(glfwInput)) {
				run(this.runnersOnPress.getOrDefault(glfwInput, new ArrayList<>()));
			}
		}

		for (Integer glfwInput : this.runnersOnUiquePress.keySet()) {
			if (userInputListener.activateOnPressOneTime(glfwInput)) {
				run(this.runnersOnUiquePress.getOrDefault(glfwInput, new ArrayList<>()));
			}
		}
		
		for (Integer glfwInput : this.runnersOnRelease.keySet()) {
			if (userInputListener.activateOnReleaseOnce(glfwInput)) {
				run(this.runnersOnRelease.getOrDefault(glfwInput, new ArrayList<>()));
			}
		}
	}

	private void run(List<Runnable> runnables) {
		for (Runnable runner : runnables) {
			runner.run();
		}
	}
}