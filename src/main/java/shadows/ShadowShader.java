package shadows;

import java.io.IOException;

import org.lwjglx.util.vector.Matrix4f;

import shaderManager.ShaderProgram;

public class ShadowShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "shadowVertexShader.txt";
	private static final String FRAGMENT_FILE = "shadowFragmentShader.txt";
	
	private int location_mvpMatrix;

	protected ShadowShader() throws IOException {
		super(ShadowShader.class::getResourceAsStream,VERTEX_FILE, FRAGMENT_FILE);
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

	@Override
	public int getColorShaderIndex() {
		return -1;
	}

	@Override
	public int getTextureShaderIndex() {
		return -1;
	}

	@Override
	public int getPositionShaderIndex() {
		return 0;
	}

}
