package models;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.lwjglx.util.vector.Vector4f;
import org.newdawn.slick.opengl.TextureLoader;

import com.mokiat.data.front.parser.MTLColor;
import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.MTLMaterial;

public class MTLUtils {
	private ArrayList<MaterialMapper> materialMappers;
	private HashSet<String> texturesList;
	private List<Integer> texturesIndexes;
	private boolean isUsingImage;
	private boolean useFakeLighting = false;
	private boolean hasTransparency = false;
	private float reflectivity = 0;
	/**
	 * overide specularExponent of material if needed.
	 */
	private float specularExponent = 0; 

	public MTLUtils(MTLLibrary mtlLibrary) {
		materialMappers = new ArrayList<>();
		texturesList = new HashSet<>(); //TODO may be put those 2 params in MaterialMapper.
		texturesIndexes = new ArrayList<>();
		mtlLibrary.getMaterials().forEach(mat -> {
			MaterialMapper materialMapper = new MaterialMapper(mat);
			if(materialMapper.getType() == MaterialType.IMAGE) {
				loadTextureInMemory(mat);
			}
			else {
				MTLColor color = mat.getDiffuseColor();
				Vector4f colorVector = new Vector4f(color.r,color.g,color.b,mat.getDissolve());
				materialMapper.setColor(colorVector);
			}
			materialMappers.add(materialMapper);
		});

		//TODO is this still meaningful
		isUsingImage = materialMappers.stream().filter(matMapper -> {
			return matMapper.getType() == MaterialType.IMAGE;
		}).count() > 0;
	}
	
	private void loadTextureInMemory(MTLMaterial mat) {
		if(texturesList.add(mat.getDiffuseTexture())) {
			try {
				int textId = TextureLoader.getTexture("png", new FileInputStream(mat.getDiffuseTexture())).getTextureID();
				texturesIndexes.add(Integer.valueOf(textId));
			} catch (IOException e) {
				System.err.println("["+ mat.getName() +"] File not found "+ mat.getDiffuseTexture() +" specified in MTL file. ");
			}
		}
	}

	/**
	 * TODO FIXME change list of integer for a list of TextureId containing [TextureName, GL_ID], to know if a material is valid.
	 * @param materialName
	 * @return
	 */
	public boolean isMaterialValid(String materialName) {
		return !texturesIndexes.isEmpty(); //TODO maybe distinct indexes by material.
	}

	/**
	 * 
	 * @return indexes created by gl allocation.
	 */
	public List<Integer> getTexturesIndexes() {
		return texturesIndexes;
	}

	public boolean isUsingImage() {
		return isUsingImage;
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

	public MaterialMapper getMaterial(String materialName) {
		for(MaterialMapper mapper: materialMappers) {
			if(mapper.getMaterial().getName().contentEquals(materialName)) {
				return mapper;
			}
		}
		return null;
	}
}
