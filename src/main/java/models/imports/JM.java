package models.imports;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import models.Model3D;
import renderEngine.Loader;

public class JM extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "plane_zoomed.obj";
	private static final String TEXTURE_DESCRIPTOR = "plane_zoomed.mtl";
	
	public JM(Loader loader) throws FileNotFoundException, IOException {
		super(OBJECT_DESCRIPTOR,
				TEXTURE_DESCRIPTOR, loader);
	}

}
