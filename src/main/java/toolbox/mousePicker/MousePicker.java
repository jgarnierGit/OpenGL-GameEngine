package toolbox.mousePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.Camera;
import entities.UserInputHandler;
import renderEngine.DisplayManager;
import toolbox.Maths;
import toolbox.mousePicker.MouseBehaviour.IMouseBehaviour;

public class MousePicker {
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	private Logger logger;
	private List<IMouseBehaviour> mouseBehaviours;

	public MousePicker(Camera cam, Matrix4f projection) {
		this.camera = cam;
		this.projectionMatrix = projection;
		this.viewMatrix = Maths.createViewMatrix(cam);
		this.logger = Logger.getLogger("MousePicker");
		this.mouseBehaviours = new ArrayList<>();
	}

	public void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		Vector3f currentRay = calculateMouseRay();
		for(IMouseBehaviour behaviour: mouseBehaviours) {
			behaviour.process(currentRay);
		}
	}
	
	public void addMouseBehaviour(IMouseBehaviour mouseBehaviour) {
		this.mouseBehaviours.add(mouseBehaviour);
	}

	private Vector3f calculateMouseRay() {
		float mouseX = UserInputHandler.getMouseXpos();
		float mouseY = UserInputHandler.getMouseYpos();
		Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f); //pointing into the screen
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		return toWorldCoords(eyeCoords);
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();//just want to be a direction
		return mouseRay;
	}

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
