package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

public class Container3DImpl{
	public ArrayList<Integer> vertexIndices;
//	public ArrayList<Boolean> vertexIsUsingTextureIndices;
	public ArrayList<Vector3f> positions;
	public TextureConfig textureConfig;
	/** public ArrayList<Vector2f> textures; //TODO deported in TextureConfig
	public ArrayList<String> colors; **/ 
	public ArrayList<Vector3f> normals;
	
	public Container3DImpl(ArrayList<Integer> vertexIndices, ArrayList<Vector3f> vertices,
			Optional<ArrayList<Vector2f>> textureIndices, Optional<ArrayList<String>> colors, ArrayList<Vector3f> normalIndices) { 
		this.vertexIndices = vertexIndices;
		this.positions = vertices;
		this.textureConfig = new TextureConfig(textureIndices,colors);
		/**this.textures= textureIndices;
		this.colors = colors; // Container3D doesn't know actual state of Textures. Just have clue of texture (u,v) indices and marker to actual MTL material.
	**/	this.normals = normalIndices;
	}
	
	public ArrayList<Float> getFlatPositions(){
		return getVertexArray3f(positions);
	}
	
	public ArrayList<Integer> getFlatIndices(){
		/** ArrayList<Integer> flatVertexIndices = new ArrayList<>();
		vertexIndices.entrySet().stream().map(entry -> entry.getValue()).forEach(list -> {
			flatVertexIndices.addAll(list);
		}); **/
		return this.vertexIndices;
	}
	
	public ArrayList<Float> getFlatTextures(){
		return getVertexArray2f(textureConfig.getImageMapping());
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

	public TextureConfig getTextureConfig() {
		return this.textureConfig;
	}
	
/** @deprecated	
 * public void addIsTexturedVertex(boolean simpleColor) {
		if(vertexIsUsingTextureIndices == null) {
			vertexIsUsingTextureIndices = new ArrayList<>();
		}
		vertexIsUsingTextureIndices.add(simpleColor);
	}

	public ArrayList<Boolean> getFlatIndicesUsingTexture() {
		return this.vertexIsUsingTextureIndices;
	}**/

}
