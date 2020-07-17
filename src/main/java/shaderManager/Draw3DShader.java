package shaderManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector4f;

import renderEngine.Loader.VBOIndex;

public class Draw3DShader extends ShaderProgram implements IShader3D {
	private static final String VERTEX_FILE = "rayVertexShader.txt";
	private static final String FRAGMENT_FILE = "rayFragmentShader.txt";
	public static final int COLOR_INDEX = 1;
	public static final int TEXTURE_INDEX = 2;
	public static final int NORMAL_INDEX = 3;
	private int location_transformationMatrix;
	private int projectionMatrix;
	private int location_viewMatrix;
	private int location_planeClipping;
	private int location_useImage;
	private int locationNumberOfRows;
	private int locationOffset;

	private static Draw3DShader defaultDraw3DShader = null;

	private Draw3DShader(Function<String, InputStream> consumer, String vertexFile, String fragmentFile) throws IOException {
		super(consumer, vertexFile, fragmentFile);
	}
	
	private Draw3DShader(String vertexFile, String fragmentFile) throws IOException {
		super(vertexFile, fragmentFile);
	}

	public static Draw3DShader createDefault() throws IOException {
		if (defaultDraw3DShader == null) {
			defaultDraw3DShader = new Draw3DShader(VERTEX_FILE, FRAGMENT_FILE);
		}
		return defaultDraw3DShader;
	}

	public static Draw3DShader create(Function<String, InputStream> consumer, String vertexFile, String fragmentFile) throws IOException {
		Draw3DShader shader = new Draw3DShader(consumer, vertexFile, fragmentFile);
		return shader;
	}

	@Override
	protected void getAllUniformLocation() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_planeClipping = super.getUniformLocation("planeClipping");
		location_useImage = super.getUniformLocation("useImage");
		locationNumberOfRows = super.getUniformLocation("numberOfRows");
		locationOffset = super.getUniformLocation("offset");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(VBOIndex.POSITION_INDEX, "position");
		super.bindAttribute(COLOR_INDEX, "color");
		super.bindAttribute(TEXTURE_INDEX, "textureCoords");
		super.bindAttribute(NORMAL_INDEX, "normals");
	}
	
	public void loadClipPlane(Vector4f plane) {
		super.loadVector(location_planeClipping, plane);
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
	
	public void setUseImage(boolean useImage) {
		super.loadBoolean(location_useImage, useImage);
	}
	
	public void loadNumberOfRows(int numberOfRows) {
		super.loadFloat(locationNumberOfRows, numberOfRows);
	}
	
	public void loadOffset(int x, int y) {
		super.loadVector(locationOffset, new Vector2f(x,y));
	}

	@Override
	public int getColorShaderIndex() {
		return COLOR_INDEX;
	}

	@Override
	public int getTextureShaderIndex() {
		return TEXTURE_INDEX;
	}

	@Override
	public int getPositionShaderIndex() {
		return VBOIndex.POSITION_INDEX;
	}
	@Override
	public int getNormalShaderIndex() {
		return NORMAL_INDEX;
	}
}
