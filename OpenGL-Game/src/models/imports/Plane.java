package models.imports;

import java.io.FileNotFoundException;

import models.Imported3DModel;
import renderEngine.Loader;

public class Plane  extends Imported3DModel{

	public Plane(Loader loader) throws FileNotFoundException {
		super("plane.obj", "plane.mtl",loader);
	}
}
