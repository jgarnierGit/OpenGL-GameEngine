package models;

import java.io.FileNotFoundException;

import models.imports.Cube;
import models.imports.Grass;
import models.imports.Plane;
import renderEngine.Loader;

public abstract class Model3D {
	private int vaoID;
	private Container3D container3D;
	protected TextureContainer textureContainer; //TODO move this parameter to another class.
	
	protected Model3D(Container3D container3D, TextureContainer textureContainer, Loader loader) throws FileNotFoundException {
		this.container3D = container3D;
		this.textureContainer = textureContainer;
		vaoID = loader.load3DContainerToVAO(this.container3D);
		loader.loadTextureToVAO(this.textureContainer);
	}

	public static Model3D importCube(Loader loader) throws FileNotFoundException {
		return new Cube(loader);
	}
	
	public static Model3D importPlan(Loader loader) throws FileNotFoundException {
		return new Plane(loader);
	}
	
	public static Model3D importGrass(Loader loader) throws FileNotFoundException {
		return new Grass(loader);
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
		return this.textureContainer;
	}
}
