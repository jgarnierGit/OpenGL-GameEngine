package models.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector4f;

import utils.GeomUtils;

/**
 * Can be either a list of colors or a texture with coordinates mapping.
 * VBOContent must be ordered in indices order.
 * 
 * @author chezmoi
 *
 */
public class MaterialContent {
	private static final List<Float> DEFAULT_UV_MAPPING = Arrays.asList(-1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f);
	private VBOContent materialCoontent;
	private MaterialType type;
	private List<String> url;
	private int numberOfRowsMaterialIndex;

	public VBOContent getContent() {
		return this.materialCoontent;
	}

	private MaterialContent(VBOContent materialContent, MaterialType type, List<String> url) {
		this.materialCoontent = materialContent;
		this.type = type;
		this.url = url;
		this.numberOfRowsMaterialIndex = 1;
	}

	public static MaterialContent createImageContent(int shaderInputIndex, List<Vector2f> textures, String url) {
		VBOContent materialCoordinates = VBOContent.create2f(shaderInputIndex, textures);
		return new MaterialContent(materialCoordinates, MaterialType.IMAGE, Arrays.asList(url));
	}

	public static MaterialContent createColorContent(int shaderInputIndex, List<Vector4f> colors) {
		VBOContent materialCoordinates = VBOContent.create4f(shaderInputIndex, colors);
		return new MaterialContent(materialCoordinates, MaterialType.IMAGE, new ArrayList<>());
	}

	public static MaterialContent createEmpty(int shaderInputIndex, int dimension) {
		VBOContent material = VBOContent.createEmpty(shaderInputIndex, dimension);
		return new MaterialContent(material, MaterialType.NONE, new ArrayList<>());
	}

	public MaterialType getType() {
		return this.type;
	}

	public List<String> getUrl() {
		return this.url;
	}

	public static MaterialContent copy(MaterialContent material) {
		List<Float> textureContent = new ArrayList<>(material.getContent().getContent());
		VBOContent textureVBO = null;
		if(material.getContent().getDimension() == 2) {
			textureVBO =  VBOContent.create2f(material.getContent().getShaderInputIndex(), GeomUtils.createVector2fList(textureContent));
		}
		else if(material.getContent().getDimension() == 4) {
			textureVBO =  VBOContent.create4f(material.getContent().getShaderInputIndex(), GeomUtils.createVector4fList(textureContent));
		}
		else {
			throw new IllegalArgumentException("unsupported format :"+ material.getContent().getDimension());
		}
		return new MaterialContent(textureVBO, material.type, material.getUrl());
	}
	
	public void setMaterialAsImage(int shaderInputIndex, List<String> materialsUrl, int numberOfRows) {
		this.materialCoontent = VBOContent.create2f(shaderInputIndex, GeomUtils.createVector2fList(MaterialContent.DEFAULT_UV_MAPPING));
		this.url = new ArrayList<>(materialsUrl);
		this.numberOfRowsMaterialIndex = numberOfRows;
		this.type = MaterialType.IMAGE;
	}
	
	public int getNumberOfRows() {
		return this.numberOfRowsMaterialIndex;
	}

	public void setUrls(List<String> materialsUrl) {
		this.url = new ArrayList<>(materialsUrl);
	}
}
