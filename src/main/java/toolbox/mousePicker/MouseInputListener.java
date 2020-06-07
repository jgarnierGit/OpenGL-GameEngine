package toolbox.mousePicker;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.List;

import entities.UserInputHandler;

/**
 * Methods to bind methods execution to "one time" press event.
 * Maybe also put continuous press event if many event need to be listened.
 * @author chezmoi
 *
 */
public class MouseInputListener extends UserInputHandler{
	private List<Runnable> runners;
	
	public MouseInputListener() {
		super();
		this.runners = new ArrayList<>();
	}

	public void listen() {
		if (UserInputHandler.activateOnPressOneTime(GLFW_MOUSE_BUTTON_LEFT)) {
			for(Runnable runner : this.runners) {
				runner.run();
			}
		}
	}
	/**
	 * Use Runnable when you have void arg & return.
	 * Use Predicate when you have a boolean return type with arguments
	 * Use Supplier when you have a return type
	 * Use Consumer when you have an argument
	 * Use Function when you have both an argument and a return value
	 * @param function
	 */
	public void addRunner(Runnable function) {
		this.runners.add(function);
	}
	
	public void clear() {
		this.runners.clear();
	}
/**
	public <T> void listen(T t, Consumer<T> function) {
		if (UserInputHandler.activateOnPressOneTime(GLFW_MOUSE_BUTTON_LEFT)) {
			function.accept(t);
		}
	}**/
}
