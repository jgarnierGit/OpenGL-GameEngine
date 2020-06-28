package modelsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import com.mokiat.data.front.parser.OBJDataReference;
import com.mokiat.data.front.parser.OBJFace;
import com.mokiat.data.front.parser.OBJMesh;
import com.mokiat.data.front.parser.OBJModel;
import com.mokiat.data.front.parser.OBJNormal;
import com.mokiat.data.front.parser.OBJObject;
import com.mokiat.data.front.parser.OBJTexCoord;
import com.mokiat.data.front.parser.OBJVertex;

import modelsManager.bufferCreator.VBOContent;

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
		int indicesListSize = 0;
		for(OBJObject obj : objModel.getObjects()) {
			for(OBJMesh mesh : obj.getMeshes()) {
				for(OBJFace face: mesh.getFaces()) {
					indicesListSize += face.getReferences().size();
				}
			}
		}
		
		int[] registeredIndice = new int[indicesListSize];
		Arrays.fill(registeredIndice, -1);
		
		OBJDataReferenceUtils objDataReferenceUtils = new OBJDataReferenceUtils(objModel,mtlUtils);
		for(OBJObject obj : objModel.getObjects()) {
			for(OBJMesh mesh : obj.getMeshes()) {
				int materialIndex = mtlUtils.getMaterials().indexOf(mtlUtils.getMaterial(mesh.getMaterialName()));
				for(OBJFace face: mesh.getFaces()) {
					for(OBJDataReference ref : face.getReferences()) {
						if(ref.hasVertexIndex()) {
							// ref.vertexIndex only contains original ids.
							int registeredIndex = registeredIndice[ref.vertexIndex];
							if(registeredIndex != -1) {
								if(objDataReferenceUtils.isConfigRegistered(registeredIndex, ref)) {
									indicesList.add(ref.vertexIndex);
								}
								else {
									OBJDataReferenceMapper mapper = objDataReferenceUtils.duplicateVertex(ref,materialIndex);
									registeredIndice[ref.vertexIndex] = objDataReferenceUtils.getDataReferences().size();
									objDataReferenceUtils.addChildConfig(registeredIndex, mapper, materialIndex);
									indicesList.add(ref.vertexIndex);
								}
							}
							else {
								registeredIndice[ref.vertexIndex] = objDataReferenceUtils.getDataReferences().size();
								indicesList.add(ref.vertexIndex);
								objDataReferenceUtils.addVertex(ref, materialIndex);
							}
						}
						else {
							System.out.println(obj.getName() +" has no vertexIndex");
						}
					}
				}
			}
		}
			
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
		int shaderPosition = 0;
		float[] coords;
		if(materials.get(0).getType() == MaterialType.IMAGE) {
			dimension = 2;
			shaderPosition = 2;
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
			shaderPosition = 4;
			List<MaterialMapper> colorsList = objDataReferenceUtils.getColorsIndices().stream().map(indice -> {
						return objDataReferenceUtils.getMaterialsList().get(indice);
					}).collect(Collectors.toList());
			coords = new float[colorsList.size() * 4];
			for(MaterialMapper color : colorsList) {
				if(color.getColor() != null) {
					coords[indexCoords++] = color.getColor().x;
					coords[indexCoords++] = color.getColor().y;
					coords[indexCoords++] = color.getColor().z;
					coords[indexCoords++] = color.getColor().w;
				}
				else {
					coords[indexCoords++] = 0;
					coords[indexCoords++] = 0;
					coords[indexCoords++] = 0;
					coords[indexCoords++] = 0;
				}
			}
		}
		return new VBOContent(shaderPosition,dimension, coords);
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
		return new VBOContent(1, 3, positions);
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
		return new VBOContent(3, 3, normals);
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
		int shaderIndex = 0;
		int indexMat = 0;
		float[] matList;
		//TODO absolutly unclear use of parametrized types... change to builder (materials : 2f / 4f/ none], normals : none if no surfacic )
		if(materials.get(0) instanceof Vector4f) {
			materialDimension = 4;
			shaderIndex =4;
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
			shaderIndex =2;
			matList = new float[materials.size() * 2];
			for(T mat : materials) {
				Vector2f matV2f = (Vector2f) mat;
				matList[indexMat++] = matV2f.x;
				matList[indexMat++] = matV2f.y;
			}
		}
		else {
			matList = null;
			shaderIndex = -1;
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
		return new OBJUtils(indicesList, new VBOContent(1,3, positionsList), new VBOContent(3, 3, normals), new VBOContent(shaderIndex, materialDimension, matList));
	}
	
	private OBJUtils(int[] vertexIndices, VBOContent positions2,
			VBOContent normalsVector, VBOContent materials) {
		this.indices = vertexIndices;
		this.positions = positions2;
		this.normals = normalsVector;
		this.material = materials;
	}
}
