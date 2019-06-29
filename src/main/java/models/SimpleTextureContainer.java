package models;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

public class SimpleTextureContainer implements TextureContainer {
	private ArrayList<TextureData> textures = new ArrayList<>(1);
	
	public static class SimpleTextureBuilderEmpty{
		
		public SimpleTextureBuilderFulfilled setTexture(int textureID) {
			return new SimpleTextureBuilderFulfilled(textureID);
		}
	}
	public static class SimpleTextureBuilderFulfilled{
		private int textureID;
		private SimpleTextureBuilderFulfilled(int textureID) {
			this.textureID = textureID;
		}
		public SimpleTextureContainer build() {
			return new SimpleTextureContainer(textureID);
		}
	}
	private SimpleTextureContainer(int textureID) {
		TextureData textureContainer = new TextureData(textureID);
		this.textures.add(textureContainer);
	}
	
	public static SimpleTextureBuilderEmpty create() {
		return new SimpleTextureBuilderEmpty();
	}
	
	@Override
	public ArrayList<TextureData> getTextures() {
		return textures;
	}

}
