package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

public class GeneratedModelContainer implements Container3D {
	
	private Container3DImpl containerData;

	public GeneratedModelContainer(ArrayList<Integer> vertexIndicesMTL, ArrayList<Vector3f> vertices,
			Optional<ArrayList<Vector2f>> textureIndices, Optional<ArrayList<String>> colorsIndices, ArrayList<Vector3f> normalIndices) {
		containerData = new Container3DImpl(vertexIndicesMTL, vertices, textureIndices, colorsIndices, normalIndices);
	}

	@Override
	public ArrayList<Integer> getVertexIndices() {
		return containerData.vertexIndices;
	}

	@Override
	public ArrayList<Vector3f> getPositions() {
		return containerData.positions;
	}

	@Override
	public ArrayList<Vector3f> getNormals() {
		return containerData.normals;
	}
	
	public ArrayList<Float> getFlatPositions(){
		return containerData.getFlatPositions();
	}
	
	public ArrayList<Integer> getFlatIndices(){
		return containerData.getFlatIndices();
	}
	
	public ArrayList<Float> getFlatTextures(){
		return containerData.getFlatTextures();
	}

	public ArrayList<Float> getFlatNormals() {
		return containerData.getFlatNormals();
	}

	@Override
	public TextureConfig getTextureConfig() {
		return containerData.getTextureConfig();
	}

	@Override
	public ArrayList<String> getColorLinks() {
		return containerData.getColorLinks();
	}

	/** @Override
	public void addIsTexturedVertex(boolean simpleColor) {
		containerData.addIsTexturedVertex(simpleColor);
	}

	@Override
	public ArrayList<Boolean> getFlatIndicesUsingTexture() {
		return containerData.getFlatIndicesUsingTexture();
	} **/

}
