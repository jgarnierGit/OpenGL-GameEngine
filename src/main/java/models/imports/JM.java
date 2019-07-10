package models.imports;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import models.Imported3DModelContainer;
import models.Model3D;
import renderEngine.Loader;

public class JM extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "JM.obj";
	private static final String TEXTURE_DESCRIPTOR = "JM.mtl";
	private final static Logger logger = Logger.getLogger(JM.class.getName());
	
	public JM(Loader loader) throws FileNotFoundException, IOException {
		super(Imported3DModelContainer.importModel(OBJECT_DESCRIPTOR),
				Imported3DModelContainer.importTexture(TEXTURE_DESCRIPTOR, logger), loader);
	}

}
