package modelsLibrary;

import java.util.List;
import java.util.SortedSet;

import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector3f;
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
	 * add a point with last active color to apply
	 * 
	 * @param point
	 */
	public void addPoint(Vector point);

	/**
	 * add Point coordinates with a color to apply
	 * 
	 * @param vector
	 * @param color
	 */
	public void addPoint(Vector point, Vector4f color);

	/**
	 * update color for active point.
	 * 
	 * @param index of point in list
	 * @param color
	 */
	public void updateColor(int index, Vector4f color);

	/**
	 * Reload points coordinates to binded Vertex Buffer Object attached to VAO
	 * 
	 * @param colorIndex index binded by glEnableVertexAttribArray
	 */
	public void reloadPositions(int colorIndex);

	/**
	 * clear points arrays coordinates.
	 */
	public void resetGeom();

	/**
	 * return the rendering parameters list for current geom.
	 * 
	 * @return the rendering parameters list for current geom.
	 */
	public List<RenderingParameters> getRenderingParameters();

	public boolean hasTransparency();

	/**
	 * create a RenderingParameter and add it to list for current geom
	 * @return index of newly created RenderingParameter
	 */
	public int createRenderingPamater();

}
