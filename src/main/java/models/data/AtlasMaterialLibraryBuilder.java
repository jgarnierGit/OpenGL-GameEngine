package models.data;

import java.util.ArrayList;
import java.util.List;

import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.MTLMaterial;

public class AtlasMaterialLibraryBuilder {
	
	private AtlasMaterialLibraryBuilder() {
		//hidden
	}
	
	public static MaterialLibrary create(int numberOfRows, String path) {
		MTLLibrary mtlLibrary = new MTLLibrary();
		MTLMaterial material = new MTLMaterial(path);
		material.setDiffuseTexture(path);
		List<MTLMaterial> materials = new ArrayList<>();
		materials.add(material);
		mtlLibrary.getMaterials().addAll(materials);
		MaterialLibraryImpl matLib =  new MaterialLibraryImpl(mtlLibrary);
		matLib.setNumberOfRows(numberOfRows);
		return matLib;
	}
}
