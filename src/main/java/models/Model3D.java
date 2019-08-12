package models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.mokiat.data.front.error.WFException;
import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.MTLMaterial;
import com.mokiat.data.front.parser.OBJModel;

import renderEngine.Loader;

public abstract class Model3D {
	private int vaoID;
	private OBJUtils objUtils;
	private MTLUtils mtlUtils;
	
	public Model3D(OBJModel model, MTLLibrary materialsLibrary, Loader loader, boolean isImported) throws WFException, FileNotFoundException, IOException {
		Objects.requireNonNull(loader);
		objUtils = new OBJUtils(model,isImported);
		mtlUtils = new MTLUtils(materialsLibrary);
		vaoID = loader.loadModelToVAO(objUtils,mtlUtils);
	}

	/**
	 * @return The ID of the VAO which contains the data about all the geometry
	 *         of this model.
	 */
	public int getVaoID() {
		return vaoID;
	}
	
	public OBJModel getObjModel() {
		return objUtils.getObjModel();
	}

	public List<MTLMaterial> getMaterials() {
		return mtlUtils.getMaterials();
	}

/**
 * manual parameter since not found corresponding value in mtl.
 * @param value
 */
	public void setReflectivity(float value) {
		mtlUtils.setReflectivity(value);
	}
	
	public void setHasTransparency(boolean value) {
		mtlUtils.setHasTransparency(value);
	}
	
	public void setUseFakeLighting(boolean value) {
		mtlUtils.setUseFakeLighting(value);
	}

	public void setSpecularExponent(float value) {
		mtlUtils.setSpecularExponent(value);
	}

	public OBJUtils getObjUtils() {
		return objUtils;
	}

	public MTLUtils  getMtlUtils() {
		return mtlUtils;
	}
}
