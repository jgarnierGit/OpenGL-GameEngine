package toolbox;

import static org.lwjgl.glfw.GLFW.*;

import java.util.logging.Logger;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.Camera;
import entities.UserInputHandler;
import renderEngine.DisplayManager;

public class MousePicker {
private Vector3f currentRay;
private Matrix4f projectionMatrix;
private Matrix4f viewMatrix;
private Camera camera;
private Logger logger;

public MousePicker(Camera cam, Matrix4f projection) {
	this.camera = cam;
	this.projectionMatrix = projection;
	this.viewMatrix = Maths.createViewMatrix(cam);
	this.logger = Logger.getLogger("MousePicker");
}

public Vector3f getCurrentRay() {
	return currentRay;
	
}

public void update() {
	viewMatrix = Maths.createViewMatrix(camera);
	currentRay = calculateMouseRay();
	log();
}

private void log() {
	if(UserInputHandler.activateOnPressOneTime(GLFW_MOUSE_BUTTON_RIGHT)) {
			float mouseX = UserInputHandler.getMouseXpos();
			float mouseY = UserInputHandler.getMouseYpos();
			System.out.println("ViewPort Space ["+mouseX +", "+ mouseY +"]");
			Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);
			System.out.println("Normalized device Space ["+normalizedCoords.x +", "+ normalizedCoords.y +"]");
			Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f);
			System.out.println("Homogeneous clip Space ["+clipCoords.x +", "+ clipCoords.y +", "+ clipCoords.z +", "+ clipCoords.w +"]");
			Vector4f eyeCoords = toEyeCoords(clipCoords);
			System.out.println("Eye Space ["+eyeCoords.x +", "+ eyeCoords.y +", "+ eyeCoords.z +", "+ eyeCoords.w +"]");
			Vector3f worldCoords = toWorldCoords(eyeCoords);
			System.out.println("World Space ["+worldCoords.x +", "+ worldCoords.y +", "+ worldCoords.z +"]");
			System.out.println("-----");
	}
}

private Vector3f calculateMouseRay() {
	float mouseX = UserInputHandler.getMouseXpos();
	float mouseY = UserInputHandler.getMouseYpos();
	Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);
	Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f);
	Vector4f eyeCoords = toEyeCoords(clipCoords);
	return toWorldCoords(eyeCoords);
}

private Vector3f toWorldCoords(Vector4f eyeCoords) {
	Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
	Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
	Vector3f mouseRay = new Vector3f(rayWorld.x,rayWorld.y,rayWorld.z);
	mouseRay.normalise();
	return mouseRay;
}

private Vector4f toEyeCoords(Vector4f clipCoords) {
	Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
	Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
	return new Vector4f(eyeCoords.x, eyeCoords.y,-1f,0f);
}

private Vector2f getNormalizedDeviceCoords(float mouseX, float mouseY) {
	float x = (2f*mouseX) / DisplayManager.WIDTH  -1f;
	float y = (2f*mouseY) / DisplayManager.HEIGHT  -1f;
	return new Vector2f(x,y);
}
}
