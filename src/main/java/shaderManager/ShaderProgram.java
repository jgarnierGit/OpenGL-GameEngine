package shaderManager;

import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.nio.file.Path;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;


public abstract class ShaderProgram {
	 private int programID;
	    private int vertexShaderID;
	    private int fragmentShaderID;
	    
	    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	     
	    public ShaderProgram(Path vertexFile,Path fragmentFile) throws FileNotFoundException{
	        vertexShaderID = loadShader(vertexFile,GL20.GL_VERTEX_SHADER);
	        fragmentShaderID = loadShader(fragmentFile,GL20.GL_FRAGMENT_SHADER);
	        programID = GL20.glCreateProgram();
	        GL20.glAttachShader(programID, vertexShaderID);
	        GL20.glAttachShader(programID, fragmentShaderID);
	        bindAttributes();
	        GL20.glLinkProgram(programID);
	        GL20.glValidateProgram(programID);
	        getAllUniformLocation();
	    }
	    
	    protected abstract void getAllUniformLocation();
	    
	    protected int getUniformLocation(String uniformName) {
	    	return GL20.glGetUniformLocation(programID, uniformName);
	    }
	    
	    protected void loadFloat(int location, float value) {
	    	GL20.glUniform1f(location, value);
	    }
	    
	    protected void loadInt(int location, int value) {
	    	GL20.glUniform1i(location, value);
	    }
	    
	    protected void loadVector(int location, Vector3f vector) {
	    	GL20.glUniform3f(location, vector.getX(), vector.getY(), vector.getZ());
	    }
	    
	    protected void loadBoolean(int location, boolean value) {
	    	float toLoad = 0;
	    	if(value) {
	    		toLoad = 1;
	    	}
	    	GL20.glUniform1f(location, toLoad);
	    }
	    
	    protected void loadMatrix(int location, Matrix4f matrix) {
	    	matrix.store(matrixBuffer);
	    	matrixBuffer.flip();
	    	GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	    }
	     
	    public void start(){
	        GL20.glUseProgram(programID);
	    }
	     
	    public void stop(){
	        GL20.glUseProgram(0);
	    }
	     
	    public void cleanUp(){
	        stop();
	        GL20.glDetachShader(programID, vertexShaderID);
	        GL20.glDetachShader(programID, fragmentShaderID);
	        GL20.glDeleteShader(vertexShaderID);
	        GL20.glDeleteShader(fragmentShaderID);
	        GL20.glDeleteProgram(programID);
	    }
	     
	    protected abstract void bindAttributes();
	     
	    protected void bindAttribute(int attribute, String variableName){
	        GL20.glBindAttribLocation(programID, attribute, variableName);
	    }
	     
	    private static int loadShader(Path pathFile, int type) throws FileNotFoundException{
	        StringBuilder shaderSource = new StringBuilder();
	        	Scanner file = new Scanner(pathFile.toFile());
	        	while(file.hasNextLine()) {
	        		String line = file.nextLine();
	        		shaderSource.append(line).append("\n");
	        	}
	        	file.close();
	        int shaderID = GL20.glCreateShader(type);
	        GL20.glShaderSource(shaderID, shaderSource);
	        GL20.glCompileShader(shaderID);
	        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS )== GL11.GL_FALSE){
	            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
	            System.err.println("Could not compile shader "+ pathFile.getFileName());
	            System.exit(-1);
	        }
	        return shaderID;
	    }
}
