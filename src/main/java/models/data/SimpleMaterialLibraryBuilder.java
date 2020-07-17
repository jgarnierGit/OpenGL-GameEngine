package models.data;

import java.util.ArrayList;
import java.util.List;

import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.MTLMaterial;

public class SimpleMaterialLibraryBuilder {
	
	private SimpleMaterialLibraryBuilder() {
		//hidden
	}
	
	public static MaterialLibrary create(String materialPath) {
		MTLLibrary mtlLibrary = new MTLLibrary();
		MTLMaterial material = new MTLMaterial(materialPath);
		material.setDiffuseTexture(materialPath);
		List<MTLMaterial> materials = new ArrayList<>();
		materials.add(material);
		mtlLibrary.getMaterials().addAll(materials);
		return new MaterialLibraryImpl(mtlLibrary);
	}
}
