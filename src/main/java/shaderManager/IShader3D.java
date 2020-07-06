package shaderManager;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector4f;

public interface IShader3D extends IShader {

	public void loadTransformationMatrix(Matrix4f transformationMatrix);

	public void loadProjectionMatrix(Matrix4f projection);

	public void loadViewMatrix(Matrix4f viewMatrix);
	
	public void loadClipPlane(Vector4f plane);
	
	public void setUseImage(boolean useImage);
}
