package toolbox;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

public class Maths {

	/**
	 * 4*4 Matrix in Row Column Order: last line is for translation x,y,z.
	 * 
	 * @param translation
	 * @param scale
	 * @return
	 */
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector3f location, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(location, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static Vector3f getBarycenter(Vector3f origin, Vector3f uPoint, Vector3f vPoint) {
		return new Vector3f((origin.x + uPoint.x + vPoint.x) / 3, (origin.y + uPoint.y + vPoint.y) / 3,
				(origin.z + uPoint.z + vPoint.z) / 3);
	}

	/**
	 * Normalize vector from specified origin.
	 * 
	 * @param vector vector to normalize
	 * @param origin translate vector to origin to override default world origin
	 * @return normalized vector
	 */
	public static Vector3f normalizeFromOrigin(Vector3f vector, Vector3f origin) {
		Vector3f rayPositionOriginCam = Vector3f.sub(vector, origin, null);
		rayPositionOriginCam.normalise();
		return Vector3f.add(origin, rayPositionOriginCam, null);
	}

	/**
	 * 
	 * @param pitch degree angle; convention: counter-clockwise (0 = horizontal)
	 * @param yaw   degree angle; convention: anti-counter clockwise (0 = z-forward
	 *              / x-left)
	 * @return unitVector
	 */
	public static Vector3f degreesToCartesianUnitVector(float pitch, float yaw) {
		float cameraXDirection = (float) (Math.sin(-Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		float cameraZDirection = (float) (Math.cos(-Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		float cameraYDirection = (float) (Math.sin(Math.toRadians(pitch)));
		return new Vector3f(cameraXDirection, cameraYDirection, cameraZDirection);
	}

}
