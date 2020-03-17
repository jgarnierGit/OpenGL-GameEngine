package shaderManager;

import java.io.IOException;

import org.lwjglx.util.vector.Matrix4f;

import entities.Camera;
import modelsLibrary.Ray;
import renderEngine.Loader.VBOIndex;
import toolbox.Maths;

public class RayShader extends ShaderProgram {
	private static final String VERTEX_FILE= "rayVertexShader.txt";
	private static final String FRAGMENT_FILE= "rayFragmentShader.txt";
	private int location_transformationMatrix;
	private int projectionMatrix;
	private int location_modelViewMatrix;
	
	public RayShader() throws IOException {
		super(VERTEX_FILE,FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocation() {
		//location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_modelViewMatrix = super.getUniformLocation("modelViewMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(VBOIndex.POSITION_INDEX, "position");
	}

/**	public void loadTransformationMatrix(Matrix4f transformationMatrix) {
		super.loadMatrix(location_transformationMatrix, transformationMatrix);
	}**/
	
	/**public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}**/
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(projectionMatrix,projection);
	}

	public void loadModelViewMatrix(Matrix4f modelViewMatrix) {
		super.loadMatrix(location_modelViewMatrix, modelViewMatrix);
	}
}
