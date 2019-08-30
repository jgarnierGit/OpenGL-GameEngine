package models;

import java.util.ArrayList;
import java.util.List;

import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.MTLMaterial;

public class BlendedMaterialLibraryBuilder {
	
	public static class MixedTexturesBuilder {
		public List<MTLMaterial> materials = new ArrayList<MTLMaterial>();
		
		public MixedTexturesBuilder addTexture(String textureName) {
			MTLMaterial material = new MTLMaterial(textureName);
			material.setDiffuseTexture(textureName);
			materials.add(material);
			return this;
		}
		
		public MTLLibrary addBlendTexturesAndBuild(String textureName) {
			MTLMaterial material = new MTLMaterial(textureName);
			material.setDiffuseTexture(textureName);
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
