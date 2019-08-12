package models;

import java.util.ArrayList;

import com.mokiat.data.front.parser.OBJModel;
import com.mokiat.data.front.parser.OBJNormal;
import com.mokiat.data.front.parser.OBJTexCoord;
import com.mokiat.data.front.parser.OBJVertex;

public class OBJUtils {
	private OBJModel objModel;
	private ArrayList<Integer> indices;
	private ArrayList<Float> positions; // getVertexArray3f
	private ArrayList<Float> texturesCoords; // getVertexArray2f
	private ArrayList<Float> normals; // getVertexArray3f
	
	/**
	 * FIXME, visual KO, indices starts well from 0..., maybe boolean is imported is useless.
	 * @param objModel
	 * @param isImported
	 */
	public OBJUtils(OBJModel objModel, boolean isImported) {
		this.objModel= objModel;
		indices = new ArrayList<>();
		positions = new ArrayList<>();
		texturesCoords = new ArrayList<>();
		normals = new ArrayList<>();
		objModel.getObjects().forEach(obj -> {
			obj.getMeshes().forEach(mesh -> {
				mesh.getFaces().forEach(face -> {
					face.getReferences().forEach(ref -> {
						if(ref.hasVertexIndex()) {
							indices.add(ref.vertexIndex);
							OBJVertex vertex = objModel.getVertices().get(ref.vertexIndex);
							positions.add(vertex.x);
							positions.add(vertex.y);
							positions.add(vertex.z);
						}
						else {
							System.out.println(obj.getName() +" has no vertexIndex");
						}
						if(ref.hasNormalIndex()) {
							OBJNormal normal = objModel.getNormals().get(ref.normalIndex);
							normals.add(normal.x);
							normals.add(normal.y);
							normals.add(normal.z);
						}
						else {
							System.out.println(obj.getName() +" has no normalIndex");
						}
						if(ref.hasTexCoordIndex()) {
							OBJTexCoord textCoord = objModel.getTexCoords().get(ref.texCoordIndex);
							texturesCoords.add(textCoord.u);
							texturesCoords.add(textCoord.v);
						}
						else {
							System.out.println(obj.getName() +" has no textureCoordsIndex for index :"+ ref.vertexIndex);
						}
					});
				});
			});
		});
	}

	public ArrayList<Integer> getIndices() {
		return this.indices;
	}

	public ArrayList<Float> getPositions() {
		return this.positions;
	}

	public ArrayList<Float> getTexturesCoords() {
		return this.texturesCoords;
	}

	public ArrayList<Float> getNormals() {
		return this.normals;
	}
	
	public OBJModel getObjModel() {
		return objModel;
	}
}
