package models;

import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector4f;

public interface GeomEditor {

	public boolean hasTransparency();

	/**
	 * Invert normals of every triangles
	 */
	public void invertNormals();

	/**
	 * add a point with last active color
	 * 
	 * @param point
	 */
	public void addPoint(Vector point);

	/**
	 * add Point coordinates with specified color TODO allow to specify Material
	 * too. new method is not a problem
	 * 
	 * @param vector
	 * @param color
	 */
	public void addPoint(Vector point, Vector4f color);

	/**
	 * update color for each vector matched.
	 * 
	 * @param ref   vector position to updated
	 * @param color to apply for position
	 */
	public void updateColorByPosition(Vector ref, Vector4f color);

	/**
	 * set each point with color, if a canal is set to -1 original value will be
	 * used
	 * 
	 * @param color
	 */
	public void setColor(Vector4f color);
}
