package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

import org.lwjglx.util.vector.Vector4f;

public class SimpleTextureContainer implements TextureContainer {
	private ArrayList<TextureData> textures;
	
	public static class SimpleTextureBuilderEmpty{
		public SimpleTextureBuilderFulfilled setTexture(HashMap<String, ArrayList<Integer>> textureIds) {
			SimpleTextureBuilderFulfilled.get().addTextureIDs(textureIds);
			return SimpleTextureBuilderFulfilled.get();
		}

		public SimpleTextureBuilderFulfilled setColors(HashMap<String, ArrayList<Vector4f>> diffuseColorMTL) {
			SimpleTextureBuilderFulfilled.get().addColorIDs(diffuseColorMTL);
			return SimpleTextureBuilderFulfilled.get();
		}
	}
	public static class SimpleTextureBuilderFulfilled{
		private Optional<HashMap<String, ArrayList<Integer>>> textureIDs;
		private Optional<HashMap<String, ArrayList<Vector4f>>> colorIDs;
		private static SimpleTextureBuilderFulfilled singleton;
		
		private static SimpleTextureBuilderFulfilled get() {
			if(singleton == null) {
				singleton = new SimpleTextureBuilderFulfilled();
			}
			return singleton;
		}
		public SimpleTextureBuilderFulfilled addColorIDs(HashMap<String, ArrayList<Vector4f>> colorIDs) {
			if(colorIDs.isEmpty()) {
				this.colorIDs =  Optional.empty();
			}
			else {
				this.colorIDs = Optional.of(colorIDs);
			}
			return this;
		}
		private SimpleTextureBuilderFulfilled() {
		}
		
		public SimpleTextureBuilderFulfilled addTextureIDs(HashMap<String, ArrayList<Integer>> textureIDs) {
			if(textureIDs.isEmpty()) {
				this.textureIDs = Optional.empty();
			}
			else {
				this.textureIDs = Optional.of(textureIDs);
			}
			return this;
		}
		public SimpleTextureContainer build() {
			return new SimpleTextureContainer(textureIDs,colorIDs);
		}
	}
	
	@SuppressWarnings("unchecked")
	private SimpleTextureContainer(Optional<HashMap<String, ArrayList<Integer>>> texturesID,Optional<HashMap<String, ArrayList<Vector4f>>> colorIDs) {
		ArrayList<TextureData> textureContainerList = new ArrayList<>();
		if(texturesID.isPresent()) {
			for(Entry e : texturesID.get().entrySet()) {
				for(Integer textID : (ArrayList<Integer>) e.getValue()) {
					textureContainerList.add(new TextureData(textID, (String) e.getKey()));
				}
			}
			this.textures = textureContainerList;
		}
		if(colorIDs.isPresent()) {
			for(Entry e : colorIDs.get().entrySet()) {
				for(Vector4f color : (ArrayList<Vector4f>) e.getValue()) {
					textureContainerList.add(new TextureData(color.getX(),color.getY(), color.getZ(), color.getW(), (String) e.getKey()));
				}
			}
		}
	}
	
	public static SimpleTextureBuilderEmpty create() {
		return new SimpleTextureBuilderEmpty();
	}
	
	@Override
	public ArrayList<TextureData> getTextures() {
		return textures;
	}

}
