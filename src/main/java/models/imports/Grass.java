package models.imports;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import models.Imported3DModelContainer;
import models.Model3D;
import renderEngine.Loader;

public class Grass extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "grass.obj";
	private static final String TEXTURE_DESCRIPTOR = "grass.mtl";
	private final static Logger logger = Logger.getLogger(Grass.class.getName());

	public Grass(Loader loader) throws IOException {
		super(Imported3DModelContainer.importModel(OBJECT_DESCRIPTOR),Imported3DModelContainer.importTexture(TEXTURE_DESCRIPTOR, logger),loader);
		super.setHasTransparency(0,true);//TODO may be not change value in classes because it will be fastidious.
		super.setUseFakeLighting(0,true);
	}

}
