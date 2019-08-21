package models.imports;

import java.io.FileNotFoundException;
import java.io.IOException;

import models.Model3D;
import models.Model3DImporter;
import models.ModelUtils;
import renderEngine.Loader;

public class Plane extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "plane.obj";
	private static final String TEXTURE_DESCRIPTOR = "plane.mtl";
	
	public Plane(Loader loader) throws FileNotFoundException, IOException{
		super(ModelUtils.importModel(Model3DImporter.importOBJ(OBJECT_DESCRIPTOR),
				Model3DImporter.importMTL(TEXTURE_DESCRIPTOR)),loader);
	}
}
