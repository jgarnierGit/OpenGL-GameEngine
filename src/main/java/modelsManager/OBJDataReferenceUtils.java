package modelsManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.mokiat.data.front.parser.OBJDataReference;
import com.mokiat.data.front.parser.OBJModel;
import com.mokiat.data.front.parser.OBJNormal;
import com.mokiat.data.front.parser.OBJTexCoord;
import com.mokiat.data.front.parser.OBJVertex;

public class OBJDataReferenceUtils {
	private ArrayList<OBJDataReferenceMapper> dataReferences;
	private List<OBJNormal> normalsList;
	private List<OBJTexCoord> textCoordsList;
	private List<OBJVertex> verticesList;
	private List<MaterialMapper> materialsList;
	private int initialReferencesSize;
	private MaterialType materialType;
	private Logger logger;

	public List<OBJDataReferenceMapper> getDataReferences() {
		return dataReferences;
	}

	public OBJDataReferenceUtils(int initialReferencesSize, List<OBJNormal> normalsList,
			List<OBJTexCoord> textCoordsList, List<OBJVertex> verticesList, List<MaterialMapper> materialsList) {
		dataReferences = new ArrayList<>();
		this.normalsList = normalsList;
		this.textCoordsList = textCoordsList;
		this.verticesList = verticesList;
		this.materialsList = materialsList;
		this.initialReferencesSize = initialReferencesSize;
		this.materialType = MaterialType.NONE;
	}

	public static OBJDataReferenceUtils create(OBJModel objModel, List<MaterialMapper> materialMappers) {
		
		OBJDataReferenceUtils objDataRefUtil = new OBJDataReferenceUtils(objModel.getVertices().size(),
				objModel.getNormals(), objModel.getTexCoords(), objModel.getVertices(), materialMappers);
		objDataRefUtil.getMaterialType(materialMappers);
		objDataRefUtil.logger = Logger.getLogger("OBJDataReferenceUtils");
		return objDataRefUtil;
	}

	private void getMaterialType(List<MaterialMapper> materialMappers) {
		MaterialType type = MaterialType.COLOR;
		int countTexture = 0;
		for (MaterialMapper mat : materialMappers) {
			if (mat.getType() == MaterialType.IMAGE) {
				type = MaterialType.IMAGE;
				countTexture++;
				if (countTexture > 1) {
					logger.severe("Model can't have more than one texture");
				}
			}
		}
		this.materialType = type;
	}

	/**
	 * @param OBJDataReference
	 * @param int              Mesh Index, link with MTL file.
	 */
	public OBJDataReferenceMapper addVertex(OBJDataReference objDataRef, int materialIndex) {
		OBJDataReferenceMapper newMapper = new OBJDataReferenceMapper(objDataRef, materialIndex, dataReferences.size());
		dataReferences.add(newMapper);
		return newMapper;
	}

	public List<Integer> getPositionsIndices() {
		return getOrderedReferences(OBJDataReferenceMapper::getOriginalIndex);
	}

	/**
	 * @return ArrayList<Integer> list of normals indexes ordered by vertices
	 *         indexes as sorted by obj file.
	 */
	public List<Integer> getNormalsIndices() {
		return getOrderedReferences(OBJDataReferenceMapper::getNormalIndex);
	}

	/**
	 * @return ArrayList<Integer> list of textures coordinates indexes ordered by
	 *         vertices indexes as sorted by obj file.
	 */
	public List<Integer> getTexturesCoordsIndices() {
		return getOrderedReferences(OBJDataReferenceMapper::getTextureCoordIndex);
	}

	/**
	 * @return ArrayList<Integer> list of colors indexes ordered by vertices indexes
	 *         as sorted by obj file.
	 */
	public List<Integer> getColorsIndices() {
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
	
	public MaterialType getMaterialType() {
		return materialType;
	}

	private List<Integer> getOrderedReferences(Function<OBJDataReferenceMapper, Integer> method) {
		List<OBJDataReferenceMapper> orderedByIndex = dataReferences.stream().filter(dataRefMapper -> {
			return dataRefMapper.dataRef.hasVertexIndex();
		}).sorted(Comparator.comparingInt(OBJDataReferenceMapper::getIndex)).collect(Collectors.toList());

		HashSet<Integer> uniqueIndex = new HashSet<>();
		ArrayList<Integer> indices = new ArrayList<>();
		for (OBJDataReferenceMapper mapper : orderedByIndex) {
			if (uniqueIndex.add(mapper.getIndex())) {
				int index = method.apply(mapper);
				if (index > -1) {
					indices.add(index);
				}
			}
		}
		return indices;
	}

	/**
	 * compare dataReference configuration for specified index. dataReferences is
	 * constructed in met order.
	 * 
	 * @param dataReferencesIndex
	 * @param ref
	 * @return code{true} if configuration is already known.
	 */
	public boolean isConfigRegistered(int dataReferencesIndex, OBJDataReference ref) {
		OBJDataReferenceMapper currentDataRef = dataReferences.get(dataReferencesIndex);
		if (ref.equals(currentDataRef.dataRef)) {
			return true;
		}
		if (currentDataRef.children.contains(ref)) {
			return true;
		}
		return false;
	}

	/**
	 * @param registeredIndex
	 * @param ref
	 * @param materialIndex
	 */
	public void addChildConfig(int registeredIndex, OBJDataReferenceMapper refMapper, int materialIndex) {
		OBJDataReferenceMapper registeredConfig = dataReferences.get(registeredIndex);
		OBJDataReferenceMapper firstConfig = registeredConfig.getParentIfPresent();
		firstConfig.children.add(refMapper.dataRef);
		OBJDataReferenceMapper currentDataRef = dataReferences.get(refMapper.mapperIndex);
		currentDataRef.setParent(firstConfig);
	}

	/**
	 * Alter ref.vertexIndex to duplicate it in the end list.
	 * 
	 * @param ref
	 * @param materialIndex
	 * @return OBJDataReferenceMapper new mapper
	 */
	public OBJDataReferenceMapper duplicateVertex(OBJDataReference ref, int materialIndex) {
		ref.vertexIndex = initialReferencesSize;
		initialReferencesSize++;
		return addVertex(ref, materialIndex);
	}
}
