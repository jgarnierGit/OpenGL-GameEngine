package models.imports;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import models.Model3D;
import renderEngine.Loader;

public class Tree  extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "tree.obj";
	private static final String TEXTURE_DESCRIPTOR = "tree.mtl";
	
	public Tree(Loader loader) throws FileNotFoundException, IOException {
		super(OBJECT_DESCRIPTOR,TEXTURE_DESCRIPTOR,loader);
	}

}
