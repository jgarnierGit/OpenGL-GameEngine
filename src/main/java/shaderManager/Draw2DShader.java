package shaderManager;

import java.io.IOException;

import renderEngine.Loader.VBOIndex;

public class Draw2DShader extends ShaderProgram {
	private static final String VERTEX_FILE= "draw2DVertexShader.txt";
	private static final String FRAGMENT_FILE= "draw2DFragmentShader.txt";
	public static final int COLOR_INDEX = 1;
	
	public Draw2DShader() throws IOException {
		super(VERTEX_FILE,FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(VBOIndex.POSITION_INDEX, "position");
		super.bindAttribute(COLOR_INDEX, "color");
	}

	@Override
	protected void getAllUniformLocation() {
		//empty
	}
}
