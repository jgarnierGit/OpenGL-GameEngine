package models.library.terrain;

import java.util.Optional;

import org.lwjglx.util.vector.Vector3f;

import entities.GeomContainer;

public interface ITerrain extends GeomContainer{
	
	/**
	 * Return Height elevation for given coordinates
	 * @param worldX
	 * @param worldZ
	 * @return elevation if coordinates intersects terrain, empty Optional else
	 */
	public Optional<Float> getHeight(Vector3f worldPosition);
	
}
