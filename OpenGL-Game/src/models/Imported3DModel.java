package models;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import objectManager.MTLLoader;
import objectManager.OBJLoader;
import renderEngine.Loader;

public abstract class Imported3DModel extends Model3D{
	static final Path parentPath = Paths.get("./", "resources", "3D");
	
	public TextureContainer getTextureContainer() {
		return super.textureContainer;
	}
	
	protected Imported3DModel(String modelName,String texturePathName, Loader loader) throws FileNotFoundException {
		super(OBJLoader.loadModel(Paths.get(parentPath.toString(), modelName).toFile()),
				MTLLoader.loadModel(Paths.get(parentPath.toString(), texturePathName).toFile()),loader);
	}
}
