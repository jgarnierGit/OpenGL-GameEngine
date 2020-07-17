package models.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

public class VBOContent {
	private int dimension;
	private List<Float> content;
	private int shaderInputIndex;

	private VBOContent(int shaderInputIndex, int dimension, List<Float> content) {
		this.dimension = dimension;
		this.content = content;
		this.shaderInputIndex = shaderInputIndex;
	}

	public static VBOContent createEmpty(int shaderInputIndex, int dimension) {
		List<Float> content = new ArrayList<>();
		return new VBOContent(shaderInputIndex, dimension, content);
	}

	public static VBOContent create3f(int shaderInputIndex, List<Vector3f> content) {
		List<Float> contentFloat = new ArrayList<>();
		for(Vector3f v : content) {
			contentFloat.add(v.x);
			contentFloat.add(v.y);
			contentFloat.add(v.z);
		}
		return new VBOContent(shaderInputIndex, 3, contentFloat);
	}
	
	public static VBOContent create2f(int shaderInputIndex, List<Vector2f> content) {
		List<Float> contentFloat = new ArrayList<>();
		for(Vector2f v : content) {
			contentFloat.add(v.x);
			contentFloat.add(v.y);
		}
		return new VBOContent(shaderInputIndex, 2, contentFloat);
	}
	

	public static VBOContent create4f(int shaderInputIndex, List<Vector4f> content) {
		List<Float> contentFloat = new ArrayList<>();
		for(Vector4f v : content) {
			contentFloat.add(v.x);
			contentFloat.add(v.y);
			contentFloat.add(v.z);
			contentFloat.add(v.w);
		}
		return new VBOContent(shaderInputIndex, 4, contentFloat);
	}

	public int getDimension() {
		return dimension;
	}

	public List<Float> getContent() {
		return content;
	}

	public int getShaderInputIndex() {
		return shaderInputIndex;
	}

	//TODO setContent4f + 2f + 3f
	public void setContent(List<Float> clone) {
		if (clone.size() % dimension != 0) {
			throw new IllegalArgumentException(
					"expected a content of dimension " + dimension + ", got " + clone.size());
		}
		this.content = clone;
	}

	public float[] getContentAsPrimitiveArray() {
		return ArrayUtils.toPrimitive(content.toArray(new Float[content.size()]));
	}
}
