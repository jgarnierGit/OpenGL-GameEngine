package models;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

public interface Container3D {
	static final Path resourcePath = Paths.get("./", "src", "main", "resources", "3D");
	
	public ArrayList<Integer> getVertexIndices();
	public ArrayList<Vector3f> getPositions();
	public ArrayList<Vector2f> getTextures();
	public ArrayList<Vector3f> getNormals();
	
	//TODO not ideal... because bridge methods seems like ugly
	public ArrayList<Float> getFlatPositions();
	
	public ArrayList<Integer> getFlatIndices();
	
	public ArrayList<Float> getFlatTextures();

	public ArrayList<Float> getFlatNormals();
}
