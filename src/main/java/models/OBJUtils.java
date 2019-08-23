package models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import com.mokiat.data.front.parser.OBJModel;
import com.mokiat.data.front.parser.OBJNormal;
import com.mokiat.data.front.parser.OBJTexCoord;
import com.mokiat.data.front.parser.OBJVertex;

import models.bufferCreator.VBOContent;

public class OBJUtils {
	private final int[] indices;
	private final VBOContent positions;
	private final VBOContent material;
	private final VBOContent normals;
	
	/**
	 * @param objModel
	 * @param materialTypes 
	 * @param mtlUtils 
	 */
	public OBJUtils(OBJModel objModel, MTLUtils mtlUtils) {
		ArrayList<Integer> indicesList = new ArrayList<>();
		
		OBJDataReferenceUtils objDataReferenceUtils = new OBJDataReferenceUtils(objModel,mtlUtils);

		objModel.getObjects().forEach(obj -> {
			obj.getMeshes().forEach(mesh -> {
				int materialIndex = mtlUtils.getMaterials().indexOf(mtlUtils.getMaterial(mesh.getMaterialName()));
				mesh.getFaces().forEach(face -> {
					face.getReferences().forEach(ref -> {
						if(ref.hasVertexIndex()) {
							// ref.vertexIndex only contains original ids.
							if(indicesList.contains(ref.vertexIndex)) {
								if(objDataReferenceUtils.isConfigRegistered(ref)) {
									indicesList.add(ref.vertexIndex);
								}
								else {
									objDataReferenceUtils.addChildConfig(ref, materialIndex);
									indicesList.add(ref.vertexIndex);
								}
							}
							else {
								indicesList.add(ref.vertexIndex);
								objDataReferenceUtils.addVertex(ref, materialIndex);
							}
						}
						else {
							System.out.println(obj.getName() +" has no vertexIndex");
						}
					});
				});
			});
		});
		positions = setPositionContent(objDataReferenceUtils);
		normals = setNormalContent(objDataReferenceUtils);
		//TODO update to set type for each material of an object
		material = setMaterialContent(objDataReferenceUtils,mtlUtils.getMaterials());
		
		indices = new int[indicesList.size()];
		for(int i=0;i<indicesList.size();i++){
			indices[i] = indicesList.get(i);
		}
		/**System.out.println(objModel.getObjects().get(0).getName() +" :");
		System.out.println(indices);
		System.out.println(positions.getContent());
		System.out.println(material.getContent());
		System.out.println(normals.getContent());**/
	}
	
	private VBOContent setMaterialContent(OBJDataReferenceUtils objDataReferenceUtils, List<MaterialMapper> materials) {
		int dimension = 0;
		int indexCoords = 0;
		float[] coords;
		if(materials.get(0).getType() == MaterialType.IMAGE) {
			dimension = 2;
			List<OBJTexCoord> textCoordList = objDataReferenceUtils.getTexturesCoordsIndices().stream().map(indice -> {
						return objDataReferenceUtils.getTextCoordsList().get(indice);
					}).collect(Collectors.toList());
			coords = new float[textCoordList.size() * 2];
			for(OBJTexCoord textCoord : textCoordList) {
				coords[indexCoords++] = textCoord.u;
				coords[indexCoords++] = textCoord.v;
			}
		}else {
			dimension = 4;
			List<MaterialMapper> colorsList = objDataReferenceUtils.getColorsIndices().stream().map(indice -> {
						return objDataReferenceUtils.getMaterialsList().get(indice);
					}).collect(Collectors.toList());
			coords = new float[colorsList.size() * 4];
			for(MaterialMapper color : colorsList) {
				coords[indexCoords++] = color.getColor().x;
				coords[indexCoords++] = color.getColor().y;
				coords[indexCoords++] = color.getColor().z;
				coords[indexCoords++] = color.getColor().w;
			}
		}
		return new VBOContent(dimension, coords);
	}

	private VBOContent setPositionContent(OBJDataReferenceUtils objDataReferenceUtils) {
		List<OBJVertex> vertices = objDataReferenceUtils.getPositionsIndices().stream().map(indice -> {
					return objDataReferenceUtils.getVerticesList().get(indice);
				}).collect(Collectors.toList());
		int indexCoords = 0;
		float[] positions = new float[vertices.size() *3];
		for(OBJVertex vertex : vertices) {
			positions[indexCoords++] = vertex.x;
			positions[indexCoords++] = vertex.y;
			positions[indexCoords++] = vertex.z;
		}
		return new VBOContent(3, positions);
	}
	
	private VBOContent setNormalContent(OBJDataReferenceUtils objDataReferenceUtils) {
		List<OBJNormal> normalsList = objDataReferenceUtils.getNormalsIndices().stream().map(indice -> {
					return objDataReferenceUtils.getNormalsList().get(indice);
				}).collect(Collectors.toList());
		int indexCoords = 0;
		float[] normals = new float[normalsList.size() *3];
		for(OBJNormal normal : normalsList) {
			normals[indexCoords++] = normal.x;
			normals[indexCoords++] = normal.y;
			normals[indexCoords++] = normal.z;
		}
		return new VBOContent(3, normals);
	}
	

	public int[] getIndices() {
		return this.indices;
	}

	public VBOContent getPositions() {
		return positions;
	}

	public VBOContent getMaterial() {
		return this.material;
	}

	public VBOContent getNormals() {
		return this.normals;
	}

	public static <T> OBJUtils create(ArrayList<Integer> vertexIndices, ArrayList<Vector3f> positions2,
			ArrayList<Vector3f> normalsVector, ArrayList<T> materials) {
		
		int[] indicesList = new int[vertexIndices.size()];
		int indexIndice = 0;
		for(Integer i : vertexIndices) {
			indicesList[indexIndice++] = i;
		}
		
		int materialDimension = 0;
		int indexMat = 0;
		float[] matList;
		if(materials.get(0) instanceof Vector4f) {
			materialDimension = 4;
			matList = new float[materials.size() * 4];
			for(T mat : materials) {
				Vector4f matV4f = (Vector4f) mat;
				matList[indexMat++] = matV4f.x;
				matList[indexMat++] = matV4f.y;
				matList[indexMat++] = matV4f.z;
				matList[indexMat++] = matV4f.w;
			}
		}else if(materials.get(0) instanceof Vector2f) {
			materialDimension = 2;
			matList = new float[materials.size() * 2];
			for(T mat : materials) {
				Vector2f matV2f = (Vector2f) mat;
				matList[indexMat++] = matV2f.x;
				matList[indexMat++] = matV2f.y;
			}
		}
		else {
			matList = null;
			System.err.println("unsupported type for materials.");
		}
		int indexCoords = 0;
		float[] normals = new float[normalsVector.size() * 3];
		for(Vector3f normal: normalsVector ) {
			normals[indexCoords++] = normal.x;
			normals[indexCoords++] = normal.y;
			normals[indexCoords++] = normal.z;
		}
		
		int indexPositions = 0;
		float[] positionsList = new float[positions2.size() * 3];
		for(Vector3f position: positions2) {
			positionsList[indexPositions++] = position.x;
			positionsList[indexPositions++] = position.y;
			positionsList[indexPositions++] = position.z;
		}
		return new OBJUtils(indicesList, new VBOContent(3, positionsList), new VBOContent(3, normals), new VBOContent(materialDimension, matList));
	}
	
	private OBJUtils(int[] vertexIndices, VBOContent positions2,
			VBOContent normalsVector, VBOContent materials) {
		this.indices = vertexIndices;
		this.positions = positions2;
		this.normals = normalsVector;
		this.material = materials;
	}
}
