package shaderManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import renderEngine.Loader.VBOIndex;
import toolbox.Maths;


public class StaticShader extends ShaderProgram {
	
	private static final Path VERTEX_FILE= Paths.get("OpenGL-Game","src", "shaderManager", "vertexShader.txt");
	private static final Path FRAGMENT_FILE= Paths.get("OpenGL-Game","src", "shaderManager", "fragmentShader.txt");
	private int location_transformationMatrix;
	private int projectionMatrix;
	private int location_viewMatrix;
	private int location_lightColor;
	private int location_lightPosition;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColour;
	
	public StaticShader() {
		super(VERTEX_FILE,FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		// binds attribute "position" of VertexShader in index 0 of vao
		super.bindAttribute(VBOIndex.POSITION_INDEX, "position");
		super.bindAttribute(VBOIndex.TEXTURE_INDEX, "textureCoords");
		super.bindAttribute(VBOIndex.NORMAL_INDEX, "normals");
	}

	@Override
	protected void getAllUniformLocation() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_lightColor = super.getUniformLocation("lightColor");
		location_lightPosition = super.getUniformLocation("lightPosition");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColour = super.getUniformLocation("skyColour");
	}
	
	public void loadSkyColour(float r, float g, float b) {
		super.loadVector(location_skyColour, new Vector3f(r,g,b));
	}
	
	public void loadFakeLighting(boolean useFakeLighting) {
		super.loadBoolean(location_useFakeLighting, useFakeLighting);
	}
	
	public void loadShineVariables(float shineDamper, float reflectivity) {
		super.loadFloat(location_shineDamper, shineDamper);
		super.loadFloat(location_reflectivity, reflectivity);
		
	}
	
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(projectionMatrix,projection);
	}

	public void loadLightColor(Light light) {
		super.loadVector(location_lightPosition, light.getPosition());
		super.loadVector(location_lightColor, light.getColour());
	}
}
