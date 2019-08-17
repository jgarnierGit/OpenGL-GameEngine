package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

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
		int vertexSizeList = objModel.getVertices().size();
		OBJDataReferenceUtils objDataReferenceUtils = new OBJDataReferenceUtils(vertexSizeList);

		objModel.getObjects().forEach(obj -> {
			obj.getMeshes().forEach(mesh -> {
				mesh.getFaces().forEach(face -> {
					face.getReferences().forEach(ref -> {
						if(ref.hasVertexIndex()) {
							// ref.vertexIndex only contains original ids.
							if(indices.contains(ref.vertexIndex)) {
								if(objDataReferenceUtils.isConfigRegistered(ref)) {
									indices.add(ref.vertexIndex);
								}
								else {
									int newIndex = objDataReferenceUtils.addChildConfig(ref);
									indices.add(ref.vertexIndex);
								}
							}
							else {
								indices.add(ref.vertexIndex);
								objDataReferenceUtils.addVertex(ref);
							}
						}
						else {
							System.out.println(obj.getName() +" has no vertexIndex");
						}
					});
				});
			});
		});
		List<OBJVertex> vertices = objDataReferenceUtils.getPositionsIndices().stream().map(indice -> {
			return objModel.getVertices().get(indice);
		}).collect(Collectors.toList());
		for(OBJVertex vertex : vertices) {
			positions.add(vertex.x);
			positions.add(vertex.y);
			positions.add(vertex.z);
		}
		List<OBJNormal> normalsList = objDataReferenceUtils.getNormalsIndices().stream().map(indice -> {
			return objModel.getNormals().get(indice);
		}).collect(Collectors.toList());
		for(OBJNormal normal : normalsList) {
			normals.add(normal.x);
			normals.add(normal.y);
			normals.add(normal.z);
		}
		List<OBJTexCoord> textCoordList = objDataReferenceUtils.getTexturesCoordsIndices().stream().map(indice -> {
			return objModel.getTexCoords().get(indice);
		}).collect(Collectors.toList());
		for(OBJTexCoord textCoord : textCoordList) {
			texturesCoords.add(textCoord.u);
			texturesCoords.add(textCoord.v);
		}
		System.out.println(objModel.getObjects().get(0).getName() +" :");
		System.out.println(indices);
		System.out.println(positions);
		System.out.println(texturesCoords);
		System.out.println(normals);
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
