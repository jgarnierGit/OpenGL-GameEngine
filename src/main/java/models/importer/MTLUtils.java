package models.importer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.mokiat.data.front.parser.MTLLibrary;

import models.importer.mokiatMapper.MaterialMapper;

public class MTLUtils {
	private ArrayList<MaterialMapper> materialMappers;
	private HashSet<String> texturesList;
	// TODO delete four below params.
	private boolean useFakeLighting = false;
	private boolean hasTransparency = false;
	private float reflectivity = 0;
	/**
	 * overide specularExponent of material if needed.
	 */
	private float specularExponent = 0;

	public MTLUtils(MTLLibrary mtlLibrary) {
		materialMappers = new ArrayList<>();
		texturesList = new HashSet<>(); // TODO may be put this param in MaterialMapper.
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public float getSpecularExponent() {
		return this.specularExponent;
	}

	public float getReflectivity() {
		return this.reflectivity;
	}

	/**
	 * manual parameter since not found corresponding value in mtl.
	 * 
	 * @param value
	 */
	public void setReflectivity(float value) {
		this.reflectivity = value;
	}

	public void setHasTransparency(boolean value) {
		this.hasTransparency = value;
	}

	public void setUseFakeLighting(boolean value) {
		this.useFakeLighting = value;
	}

	public boolean isHasTransparency() {
		return hasTransparency;
	}

	public void setSpecularExponent(float value) {
		this.specularExponent = value;
	}

	public List<MaterialMapper> getMaterials() {
		return materialMappers;
	}

	public static MTLUtils createEmpty() {
		MTLLibrary mtlLibrary = new MTLLibrary();
		return new MTLUtils(mtlLibrary);
	}
}
