package models.importer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import com.mokiat.data.front.error.WFException;
import com.mokiat.data.front.parser.IMTLParser;
import com.mokiat.data.front.parser.IOBJParser;
import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.MTLMaterial;
import com.mokiat.data.front.parser.MTLParser;
import com.mokiat.data.front.parser.OBJDataReference;
import com.mokiat.data.front.parser.OBJFace;
import com.mokiat.data.front.parser.OBJMesh;
import com.mokiat.data.front.parser.OBJModel;
import com.mokiat.data.front.parser.OBJNormal;
import com.mokiat.data.front.parser.OBJObject;
import com.mokiat.data.front.parser.OBJParser;
import com.mokiat.data.front.parser.OBJTexCoord;
import com.mokiat.data.front.parser.OBJVertex;

import models.data.MaterialContent;
import models.data.MaterialType;
import models.data.OBJContent;
import models.data.VBOContent;
import models.importer.mokiatMapper.MaterialMapper;
import models.importer.mokiatMapper.OBJDataReferenceMapper;
import models.importer.mokiatMapper.OBJDataReferenceUtils;
import shaderManager.IShader3D;

/**
 * TODO add a check for triangle configuration.
 * 
 * @author chezmoi
 *
 */
public class OBJImporter {
	static final Path resourcePath = Paths.get("3D");
	private OBJModel objModel;
	private MTLLibrary mtlLibrary;
	private Logger logger = Logger.getLogger("OBJImporter");

	private OBJImporter() {
		// hidden
	}

	private void importOBJ(String objectDescriptor) throws WFException, FileNotFoundException, IOException {
		IOBJParser objParser = new OBJParser();
		objModel = new OBJModel();
		try (InputStream fileStream = OBJImporter.class.getClassLoader()
				.getResourceAsStream("3D/" + objectDescriptor)) {
			objModel = objParser.parse(fileStream);
		}
	}

	private void importMTL(String textureDescriptor) throws WFException, FileNotFoundException, IOException {
		IMTLParser mtlParser = new MTLParser();
		mtlLibrary = new MTLLibrary();
		try (InputStream fileStream = OBJImporter.class.getClassLoader()
				.getResourceAsStream("3D/" + textureDescriptor)) {
			mtlLibrary = mtlParser.parse(fileStream);
		}
	}

	public static OBJContent parse(IShader3D shader, String alias, String objectDescriptor, String textureDescriptor)
			throws WFException, FileNotFoundException, IOException {
		OBJImporter importer = new OBJImporter();
		importer.importOBJ(objectDescriptor);
		importer.importMTL(textureDescriptor);
		return importer.parse(shader, alias);
	}

	/**
	 * 
	 * @param objModel
	 * @param materialTypes
	 * @param mtlUtils
	 */
	private OBJContent parse(IShader3D shader, String alias) {
		List<MaterialMapper> materialMappers = getMaterialMappers();
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

		OBJDataReferenceUtils objDataReferenceUtils = OBJDataReferenceUtils.create(objModel, materialMappers);
		for (OBJObject obj : objModel.getObjects()) {
			for (OBJMesh mesh : obj.getMeshes()) {
				int materialIndex = getMaterialIndex(materialMappers, mesh.getMaterialName());
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

		VBOContent positions;
		MaterialContent material;
		VBOContent normals;

		positions = setPositionContent(shader.getPositionShaderIndex(), objDataReferenceUtils);
		material = setMaterialContent(shader.getColorShaderIndex(), shader.getTextureShaderIndex(),
				objDataReferenceUtils);
		// TODO update to set type for each material of an object
		normals = setNormalContent(shader.getNormalShaderIndex(), objDataReferenceUtils);

		ArrayList<Integer> indices = new ArrayList<>();
		for (int i = 0; i < indicesList.size(); i++) {
			indices.add(indicesList.get(i));
		}

		return OBJContent.create(indices, positions, material, normals, alias);
	}

	private int getMaterialIndex(List<MaterialMapper> materialMappers, String materialName) {
		int index = 0;
		for (MaterialMapper mapper : materialMappers) {
			if (mapper.getMaterial().getName().contentEquals(materialName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	private List<MaterialMapper> getMaterialMappers() {
		ArrayList<MaterialMapper> materialMappers = new ArrayList<>();
		for (MTLMaterial mat : mtlLibrary.getMaterials()) {
			MaterialMapper materialMapper = MaterialMapper.create(mat);
			materialMappers.add(materialMapper);
		}
		return materialMappers;
	}

	private MaterialContent setMaterialContent(int colorShaderIndex, int textureShaderIndex,
			OBJDataReferenceUtils objDataReferenceUtils) {
		MaterialContent matContent;

		if (objDataReferenceUtils.getMaterialType() == MaterialType.IMAGE) {
			List<Vector2f> coords = new ArrayList<>();
			List<OBJTexCoord> textCoordList = objDataReferenceUtils.getTexturesCoordsIndices().stream().map(indice -> {
				return objDataReferenceUtils.getTextCoordsList().get(indice);
			}).collect(Collectors.toList());
			for (OBJTexCoord textCoord : textCoordList) {
				coords.add(new Vector2f(textCoord.u, textCoord.v));
			}
			Path diffuseUrl = Paths.get(objDataReferenceUtils.getMaterialsList().get(0).getUrl().get());
			matContent = MaterialContent.createImageContent(textureShaderIndex, coords,
					diffuseUrl.getFileName().toString());
		} else {
			List<Vector4f> coords = new ArrayList<>();
			List<MaterialMapper> colorsList = objDataReferenceUtils.getColorsIndices().stream().map(indice -> {
				return objDataReferenceUtils.getMaterialsList().get(indice);
			}).collect(Collectors.toList());
			for (MaterialMapper color : colorsList) {
				Optional<Vector4f> optColor = color.getColor();
				if (optColor.isPresent()) {
					coords.add(optColor.get());
				} else {
					coords.add(new Vector4f(0f, 0f, 0f, 0f));
				}
			}
			matContent = MaterialContent.createColorContent(colorShaderIndex, coords);
		}

		return matContent;
	}

	private VBOContent setPositionContent(int positionShaderIndex, OBJDataReferenceUtils objDataReferenceUtils) {
		List<OBJVertex> vertices = objDataReferenceUtils.getPositionsIndices().stream().map(indice -> {
			return objDataReferenceUtils.getVerticesList().get(indice);
		}).collect(Collectors.toList());
		List<Vector3f> positionsList = new ArrayList<>();
		for (OBJVertex vertex : vertices) {
			positionsList.add(new Vector3f(vertex.x, vertex.y, vertex.z));
		}
		return VBOContent.create3f(positionShaderIndex, positionsList);
	}

	private VBOContent setNormalContent(int normalShaderIndex, OBJDataReferenceUtils objDataReferenceUtils) {
		List<OBJNormal> normalsListObj = objDataReferenceUtils.getNormalsIndices().stream().map(indice -> {
			return objDataReferenceUtils.getNormalsList().get(indice);
		}).collect(Collectors.toList());

		List<Vector3f> normalsList = new ArrayList<>();
		for (OBJNormal normal : normalsListObj) {
			normalsList.add(new Vector3f(normal.x, normal.y, normal.z));
		}
		return VBOContent.create3f(normalShaderIndex, normalsList);
	}
}
