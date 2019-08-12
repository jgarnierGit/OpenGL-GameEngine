package renderEngine;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.opengl.GL;

import org.lwjgl.system.MemoryUtil;

/**
 * This class contains all the methods needed to set-up, maintain, and close a LWJGL display.
 * 
 * @author Karl
 *
 */
public class DisplayManager {

	public static final int WIDTH = 1280; //for debug
	public static final int HEIGHT = 720;//for debug
	private static final String TITLE = "Our First Display";
	public static long WINDOW_ID;
	
	private static double lastFrameTime;
	private static float delta;
	
	
	/**
	 * Creates a display window on which we can render our game. The dimensions
	 * of the window are determined by setting the display mode. By using
	 * "glViewport" we tell OpenGL which part of the window we want to render
	 * our game onto. We indicated that we want to use the entire window.
	 */
	public static void createDisplay() {
		
		//set fullscreen
		glfwInit();
		//GLFWVidMode monitor = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); 
		
		// Configure the GLFW window
		//fullscreen tip : use glfwCreateWindow(monitor.width(),monitor.height(),TITLE, glfwGetPrimaryMonitor(),MemoryUtil.NULL)
		WINDOW_ID = glfwCreateWindow(WIDTH, HEIGHT, TITLE, MemoryUtil.NULL,MemoryUtil.NULL);
		if (WINDOW_ID == MemoryUtil.NULL) {
			throw new IllegalStateException("GLFW window creation failed");
		}
		glfwMakeContextCurrent(WINDOW_ID); // Links the OpenGL context of the window to the current thread (GLFW_NO_CURRENT_CONTEXT error)
		glfwSwapInterval(1); // Enable VSync, which effective caps the frame-rate of the application to 60 frames-per-second
		glfwShowWindow(WINDOW_ID);

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(WINDOW_ID, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});
		// Make the OpenGL context current
		glfwMakeContextCurrent(WINDOW_ID);
		glfwShowWindow(WINDOW_ID);
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		lastFrameTime = getCurrentTime();
		GL.createCapabilities();

	}

	/**
	 * This method is used to update the display at the end of every frame. When
	 * we have set up a rendering process this method will display whatever
	 * we've been rendering onto the screen. The "sync" method is used here to
	 * cap the frame rate. Without this the computer would just try to run the
	 * game as fast as it possibly can, doing more work than it needs to.
	 */
	public static void updateDisplay() {
		// Swaps the front and back framebuffers, this is a very technical process which you don't necessarily
		//need to understand. You can simply see this method as updating the window contents.
		glfwPollEvents();
		glfwSwapBuffers(WINDOW_ID); // swap the color buffers
		double currentFrameTime = getCurrentTime();
		delta = (float) (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
		// replace old Display.update();
		// Poll for window events. The key callback above will only be
		// invoked during this call.

	}
	
	public static float getFrameTimeSeconds(){
		return delta;
	}
	/**
	 * This closes the window when the game is closed.
	 */
	public static void closeDisplay() {
		// It's important to release the resources when the program has finished to prevent dreadful memory leaks
		glfwDestroyWindow(WINDOW_ID);
		// Destroys all remaining windows and cursors (LWJGL JavaDoc)
		glfwTerminate();
	}

	public static boolean isRunning() {
		return !glfwWindowShouldClose(WINDOW_ID);
	}
	
	/**
	 * Timer has changed between lwjgl 2 and 3, now and because java is much stronger on time consistency , we can use direct function.
	 * @return
	 */
	private static double getCurrentTime() {
		return glfwGetTime() * 1000;
	}

}
