package modelsManager.bufferCreator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

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

	public static VBOContent create(int shaderInputIndex, int dimension, List<Float> content) {
		if (content.size() % dimension != 0) {
			throw new IllegalArgumentException(
					"expected a content of dimension " + dimension + ", got " + content.size());
		}
		return new VBOContent(shaderInputIndex, dimension, content);
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
