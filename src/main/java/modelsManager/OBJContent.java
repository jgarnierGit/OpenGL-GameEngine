package modelsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import modelsLibrary.MaterialContent;
import modelsManager.bufferCreator.VBOContent;

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
	// FIXME extract those shader position which led to confusion OBJContent must
	// not specify itself shader Position.
	private static final int POSITION_INDEX = 0;
	private static final int COLOR_INDEX = 1;
	private static final int NORMAL_INDEX = 2;
	public static final float[] DEFAULT_COLOR = new float[] { 1.0f, 0.0f, 1.0f, 1.0f };

	private OBJContent(int dimension, List<Integer> indices, VBOContent positions, MaterialContent material,
			VBOContent normals) {
		this.dimension = dimension;
		this.indices = indices;
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
	 * TODO remove this creator to abstract index shader specification. use create
	 * with @see VBOContent
	 * 
	 * @param vertexIndices
	 * @param positions2
	 * @param normalsVector
	 * @param materials
	 * @return
	 */
	public static OBJContent create(List<Integer> vertexIndices, List<Vector3f> positions2,
			List<Vector3f> normalsVector, MaterialContent materialContent) {
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
		return new OBJContent(3, vertexIndices, VBOContent.create(0, 3, positionsList), materialContent,
				VBOContent.create(2, 3, normals));
	}

	public static OBJContent create(List<Integer> vertexIndices, VBOContent positions, MaterialContent materialContent,
			VBOContent normals) {
		OBJContent objContent = OBJContent.createEmpty(positions.getDimension());
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
			return objContent;
		}
		return new OBJContent(positions.getDimension(), vertexIndices, positions, materialContent, normals);
	}

	/**
	 * FIXME this empty creator is really confusing...
	 * 
	 * @param dimension
	 * @return
	 */
	public static OBJContent createEmpty(int dimension) {
		// TODO extract indexes to external list
		VBOContent positions = VBOContent.createEmpty(POSITION_INDEX, dimension);
		// TODO remove this weird empty creator...
		MaterialContent material = MaterialContent.createEmpty(COLOR_INDEX, 4);// VBOContent.createEmpty(COLOR_INDEX,
																				// 4);
		VBOContent normals = VBOContent.createEmpty(NORMAL_INDEX, 3);
		return new OBJContent(dimension, new ArrayList<>(), positions, material, normals);
	}

	public static OBJContent copy(OBJContent objContent) {
		VBOContent positions = VBOContent.create(objContent.positions.getShaderInputIndex(),
				objContent.positions.getDimension(), new ArrayList<>(objContent.positions.getContent()));
		MaterialContent material = null;
		switch (objContent.material.getType()) {
		case COLOR:
			List<Vector4f> colorsContent = new ArrayList<>();
			for (int index = 0; index < objContent.material.getContent().getContent().size(); index += 4) {
				float x = objContent.material.getContent().getContent().get(index);
				float y = objContent.material.getContent().getContent().get(index + 1);
				float z = objContent.material.getContent().getContent().get(index + 2);
				float w = objContent.material.getContent().getContent().get(index + 3);
				colorsContent.add(new Vector4f(x, y, z, w));
			}

			material = MaterialContent.createColorContent(objContent.material.getContent().getShaderInputIndex(),
					colorsContent);
			break;
		case IMAGE:
			List<Vector2f> textureContent = new ArrayList<>();
			for (int index = 0; index < objContent.material.getContent().getContent().size(); index += 2) {
				float x = objContent.material.getContent().getContent().get(index);
				float y = objContent.material.getContent().getContent().get(index + 1);
				textureContent.add(new Vector2f(x, y));
			}
			material = MaterialContent.createImageContent(objContent.material.getContent().getShaderInputIndex(),
					textureContent, objContent.material.getUrl().get());
			break;
		default:
			break;
		}
		VBOContent normals = VBOContent.create(objContent.normals.getShaderInputIndex(),
				objContent.normals.getDimension(), new ArrayList<>(objContent.normals.getContent()));
		ArrayList<Integer> indices = new ArrayList<>(objContent.indices);
		return new OBJContent(objContent.dimension, indices, positions, material, normals);
	}
}
