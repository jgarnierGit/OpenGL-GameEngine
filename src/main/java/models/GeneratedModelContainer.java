package models;

import java.util.ArrayList;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

public class GeneratedModelContainer implements Container3D {
	
	private Container3DImpl containerData;

	public GeneratedModelContainer(ArrayList<Integer> vertexIndices, ArrayList<Vector3f> vertices,
			ArrayList<Vector2f> textureIndices, ArrayList<Vector3f> normalIndices) {
		containerData = new Container3DImpl(vertexIndices, vertices, textureIndices, normalIndices);
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
	public ArrayList<Vector2f> getTextures() {
		return containerData.textures;
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

}
