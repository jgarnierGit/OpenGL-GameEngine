package models.data;

import java.util.ArrayList;
import java.util.List;

import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.MTLMaterial;

public class SimpleMaterialLibrary implements IMaterialLibrary{
	private MTLLibrary mtlLibrary;

	private SimpleMaterialLibrary(MTLLibrary mtlLibrary) {
		this.mtlLibrary = mtlLibrary;
	}
	
	public static SimpleMaterialLibrary create(String materialPath) {
		MTLLibrary mtlLibrary = new MTLLibrary();
		MTLMaterial material = new MTLMaterial(materialPath);
		material.setDiffuseTexture(materialPath);
		List<MTLMaterial> materials = new ArrayList<>();
		materials.add(material);
		mtlLibrary.getMaterials().addAll(materials);
		return new SimpleMaterialLibrary(mtlLibrary);
	}

	@Override
	public MTLLibrary getMaterialLibrary() {
		return mtlLibrary;
	}
}
