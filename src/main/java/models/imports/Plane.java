package models.imports;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import models.Imported3DModelContainer;
import models.Model3D;
import renderEngine.Loader;

public class Plane extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "plane.obj";
	private static final String TEXTURE_DESCRIPTOR = "plane.mtl";
	private final static Logger logger = Logger.getLogger(Plane.class.getName());
	
	public Plane(Loader loader) throws FileNotFoundException, IOException{
		super(Imported3DModelContainer.importModel(OBJECT_DESCRIPTOR),
				Imported3DModelContainer.importTexture(TEXTURE_DESCRIPTOR, logger),loader);
	}
}
