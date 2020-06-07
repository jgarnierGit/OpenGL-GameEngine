package modelsManager.bufferCreator;

public class VBOContent {
	private int dimension;
	private float[] content;
	
	public VBOContent(int dimension, float[] content) {
		this.dimension = dimension;
		this.content = content;
	}

	public int getDimension() {
		return dimension;
	}

	public float[] getContent() {
		return content;
	}
	
	
}
