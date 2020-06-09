package modelsLibrary;

import renderEngine.Draw3DRenderer;
import renderEngine.Loader;

public abstract class RegularTerrain3D extends Terrain3D {

	protected float size;
	protected int definition;
	protected float origineX;
	protected float origineZ;
	/**
	 * 
	 * @param loader
	 * @param draw3dRenderer
	 * @param alias
	 * @param size total length of terrain
	 * @param definition number of point by sides
	 */
	public RegularTerrain3D(Loader loader, Draw3DRenderer draw3dRenderer, String alias, float size, int definition, float x, float z) {
		super(loader, draw3dRenderer, alias);
		this.size = size;
		this.definition = definition;
		this.origineX = x*size;
		this.origineZ = z*size;
	}
}
