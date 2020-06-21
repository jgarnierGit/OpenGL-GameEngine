package modelsLibrary;

import java.util.List;

import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector4f;

import renderEngine.RenderingParameters;

public interface ISimpleGeom {

	public List<? extends Vector> buildVerticesList();
	
	/**
	 * copy geom and load it in a new Vao
	 * copy renderingParameters with empty entities list.
	 * @param alias new alias to use
	 * @return SimpleGeom a deep copy of geom
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
	 * TODO extract to RenderingParameter.
	 * Reload points coordinates to binded Vertex Buffer Object attached to VAO
	 */
	public void reloadVao();
/**
 * TODO extract this also
 */
	public void updateRenderer();
	
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
	
	public RawGeom getRawGeom();

}
