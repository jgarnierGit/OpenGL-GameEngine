package models;

import java.util.ArrayList;
import java.util.Optional;

import org.lwjglx.util.vector.Vector2f;

public class TextureConfig {
	private ArrayList<Vector2f> imageMapping;
	private ArrayList<String> colorLinksMTL;
	private Boolean usingImage;
	//TODO change logic to allow image + color (if needed)
	public TextureConfig(Optional<ArrayList<Vector2f>> textureIndices, Optional<ArrayList<String>> colors) {
		this.imageMapping = new ArrayList<>();
		this.colorLinksMTL = new ArrayList<>();
		if(colors.isPresent()) {
			this.usingImage = false;
			this.colorLinksMTL = colors.get();
		}
		if(textureIndices.isPresent()) {
			this.usingImage = true;
			this.imageMapping = textureIndices.get();
		}
	}
	
	public ArrayList<Vector2f> getImageMapping() {
		return imageMapping;
	}
	public ArrayList<String> getColorsLinks() {
		return colorLinksMTL;
	}
	public Boolean getUsingImage() {
		return usingImage;
	}

	
}
