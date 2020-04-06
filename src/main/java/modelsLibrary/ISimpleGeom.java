package modelsLibrary;

import java.util.List;

import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

public interface ISimpleGeom {
	/**
	 * returns Vertex Array Object Id already binded.
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
	 * @return array of points coordinates.
	 */
	public float[] getPoints();
	
	/**
	 * return array of colors for each points.
	 * @return array of colors for each points.
	 */
	public float[] getColors();
	
	public List<? extends Vector> getVertices();
	
	/**
	 * add a point with last active color to apply
	 * @param point
	 */
	public void addPoint(Vector point);
	
	/**
	 * add Point coordinates with a color to apply
	 * @param vector
	 * @param color
	 */
	public void addPoint(Vector point, Vector4f color);
	
	/**
	 * update color for active point.
	 * @param point
	 * @param color
	 */
	public void updateColor(Vector point, Vector4f color);

	/**
	 * Reload points coordinates to binded Vertex Buffer Object attached to VAO
	 * @param colorIndex index binded by glEnableVertexAttribArray
	 */
	public void reloadPositions(int colorIndex);

	/**
	 * clear points arrays coordinates.
	 */
	public void resetGeom();

	/**
	 * returns list of GL constant for rendering Mode (GL11.GL_POINTS / GL_LINES / GL_TRIANGLES and subtypes)
	 * @return list of GL constant for rendering Mode (GL11.GL_POINTS / GL_LINES / GL_TRIANGLES and subtypes)
	 */
	public List<Integer> getRenderModes();

	/**
	 * add GL constant to render current object. Each renderMode added are rendered simultaneously.
	 * @param glRenderMode (GL11.GL_POINTS / GL_LINES / GL_TRIANGLES and subtypes)
	 */
	public void addRenderMode(int glRenderMode);
}
