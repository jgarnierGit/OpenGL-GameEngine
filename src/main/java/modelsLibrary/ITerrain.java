package modelsLibrary;

import java.util.Optional;

import org.lwjglx.util.vector.Vector3f;

public interface ITerrain {
	
	/**
	 * Return Height elevation for given coordinates
	 * @param worldX
	 * @param worldZ
	 * @return elevation if coordinates intersects terrain, empty Optional else
	 */
	public Optional<Float> getHeight(Vector3f worldPosition);
	
}
