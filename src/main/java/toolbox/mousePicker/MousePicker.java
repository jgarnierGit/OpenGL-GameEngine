package toolbox.mousePicker;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.Camera;
import inputListeners.MouseInputListener;
import inputListeners.UserInputHandler;
import renderEngine.DisplayManager;
import toolbox.Maths;
import toolbox.mousePicker.MouseBehaviour.IMouseBehaviour;

public class MousePicker {
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	private Logger logger;
	private List<IMouseBehaviour> mouseBehaviours;
	private MouseInputListener mouseInputListener;

	public MousePicker(Camera cam, Matrix4f projection, MouseInputListener mouseInputListener) {
		this.camera = cam;
		this.projectionMatrix = projection;
		this.viewMatrix = Maths.createViewMatrix(cam);
		this.logger = Logger.getLogger("MousePicker");
		this.mouseBehaviours = new ArrayList<>();
		this.mouseInputListener = mouseInputListener;
	}

	public void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		// currentRay has coordinate near world(0,0,0) : has to be used as a delta to
		// apply to camera position.
		Vector3f currentRay = calculateMouseRay();
		this.mouseInputListener.addRunnerOnUniquePress(GLFW_MOUSE_BUTTON_LEFT, () -> log());

		for (IMouseBehaviour behaviour : mouseBehaviours) {
			behaviour.process(currentRay);
		}
	}

	private void log() {
		UserInputHandler inputHandler = this.mouseInputListener.getUserInputHandler();
		float mouseX = inputHandler.getMouseXpos();
		float mouseY = inputHandler.getMouseYpos();
		System.out.println("ViewPort Space [" + mouseX + ", " + mouseY + "]");
		Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);
		System.out.println("Normalized device Space [" + normalizedCoords.x + ", " + normalizedCoords.y + "]");
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f);
		System.out.println("Homogeneous clip Space [" + clipCoords.x + ", " + clipCoords.y + ", " + clipCoords.z + ", "
				+ clipCoords.w + "]");
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		System.out.println(
				"Eye Space [" + eyeCoords.x + ", " + eyeCoords.y + ", " + eyeCoords.z + ", " + eyeCoords.w + "]");
		Vector3f worldCoords = toWorldCoords(eyeCoords);
		System.out.println("World Space [" + worldCoords.x + ", " + worldCoords.y + ", " + worldCoords.z + "]");
		System.out.println("-----");
	}

	public void addMouseBehaviour(IMouseBehaviour mouseBehaviour) {
		this.mouseBehaviours.add(mouseBehaviour);
	}

	private Vector3f calculateMouseRay() {
		UserInputHandler inputHandler = this.mouseInputListener.getUserInputHandler();
		float mouseX = inputHandler.getMouseXpos();
		float mouseY = inputHandler.getMouseYpos();
		Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f); // pointing into the screen
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		return toWorldCoords(eyeCoords);
	}

	/**
	 * Origin of cursor Point becomes the world(0,0,0)
	 * 
	 * @param eyeCoords
	 * @return
	 */
	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();// just want to be a direction
		return mouseRay;
	}

	/**
	 * 
	 * @param clipCoords
	 * @return vec4 with x and y set by mouse coordinate conversion, -1 as z stands
	 *         for "forward" and 0 as w stands for "not a point a.k.a no depth".
	 */
	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private Vector2f getNormalizedDeviceCoords(float mouseX, float mouseY) {
		float x = (2f * mouseX) / DisplayManager.WIDTH - 1f;
		float y = 1f - (2f * mouseY) / DisplayManager.HEIGHT;
		return new Vector2f(x, y);
	}
}
