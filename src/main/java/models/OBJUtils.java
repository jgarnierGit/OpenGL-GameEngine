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
	private final ArrayList<Integer> indices;
	private final VBOContent positions; // dimension 3.
	private final VBOContent material;
	private final VBOContent normals; // dimension 3.
	
	/**
	 * @param objModel
	 * @param materialTypes 
	 * @param mtlUtils 
	 */
	public OBJUtils(OBJModel objModel, MTLUtils mtlUtils) {
		indices = new ArrayList<>();
		
		OBJDataReferenceUtils objDataReferenceUtils = new OBJDataReferenceUtils(objModel,mtlUtils);

		objModel.getObjects().forEach(obj -> {
			obj.getMeshes().forEach(mesh -> {
				int materialIndex = mtlUtils.getMaterials().indexOf(mtlUtils.getMaterial(mesh.getMaterialName()));
				mesh.getFaces().forEach(face -> {
					face.getReferences().forEach(ref -> {
						if(ref.hasVertexIndex()) {
							// ref.vertexIndex only contains original ids.
							if(indices.contains(ref.vertexIndex)) {
								if(objDataReferenceUtils.isConfigRegistered(ref)) {
									indices.add(ref.vertexIndex);
								}
								else {
									objDataReferenceUtils.addChildConfig(ref, materialIndex);
									indices.add(ref.vertexIndex);
								}
							}
							else {
								indices.add(ref.vertexIndex);
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
		/**System.out.println(objModel.getObjects().get(0).getName() +" :");
		System.out.println(indices);
		System.out.println(positions.getContent());
		System.out.println(material.getContent());
		System.out.println(normals.getContent());**/
	}
	
	private VBOContent setMaterialContent(OBJDataReferenceUtils objDataReferenceUtils, List<MaterialMapper> materials) {
		int dimension = 0;
		ArrayList<Float> coords = new ArrayList<>();
		if(materials.get(0).getType() == MaterialType.IMAGE) {
			dimension = 2;
			List<OBJTexCoord> textCoordList = objDataReferenceUtils.getTexturesCoordsIndices().stream().map(indice -> {
						return objDataReferenceUtils.getTextCoordsList().get(indice);
					}).collect(Collectors.toList());
			for(OBJTexCoord textCoord : textCoordList) {
				coords.add(textCoord.u);
				coords.add(textCoord.v);
			}
		}else {
			dimension = 4;
			List<MaterialMapper> colorsList = objDataReferenceUtils.getColorsIndices().stream().map(indice -> {
						return objDataReferenceUtils.getMaterialsList().get(indice);
					}).collect(Collectors.toList());
			for(MaterialMapper color : colorsList) {
				coords.add(color.getColor().x);
				coords.add(color.getColor().y);
				coords.add(color.getColor().z);
				coords.add(color.getColor().w);
			}
		}
		return new VBOContent(dimension, coords);
	}

	private VBOContent setPositionContent(OBJDataReferenceUtils objDataReferenceUtils) {
		List<OBJVertex> vertices = objDataReferenceUtils.getPositionsIndices().stream().map(indice -> {
					return objDataReferenceUtils.getVerticesList().get(indice);
				}).collect(Collectors.toList());
		ArrayList<Float> positions = new ArrayList<>();
		for(OBJVertex vertex : vertices) {
			positions.add(vertex.x);
			positions.add(vertex.y);
			positions.add(vertex.z);
		}
		return new VBOContent(3, positions);
	}
	
	private VBOContent setNormalContent(OBJDataReferenceUtils objDataReferenceUtils) {
		List<OBJNormal> normalsList = objDataReferenceUtils.getNormalsIndices().stream().map(indice -> {
					return objDataReferenceUtils.getNormalsList().get(indice);
				}).collect(Collectors.toList());
		ArrayList<Float> normals = new ArrayList<>();
		for(OBJNormal normal : normalsList) {
			normals.add(normal.x);
			normals.add(normal.y);
			normals.add(normal.z);
		}
		return new VBOContent(3, normals);
	}
	

	public ArrayList<Integer> getIndices() {
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
		
		int materialDimension = 0;
		ArrayList<Float> matList = new ArrayList<>();
		if(materials.get(0) instanceof Vector4f) {
			materialDimension = 4;
			for(T mat : materials) {
				Vector4f matV4f = (Vector4f) mat;
				matList.add(matV4f.x);
				matList.add(matV4f.y);
				matList.add(matV4f.z);
				matList.add(matV4f.w);
			}
		}else if(materials.get(0) instanceof Vector2f) {
			materialDimension = 2;
			for(T mat : materials) {
				Vector2f matV2f = (Vector2f) mat;
				matList.add(matV2f.x);
				matList.add(matV2f.y);
			}
		}
		else {
			System.err.println("unsupported type for materials.");
		}
		ArrayList<Float> normalList = new ArrayList<>();
		for(Vector3f normal: normalsVector ) {
			normalList.add(normal.x);
			normalList.add(normal.y);
			normalList.add(normal.z);
		}
		
		ArrayList<Float> positionsList = new ArrayList<>();
		for(Vector3f position: positions2) {
			positionsList.add(position.x);
			positionsList.add(position.y);
			positionsList.add(position.z);
		}
		return new OBJUtils(vertexIndices, new VBOContent(3, positionsList), new VBOContent(3, normalList), new VBOContent(materialDimension, matList));
	}
	
	private OBJUtils(ArrayList<Integer> vertexIndices, VBOContent positions2,
			VBOContent normalsVector, VBOContent materials) {
		this.indices = vertexIndices;
		this.positions = positions2;
		this.normals = normalsVector;
		this.material = materials;
	}
}
