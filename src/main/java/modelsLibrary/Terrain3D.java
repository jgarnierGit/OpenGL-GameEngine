package modelsLibrary;

import java.util.logging.Logger;

import entities.Entity;
import renderEngine.Draw3DRenderer;
import renderEngine.Loader;

public abstract class Terrain3D extends SimpleGeom3D implements ITerrain{
	
	protected Logger logger = Logger.getLogger("Terrain3D");

	public Terrain3D(Loader loader, Draw3DRenderer draw3dRenderer, String alias, Entity entity) {
		super(loader, draw3dRenderer, alias, entity);
	}
}
