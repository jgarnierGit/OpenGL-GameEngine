package modelsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
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
	protected int dimension;
	private final List<Integer> indices;
	private final VBOContent positions;
	private final VBOContent material;
	private final VBOContent normals;
	private static final int POSITION_INDEX = 0;
	private static final int COLOR_INDEX = 1;
	private static final int NORMAL_INDEX = 2;
	public static final float[] DEFAULT_COLOR = new float[] { 1.0f, 0.0f, 1.0f, 1.0f };

	public OBJUtils(int dimension, VBOContent positions, VBOContent material, VBOContent normals) {
		this.dimension = dimension;
		indices = new ArrayList<>();
		this.positions = positions;
		this.material = material;
		this.normals = normals;
	}

	/**
	 * 
	 * @return dimension count to apply for each vertice.
	 */
	public int getDimension() {
		return this.dimension;
	}

	/**
	 * return array of points coordinates.
	 * 
	 * @return VBOContent of points coordinates.
	 */
	public VBOContent getPoints() {
		return this.positions;
	}

	/**
	 * return array of colors for each points.
	 * 
	 * @return VBOContent of colors for each points.
	 */
	public VBOContent getColors() {
		return this.material;
	}

	public VBOContent getNormals() {
		return this.normals;
	}

	public List<VBOContent> getVBOs() {
		return Arrays.asList(positions, material, normals);
	}

	public Vector4f getDefaultColor() {
		return new Vector4f(DEFAULT_COLOR[0], DEFAULT_COLOR[1], DEFAULT_COLOR[2], DEFAULT_COLOR[3]);
	}

	public boolean hasTransparency() {
		List<Float> content = this.material.getContent();
		for (int i = 3; i <= content.size(); i += 4) {
			if (content.get(i) < 1f) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * TODO extract to an OBJImporter class, rewrite
	 * 
	 * @param objModel
	 * @param materialTypes
	 * @param mtlUtils
	 */
	public OBJUtils(OBJModel objModel, MTLUtils mtlUtils) {
		ArrayList<Integer> indicesList = new ArrayList<>();
		int indicesListSize = 0;
		for (OBJObject obj : objModel.getObjects()) {
			for (OBJMesh mesh : obj.getMeshes()) {
				for (OBJFace face : mesh.getFaces()) {
					indicesListSize += face.getReferences().size();
				}
			}
		}

		int[] registeredIndice = new int[indicesListSize];
		Arrays.fill(registeredIndice, -1);

		OBJDataReferenceUtils objDataReferenceUtils = new OBJDataReferenceUtils(objModel, mtlUtils);
		for (OBJObject obj : objModel.getObjects()) {
			for (OBJMesh mesh : obj.getMeshes()) {
				int materialIndex = mtlUtils.getMaterials().indexOf(mtlUtils.getMaterial(mesh.getMaterialName()));
				for (OBJFace face : mesh.getFaces()) {
					for (OBJDataReference ref : face.getReferences()) {
						if (ref.hasVertexIndex()) {
							// ref.vertexIndex only contains original ids.
							int registeredIndex = registeredIndice[ref.vertexIndex];
							if (registeredIndex != -1) {
								if (objDataReferenceUtils.isConfigRegistered(registeredIndex, ref)) {
									indicesList.add(ref.vertexIndex);
								} else {
									OBJDataReferenceMapper mapper = objDataReferenceUtils.duplicateVertex(ref,
											materialIndex);
									registeredIndice[ref.vertexIndex] = objDataReferenceUtils.getDataReferences()
											.size();
									objDataReferenceUtils.addChildConfig(registeredIndex, mapper, materialIndex);
									indicesList.add(ref.vertexIndex);
								}
							} else {
								registeredIndice[ref.vertexIndex] = objDataReferenceUtils.getDataReferences().size();
								indicesList.add(ref.vertexIndex);
								objDataReferenceUtils.addVertex(ref, materialIndex);
							}
						} else {
							System.out.println(obj.getName() + " has no vertexIndex");
						}
					}
				}
			}
		}

		positions = setPositionContent(objDataReferenceUtils);
		normals = setNormalContent(objDataReferenceUtils);
		// TODO update to set type for each material of an object
		material = setMaterialContent(objDataReferenceUtils, mtlUtils.getMaterials());

		indices = new int[indicesList.size()];
		for (int i = 0; i < indicesList.size(); i++) {
			indices[i] = indicesList.get(i);
		}
	}

	private VBOContent setMaterialContent(OBJDataReferenceUtils objDataReferenceUtils, List<MaterialMapper> materials) {
		int dimension = 0;
		int shaderPosition = 0;
		List<Float> coords = new ArrayList<>();
		if (materials.get(0).getType() == MaterialType.IMAGE) {
			dimension = 2;
			shaderPosition = 1;
			List<OBJTexCoord> textCoordList = objDataReferenceUtils.getTexturesCoordsIndices().stream().map(indice -> {
				return objDataReferenceUtils.getTextCoordsList().get(indice);
			}).collect(Collectors.toList());
			for (OBJTexCoord textCoord : textCoordList) {
				coords.add(textCoord.u);
				coords.add(textCoord.v);
			}
		} else {
			dimension = 4;
			shaderPosition = 3;
			List<MaterialMapper> colorsList = objDataReferenceUtils.getColorsIndices().stream().map(indice -> {
				return objDataReferenceUtils.getMaterialsList().get(indice);
			}).collect(Collectors.toList());
			for (MaterialMapper color : colorsList) {
				if (color.getColor() != null) {
					coords.add(color.getColor().x);
					coords.add(color.getColor().y);
					coords.add(color.getColor().z);
					coords.add(color.getColor().w);
				} else {
					coords.add(0f);
					coords.add(0f);
					coords.add(0f);
					coords.add(0f);
				}
			}
		}
		return VBOContent.create(shaderPosition, dimension, coords);
	}

	private VBOContent setPositionContent(OBJDataReferenceUtils objDataReferenceUtils) {
		List<OBJVertex> vertices = objDataReferenceUtils.getPositionsIndices().stream().map(indice -> {
			return objDataReferenceUtils.getVerticesList().get(indice);
		}).collect(Collectors.toList());
		List<Float> positionsList = new ArrayList<>();
		for (OBJVertex vertex : vertices) {
			positionsList.add(vertex.x);
			positionsList.add(vertex.y);
			positionsList.add(vertex.z);
		}
		return VBOContent.create(0, 3, positionsList);
	}

	private VBOContent setNormalContent(OBJDataReferenceUtils objDataReferenceUtils) {
		List<OBJNormal> normalsListObj = objDataReferenceUtils.getNormalsIndices().stream().map(indice -> {
			return objDataReferenceUtils.getNormalsList().get(indice);
		}).collect(Collectors.toList());

		List<Float> normalsList = new ArrayList<>();
		for (OBJNormal normal : normalsListObj) {
			normalsList.add(normal.x);
			normalsList.add(normal.y);
			normalsList.add(normal.z);
		}
		return VBOContent.create(2, 3, normalsList);
	}

	public List<Integer> getIndices() {
		return this.indices;
	}

	public int[] getIndicesAsPrimitiveArray() {
		return ArrayUtils.toPrimitive(this.indices.toArray(new Integer[this.indices.size()]));
	}

	/**
	 * TODO evolve this creator to allow pass materials or color step by step
	 * 
	 * @param <T>
	 * @param vertexIndices
	 * @param positions2
	 * @param normalsVector
	 * @param materials
	 * @return
	 */
	public static <T> OBJUtils create(List<Integer> vertexIndices, List<Vector3f> positions2,
			List<Vector3f> normalsVector, List<T> materials) {

		int materialDimension = 0;
		int shaderIndex = 0;
		List<Float> matList = new ArrayList<>();
		// TODO absolutly unclear use of parametrized types... change to builder
		// (materials : 2f / 4f/ none], normals : none if no surfacic )
		if (materials.get(0) instanceof Vector4f) {
			materialDimension = 4;
			shaderIndex = 3;
			for (T mat : materials) {
				Vector4f matV4f = (Vector4f) mat;
				matList.add(matV4f.x);
				matList.add(matV4f.y);
				matList.add(matV4f.z);
				matList.add(matV4f.w);
			}
		} else if (materials.get(0) instanceof Vector2f) {
			materialDimension = 2;
			shaderIndex = 1;
			for (T mat : materials) {
				Vector2f matV2f = (Vector2f) mat;
				matList.add(matV2f.x);
				matList.add(matV2f.y);
			}
		} else {
			matList = null;
			shaderIndex = -1;
			System.err.println("unsupported type for materials.");
		}
		List<Float> normals = new ArrayList<>();
		for (Vector3f normal : normalsVector) {
			normals.add(normal.x);
			normals.add(normal.y);
			normals.add(normal.z);
		}

		List<Float> positionsList = new ArrayList<>();
		for (Vector3f position : positions2) {
			positionsList.add(position.x);
			positionsList.add(position.y);
			positionsList.add(position.z);
		}
		return new OBJUtils(vertexIndices, VBOContent.create(0, 3, positionsList), VBOContent.create(2, 3, normals),
				VBOContent.create(shaderIndex, materialDimension, matList));
	}

	private OBJUtils(List<Integer> vertexIndices, VBOContent positions2, VBOContent normalsVector,
			VBOContent materials) {
		this.indices = vertexIndices;
		this.positions = positions2;
		this.normals = normalsVector;
		this.material = materials;
	}

	public static OBJUtils createEmpty(int dimension) {
		VBOContent positions = VBOContent.createEmpty(POSITION_INDEX, dimension);
		VBOContent material = VBOContent.createEmpty(COLOR_INDEX, 4);
		VBOContent normals = VBOContent.createEmpty(NORMAL_INDEX, 3);
		return new OBJUtils(dimension, positions, material, normals);
	}

	public static OBJUtils copy(OBJUtils objContent) {
		VBOContent positions = VBOContent.create(objContent.positions.getShaderInputIndex(),
				objContent.positions.getDimension(), new ArrayList<>(objContent.positions.getContent()));
		VBOContent material = VBOContent.create(objContent.material.getShaderInputIndex(),
				objContent.material.getDimension(), new ArrayList<>(objContent.material.getContent()));
		VBOContent normals = VBOContent.create(objContent.normals.getShaderInputIndex(),
				objContent.normals.getDimension(), new ArrayList<>(objContent.normals.getContent()));
		return new OBJUtils(objContent.dimension, positions, material, normals);
	}
}
