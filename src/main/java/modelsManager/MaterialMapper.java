package modelsManager;

import java.util.Optional;

import org.lwjglx.util.vector.Vector4f;

import com.mokiat.data.front.parser.MTLColor;
import com.mokiat.data.front.parser.MTLMaterial;

public class MaterialMapper {
	private MTLMaterial material;
	private Optional<Vector4f> color;
	private MaterialType type;
	private Optional<String> url;
	
	private MaterialMapper(MaterialType matType, MTLMaterial mat, Optional<Vector4f> colorVector, Optional<String> url) {
		type = matType;
		material = mat;
		color = colorVector;
		this.url = url;
	}
	
	public static MaterialMapper create(MTLMaterial mtlMat) {
		MaterialType type = MaterialType.COLOR;
		Optional<Vector4f> colorVector = Optional.empty();
		Optional<String> url = Optional.empty();
		if(mtlMat.getDiffuseTexture() != null) {
			type = MaterialType.IMAGE;
			url = Optional.of(mtlMat.getDiffuseTexture());
		}
		if (type == MaterialType.COLOR) {
			MTLColor color = mtlMat.getDiffuseColor();
			colorVector =  Optional.of(new Vector4f(color.r, color.g, color.b, mtlMat.getDissolve()));
		}
		return new MaterialMapper(type,mtlMat,colorVector,url);
	}

	public MTLMaterial getMaterial() {
		return material;
	}
	
	public Optional<String> getUrl(){
		return this.url;
	}

	public MaterialType getType() {
		return type;
	}

	public Optional<Vector4f> getColor() {
		return color;
	}
	
}
