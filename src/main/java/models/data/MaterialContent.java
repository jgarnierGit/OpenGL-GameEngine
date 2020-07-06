package models.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	private VBOContent materialCoordinates;
	private MaterialType type;
	private Optional<String> url;

	public VBOContent getContent() {
		return this.materialCoordinates;
	}

	private MaterialContent(VBOContent materialCoordinates, MaterialType type, Optional<String> url) {
		this.materialCoordinates = materialCoordinates;
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
		return new MaterialContent(materialCoordinates, MaterialType.IMAGE, Optional.of(url));
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
		return new MaterialContent(materialCoordinates, MaterialType.IMAGE, Optional.empty());
	}

	public static MaterialContent createEmpty(int shaderInputIndex, int dimension) {
		VBOContent material = VBOContent.createEmpty(shaderInputIndex, dimension);
		return new MaterialContent(material, MaterialType.NONE, Optional.empty());
	}

	public MaterialType getType() {
		return this.type;
	}

	public Optional<String> getUrl() {
		return this.url;
	}
}
