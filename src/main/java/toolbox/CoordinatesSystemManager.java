package toolbox;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

public class CoordinatesSystemManager {
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	
	public CoordinatesSystemManager(Matrix4f projection) {
		this.projectionMatrix = projection;
	}

	/**
	 * V'.x = V.x * XScale; (PM[10] = PM[20] = PM[30] = 0) 
	 * V'.y = V.y * YScale; (PM[01] = PM[21] = PM[31] = 0) 
	 * V'.z = V.z * -; (PM[02] = PM[12] = 0) 
	 * V'.w = -V.z; (PM[03] = PM[13] = PM[33] = 0)
	 * 
	 * @param vector3f Homogeneous vector point (V[w]=1)
	 * @return [vector] * [projectionMatrix] (Row major column)
	 */
	public Vector4f objectToProjectionMatrix(Vector3f vector) {
		return Matrix4f.transform(this.projectionMatrix,
				new Vector4f(vector.x, vector.y, vector.z, 1), null);
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
	 * @param worldPosition
	 * @return [x,y,z] rotated * translated homogeneous vector point
	 */
	public Vector3f objectToViewCoord(Vector3f worldPosition) {
		Vector4f objectPos4f = new Vector4f(worldPosition.x, worldPosition.y, worldPosition.z, 1f);
		Vector4f objectWorld = Matrix4f.transform(viewMatrix, objectPos4f, null);
		return new Vector3f(objectWorld.x, objectWorld.y, objectWorld.z);
	}
	
	/**
	 * @param eyeCoords
	 * @return
	 */
	public Vector3f viewCoordToWorldCoord(Vector3f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, new Vector4f(eyeCoords.x, eyeCoords.y, -eyeCoords.z, 0),
				null);
		return new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
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
		// clipped z seems to be twice as it should but I can't change value here as z, once projected is no more linear.
	}

	public void setViewMatrix(Matrix4f viewMatrix) {
		this.viewMatrix = viewMatrix;
	}
}
