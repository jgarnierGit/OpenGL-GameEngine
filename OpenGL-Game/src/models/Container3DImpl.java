package models;

import java.util.ArrayList;
import java.util.List;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

public class Container3DImpl{
	public ArrayList<Integer> vertexIndices;
	public ArrayList<Vector3f> positions;
	public ArrayList<Vector2f> textures;
	public ArrayList<Vector3f> normals;
	
	public Container3DImpl(ArrayList<Integer> vertexIndices, ArrayList<Vector3f> vertices,
			ArrayList<Vector2f> textureIndices, ArrayList<Vector3f> normalIndices) {
		this.vertexIndices = vertexIndices;
		this.positions = vertices;
		this.textures= textureIndices;
		this.normals = normalIndices;
	}
	
	public ArrayList<Float> getFlatPositions(){
		return getVertexArray3f(positions);
	}
	
	public ArrayList<Integer> getFlatIndices(){
		return vertexIndices;
	}
	
	public ArrayList<Float> getFlatTextures(){
		return getVertexArray2f(textures);
	}

	public ArrayList<Float> getFlatNormals() {
		return getVertexArray3f(normals);
	}
	
	private static ArrayList<Float> getVertexArray3f(List<Vector3f> vertices) {
		ArrayList<Float> floatArray = new ArrayList<>();
		for(Vector3f vector :vertices) {
			floatArray.add(vector.getX());
			floatArray.add(vector.getY());
			floatArray.add(vector.getZ());
		}
		return floatArray;
	}
	
	private static ArrayList<Float> getVertexArray2f(List<Vector2f> vertices) {
		ArrayList<Float> floatArray = new ArrayList<>();
		for(Vector2f vector :vertices) {
			floatArray.add(vector.getX());
			floatArray.add(vector.getY());
		}
		return floatArray;
	}

}
