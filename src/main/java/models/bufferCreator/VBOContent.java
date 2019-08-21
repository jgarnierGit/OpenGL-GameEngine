package models.bufferCreator;

import java.util.ArrayList;

public class VBOContent {
	private int dimension;
	private ArrayList<Float> content;
	
	public VBOContent(int dimension, ArrayList<Float> content) {
		this.dimension = dimension;
		this.content = content;
	}

	public int getDimension() {
		return dimension;
	}

	public ArrayList<Float> getContent() {
		return content;
	}
	
	
}
