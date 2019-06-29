package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class SimpleTextureContainer implements TextureContainer {
	private Optional<ArrayList<TextureData>> textures;
	
	public static class SimpleTextureBuilderEmpty{
		private Optional<Integer> textureID;
		
		public SimpleTextureBuilderFulfilled setTexture(ArrayList<Integer> textureIds) {
			
			if(textureIds.isEmpty()) {
				textureID = Optional.empty();
			}
			else {
				textureID = Optional.of(textureIds.get(0));
			}
			return new SimpleTextureBuilderFulfilled(textureID);
		}
	}
	public static class SimpleTextureBuilderFulfilled{
		private Optional<Integer> textureID;
		private SimpleTextureBuilderFulfilled(Optional<Integer> textureID) {
			this.textureID = textureID;
		}
		public SimpleTextureContainer build() {
			return new SimpleTextureContainer(textureID);
		}
	}
	private SimpleTextureContainer(Optional<Integer> textureID) {
		if(textureID.isPresent()) {
			TextureData textureContainer = new TextureData(textureID.get());
			this.textures = Optional.of(new ArrayList<>(Arrays.asList(textureContainer)));
		}
		else {
			this.textures = Optional.empty();
		}
	}
	
	public static SimpleTextureBuilderEmpty create() {
		return new SimpleTextureBuilderEmpty();
	}
	
	@Override
	public Optional<ArrayList<TextureData>> getTextures() {
		return textures;
	}

}
