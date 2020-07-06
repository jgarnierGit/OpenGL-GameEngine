package models.library.terrain;

import java.io.IOException;

import entities.Entity;
import models.SimpleGeom3D;

public abstract class RegularTerrain3D extends Terrain3D {

	protected float size;
	protected int definition;
	protected float origineX;
	protected float origineZ;

	/**
	 * 
	 * @param terrain		 terrain geom
	 * @param entity	     default terrain entity
	 * @param size           total length of terrain
	 * @param definition     number of point by sides
	 * @throws IOException
	 */
	public RegularTerrain3D(SimpleGeom3D terrain, Entity defaultEntity, float size, int definition) {
		super(terrain);
		this.size = size;
		this.definition = definition;
		this.origineX = defaultEntity.getPositions().x * size;
		this.origineZ = defaultEntity.getPositions().z * size;
	}
}
