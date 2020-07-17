package toolbox;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import renderEngine.DisplayManager;

public class CoordinatesSystemManager {
	private static final Matrix4f PROJECTION_MATRIX = createProjectionMatrix();

	private CoordinatesSystemManager() {
	}

	public static CoordinatesSystemManager create() {
		return new CoordinatesSystemManager();
	}

	public static Matrix4f getProjectionMatrix() {
		return PROJECTION_MATRIX;
	}

	private static Matrix4f createProjectionMatrix() {
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(DisplayManager.getFOV() / 2f))) * DisplayManager.ASPECT_RATIO);
		float x_scale = y_scale / DisplayManager.ASPECT_RATIO;
		float frustum_length = DisplayManager.getFarPlane() - DisplayManager.getNearPlane();

		Matrix4f projM = new Matrix4f();
		projM.m00 = x_scale;
		projM.m11 = y_scale;
		projM.m22 = -((DisplayManager.getFarPlane() + DisplayManager.getNearPlane()) / frustum_length);
		projM.m23 = -1;
		projM.m32 = -((2 * DisplayManager.getNearPlane() * DisplayManager.getFarPlane()) / frustum_length);
		projM.m33 = 0;
		return projM;
	}

	/**
	 * V'.x = V.x * XScale; (PM[10] = PM[20] = PM[30] = 0) V'.y = V.y * YScale;
	 * (PM[01] = PM[21] = PM[31] = 0) V'.z = V.z * -; (PM[02] = PM[12] = 0) V'.w =
	 * -V.z; (PM[03] = PM[13] = PM[33] = 0)
	 * 
	 * @param vector3f Homogeneous vector point (V[w]=1)
	 * @return [vector] * [projectionMatrix] (Row major column)
	 */
	public Vector4f objectToProjectionMatrix(Vector3f vector) {
		return Matrix4f.transform(this.PROJECTION_MATRIX, new Vector4f(vector.x, vector.y, vector.z, 1), null);
	}

	/**
	 * @param projectionCoords
	 * @return new Vector3f(x/w,y/w,z/w)
	 */
	public Vector3f objectToClipSpace(Vector4f projectionCoords) {
		return new Vector3f(projectionCoords.x / projectionCoords.w, projectionCoords.y / projectionCoords.w,
				projectionCoords.z / projectionCoords.w);
	}

	/**
	 * ViewMatrix as w[33] set as constant 1. Need a 1*4 Vector to apply translation
	 * of ViewMatrix. V[w] = 1 to detect entity that are backward of camera
	 * 
	 * @param viewMatrix
	 * @param worldPosition
	 * @return [x,y,z] rotated * translated homogeneous vector point
	 */
	public Vector3f objectToViewCoord(Matrix4f viewMatrix, Vector3f worldPosition) {
		Vector4f objectPos4f = new Vector4f(worldPosition.x, worldPosition.y, worldPosition.z, 1f);
		Vector4f objectWorld = Matrix4f.transform(viewMatrix, objectPos4f, null);
		return new Vector3f(objectWorld.x, objectWorld.y, objectWorld.z);
	}

	/**
	 * @param viewMatrix
	 * @param eyeCoords
	 * @return
	 */
	public Vector3f viewCoordToWorldCoord(Matrix4f viewMatrix, Vector3f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f worldCoord = Matrix4f.transform(invertedView, new Vector4f(eyeCoords.x, eyeCoords.y, -eyeCoords.z, 0),
				null);
		return new Vector3f(worldCoord.x, worldCoord.y, worldCoord.z);
	}

	/**
	 * We could have used a normalized Vector3f, but we can avoid a division.
	 * Instead we can just test if each coordinates are bounded into [-w;w]
	 * 
	 * @param position
	 * @return
	 */
	public boolean isInClipSpace(Vector4f position) {
		return position.x >= -position.w && position.x <= position.w && position.y >= -position.w
				&& position.y <= position.w && position.z >= -position.w && position.z <= position.w;
		// clipped z seems to be twice as it should but I can't change value here as z,
		// once projected is no more linear.
	}
}
