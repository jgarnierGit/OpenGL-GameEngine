package shaderManager;

import java.io.IOException;

import renderEngine.Loader.VBOIndex;

public class Draw2DShader extends ShaderProgram {
	private static final String VERTEX_FILE= "draw2DVertexShader.txt";
	private static final String FRAGMENT_FILE= "draw2DFragmentShader.txt";
	public static final int COLOR_INDEX = 1;
	private static Draw2DShader defaultDraw2DShader = null;
	
	private Draw2DShader(String vertexFile, String fragmentFile) throws IOException {
		super(vertexFile,fragmentFile);
	}
	
	public static Draw2DShader createDefault() throws IOException {
		if (defaultDraw2DShader == null) {
			defaultDraw2DShader = new Draw2DShader(VERTEX_FILE, FRAGMENT_FILE);
		}
		return defaultDraw2DShader;
	}

	public static Draw2DShader create(String vertexFile, String fragmentFile) throws IOException {
		Draw2DShader shader = new Draw2DShader(vertexFile, fragmentFile);
		return shader;
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
