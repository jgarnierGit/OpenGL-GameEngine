package entities;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjglx.util.vector.Vector3f;

import modelsLibrary.Terrain;

//poute 
public class Camera {

	private float distanceFromPlayer = 10;
	private float angleAroundPlayer;

	private Vector3f position = new Vector3f(0,40,-20);
	private float pitch = 20;
	private float yaw = 50;
	private float roll = 0;

	private Player player;


	public Camera(Player player) {
		this.player = player;
	}

	public Vector3f getPosition() {
		return position;
	}

	/**
	 * get the pitch angle.
	 * Pitch angle is the angle that makes "yes" head movement.
	 * @return
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * get the yaw angle.
	 * Yaw angle is the angle that makes "No" head movement.
	 * @return
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * get the Roll angle.
	 * Roll angle is the angle that makes the "Meh" head movement.
	 * @return
	 */
	public float getRoll() {
		return roll;
	}

	private void calculateCameraPosition(Terrain terrain, float horizontalDist, float verticalDist) {
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDist * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDist * Math.cos(Math.toRadians(theta)));
		position.x = player.getPositions().x - offsetX;
		position.z = player.getPositions().z - offsetZ;
		float y = player.getPositions().y + verticalDist;
		position.y = terrain.getHeight(position.x, position.z) +1 > y ? terrain.getHeight(position.x, position.z) + 1 : y;
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {
			float zoomLevel = (float) UserInputHandler.getScrollValue() * 0.5f;
			distanceFromPlayer -= zoomLevel;
	}

	private void calculatePitch() {
		if(UserInputHandler.activateOnPress(GLFW_MOUSE_BUTTON_MIDDLE)){
			float ypos =  UserInputHandler.getMouseDeltaY();
			pitch -= -ypos* 0.5f;
		}
	}

	private void calculateAngleAroundPlayer() {
		if (UserInputHandler.activateOnPress(GLFW_MOUSE_BUTTON_MIDDLE)) {
			float xpos = UserInputHandler.getMouseDeltaX();
			angleAroundPlayer -= xpos *0.5f;
		}
	}

	/**
	 * Do not use Keyboard library, (lwjgl 2 compatible). use GLFW instead
	 * Tip : english keyboard.
	 * @param terrain 
	 */
	public void move(Terrain terrain) {
		calculateAngleAroundPlayer();
		calculatePitch();


		calculateZoom();
		float horizontalDist = calculateHorizontalDistance();
		float verticalDist = calculateVerticalDistance();
		calculateCameraPosition(terrain,horizontalDist,verticalDist);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
	}
}
