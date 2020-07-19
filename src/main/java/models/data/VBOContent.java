package models.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import utils.GeomUtils;

public class VBOContent {
	private int dimension;
	private List<Float> content;
	private int shaderInputIndex;

	private VBOContent(int shaderInputIndex, int dimension, List<Float> content) {
		this.dimension = dimension;
		this.content = content;
		this.shaderInputIndex = shaderInputIndex;
	}

	public static VBOContent createEmpty(int shaderIndex) {
		List<Float> content = new ArrayList<>();
		return new VBOContent(shaderIndex, 0, content);
	}

	public static VBOContent create3f(int shaderInputIndex, List<Vector3f> content) {
		List<Float> contentFloat = new ArrayList<>();
		for (Vector3f v : content) {
			contentFloat.add(v.x);
			contentFloat.add(v.y);
			contentFloat.add(v.z);
		}
		return new VBOContent(shaderInputIndex, 3, contentFloat);
	}

	public static VBOContent create2f(int shaderInputIndex, List<Vector2f> content) {
		List<Float> contentFloat = new ArrayList<>();
		for (Vector2f v : content) {
			contentFloat.add(v.x);
			contentFloat.add(v.y);
		}
		return new VBOContent(shaderInputIndex, 2, contentFloat);
	}

	public static VBOContent create4f(int shaderInputIndex, List<Vector4f> content) {
		return new VBOContent(shaderInputIndex, 4, GeomUtils.createListFromVector4f(content));
	}

	public boolean isEmpty() {
		return shaderInputIndex == -1;
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

	public void setContent4f(List<Vector4f> newContent) {
		this.content = GeomUtils.createListFromVector4f(newContent);
		this.dimension = 4;
	}

	public void setContent3f(List<Vector3f> newContent) {
		this.content = GeomUtils.createListFromVector3f(newContent);
		this.dimension = 3;
	}

	public void setContent2f(List<Vector2f> newContent) {
		this.content = GeomUtils.createListFromVector2f(newContent);
		this.dimension = 2;
	}

	public float[] getContentAsPrimitiveArray() {
		return ArrayUtils.toPrimitive(content.toArray(new Float[content.size()]));
	}
}
