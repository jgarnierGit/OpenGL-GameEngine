package shaderManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.function.Function;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

public abstract class ShaderProgram implements IShader{
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	private Logger logger;

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	/**
	 * TODO only only constructor can survive...
	 * @param vertexFile
	 * @param fragmentFile
	 * @throws IOException
	 */
	public ShaderProgram(String vertexFile, String fragmentFile) throws IOException {
		logger = Logger.getLogger("ShaderProgram");
		vertexShaderID = loadShader(ShaderProgram.class::getResourceAsStream, vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(ShaderProgram.class::getResourceAsStream, fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocation();
	}

	public ShaderProgram(Function<String, InputStream> consumer, String vertexFile, String fragmentFile)
			throws IOException {
		logger = Logger.getLogger("ShaderProgram");
		vertexShaderID = loadShader(consumer, vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(consumer, fragmentFile, GL20.GL_FRAGMENT_SHADER);
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
	
	protected void loadVector(int location, Vector2f vector) {
		GL20.glUniform2f(location, vector.getX(), vector.getY());
	}

	public void loadVector(int location, Vector4f vector) {
		GL20.glUniform4f(location, vector.getX(), vector.getY(), vector.getZ(), vector.getW());
	}

	protected void loadBoolean(int location, boolean value) {
		float toLoad = 0;
		if (value) {
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}

	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer);
		((Buffer) matrixBuffer).flip();
		GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	}
	
	@Override
	public void start() {
		GL20.glUseProgram(programID);
	}
	
	@Override
	public void stop() {
		GL20.glUseProgram(0);
	}

	@Override
	public void cleanUp() {
		stop();
		//FIXME unbind many times same shaderID
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}

	protected abstract void bindAttributes();

	protected void bindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}

	private int loadShader(Function<String, InputStream> consumer, String pathFile, int type) throws IOException {
		StringBuilder shaderSource = new StringBuilder();
		try (InputStream fileStream = consumer.apply(pathFile)) {
			InputStreamReader fileReader = new InputStreamReader(fileStream);
			BufferedReader bufferedFileReader = new BufferedReader(fileReader);
			String line = bufferedFileReader.readLine();
			while (line != null) {
				shaderSource.append(line).append("\n");
				line = bufferedFileReader.readLine();
			}
			bufferedFileReader.close();
			fileReader.close();
		} catch (NullPointerException e) {
			logger.severe("File not found " + pathFile);
		}

		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader " + pathFile);
			System.exit(-1);
		}
		return shaderID;
	}
}
