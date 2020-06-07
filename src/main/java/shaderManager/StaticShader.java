package shaderManager;

import java.io.IOException;
import java.util.List;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import renderEngine.Loader.VBOIndex;
import toolbox.Maths;


public class StaticShader extends ShaderProgram {
	private static final int MAX_LIGHT = 4;
	
	private static final String VERTEX_FILE= "vertexShader.txt";
	private static final String FRAGMENT_FILE= "fragmentShader.txt";
	private int location_transformationMatrix;
	private int projectionMatrix;
	private int location_viewMatrix;
	private int location_lightColor[];
	private int location_lightPosition[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColour;
	private int location_useImage;
	private int location_isSelected;
	
	public StaticShader() throws IOException {
		super(VERTEX_FILE,FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		// binds attribute "position" of VertexShader in index 0 of vao
		super.bindAttribute(VBOIndex.POSITION_INDEX, "position");
		super.bindAttribute(VBOIndex.TEXTURE_INDEX, "textureCoords");
		super.bindAttribute(VBOIndex.NORMAL_INDEX, "normals");
		super.bindAttribute(VBOIndex.COLOR_INDEX, "colors");
	}

	@Override
	protected void getAllUniformLocation() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColour = super.getUniformLocation("skyColour");
		location_useImage = super.getUniformLocation("useImage");
		
		location_lightColor =  new int[MAX_LIGHT];
		location_lightPosition = new int[MAX_LIGHT];
		location_attenuation = new int[MAX_LIGHT];
		for(int i=0; i<MAX_LIGHT; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition["+ i+"]");
			location_lightColor[i] = super.getUniformLocation("lightColor["+ i+"]");
			location_attenuation[i] = super.getUniformLocation("atenuation["+ i+"]");
		}
		location_isSelected = super.getUniformLocation("isSelected");
	}
	
	public void loadSkyColour(float r, float g, float b) {
		super.loadVector(location_skyColour, new Vector3f(r,g,b));
	}
	
	public void loadFakeLighting(boolean useFakeLighting) {
		super.loadBoolean(location_useFakeLighting, useFakeLighting);
	}
	
	public void setUseImage(boolean useImage) {
		super.loadBoolean(location_useImage, useImage);
	}
	
	public void loadShineVariable(float shineDamper) {
		super.loadFloat(location_shineDamper, shineDamper);
	}
	
	public void loadReflectivityVariable(float reflectivity) {
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

	public void loadSelected(boolean selected) {
		super.loadBoolean(location_isSelected, selected);
	}
}
