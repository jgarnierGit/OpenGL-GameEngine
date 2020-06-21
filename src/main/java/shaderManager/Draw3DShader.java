package shaderManager;

import java.io.IOException;

import org.lwjglx.util.vector.Matrix4f;

import renderEngine.Loader.VBOIndex;

public class Draw3DShader extends ShaderProgram {
	private static final String VERTEX_FILE = "rayVertexShader.txt";
	private static final String FRAGMENT_FILE = "rayFragmentShader.txt";
	public static final int COLOR_INDEX = 1;
	private int location_transformationMatrix;
	private int projectionMatrix;
	private int location_viewMatrix;

	private static Draw3DShader defaultDraw3DShader = null;

	private Draw3DShader(String vertexFile, String fragmentFile) throws IOException {
		super(vertexFile, fragmentFile);
	}

	public static Draw3DShader createDefault() throws IOException {
		if (defaultDraw3DShader == null) {
			defaultDraw3DShader = new Draw3DShader(VERTEX_FILE, FRAGMENT_FILE);
		}
		return defaultDraw3DShader;
	}

	public static Draw3DShader create(String vertexFile, String fragmentFile) throws IOException {
		Draw3DShader shader = new Draw3DShader(vertexFile, fragmentFile);
		return shader;
	}

	@Override
	protected void getAllUniformLocation() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(VBOIndex.POSITION_INDEX, "position");
		super.bindAttribute(COLOR_INDEX, "color");
	}

	public void loadTransformationMatrix(Matrix4f transformationMatrix) {
		super.loadMatrix(location_transformationMatrix, transformationMatrix);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(projectionMatrix, projection);
	}

	public void loadViewMatrix(Matrix4f viewMatrix) {
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
}
