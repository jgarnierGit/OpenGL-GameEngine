package models;

import java.util.List;

import org.lwjglx.util.vector.Vector;

import models.data.OBJContent;
import renderEngine.DrawRenderer;

public interface RenderableGeom extends Geom {

	public List<? extends Vector> getVertices();

	/**
	 * copy geom and load it in a new Vao copy renderingParameters with empty
	 * entities list.
	 * 
	 * @param alias new alias to use
	 * @return SimpleGeom a deep copy of geom
	 */
	public RenderableGeom copy(String alias);

	/**
	 * TODO extract to RenderingParameter. Reload points coordinates to binded
	 * Vertex Buffer Object attached to VAO
	 */
	public void reloadVao();

	public void bindContentToVAO(OBJContent geomContent);

	/**
	 * TODO extract this also
	 */
	public void updateRenderer();

	public DrawRenderer getRenderer();

	/**
	 * clear points arrays coordinates.
	 */
	public void clear();

}
