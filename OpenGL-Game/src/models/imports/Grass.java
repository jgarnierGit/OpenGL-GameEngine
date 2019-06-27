package models.imports;

import java.io.FileNotFoundException;

import models.Imported3DModel;
import renderEngine.Loader;

public class Grass  extends Imported3DModel{

	public Grass(Loader loader) throws FileNotFoundException {
		super("grass.obj", "grass.mtl", loader);
		super.getTextureContainer().setHasTransparency(true);
		super.getTextureContainer().setUseFakeLighting(true);
	}

}
