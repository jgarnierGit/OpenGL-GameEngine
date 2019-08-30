package renderEngine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.TextureLoader;

import models.ModelUtils;

/**
 * Handles the loading of geometry data into VAOs. It also keeps track of all
 * the created VAOs and VBOs so that they can all be deleted when the game
 * closes.
 * 
 * @author Karl
 *
 */
public class Loader {

	public class VBOIndex{
		public final static int POSITION_INDEX = 0;
		public final static int TEXTURE_INDEX = 1;
		public final static int NORMAL_INDEX = 2;
		public static final int COLOR_INDEX = 3;
	}

	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> texturesToClean = new ArrayList<Integer>();
	
	/**
	 * Creates a VAO and stores the position data of the vertices into attribute
	 * 0 of the VAO.	
	 * @param model
	 * @return VAOId linked to the loaded model.
	 */
	public int loadModelToVAO(ModelUtils modelUtils) {
		int vaoID = createVAO();
		// OBJUtils objUtils, MTLUtils mtlUtils
		bindIndicesBuffer(modelUtils.getOBJUtils().getIndices());
		//TODO refactor architecture.
		storeDataFloatInAttrList(VBOIndex.POSITION_INDEX,modelUtils.getOBJUtils().getPositions().getDimension(), modelUtils.getOBJUtils().getPositions().getContent());
		if(modelUtils.getMtlUtils().isUsingImage()) {
			storeDataFloatInAttrList(VBOIndex.TEXTURE_INDEX,modelUtils.getOBJUtils().getMaterial().getDimension(), modelUtils.getOBJUtils().getMaterial().getContent());
			float[] emptyColor  = {0.0f,0.0f,0.0f,0.0f};
			storeDataFloatInAttrList(VBOIndex.COLOR_INDEX,4, emptyColor);
		}
		else {
			float[] emptyTexture  = {0.0f,0.0f};
			storeDataFloatInAttrList(VBOIndex.TEXTURE_INDEX,2, emptyTexture);
			storeDataFloatInAttrList(VBOIndex.COLOR_INDEX,modelUtils.getOBJUtils().getMaterial().getDimension(), modelUtils.getOBJUtils().getMaterial().getContent());
		}
		storeDataFloatInAttrList(VBOIndex.NORMAL_INDEX,modelUtils.getOBJUtils().getNormals().getDimension(),modelUtils.getOBJUtils().getNormals().getContent());
		unbindVAO();
		texturesToClean.addAll(modelUtils.getMtlUtils().getTexturesIndexes());
		return vaoID;
	}
	
	public int loadGUIToVAO(float[] positions) {
		int vaoId = createVAO();
		this.storeDataFloatInAttrList(VBOIndex.POSITION_INDEX, 2, positions);
		unbindVAO();
		return vaoId;
	}
	
	public int loadTexture(String name) {
		try (InputStream image = Loader.class.getClassLoader().getResourceAsStream("2D/"+ name)){
			int id = TextureLoader.getTexture("png", image).getTextureID();
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
			texturesToClean.add(id);
			return id;
		} catch (IOException e) {
			System.err.println("["+ name +"] not found ");
		}
		return 0;
	}

	/**
	 * Deletes all the VAOs and VBOs when the game is closed. VAOs and VBOs are
	 * located in video memory.
	 */
	public void cleanUp() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for( int texture : texturesToClean) {
			GL11.glDeleteTextures(texture);
		}
	}

	/**
	 * Creates a new VAO, makes it active by binding it, and returns its ID. A VAO holds geometry data that we
	 * can render and is physically stored in memory on the GPU, so that it can
	 * be accessed very quickly during rendering.
	 * 
	 * @return The ID of the newly created VAO.
	 */
	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}

	/**
	 * Stores the positions data in the active VBO by using the
	 * glBufferData method.
	 * 
	 * Then connects the VBO to the VAO using the glVertexAttribPointer()
	 * method.
	 * 
	 * @param attributeNumber
	 *            - The number of the attribute of the VAO where the data is to
	 *            be stored.
	 * @param arrayList
	 *            - The geometry data to be stored in the VAO, in this case the
	 *            positions of the vertices.
	 */
	private void storeDataFloatInAttrList(int attributeNumber, int coordinateSize, float[] arrayList) {
		int vboID = GL15.glGenBuffers();

		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(arrayList);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Unbinds the VAO after we're finished using it. If we want to edit or use
	 * the VAO we would have to bind it again first.
	 */
	private void unbindVAO() {
		GL30.glBindVertexArray(0);
	}

	/**
	 * Prepare datas for VBO storing.
	 * 
	 * @param arrayList
	 *            - The float data that is going to be stored in the buffer.
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
	 * @param indices
	 */
	private void bindIndicesBuffer(int[] indices) {
		int vboId = GL15.glGenBuffers();
		vbos.add(vboId);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		// Deselect (bind to 0) the VBO
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); // necessary if you want a result on a screen
	}
	
	/**
	 * Converts the indices from an int array to an IntBuffer so that they can
	 * be stored in a VBO. Very similar to the storeDataInFloatBuffer() method
	 * below.
	 * @Duplicated with {Renderer.storeDataInIntBuffer}
	 * @param data
	 *           - The indices in an int[].
	 * @return The indices in a buffer.
	 */
	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

}
