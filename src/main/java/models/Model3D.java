package models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import com.mokiat.data.front.error.WFException;
import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.OBJModel;

import renderEngine.Loader;

public abstract class Model3D {
	private int vaoID;
	private ModelUtils modelUtils;
	
	public Model3D(ModelUtils model, Loader loader) throws WFException, FileNotFoundException, IOException {
		Objects.requireNonNull(loader);
		modelUtils = model;
		vaoID = loader.loadModelToVAO(modelUtils);
	}

	/**
	 * @return The ID of the VAO which contains the data about all the geometry
	 *         of this model.
	 */
	public int getVaoID() {
		return vaoID;
	}

/**
 * manual parameter since not found corresponding value in mtl.
 * @param value
 */
	public void setReflectivity(float value) {
		modelUtils.getMtlUtils().setReflectivity(value);
	}
	
	public void setHasTransparency(boolean value) {
		modelUtils.getMtlUtils().setHasTransparency(value);
	}
	
	public void setUseFakeLighting(boolean value) {
		modelUtils.getMtlUtils().setUseFakeLighting(value);
	}

	public void setSpecularExponent(float value) {
		modelUtils.getMtlUtils().setSpecularExponent(value);
	}

	public ModelUtils getObjUtils() {
		return modelUtils;
	}
}
