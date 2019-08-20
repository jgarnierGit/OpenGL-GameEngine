package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.lwjglx.util.vector.Vector4f;

import com.mokiat.data.front.parser.OBJDataReference;
import com.mokiat.data.front.parser.OBJModel;
import com.mokiat.data.front.parser.OBJNormal;
import com.mokiat.data.front.parser.OBJTexCoord;
import com.mokiat.data.front.parser.OBJVertex;

public class OBJDataReferenceUtils {
	private ArrayList<OBJDataReferenceMapper> dataReferences;
	private int verticesListSize;
	private List<OBJNormal> normalsList;
	private List<OBJTexCoord> textCoordsList;
	private List<OBJVertex> verticesList;
	private List<MaterialMapper> materialsList;

	public ArrayList<OBJDataReferenceMapper> getVertices() {
		return dataReferences;
	}

	public OBJDataReferenceUtils(OBJModel objModel, MTLUtils mtlUtils) {
		verticesListSize = objModel.getVertices().size();
		dataReferences = new ArrayList<>();
		normalsList = objModel.getNormals();
		textCoordsList = objModel.getTexCoords();
		verticesList = objModel.getVertices();
		materialsList = mtlUtils.getMaterials();
	}

	/**
	 * @param OBJDataReference
	 * @param int Mesh Index, link with MTL file.
	 */
	public void addVertex(OBJDataReference objDataRef,int materialIndex) {
		dataReferences.add(new OBJDataReferenceMapper(objDataRef,materialIndex));
	}

	public ArrayList<Integer> getPositionsIndices() {
		return getOrderedReferences(OBJDataReferenceMapper::getOriginalIndex);
	}

	/**
	 * @return ArrayList<Integer> list of normals indexes ordered by vertices indexes as sorted by obj file.
	 */
	public ArrayList<Integer> getNormalsIndices() {
		return getOrderedReferences(OBJDataReferenceMapper::getNormalIndex);
	}

	/**
	 * @return ArrayList<Integer> list of textures coordinates indexes ordered by vertices indexes as sorted by obj file.
	 */
	public ArrayList<Integer> getTexturesCoordsIndices() {
		return getOrderedReferences(OBJDataReferenceMapper::getTextureCoordIndex);
	}

	/**
	 * @return ArrayList<Integer> list of colors indexes ordered by vertices indexes as sorted by obj file.
	 */
	public ArrayList<Integer> getColorsIndices() {
		return getOrderedReferences(OBJDataReferenceMapper::getMaterialIndex);
	}


	public List<OBJNormal> getNormalsList() {
		return normalsList;
	}

	public List<OBJTexCoord> getTextCoordsList() {
		return textCoordsList;
	}

	public List<OBJVertex> getVerticesList() {
		return verticesList;
	}

	public List<MaterialMapper> getMaterialsList() {
		return materialsList;
	}

	private ArrayList<Integer> getOrderedReferences(Function<OBJDataReferenceMapper, Integer> method){
		List<OBJDataReferenceMapper> orderedByIndex = dataReferences.stream().filter(dataRefMapper -> {
			return dataRefMapper.dataRef.hasVertexIndex();
		}).sorted(Comparator.comparingInt(OBJDataReferenceMapper::getIndex)).collect(Collectors.toList());

		HashSet<Integer> uniqueIndex = new HashSet<>();
		ArrayList<Integer> indices = new ArrayList<>();
		for(OBJDataReferenceMapper mapper : orderedByIndex) {
			if(uniqueIndex.add(mapper.getIndex())) {
				int index = method.apply(mapper);
				if(index > -1) {
					indices.add(index);
				}
			}
		}
		return indices;
	}

	public boolean isConfigRegistered(OBJDataReference ref) {
		for(OBJDataReferenceMapper currentDataRef : dataReferences) {
			if(currentDataRef.dataRef.vertexIndex == ref.vertexIndex) {
				if(ref.equals(currentDataRef.dataRef)) {
					return true;
				}
				for(OBJDataReference child: currentDataRef.children) {
					if(ref.equals(child)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param ref
	 * @param materialIndex
	 */
	public void addChildConfig(OBJDataReference ref,int materialIndex) {
		OBJDataReferenceMapper firstConfig = null;
		for(OBJDataReferenceMapper currentDataRef : dataReferences) {
			if(currentDataRef.dataRef.vertexIndex == ref.vertexIndex) {
				firstConfig = currentDataRef;
			}
		}
		ref.vertexIndex = verticesListSize;
		verticesListSize++;
		addVertex(ref,materialIndex);
		firstConfig.children.add(ref);
		for(OBJDataReferenceMapper currentDataRef : dataReferences) {
			if(currentDataRef.dataRef.vertexIndex == ref.vertexIndex) {
				currentDataRef.setParent(firstConfig);
			}
		}
	}
}
