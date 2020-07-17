package models.data;

import java.util.ArrayList;
import java.util.List;

import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.MTLMaterial;

public class BlendedMaterialLibraryBuilder {
	
	private BlendedMaterialLibraryBuilder() {
		//hidden
	}
	
	public static class BlendedMaterialLibraryInBuild {
		private List<MTLMaterial> materials = new ArrayList<>();
		
		public BlendedMaterialLibraryInBuild addTexture(String textureName) {
			MTLMaterial material = new MTLMaterial(textureName);
			material.setDiffuseTexture(textureName);
			materials.add(material);
			return this;
		}
		
		public MaterialLibrary addBlendTexturesAndBuild(String textureName) {
			MTLMaterial material = new MTLMaterial(textureName);
			material.setDiffuseTexture(textureName);
			materials.add(material);
			MTLLibrary library = new MTLLibrary();
			library.getMaterials().addAll(materials);
			return new MaterialLibraryImpl(library);
		}
	}

	public static BlendedMaterialLibraryInBuild create() {
		return new BlendedMaterialLibraryInBuild();
	}
}
