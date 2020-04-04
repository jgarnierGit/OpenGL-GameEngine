package toolbox.mousePicker;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.List;

import entities.UserInputHandler;

public class MouseInputListener {
	private List<Runnable> runners;
	
	public MouseInputListener() {
		this.runners = new ArrayList<>();
	}

	public void listen() {
		if (UserInputHandler.activateOnPressOneTime(GLFW_MOUSE_BUTTON_LEFT)) {
			for(Runnable runner : this.runners) {
				runner.run();
			}
		}
	}
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
