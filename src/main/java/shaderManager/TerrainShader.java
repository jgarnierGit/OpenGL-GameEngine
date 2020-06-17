package shaderManager;

import java.io.IOException;
import java.util.List;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;

import camera.CameraEntity;
import entities.Light;
import renderEngine.Loader.VBOIndex;
import toolbox.Maths;

public class TerrainShader extends ShaderProgram{
	private static final int MAX_LIGHT = 4;
	private static final String VERTEX_FILE= "terrainVertexShader.txt";
	private static final String FRAGMENT_FILE= "terrainFragmentShader.txt";
	private int location_transformationMatrix;
	private int projectionMatrix;
	private int location_viewMatrix;
	private int location_lightColor[];
	private int location_lightPosition[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColour;
	private int location_backgroundTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;
	
	public TerrainShader() throws IOException {
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
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_skyColour = super.getUniformLocation("skyColour");
		location_backgroundTexture = super.getUniformLocation("backgroundTexture");
		location_rTexture = super.getUniformLocation("rTexture");
		location_gTexture = super.getUniformLocation("gTexture");
		location_bTexture = super.getUniformLocation("bTexture");
		location_blendMap = super.getUniformLocation("blendMap");
		location_lightColor =  new int[MAX_LIGHT];
		location_lightPosition = new int[MAX_LIGHT];
		location_attenuation = new int[MAX_LIGHT];
		for(int i=0; i<MAX_LIGHT; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition["+ i+"]");
			location_lightColor[i] = super.getUniformLocation("lightColor["+ i+"]");
			location_attenuation[i] = super.getUniformLocation("atenuation["+ i+"]");
		}
	}
	
	public void connectTextureUnits() {
		super.loadInt(location_backgroundTexture,0);
		super.loadInt(location_rTexture,1);
		super.loadInt(location_gTexture,2);
		super.loadInt(location_bTexture,3);
		super.loadInt(location_blendMap,4);
		
	}
	
	public void loadShineVariables(float shineDamper, float reflectivity) {
		super.loadFloat(location_shineDamper, shineDamper);
		super.loadFloat(location_reflectivity, reflectivity);
		
	}
	
	public void loadViewMatrix(CameraEntity camera) {
		Matrix4f viewMatrix = camera.getViewMatrix();
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	/**
	 * TODO builder might be a good idea here since loadProjectionMatrix is set only at object construction.
	 * @param projection
	 */
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(projectionMatrix,projection);
	}

	public void loadLightsColor(List<Light> lights) {
		for(int i=0; i< MAX_LIGHT; i++) {
			if(i< lights.size()) {
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColor[i], lights.get(i).getColour());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			}
			else {
				super.loadVector(location_lightPosition[i], new Vector3f(0,0,0));
				super.loadVector(location_lightColor[i], new Vector3f(0,0,0));
				super.loadVector(location_attenuation[i], new Vector3f(1,0,0));
			}
		}
	}

	public void loadSkyColour(float red, float green, float blue) {
		super.loadVector(location_skyColour, new Vector3f(red,green,blue));
		
	}
}
