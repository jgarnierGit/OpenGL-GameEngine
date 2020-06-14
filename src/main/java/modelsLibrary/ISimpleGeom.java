package modelsLibrary;

import java.util.List;

import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector4f;

import renderEngine.RenderingParameters;

public interface ISimpleGeom {
	
	/**
	 * returns Vertex Array Object Id already binded.
	 * 
	 * @return Vertex Array Object Id already binded.
	 */
	public int getVaoId();

	/**
	 * 
	 * @return dimension count to apply for each vertice.
	 */
	public int getDimension();

	/**
	 * return array of points coordinates.
	 * 
	 * @return array of points coordinates.
	 */
	public float[] getPoints();

	/**
	 * return array of colors for each points.
	 * 
	 * @return array of colors for each points.
	 */
	public float[] getColors();

	public List<? extends Vector> getVertices();
	
	/**
	 * TODO find a better way.
	 * return a deep copy of geom
	 * @param alias to identify copy
	 * @return SimpleGeom
	 */
	public ISimpleGeom copy(String alias);

	/**
	 * add a point with last active color
	 * 
	 * @param point
	 */
	public void addPoint(Vector point);

	/**
	 * add Point coordinates with specified color
	 * 
	 * @param vector
	 * @param color
	 */
	public void addPoint(Vector point, Vector4f color);

	/**
	 * update color for each vector matched.
	 * 
	 * @param ref vector position to updated
	 * @param color to apply for position
	 */
	public void updateColorByPosition(Vector ref, Vector4f color);
	
	/**
	 * set each point with color, if a canal is set to -1 original value will be used
	 * @param color
	 */
	public void setColor(Vector4f color);

	/**
	 * Reload points coordinates to binded Vertex Buffer Object attached to VAO
	 */
	public void reloadVao();

	/**
	 * clear points arrays coordinates.
	 */
	public void reset();

	/**
	 * return the rendering parameters for current geom.
	 * 
	 * @return the rendering parameters for current geom.
	 */
	public RenderingParameters getRenderingParameters();

	public boolean hasTransparency();
	
	/**
	 * Invert normals of every triangles
	 */
	public void invertNormals();

}
