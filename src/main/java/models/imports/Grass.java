package models.imports;

import java.io.IOException;
import java.util.logging.Logger;

import models.Model3D;
import renderEngine.Loader;

public class Grass extends Model3D{
	private static final String OBJECT_DESCRIPTOR = "grass.obj";
	private static final String TEXTURE_DESCRIPTOR = "grass.mtl";

	public Grass(Loader loader) throws IOException {
		super(OBJECT_DESCRIPTOR,TEXTURE_DESCRIPTOR,loader);
		super.setHasTransparency(true);
		super.setUseFakeLighting(true);
	}

}
