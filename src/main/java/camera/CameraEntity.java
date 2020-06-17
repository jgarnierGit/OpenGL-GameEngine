package camera;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;

import renderEngine.GameBehavior;

public class CameraEntity {
	private float pitch;
	private float yaw;
	private float roll;
	private Vector3f position;
	private Matrix4f viewMatrix;
	private GameBehavior cameraBehavior;
	
	public CameraEntity(Vector3f position, int pitch, int yaw, int roll) {
		this.position = position;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
		this.viewMatrix = new Matrix4f();
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
	
	/**
	 * Rotate and then translate an identity matrix in order to create View Matrix.
	 * apply inverse camera translation to each object.
	 * w row = [-TranslM.x, -TranslM.y, -TranslM.z, 1]
	 * w col = [0, 0, 0, 1]
	 * @param camera
	 * @return [IdMatrix] * [RotMatrix] * [TranslMatrix]
	 */
	public void updateViewMatrix() {
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1,0,0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0,1,0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(roll), new Vector3f(0,0,1), viewMatrix, viewMatrix);
		Vector3f cameraPos =  position;
		// set negative camera position which will be applied to each objects.
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
	}
	
	public Matrix4f getViewMatrix() {
		return this.viewMatrix;
	}
	
}
