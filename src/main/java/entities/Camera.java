package entities;

import org.lwjglx.util.vector.Vector3f;

public class Camera {
	private float pitch;
	private float yaw;
	private float roll;
	private Vector3f position;
	
	public Camera(Vector3f position, int pitch, int yaw, int roll) {
		this.position = position;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	/**
	 * Get the pitch angle. Pitch angle is the angle that makes "yes" head movement.
	 * 
	 * @return pitch degree angle 
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * Get the yaw angle. Yaw angle is the angle that makes "No" head movement.
	 * 
	 * @return yaw degree angle 
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * Get the roll angle. Roll angle is the angle that makes the "Meh" head
	 * movement.
	 * 
	 * @return roll degree angle 
	 */
	public float getRoll() {
		return roll;
	}
	
	/**
	 * Get camera world position
	 * @return vector3f worldPosition
	 */
	public Vector3f getPosition() {
		return position;
	}
	
	/**
	 * @param angle degree
	 */
	public void setYaw(float angle) {
		yaw = angle;
	}

	/**
	 * @param angle degree
	 */
	public void setRoll(float angle) {
		roll = angle;
	}
	
	/**
	 * @param angle degree
	 */
	public void setPitch(float angle) {
		pitch = angle;
	}

	public void setPosition(Vector3f vector3f) {
		this.position = vector3f;
	}
}
