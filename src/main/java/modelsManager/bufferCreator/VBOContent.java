package modelsManager.bufferCreator;

public class VBOContent {
	private int dimension;
	private float[] content;
	private int shaderInputIndex;
	
	public VBOContent(int shaderInputIndex, int dimension, float[] content) {
		this.dimension = dimension;
		this.content = content;
		this.shaderInputIndex = shaderInputIndex;
	}

	public int getDimension() {
		return dimension;
	}

	public float[] getContent() {
		return content;
	}
	
	public int getShaderInputIndex() {
		return shaderInputIndex;
	}
	
	
}
