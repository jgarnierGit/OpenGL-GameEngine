package models.imports;

import java.io.IOException;
import java.util.logging.Logger;

import models.Imported3DModelContainer;
import models.Model3D;
import renderEngine.Loader;

public class Cube extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "cube_mat.obj";
	private static final String TEXTURE_DESCRIPTOR = "cube_mat.mtl";
	private final static Logger logger = Logger.getLogger(Cube.class.getName());
	
	public Cube(Loader loader) throws IOException {
		super(Imported3DModelContainer.importModel(OBJECT_DESCRIPTOR),
				Imported3DModelContainer.importTexture(TEXTURE_DESCRIPTOR, logger),loader);
		super.setReflectivity(0,1);//TODO may be not change value in classes because it will be fastidious.
		super.setShineDamper(0,10);
		/**
		 * TODO cube.obj ko
		 * cube_mixed_texture ko
		 * cube_mixed_texture2 ko
		 * cube_no_text ko
		 * cube_mat ok
		 */
	}
}
