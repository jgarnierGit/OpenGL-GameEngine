package modelsLibrary.terrain;

import java.io.IOException;
import java.util.logging.Logger;

import entities.Entity;
import modelsLibrary.SimpleGeom3D;
import modelsLibrary.SimpleGeom3DBuilder;
import renderEngine.Draw3DRenderer;
import renderEngine.Loader;

public abstract class Terrain3D implements ITerrain {
	protected SimpleGeom3D terrain;

	protected Logger logger = Logger.getLogger("Terrain3D");

	public Terrain3D(Loader loader, Draw3DRenderer draw3DRenderer, String alias, Entity entity) throws IOException {
		super();
		this.terrain = SimpleGeom3DBuilder.create(loader, draw3DRenderer, alias).withDefaultShader().withEntity(entity)
				.build();
	}

	@Override
	public SimpleGeom3D getSimpleGeom() {
		return terrain;
	}
}
