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
	protected int dimension;
	private final List<Integer> indices;
	private final VBOContent positions;
	private final MaterialContent material;
	private final VBOContent normals;
	private Logger logger = Logger.getLogger("OBJContent");
	public static final float[] DEFAULT_COLOR = new float[] { 1.0f, 0.0f, 1.0f, 1.0f };

	private OBJContent(int dimension, List<Integer> indices, VBOContent positions, MaterialContent material,
			VBOContent normals) {
		this.dimension = dimension;
		this.indices = indices;
		this.positions = positions;
		this.material = material;
		this.normals = normals;
	}

	private OBJContent() {
		this.indices = new ArrayList<>();
		positions = null;
		material = null;
		normals = null;
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
	 * return array of material coordinates mapping for each points.
	 * 
	 * @return VBOContent of colors for each points.
	 */
	public VBOContent getMaterialsContent() {
		return this.material.getContent();
	}

	public MaterialContent getMaterials() {
		return this.material;
	}

	public VBOContent getNormals() {
		return this.normals;
	}

	public List<VBOContent> getVBOs() {
		return Arrays.asList(positions, material.getContent(), normals);
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
			VBOContent normals) {
		OBJContent objContent = new OBJContent();
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
		return new OBJContent(positions.getDimension(), vertexIndices, positions, materialContent, normals);
	}

	public static OBJContent copy(OBJContent objContent) {
		VBOContent positions = null;
		if(objContent.positions.getDimension() == 2) {
			positions = VBOContent.create2f(objContent.positions.getShaderInputIndex(), GeomUtils.createVector2fList(objContent.positions.getContent()));
		}
		else if(objContent.positions.getDimension() == 3) {
			positions = VBOContent.create3f(objContent.positions.getShaderInputIndex(), GeomUtils.createVector3fList(objContent.positions.getContent()));
		}
		
		MaterialContent material = MaterialContent.copy(objContent.material);
		
		VBOContent normals = VBOContent.create3f(objContent.normals.getShaderInputIndex(),
				GeomUtils.createVector3fList(objContent.normals.getContent()));

		ArrayList<Integer> indices = new ArrayList<>(objContent.indices);
		return new OBJContent(objContent.dimension, indices, positions, material, normals);
	}
	
	public void addMaterialLibrary(MaterialLibrary materials) {
		material.setUrls(materials.getMaterialLibrary().getMaterials().stream().map(MTLMaterial::getName)
				.collect(Collectors.toList()));
	}

	public void setMaterials(int inputShaderIndex, MaterialLibrary materials) {
		material.setMaterialAsImage(inputShaderIndex, materials.getMaterialLibrary().getMaterials().stream().map(MTLMaterial::getName)
				.collect(Collectors.toList()), materials.getNumberOfRows());
	}
}
