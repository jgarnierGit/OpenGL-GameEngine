package models.imports;

import java.io.IOException;

import models.Model3D;
import models.Model3DImporter;
import renderEngine.Loader;

public class Cube extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "cube_mat.obj";
	private static final String TEXTURE_DESCRIPTOR = "cube_mat.mtl";
	
	public Cube(Loader loader) throws IOException {
		super(Model3DImporter.importOBJ(OBJECT_DESCRIPTOR),
				Model3DImporter.importMTL(TEXTURE_DESCRIPTOR),loader);
		super.setReflectivity(1);//TODO may be not change value in classes because it will be fastidious.
		super.setSpecularExponent(10);
		/**
		 * TODO cube.obj ko
		 * cube_mixed_texture ko
		 * cube_mixed_texture2 ko
		 * cube_no_text ko
		 * cube_mat ok
		 */
	}
}
