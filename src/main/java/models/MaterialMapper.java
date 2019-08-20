package models;

import org.lwjglx.util.vector.Vector4f;

import com.mokiat.data.front.parser.MTLMaterial;

public class MaterialMapper {
	private MTLMaterial material;
	private Vector4f color;
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

	public void setColor(Vector4f colorVector) {
		color = colorVector;
	}

	public Vector4f getColor() {
		return color;
	}
	
}
