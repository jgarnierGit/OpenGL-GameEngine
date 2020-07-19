package models.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector4f;

import com.mokiat.data.front.parser.MTLMaterial;

import utils.GeomUtils;

/**
 * TODO move to modelsLibrary OBJContent can share multiple textures, but
 * coordinates are absolute.
 * 
 * @author chezmoi
 *
 */
public class OBJContent {
	private final List<Integer> indices;
	private final VBOContent positions;
	private final MaterialContent material;
	private final VBOContent normals;
	// duplicate alias to facilitate debugging
	protected final String alias;
	private Logger logger = Logger.getLogger("OBJContent");
	public static final float[] DEFAULT_COLOR = new float[] { 1.0f, 0.0f, 1.0f, 1.0f };

	private OBJContent(List<Integer> indices, VBOContent positions, MaterialContent material, VBOContent normals,
			String alias) {
		this.indices = indices;
		this.positions = positions;
		this.material = material;
		this.normals = normals;
		this.alias = alias;
	}

	private OBJContent(String alias, int positionShaderIndex, int materialShaderIndex, int normalShaderIndex) {
		this.indices = new ArrayList<>();
		positions = VBOContent.createEmpty(positionShaderIndex);
		material = MaterialContent.createEmpty(materialShaderIndex);
		normals = VBOContent.createEmpty(normalShaderIndex);
		this.alias = alias;
	}

	/**
	 * return array of points coordinates.
	 * 
	 * @return VBOContent of points coordinates.
	 */
	public VBOContent getPositions() {
		return this.positions;
	}

	public MaterialContent getMaterials() {
		return this.material;
	}

	public VBOContent getNormals() {
		return this.normals;
	}

	public List<VBOContent> getVBOs() {
		return Arrays.asList(positions, material.getVBOContent(), normals);
	}

	public Vector4f getDefaultColor() {
		return new Vector4f(DEFAULT_COLOR[0], DEFAULT_COLOR[1], DEFAULT_COLOR[2], DEFAULT_COLOR[3]);
	}

	public List<Integer> getIndices() {
		return this.indices;
	}

	public int[] getIndicesAsPrimitiveArray() {
		return ArrayUtils.toPrimitive(this.indices.toArray(new Integer[this.indices.size()]));
	}

	/**
	 * 
	 * @param vertexIndices
	 * @param positions
	 * @param materialContent
	 * @param normals
	 * @return OBJContent or null if error detected in VBO parameters
	 */
	public static OBJContent create(List<Integer> vertexIndices, VBOContent positions, MaterialContent materialContent,
			VBOContent normals, String alias) {
		OBJContent objContent = new OBJContent(alias, -1, -1, -1);
		boolean error = false;
		if (positions.getDimension() != 3) {
			objContent.logger.severe("Position must be Vector3f");
			error = true;
		}
		if (normals.getDimension() != 3) {
			objContent.logger.severe("Normals must be Vector3f");
			error = true;
		}
		if (error) {
			return null;
		}
		return new OBJContent(vertexIndices, positions, materialContent, normals, alias);
	}

	public static OBJContent copy(OBJContent objContent) {
		VBOContent positions = null;
		if (objContent.positions.getDimension() == 2) {
			positions = VBOContent.create2f(objContent.positions.getShaderInputIndex(),
					GeomUtils.createVector2fList(objContent.positions.getContent()));
		} else if (objContent.positions.getDimension() == 3) {
			positions = VBOContent.create3f(objContent.positions.getShaderInputIndex(),
					GeomUtils.createVector3fList(objContent.positions.getContent()));
		} else {
			positions = VBOContent.createEmpty(-1);
			objContent.logger.warning("position shader index desactivated (set to -1) due to unknown dimension : "
					+ objContent.positions.getDimension());
		}

		MaterialContent material = MaterialContent.copy(objContent.material);

		VBOContent normals = VBOContent.create3f(objContent.normals.getShaderInputIndex(),
				GeomUtils.createVector3fList(objContent.normals.getContent()));

		ArrayList<Integer> indices = new ArrayList<>(objContent.indices);
		return new OBJContent(indices, positions, material, normals, objContent.alias + "");
	}

	public static OBJContent createEmpty(String alias, int positionShaderIndex, int colorShaderIndex,
			int textureShaderIndex, int normalShaderIndex) {
		int materialShaderIndex = textureShaderIndex == -1 ? colorShaderIndex : textureShaderIndex;
		return new OBJContent(alias, positionShaderIndex, materialShaderIndex, normalShaderIndex);
	}

	public void addMaterialLibrary(MaterialLibrary materials) {
		material.setUrls(materials.getMaterialLibrary().getMaterials().stream().map(MTLMaterial::getName)
				.collect(Collectors.toList()));
	}

	public void setMaterials(int inputShaderIndex, MaterialLibrary materials) {
		material.setMaterialAsImage(inputShaderIndex, materials.getMaterialLibrary().getMaterials().stream()
				.map(MTLMaterial::getName).collect(Collectors.toList()), materials.getNumberOfRows());
	}
}
