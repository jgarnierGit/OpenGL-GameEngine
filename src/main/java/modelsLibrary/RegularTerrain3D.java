package modelsLibrary;

import org.lwjglx.util.vector.Vector3f;

import renderEngine.Draw3DRenderer;
import renderEngine.Loader;

public abstract class RegularTerrain3D extends Terrain3D {

	protected int size;
	protected int definition;
	protected int origineX;
	protected int origineZ;
	/**
	 * 
	 * @param loader
	 * @param draw3dRenderer
	 * @param alias
	 * @param size total length of terrain
	 * @param definition number of point by sides
	 */
	public RegularTerrain3D(Loader loader, Draw3DRenderer draw3dRenderer, String alias, int size, int definition, int x, int z) {
		super(loader, draw3dRenderer, alias);
		this.size = size;
		this.definition = definition;
		this.origineX = x*size;
		this.origineZ = z*size;
		this.generateRegular();
	}
	
	protected abstract void generateRegular();

}
