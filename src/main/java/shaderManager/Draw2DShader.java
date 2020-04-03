package shaderManager;

import java.io.IOException;

import org.lwjglx.util.vector.Matrix4f;

import entities.Camera;
import modelsLibrary.Ray;
import renderEngine.Loader.VBOIndex;
import toolbox.Maths;

public class Draw2DShader extends ShaderProgram {
	private static final String VERTEX_FILE= "draw2DVertexShader.txt";
	private static final String FRAGMENT_FILE= "draw2DFragmentShader.txt";
	
	public Draw2DShader() throws IOException {
		super(VERTEX_FILE,FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(VBOIndex.POSITION_INDEX, "position");
	}

	@Override
	protected void getAllUniformLocation() {
		//empty
	}
}
