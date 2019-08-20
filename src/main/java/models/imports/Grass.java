package models.imports;

import java.io.IOException;

import models.Model3D;
import models.Model3DImporter;
import renderEngine.Loader;

public class Grass extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "grass.obj";
	private static final String TEXTURE_DESCRIPTOR = "grass.mtl";

	public Grass(Loader loader) throws IOException {
		super(Model3DImporter.importOBJ(OBJECT_DESCRIPTOR),
				Model3DImporter.importMTL(TEXTURE_DESCRIPTOR),loader);
		super.setHasTransparency(true);
		super.setUseFakeLighting(true);
	}

}
