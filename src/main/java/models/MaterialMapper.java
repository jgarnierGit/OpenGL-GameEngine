package models;

import com.mokiat.data.front.parser.MTLMaterial;

public class MaterialMapper {
	private MTLMaterial material;
	private MaterialType type;
	
	public MaterialMapper(MTLMaterial mat) {
		material = mat;
		if(material.getDiffuseTexture() != null) {
			type = MaterialType.IMAGE;
		}
		else {
			type = MaterialType.COLOR;
		}
	}

	public MTLMaterial getMaterial() {
		return material;
	}

	public MaterialType getType() {
		return type;
	}
}
