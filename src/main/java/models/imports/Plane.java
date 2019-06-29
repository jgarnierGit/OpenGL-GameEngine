package models.imports;

import java.io.FileNotFoundException;
import java.io.IOException;

import models.Imported3DModelContainer;
import models.Model3D;
import renderEngine.Loader;

public class Plane  extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "plane.obj";
	private static final String TEXTURE_DESCRIPTOR = "plane.mtl";
	
	public Plane(Loader loader) throws IOException {
		super(Imported3DModelContainer.importModel(OBJECT_DESCRIPTOR),Imported3DModelContainer.importTexture(TEXTURE_DESCRIPTOR),loader);
	}
}
