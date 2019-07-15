package models.imports;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import models.Imported3DModelContainer;
import models.Model3D;
import renderEngine.Loader;

public class Tree  extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "tree.obj";
	private static final String TEXTURE_DESCRIPTOR = "tree.mtl";
	private final static Logger logger = Logger.getLogger(Tree.class.getName());
	public Tree(Loader loader) throws FileNotFoundException, IOException {
		super(Imported3DModelContainer.importModel(OBJECT_DESCRIPTOR),Imported3DModelContainer.importTexture(TEXTURE_DESCRIPTOR, logger),loader);
	}

}
