package modelsLibrary.terrain;

import java.io.IOException;

import entities.Entity;
import renderEngine.Draw3DRenderer;
import renderEngine.Loader;

public abstract class RegularTerrain3D extends Terrain3D {

	protected float size;
	protected int definition;
	protected float origineX;
	protected float origineZ;

	/**
	 * 
	 * @param alias
	 * @param draw3dRenderer
	 * @param loader
	 * @param loader
	 * @param draw3dRenderer
	 * @param alias
	 * @param size           total length of terrain
	 * @param definition     number of point by sides
	 * @throws IOException
	 */
	public RegularTerrain3D(Loader loader, Draw3DRenderer draw3dRenderer, String alias, float size, int definition,
			Entity entity) throws IOException {
		super(loader, draw3dRenderer, alias, entity);
		this.size = size;
		this.definition = definition;
		this.origineX = entity.getPositions().x * size;
		this.origineZ = entity.getPositions().z * size;
	}
}
