package engineTester;

import static org.lwjgl.glfw.GLFW.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjglx.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.Imported3DModelContainer;
import models.Model3D;
import models.imports.Cube;
import models.imports.Grass;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;

/**
 * This class contains the main method and is used to test the engine.
 * 
 * @author Karl
 *
 */
public class MainGameLoop {

	// The GLFW error callback: this tells GLFW what to do if things go wrong
	private static GLFWErrorCallback errorCallback;


	/**
	 * Loads up the position data for two triangles (which together make a quad)
	 * into a VAO. This VAO is then rendered to the screen every frame.
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// Set the error handling code: all GLFW errors will be printed to the system error stream (just like println)
		errorCallback = GLFWErrorCallback.createPrint(System.err).set();
		glfwSetErrorCallback(errorCallback);

		// initialize GLFW and store the result (pass or fail)
		if (!glfwInit()) {
			throw new IllegalStateException("GLFW initialization failed");
		}

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		Model3D cube = new Cube(loader);
		Model3D grass = new Grass(loader);

		// Cube semi ko
		//plane // OK

		//Entity entity = new Entity(model, new Vector3f(0,0,-2),0,0,0,1);
		Light sun = new Light(new Vector3f(0,0,1), new Vector3f(1,1,1));
		
		Model3D terrain = new Terrain(0,0,loader);
		Model3D terrain2 = new Terrain(1,0,loader);
		
		Camera camera = new Camera();
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		
		ArrayList<Entity> grasses = new ArrayList<>();
		ArrayList<Entity> cubes = new ArrayList<>();
		Random random = new Random();
		// last optim may have been lost ?
		for(int i=0; i < 4000; i++) {
			float x = random.nextFloat() * 100 - 50;
			float y = random.nextFloat() * 100 - 50;
			float z = random.nextFloat() *- 300;
			cubes.add(new Entity(cube, new Vector3f(x,y,z), random.nextFloat() * 180, random.nextFloat() * 180, 0, 1f));
		}
		
		for(int i=0;i<200; i++) {
			float x = random.nextFloat() * 100 - 50;
			float y = -4f;
			float z = random.nextFloat() *- 300;
			float zRot = random.nextFloat() * 180;
			grasses.add(new Entity(grass, new Vector3f(x,y,z), -90, 0, zRot, 1f));
			//grasses.add(new Entity(grass, new Vector3f(x,y,z), -90, 0, zRot+180, 1f));
			grasses.add(new Entity(grass, new Vector3f(x,y,z), -90, 0,zRot + 90, 1f));
			//grasses.add(new Entity(grass, new Vector3f(x,y,z), -90, 0, zRot+270, 1f));
		}
		MasterRenderer masterRenderer = new MasterRenderer();
		while (DisplayManager.isRunning()) {
			// game logic
			//entity.increasePosition(0, 0, -0.01f);
			camera.move();
			for(Entity entityCube : cubes) {
				entityCube.increaseRotation(0.3f, 0.3f, 0.6f);
				masterRenderer.processEntity(entityCube);
			}
			for(Entity entityGrass : grasses) {
				masterRenderer.processEntity(entityGrass);
			}
			masterRenderer.processTerrain(terrain);
			masterRenderer.processTerrain(terrain2);
			masterRenderer.render(sun, camera);
			DisplayManager.updateDisplay();
		}
		masterRenderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}
