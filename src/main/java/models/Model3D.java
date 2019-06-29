package models;

import java.io.FileNotFoundException;

import models.imports.Cube;
import models.imports.Grass;
import models.imports.Plane;
import renderEngine.Loader;

public abstract class Model3D {
	private int vaoID;
	private Container3D container3D;
	protected TextureContainer textureContainer;
	
	protected Model3D(Container3D container3D, TextureContainer textureContainer, Loader loader) throws FileNotFoundException {
		this.container3D = container3D;
		this.textureContainer = textureContainer;
		vaoID = loader.load3DContainerToVAO(this.container3D);
		loader.loadTextureToVAO(this.textureContainer);
	}
	
	/**
	 * @return The ID of the VAO which contains the data about all the geometry
	 *         of this model.
	 */
	public int getVaoID() {
		return vaoID;
	}
	
	public Container3D getContainer3D() {
		return container3D;
	}

	public TextureContainer getTextureContainer() {
		return textureContainer;
	}

	/**
	 * TODO seems like heavy thing to do the passerel for each method of Texture... find a good DP.
	 * @param textureID
	 * @param value
	 */
	public void setReflectivity(int textureID, int value) {
		this.textureContainer.getTextures().get(textureID).setReflectivity(value);
		
	}
	
	/**
	 * 
	 * @param textureID
	 * @param value
	 */
	public void setShineDamper(int textureID, int value) {
		this.textureContainer.getTextures().get(textureID).setShineDamper(value);
	}
	
	/**
	 * 
	 * @param textureID
	 * @param value
	 */
	public void setHasTransparency(int textureID, boolean value) {
		this.textureContainer.getTextures().get(textureID).setHasTransparency(value);
	}
	
	/**
	 * 
	 * @param textureID
	 * @param value
	 */
	public void setUseFakeLighting(int textureID, boolean value) {
		this.textureContainer.getTextures().get(textureID).setUseFakeLighting(value);
	}
}
