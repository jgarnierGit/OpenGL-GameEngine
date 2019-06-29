package objectManager;

import java.util.Optional;

public class Vertex {
	private Integer indiceIndex;
	private Integer textureIndex;
	private Integer normalIndex;
	private Optional<Vertex> previousVertex;
	
	public Vertex(Integer indiceIndex, Integer textureIndex, Integer normalIndex) {
		this.indiceIndex = indiceIndex;
		this.textureIndex = textureIndex;
		this.normalIndex = normalIndex;
		this.previousVertex = Optional.empty();
	}

	public Integer getIndiceIndex() {
		return indiceIndex;
	}

	public Integer getTextureIndex() {
		return textureIndex;
	}

	public Integer getNormalIndex() {
		return normalIndex;
	}
	
	public void setNewIndice(int newIndice) {
		this.indiceIndex = newIndice;
	}

	public boolean hasSameConfig(Vertex vertex) {
		return this.normalIndex == vertex.getNormalIndex() && this.textureIndex == vertex.getTextureIndex();
		
	}

	public Optional<Vertex> getPreviousVertex() {
		return previousVertex;
	}

	@Override
	public String toString() {
		return "Vertex [indiceIndex=" + indiceIndex + ", textureIndex=" + textureIndex + ", normalIndex=" + normalIndex
				+ ", previousVertex=" + previousVertex + "]";
	}

	public void setPreviousVertex(Vertex vertexToAdd) {
		this.previousVertex = Optional.of(vertexToAdd);
	}
	
}
