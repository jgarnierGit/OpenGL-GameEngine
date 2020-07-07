package models.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector4f;

/**
 * Can be either a list of colors or a texture with coordinates mapping.
 * VBOContent must be ordered in indices order.
 * 
 * @author chezmoi
 *
 */
public class MaterialContent {
	private VBOContent materialCoontent;
	private MaterialType type;
	private List<String> url;

	public VBOContent getContent() {
		return this.materialCoontent;
	}

	private MaterialContent(VBOContent materialContent, MaterialType type, List<String> url) {
		this.materialCoontent = materialContent;
		this.type = type;
		this.url = url;
	}

	public static MaterialContent createImageContent(int shaderInputIndex, List<Vector2f> textures, String url) {
		ArrayList<Float> coords = new ArrayList<>();
		for (Vector2f texture : textures) {
			coords.add(texture.x);
			coords.add(texture.y);
		}
		VBOContent materialCoordinates = VBOContent.create(shaderInputIndex, 2, coords);
		return new MaterialContent(materialCoordinates, MaterialType.IMAGE, Arrays.asList(url));
	}

	public static MaterialContent createColorContent(int shaderInputIndex, List<Vector4f> colors) {
		ArrayList<Float> coords = new ArrayList<>();
		for (Vector4f color : colors) {
			coords.add(color.x);
			coords.add(color.y);
			coords.add(color.z);
			coords.add(color.w);
		}
		VBOContent materialCoordinates = VBOContent.create(shaderInputIndex, 4, coords);
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
		VBOContent textureVBO =  VBOContent.create(material.getContent().getShaderInputIndex(), material.getContent().getDimension(), textureContent);
		return new MaterialContent(textureVBO, material.type, material.getUrl());
	}

	public void setUrl(List<String> materialsUrl) {
		this.url = new ArrayList<>(materialsUrl);
	}
}
