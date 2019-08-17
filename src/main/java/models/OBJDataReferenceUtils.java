package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mokiat.data.front.parser.OBJDataReference;

public class OBJDataReferenceUtils {
	private ArrayList<OBJDataReferenceMapper> dataReferences;
	private int verticesListSize;
	
	public ArrayList<OBJDataReferenceMapper> getVertices() {
		return dataReferences;
	}
	
	public OBJDataReferenceUtils(int knownVerticesListSize) {
		verticesListSize = knownVerticesListSize;
		dataReferences = new ArrayList<>();
	}
	
	/**
	 * @param positions might be null
	 * @param textureCoords might be null
	 * @param normal might be null
	 */
	public void addVertex(OBJDataReference objDataRef) {
		dataReferences.add(new OBJDataReferenceMapper(objDataRef));
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
	 * @param ref
	 * @return vertexIndex created
	 */
	public int addChildConfig(OBJDataReference ref) {
		OBJDataReferenceMapper firstConfig = null;
		for(OBJDataReferenceMapper currentDataRef : dataReferences) {
			if(currentDataRef.dataRef.vertexIndex == ref.vertexIndex) {
				firstConfig = currentDataRef;
			}
		}
		ref.vertexIndex = verticesListSize;
		verticesListSize++;
		addVertex(ref);
		firstConfig.children.add(ref);
		for(OBJDataReferenceMapper currentDataRef : dataReferences) {
			if(currentDataRef.dataRef.vertexIndex == ref.vertexIndex) {
				currentDataRef.setParent(firstConfig);
			}
		}
		return ref.vertexIndex;
	}
}
