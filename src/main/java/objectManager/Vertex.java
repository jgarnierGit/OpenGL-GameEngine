package objectManager;

import java.util.Optional;

public class Vertex {
	private Integer indiceIndex;
	private Optional<Integer> imageIndex;
	private Optional<Integer> colorIndex;
	private Boolean useImageTexture;
	private Integer normalIndex;
	private Optional<Vertex> previousVertex;
	
	public Vertex(Integer indiceIndex, Integer normalIndex) {
		this.indiceIndex = indiceIndex;
		this.normalIndex = normalIndex;
		this.previousVertex = Optional.empty();
		this.imageIndex = Optional.empty();
		this.colorIndex = Optional.empty();
		this.useImageTexture = false;
	}
	
	public void setImageIndex(Integer textureIndex) {
		this.useImageTexture = true;
		this.imageIndex = Optional.of(textureIndex);
	}

	public Integer getIndiceIndex() {
		return indiceIndex;
	}

	public Integer getImageIndex() {
		return imageIndex.orElse(0);
	}

	public Boolean getUseImageTexture() {
		return useImageTexture;
	}

	public Integer getColorIndex() {
		return colorIndex.orElse(0);
	}

	public void setColorIndex(Integer colorIndex) {
		this.useImageTexture = false;
		this.colorIndex = Optional.of(colorIndex);
	}

	public Integer getNormalIndex() {
		return normalIndex;
	}
	
	public void setNewIndice(int newIndice) {
		this.indiceIndex = newIndice;
	}

	public boolean hasSameConfig(Vertex vertex) {
		return this.normalIndex == vertex.getNormalIndex() && 
				this.useImageTexture == vertex.getUseImageTexture() &&
				((this.useImageTexture && this.imageIndex.get() == vertex.getImageIndex()) ||
				(!this.useImageTexture && this.colorIndex.get() == vertex.getColorIndex())); 
	}

	public Optional<Vertex> getPreviousVertex() {
		return previousVertex;
	}

	@Override
	public String toString() {
		return "Vertex [indiceIndex=" + indiceIndex + ", textureIndex=" + imageIndex + ", normalIndex=" + normalIndex
				+ ", previousVertex=" + previousVertex + "]";
	}

	public void setPreviousVertex(Vertex vertexToAdd) {
		this.previousVertex = Optional.of(vertexToAdd);
	}
	
}
