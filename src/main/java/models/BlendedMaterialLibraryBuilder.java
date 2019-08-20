package models;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.MTLMaterial;

public class BlendedMaterialLibraryBuilder {
	
	public static class MixedTexturesBuilder {
		public List<MTLMaterial> materials = new ArrayList<MTLMaterial>();
		
		public MixedTexturesBuilder addTexture(String path, String textureName) {
			MTLMaterial material = new MTLMaterial(textureName);
			material.setDiffuseTexture(Paths.get(path, textureName).toString());
			materials.add(material);
			return this;
		}
		
		public MTLLibrary addBlendTexturesAndBuild(String path, String textureName) {
			MTLMaterial material = new MTLMaterial(textureName);
			material.setDiffuseTexture(Paths.get(path, textureName).toString());
			materials.add(material);
			MTLLibrary library = new MTLLibrary();
			library.getMaterials().addAll(materials);
			return library;
		}
	}

	public static MixedTexturesBuilder create() {
		return new MixedTexturesBuilder();
	}
}
