package shadows;

import java.io.IOException;

import org.lwjglx.util.vector.Matrix4f;

import shaderManager.ShaderProgram;

public class ShadowShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "src/shadows/shadowVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shadows/shadowFragmentShader.txt";
	
	private int location_mvpMatrix;

	protected ShadowShader() throws IOException {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	protected void loadMvpMatrix(Matrix4f mvpMatrix){
		super.loadMatrix(location_mvpMatrix, mvpMatrix);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "in_position");
	}

	@Override
	protected void getAllUniformLocation() {
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");
	}

}
