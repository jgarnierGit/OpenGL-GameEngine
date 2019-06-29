package models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import objectManager.MTLLoader;
import objectManager.OBJLoader;

public abstract class Imported3DModelContainer implements Container3D{

	public static Container3D importModel(String objectDescriptor) throws FileNotFoundException {
		return OBJLoader.loadModel(Paths.get(resourcePath.toString(),objectDescriptor).toFile());
	}
	
	public static TextureContainer importTexture(String textureDescriptor) throws IOException {
		return MTLLoader.loadModel(Paths.get(resourcePath.toString(),textureDescriptor).toFile());
	}
}
