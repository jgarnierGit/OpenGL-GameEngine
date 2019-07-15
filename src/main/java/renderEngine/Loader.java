package renderEngine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Vector4f;

import models.Container3D;
import models.Model3D;
import models.TextureConfig;
import models.TextureContainer;
import models.TextureData;

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
	}

	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	
	/**
	 * TODO FIXME this is confusing that loadModelToVAO is the same thing as load3DContainerToVAO, but loadTextureToVAO is required for rendering well....
	 * @param model
	 */
	public void loadModelToVAO(Model3D model) {
		load3DContainerToVAO(model.getContainer3D());
	}
	
	/**
	 * Creates a VAO and stores the position data of the vertices into attribute
	 * 0 of the VAO.
	 * @param model 3Dmodel to load
	 * @return VAOId linked to the loaded model.
	 */
	public int load3DContainerToVAO(Container3D model) {
		int vaoID = createVAO();
		bindIndicesBuffer(model.getFlatIndices());
		storeDataFloatInAttrList(VBOIndex.POSITION_INDEX,3, model.getFlatPositions());
		TextureConfig textureConfig = model.getTextureConfig(); //TODO maybe not the best composition
		if(textureConfig.getUsingImage()) {
			storeDataFloatInAttrList(VBOIndex.TEXTURE_INDEX,2, model.getFlatTextures());
		}
		storeDataFloatInAttrList(VBOIndex.NORMAL_INDEX,3,model.getFlatNormals());
		unbindVAO();
		return vaoID;
	}

	public void loadTextureToVAO(TextureContainer textureContainer) {
		textures.addAll(textureContainer.getTextures().stream().map(TextureData::getTextureID).collect(Collectors.toList()));
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
		for( int texture : textures) {
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
	private void storeDataFloatInAttrList(int attributeNumber, int coordinateSize, ArrayList<Float> arrayList) {
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
	private FloatBuffer storeDataInFloatBuffer(ArrayList<Float> arrayList) {
		// create direct ByteBuffer then cast it as float buffer.
		float[] rawTypeList = new float[arrayList.size()];
		for(int i=0; i< arrayList.size(); i++) {
			rawTypeList[i] = arrayList.get(i);
		}
		FloatBuffer buffer = BufferUtils.createFloatBuffer(rawTypeList.length);
		buffer.put(rawTypeList);
		buffer.flip();
		return buffer;
	}


	/**
	 * 
	 * @param indices
	 */
	private void bindIndicesBuffer(ArrayList<Integer> indices) {
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
	private IntBuffer storeDataInIntBuffer(ArrayList<Integer> data) {
		int[] rawTypeList = new int[data.size()];
		for(int i=0; i< data.size(); i++) {
			rawTypeList[i] = data.get(i);
		}
		IntBuffer buffer = BufferUtils.createIntBuffer(rawTypeList.length);
		buffer.put(rawTypeList);
		buffer.flip();
		return buffer;
	}

}
