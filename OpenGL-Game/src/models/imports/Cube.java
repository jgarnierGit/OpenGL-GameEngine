package models.imports;

import java.io.FileNotFoundException;

import models.Imported3DModel;
import renderEngine.Loader;

public class Cube extends Imported3DModel{

	public Cube(Loader loader) throws FileNotFoundException {
		super("cube_mat.obj","cube_mat.mtl",loader);
		super.getTextureContainer().setReflectivity(1);
		super.getTextureContainer().setShineDamper(10);
		/**
		 * TODO cube.obj ko
		 * cube_mixed_texture ko
		 * cube_mixed_texture2 ko
		 * cube_no_text ko
		 * cube_mat ok
		 */
	}
}
