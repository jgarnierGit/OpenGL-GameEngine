package models;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import com.mokiat.data.front.error.WFException;
import com.mokiat.data.front.parser.IMTLParser;
import com.mokiat.data.front.parser.IOBJParser;
import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.MTLParser;
import com.mokiat.data.front.parser.OBJModel;
import com.mokiat.data.front.parser.OBJParser;

import renderEngine.Loader;

public abstract class Model3D {
	private int vaoID;
	static final Path resourcePath = Paths.get("./", "src", "main", "resources", "3D");
	private OBJModel objModel;
	protected MTLLibrary mtlLibrary;
	private boolean useFakeLighting = false;
	private boolean hasTransparency = false;
	private float reflectivity = 0;
	/**
	 * overide specularExponent of material if needed.
	 */
	private float specularExponent = 0; 
	
	protected Model3D(String objFile, String mtlFile, Loader loader) throws WFException, FileNotFoundException, IOException {
		Objects.requireNonNull(loader);
		IOBJParser objParser = new OBJParser();
		IMTLParser mtlParser = new MTLParser();
		
		this.objModel = objParser.parse(new FileInputStream(Paths.get(resourcePath.toString(), objFile).toFile()));
		this.mtlLibrary =  mtlParser.parse(new FileInputStream(Paths.get(resourcePath.toString(), mtlFile).toFile()));
		//TODO mtlParser doesn't load texture to openGL.
		vaoID = loader.loadModelToVAO(this);
	}

	/**
	 * @return The ID of the VAO which contains the data about all the geometry
	 *         of this model.
	 */
	public int getVaoID() {
		return vaoID;
	}
	
	public OBJModel getContainer3D() {
		return objModel;
	}

	public MTLLibrary getTextureContainer() {
		return mtlLibrary;
	}

/**
 * manual parameter since not found corresponding value in mtl.
 * @param value
 */
	public void setReflectivity(float value) {
		this.reflectivity = value;
	}
	
	public void setHasTransparency(boolean value) {
		this.hasTransparency = value;
	}
	
	public boolean isHasTransparency() {
		return hasTransparency;
	}
	
	public void setUseFakeLighting(boolean value) {
		this.useFakeLighting = value;
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void setSpecularExponent(float value) {
		this.specularExponent = value;
	}

	public float getSpecularExponent() {
		return this.specularExponent;
	}

	public float getReflectivity() {
		return this.reflectivity;
	}
}
