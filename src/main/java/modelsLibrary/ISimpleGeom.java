package modelsLibrary;

import java.util.List;

import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector3f;

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
	
	public List<? extends Vector> getVertices();
	
	public void addPoint(Vector endRay);

	/**
	 * Reload points coordinates to binded Vertex Buffer Object attached to VAO
	 */
	public void reloadPositions();

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
