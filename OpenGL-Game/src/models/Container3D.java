package models;

import java.util.ArrayList;
import java.util.List;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

public class Container3D {
	private ArrayList<Integer> vertexIndices;
	private ArrayList<Vector3f> positions;
	private ArrayList<Vector2f> textures;
	private ArrayList<Vector3f> normals;
	
	public ArrayList<Integer> getVertexIndices() {
		return vertexIndices;
	}
	public ArrayList<Vector3f> getPositions() {
		return positions;
	}
	public ArrayList<Vector2f> getTextures() {
		return textures;
	}
	public ArrayList<Vector3f> getNormals(){
		return normals;
	}
	public Container3D(ArrayList<Integer> vertexIndices, ArrayList<Vector3f> positions, ArrayList<Vector2f> textures, ArrayList<Vector3f> normals) {
		this.vertexIndices = vertexIndices;
		this.positions = positions;
		this.textures = textures;
		this.normals = normals;
	}
	
	public ArrayList<Float> getFlatPositions(){
		return getVertexArray3f(positions);
	}
	
	public ArrayList<Integer> getFlatIndices(){
		return getVertexIndices();
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
