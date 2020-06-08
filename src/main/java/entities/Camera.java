package entities;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjglx.util.vector.Vector3f;

import inputListeners.InputListeners;
import inputListeners.InputInteractable;
import inputListeners.UserInputHandler;
import modelsLibrary.Terrain;

//poute 
public class Camera extends InputInteractable {

	private float distanceFromPlayer = 70;
	private float angleAroundPlayer;

	private Vector3f position;
	private float pitch;
	private float yaw;
	private float roll;

	private Entity player;

	public Camera(InputListeners inputListener) {
		super(inputListener);
		position = new Vector3f(0, 10, 50);
		pitch = 20;
		yaw = 0;
		roll = 0;
	}

	public Camera(InputListeners inputListener, Entity player) {
		this(inputListener);
		this.player = player;
	}

	@Override
	public void bindInputHanlder() {
		inputListener.addRunnerOnPress(GLFW_MOUSE_BUTTON_MIDDLE, () -> calculatePitch());
		inputListener.addRunnerOnPress(GLFW_MOUSE_BUTTON_MIDDLE, () -> calculateAngleAroundPlayer());

	}

	public void attachToEntity(Entity entity) {
		player = entity;
	}

	public Vector3f getPosition() {
		return position;
	}

	/**
	 * get the pitch angle. Pitch angle is the angle that makes "yes" head movement.
	 * 
	 * @return
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * get the yaw angle. Yaw angle is the angle that makes "No" head movement.
	 * 
	 * @return
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * get the Roll angle. Roll angle is the angle that makes the "Meh" head
	 * movement.
	 * 
	 * @return
	 */
	public float getRoll() {
		return roll;
	}

	public void updateYaw(float angle) {
		yaw += angle;
	}

	public void updateRoll(float angle) {
		roll += angle;
	}

	public void updatePitch(float angle) {
		pitch += angle;
	}

	public void updatePosition(Vector3f position) {
		this.position = position;
	}

	private void calculateCameraPosition(Terrain terrain, float horizontalDist, float verticalDist) {
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDist * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDist * Math.cos(Math.toRadians(theta)));
		position.x = player.getPositions().x - offsetX;
		position.z = player.getPositions().z - offsetZ;
		float y = player.getPositions().y + verticalDist;
		position.y = terrain.getHeight(position.x, position.z) + 1 > y ? terrain.getHeight(position.x, position.z) + 1
				: y;
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {
		UserInputHandler userInput = this.inputListener.getUserInputHandler();
		float zoomLevel = (float) userInput.getScrollValue() * 0.5f;
		distanceFromPlayer -= zoomLevel;
	}

	private void calculatePitch() {

			
			UserInputHandler userInput = inputListener.getUserInputHandler();
			float ypos = userInput.getMouseDeltaY();
			pitch -= -ypos * 0.05f;
			this.logger.log(Level.INFO, "calculatePitch y"+ pitch);
	}

	private void calculateAngleAroundPlayer() {
		UserInputHandler userInput = inputListener.getUserInputHandler();
		float xpos = userInput.getMouseDeltaX();
		//angleAroundPlayer -= xpos * 0.5f; use it if entity is linked.
		yaw += xpos * 0.05f;
		this.logger.log(Level.INFO, "calculateAngleAroundPlayer x"+ yaw);
	}

	/**
	 * Do not use Keyboard library, (lwjgl 2 compatible). use GLFW instead Tip :
	 * english keyboard.
	 * 
	 * @param terrain
	 */
	public void move(Terrain terrain) {
		calculateAngleAroundPlayer();
		calculatePitch();

		calculateZoom();
		float horizontalDist = calculateHorizontalDistance();
		float verticalDist = calculateVerticalDistance();
		calculateCameraPosition(terrain, horizontalDist, verticalDist);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
	}
}
