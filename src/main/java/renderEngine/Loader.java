package renderEngine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.opengl.PNGDecoder.Format;

import modelsManager.ModelUtils;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * Handles the loading of geometry data into VAOs. It also keeps track of all
 * the created VAOs and VBOs so that they can all be deleted when the game
 * closes.
 * 
 * @author Karl
 *
 */
public class Loader {

	public class VBOIndex {
		public static final int POSITION_INDEX = 0;
		public static final int TEXTURE_INDEX = 1;
		public static final int NORMAL_INDEX = 2;
		public static final int COLOR_INDEX = 3;
		private VBOIndex() {}
	}

	/**
	 * improve that part by linking : vao  1.n -> 1.n vbo;
	 * vao may contains one or more vbo,
	 * vbo may be shared by many vao.
	 * reloading / deleting shared vbos affects each concerned vao.
	 * 
	 * vbos are loaded once vao is binded.
	 * Doing so, this might optimized reload of position which at this state froze render for <0.5sec
	 */
	private Map<Integer, VboManager> vaos = new HashMap<>();
	private List<Integer> texturesToClean = new ArrayList<>();

	/**
	 * Creates a VAO and stores the position data of the vertices into attribute 0
	 * of the VAO.
	 * 
	 * @param model
	 * @return VAOId linked to the loaded model.
	 */
	public int loadModelToVAO(ModelUtils modelUtils) {
		int vaoID = createAndBindVAO();
		// OBJUtils objUtils, MTLUtils mtlUtils
		bindIndicesBuffer(vaoID, modelUtils.getOBJUtils().getIndices());
		// TODO refactor architecture.
		storeDataFloatInAttrList(vaoID, VBOIndex.POSITION_INDEX, modelUtils.getOBJUtils().getPositions().getDimension(),
				modelUtils.getOBJUtils().getPositions().getContent());
		if (modelUtils.getMtlUtils().isUsingImage()) {
			storeDataFloatInAttrList(vaoID, VBOIndex.TEXTURE_INDEX, modelUtils.getOBJUtils().getMaterial().getDimension(),
					modelUtils.getOBJUtils().getMaterial().getContent());
			float[] emptyColor = { 0.0f, 0.0f, 0.0f, 0.0f };
			storeDataFloatInAttrList(vaoID, VBOIndex.COLOR_INDEX, 4, emptyColor);
		} else {
			float[] emptyTexture = { 0.0f, 0.0f };
			storeDataFloatInAttrList(vaoID, VBOIndex.TEXTURE_INDEX, 2, emptyTexture);
			storeDataFloatInAttrList(vaoID, VBOIndex.COLOR_INDEX, modelUtils.getOBJUtils().getMaterial().getDimension(),
					modelUtils.getOBJUtils().getMaterial().getContent());
		}
		storeDataFloatInAttrList(vaoID, VBOIndex.NORMAL_INDEX, modelUtils.getOBJUtils().getNormals().getDimension(),
				modelUtils.getOBJUtils().getNormals().getContent());
		unbindVAO();
		texturesToClean.addAll(modelUtils.getMtlUtils().getTexturesIndexes());
		return vaoID;
	}

	public int loadToVAO(float[] positions, int dimensions) {
		int vaoId = createAndBindVAO();
		this.storeDataFloatInAttrList(vaoId, VBOIndex.POSITION_INDEX, dimensions, positions);
		unbindVAO();
		return vaoId;
	}
	
	public void reloadVAOPosition(int vaoId, float[] positions, int dimensions) {
		GL30.glBindVertexArray(vaoId);
		this.storeDataFloatInAttrList(vaoId, VBOIndex.POSITION_INDEX, dimensions, positions);
		unbindVAO();
	}

	public int loadTexture(String name) {
		try (InputStream image = Loader.class.getClassLoader().getResourceAsStream("2D/" + name)) {
			int id = TextureLoader.getTexture("png", image).getTextureID();
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f); // TODO test mipmapping values.
			texturesToClean.add(id);
			return id;
		} catch (IOException e) {
			System.err.println("[" + name + "] not found ");
		}
		return 0;
	}

	public int loadCubeMap(String[] texturesFiles) {
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		for (int i = 0; i < texturesFiles.length; i++) {
			try (InputStream image = Loader.class.getClassLoader()
					.getResourceAsStream("2D/" + texturesFiles[i] + ".png")) {
				PNGDecoder decoder = new PNGDecoder(image);
				ByteBuffer imageByteBuffer = ByteBuffer.allocateDirect(4*decoder.getWidth()*decoder.getHeight());
				decoder.decode(imageByteBuffer, decoder.getWidth()*4,PNGDecoder.RGBA);
				GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, decoder.getWidth(),
						decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
						(ByteBuffer) imageByteBuffer.flip());
				GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
				
				GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
				GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
				GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			} catch (IOException e) {
				System.err.println("[" + texturesFiles[i] + "] not found ");
			}
		}
		texturesToClean.add(texID);
		return texID;
	}

	/**
	 * Deletes all the VAOs and VBOs when the game is closed. VAOs and VBOs are
	 * located in video memory.
	 */
	public void cleanUp() {
		for (Entry<Integer, VboManager> vboManager : vaos.entrySet()) {
			GL30.glDeleteVertexArrays(vboManager.getKey());
			vboManager.getValue().clean();
		}
		for (int texture : texturesToClean) {//TODO delete.
			GL11.glDeleteTextures(texture);
		}
	}
	
	/**
	 * only delete in memory, not cleaning 3D world.
	 * @param vaoId
	 */
	public void clean(int vaoId) {
		GL30.glDeleteVertexArrays(vaoId);
		vaos.get(vaoId).clean();
	}
	/**
	 * Creates a new VAO, makes it active by binding it, and returns its ID. A VAO
	 * holds geometry data that we can render and is physically stored in memory on
	 * the GPU, so that it can be accessed very quickly during rendering.
	 * 
	 * @return The ID of the newly created VAO.
	 */
	private int createAndBindVAO() {
		int vaoID = GL30.glGenVertexArrays();
		VboManager vaoManager = new VboManager();
		vaos.put(vaoID,vaoManager);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}

	/**
	 * Stores the positions data in the active VBO by using the glBufferData method.
	 * 
	 * Then connects the VBO to the VAO using the glVertexAttribPointer() method.
	 * 
	 * @param attributeNumber - The number of the attribute of the VAO where the
	 *                        data is to be stored.
	 * @param arrayList       - The geometry data to be stored in the VAO, in this
	 *                        case the positions of the vertices.
	 */
	private void storeDataFloatInAttrList(int vaoID, int attributeNumber, int coordinateSize, float[] arrayList) {
		int vboID = 0;
		if(!this.vaos.get(vaoID).getVbos().containsKey(attributeNumber)) {
			vboID = GL15.glGenBuffers();
		}
		else {
			vboID = this.vaos.get(vaoID).getVbos().get(attributeNumber);
		}
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(arrayList);
		//Use STATIC_DRAW when the data store contents will be modified once and used many times.
		//Use DYNAMIC_DRAW when the data store contents will be modified repeatedly and used many times.
		//Use STREAM_DRAW when the data store contents will be modified once and used at most a few times.
		// does not really affect perfs. those are hints not constraints.
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		//TODO might be useless to rebind vbo to vao if vbo already exists and is already binded to vao.
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		this.vaos.get(vaoID).addVbo(attributeNumber, vboID);
	}

	/**
	 * Unbinds the VAO after we're finished using it. If we want to edit or use the
	 * VAO we would have to bind it again first.
	 */
	private void unbindVAO() {
		GL30.glBindVertexArray(0);
	}

	/**
	 * Prepare datas for VBO storing.
	 * 
	 * @param arrayList - The float data that is going to be stored in the buffer.
	 * @return The FloatBuffer containing the data ready to be loaded into a VBO.
	 */
	private FloatBuffer storeDataInFloatBuffer(float[] arrayList) {
		// create direct ByteBuffer then cast it as float buffer.
		FloatBuffer buffer = BufferUtils.createFloatBuffer(arrayList.length);
		buffer.put(arrayList);
		buffer.flip();
		return buffer;
	}

	/**
	 * 
	 * @param vaoID 
	 * @param indices
	 */
	private void bindIndicesBuffer(int vaoID, int[] indices) {
		int vboId = GL15.glGenBuffers();
		vaos.get(vaoID).addUnmutableVbo(vboId);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		// Deselect (bind to 0) the VBO
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); // necessary if you want a result on a screen
	}

	/**
	 * Converts the indices from an int array to an IntBuffer so that they can be
	 * stored in a VBO. Very similar to the storeDataInFloatBuffer() method below.
	 * 
	 * @Duplicated with {Renderer.storeDataInIntBuffer}
	 * @param data - The indices in an int[].
	 * @return The indices in a buffer.
	 */
	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
