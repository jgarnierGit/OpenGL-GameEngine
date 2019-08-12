package models;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.newdawn.slick.opengl.TextureLoader;

import com.mokiat.data.front.parser.MTLColor;
import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.MTLMaterial;

public class MTLUtils {
	private MTLLibrary mtlLibrary;
	private HashSet<String> texturesList;
	private List<Integer> texturesIndexes;
	private ArrayList<Float> colors;
	private boolean isUsingImage;
	private boolean useFakeLighting = false;
	private boolean hasTransparency = false;
	private float reflectivity = 0;
	/**
	 * overide specularExponent of material if needed.
	 */
	private float specularExponent = 0; 

	public MTLUtils(MTLLibrary mtlLibrary) {
		this.mtlLibrary =  mtlLibrary;
		texturesList = new HashSet<>();
		texturesIndexes = new ArrayList<>();
		colors = new ArrayList<>();
		mtlLibrary.getMaterials().forEach(mat -> {
			if(mat.getDiffuseTexture() != null && texturesList.add(mat.getDiffuseTexture())) {
				int textId;
				try {
					textId = TextureLoader.getTexture("png", new FileInputStream(mat.getDiffuseTexture())).getTextureID();
					texturesIndexes.add(Integer.valueOf(textId));
				} catch (IOException e) {
					System.err.println("["+ mat.getName() +"] File not found "+ mat.getDiffuseTexture() +" specified in MTL file. ");
				}
			}
			if(mat.getDiffuseColor() != null) {
				MTLColor color = mat.getDiffuseColor();
				colors.add(color.r);
				colors.add(color.g);
				colors.add(color.b);
				colors.add(mat.getDissolve());
			}
		});

		isUsingImage = mtlLibrary.getMaterials().stream().filter(mtlMat -> {
			return mtlMat.getDiffuseTexture() != null || mtlMat.getDissolveTexture() != null
					|| mtlMat.getSpecularExponentTexture() != null
					|| mtlMat.getSpecularTexture() != null;
		}).count() > 0;
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
	
	public ArrayList<Float> getColors() {
		return colors;
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

	public List<MTLMaterial> getMaterials() {
		return mtlLibrary.getMaterials();
	}
}
