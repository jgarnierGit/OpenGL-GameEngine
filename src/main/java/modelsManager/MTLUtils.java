package modelsManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Vector4f;
import org.newdawn.slick.opengl.TextureLoader;

import com.mokiat.data.front.parser.MTLColor;
import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.MTLMaterial;

public class MTLUtils {
	private ArrayList<MaterialMapper> materialMappers;
	private HashSet<String> texturesList;
	private List<Integer> texturesIndexes;
	// TODO delete four below params.
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
		texturesList = new HashSet<>(); // TODO may be put those 2 params in MaterialMapper.
		texturesIndexes = new ArrayList<>();
		for (MTLMaterial mat : mtlLibrary.getMaterials()) {
			MaterialMapper materialMapper = new MaterialMapper(mat);
			if (materialMapper.getType() == MaterialType.IMAGE) {
				loadTextureInMemory(mat);
			} else {
				MTLColor color = mat.getDiffuseColor();
				Vector4f colorVector = new Vector4f(color.r, color.g, color.b, mat.getDissolve());
				materialMapper.setColor(colorVector);
			}
			materialMappers.add(materialMapper);
		}

		// TODO is this still meaningful
		isUsingImage = materialMappers.stream().filter(matMapper -> {
			return matMapper.getType() == MaterialType.IMAGE;
		}).count() > 0;
	}

	private void loadTextureInMemory(MTLMaterial mat) {
		if (texturesList.add(mat.getDiffuseTexture())) {
			Path path = Paths.get(mat.getDiffuseTexture());

			try (InputStream image = MTLUtils.class.getClassLoader().getResourceAsStream("2D/" + path.getFileName())) {
				int textId = TextureLoader.getTexture("png", image).getTextureID();
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
				texturesIndexes.add(Integer.valueOf(textId));
			} catch (IOException | NullPointerException e) {
				System.err.println("[" + mat.getName() + "] File not found " + mat.getDiffuseTexture()
						+ " specified in MTL file. ");
			}
		}
	}

	/**
	 * TODO FIXME change list of integer for a list of TextureId containing
	 * [TextureName, GL_ID], to know if a material is valid.
	 * 
	 * @param materialName
	 * @return
	 */
	public boolean isMaterialValid(String materialName) {
		return !texturesIndexes.isEmpty(); // TODO maybe distinct indexes by material.
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

	public MaterialMapper getMaterial(String materialName) {
		for (MaterialMapper mapper : materialMappers) {
			if (mapper.getMaterial().getName().contentEquals(materialName)) {
				return mapper;
			}
		}
		return null;
	}

	public static MTLUtils createEmpty() {
		MTLLibrary mtlLibrary = new MTLLibrary();
		return new MTLUtils(mtlLibrary);
	}
}
