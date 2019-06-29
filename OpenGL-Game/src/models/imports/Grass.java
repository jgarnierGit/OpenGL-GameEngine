package models.imports;

import java.io.FileNotFoundException;

import models.Imported3DModelContainer;
import models.Model3D;
import renderEngine.Loader;

public class Grass extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "grass.obj";
	private static final String TEXTURE_DESCRIPTOR = "grass.mtl";

	public Grass(Loader loader) throws FileNotFoundException {
		super(Imported3DModelContainer.importModel(OBJECT_DESCRIPTOR),Imported3DModelContainer.importTexture(TEXTURE_DESCRIPTOR),loader);
		super.setHasTransparency(0,true);//TODO may be not change value in classes because it will be fastidious.
		super.setUseFakeLighting(0,true);
	}

}
