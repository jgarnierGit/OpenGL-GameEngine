package models.imports;

import java.io.FileNotFoundException;
import java.io.IOException;

import models.Model3D;
import models.Model3DImporter;
import renderEngine.Loader;

public class Tree  extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "tree.obj";
	private static final String TEXTURE_DESCRIPTOR = "tree.mtl";
	
	public Tree(Loader loader) throws FileNotFoundException, IOException {
		super(Model3DImporter.importOBJ(OBJECT_DESCRIPTOR),
				Model3DImporter.importMTL(TEXTURE_DESCRIPTOR),loader);
	}

}
