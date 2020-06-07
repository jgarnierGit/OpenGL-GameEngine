package modelsLibrary;

import java.util.Optional;

public interface ITerrain {
	
	/**
	 * Return Height elevation for given coordinates
	 * @param worldX
	 * @param worldZ
	 * @return elevation if coordinates intersects terrain, empty Optional else
	 */
	public Optional<Float> getHeight(float worldX, float worldZ);
}
