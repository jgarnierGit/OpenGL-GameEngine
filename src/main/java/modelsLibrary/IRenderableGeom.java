package modelsLibrary;

import java.util.List;

import org.lwjglx.util.vector.Vector;

import renderEngine.RenderingParameters;

public interface IRenderableGeom {

	public List<? extends Vector> getVertices();

	/**
	 * copy geom and load it in a new Vao copy renderingParameters with empty
	 * entities list.
	 * 
	 * @param alias new alias to use
	 * @return SimpleGeom a deep copy of geom
	 */
	public IRenderableGeom copy(String alias);

	/**
	 * TODO extract to RenderingParameter. Reload points coordinates to binded
	 * Vertex Buffer Object attached to VAO
	 */
	public void reloadVao();

	/**
	 * TODO extract this also
	 */
	public void updateRenderer();

	/**
	 * clear points arrays coordinates.
	 */
	public void clear();

	/**
	 * return the rendering parameters for current geom.
	 * 
	 * @return the rendering parameters for current geom.
	 */
	public RenderingParameters getRenderingParameters();

	public VAOGeom getVAOGeom();

	public GeomEditor getGeomEditor();

}
