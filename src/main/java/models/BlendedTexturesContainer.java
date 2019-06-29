package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import org.newdawn.slick.opengl.TextureLoader;

public class BlendedTexturesContainer implements TextureContainer{
	private Optional<ArrayList<TextureData>> textures;
	
	public static class MixedTexturesBuilder {
		public Optional<ArrayList<TextureData>> textures = Optional.of(new ArrayList<>());
		
		public MixedTexturesBuilder addTexture(String textureName) {	
			File file = Paths.get(BlendedTexturesContainer.resourceTexturePath.toString(), textureName).toFile();
			registerTextureId(file);
			return this;
		}
		
		public BlendedTexturesContainer addBlendTexturesAndBuild(String textureName) {
			File file = Paths.get(BlendedTexturesContainer.resourceTexturePath.toString(), textureName).toFile();
			registerTextureId(file);
			if(textures.get().isEmpty()) {
				textures = Optional.empty();
			}
			return new BlendedTexturesContainer(textures);
		}
		
		private void registerTextureId(File file) {
			try {
				textures.get().add(new TextureData(TextureLoader.getTexture("PNG", new FileInputStream(file)).getTextureID()));
			} catch (IOException e) {
				System.err.println("Texture "+ file.getPath() +" "+ file.getName() +" not found");
			}
		}
	}

	private BlendedTexturesContainer(Optional<ArrayList<TextureData>> textures2) {
		this.textures = textures2;
	}

	public static MixedTexturesBuilder create() {
		return new MixedTexturesBuilder();
	}

	@Override
	public Optional<ArrayList<TextureData>> getTextures() {
		return textures;
	}
	


}
