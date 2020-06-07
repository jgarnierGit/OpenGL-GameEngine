package shaderManager;

import java.io.IOException;

import org.lwjglx.util.vector.Matrix4f;

public class GuiShader extends ShaderProgram{
	   
    private static final String VERTEX_FILE = "guiVertexShader.txt";
    private static final String FRAGMENT_FILE = "guiFragmentShader.txt";

    private int location_transformationMatrix;
    
	public GuiShader() throws IOException {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	 public void loadTransformation(Matrix4f matrix){
	        super.loadMatrix(location_transformationMatrix, matrix);
	    }
	 
	@Override
	protected void getAllUniformLocation() {
		 location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
