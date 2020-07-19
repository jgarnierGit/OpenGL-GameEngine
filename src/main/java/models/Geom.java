package models;

import models.data.OBJContent;
import models.data.VAOGeom;
import renderEngine.RenderingParameters;

public interface Geom {

	/**
	 * returns VaoGeom informations and memory manager.
	 * 
	 * @return VaoGeom
	 */
	public VAOGeom getVAOGeom();

	/**
	 * returns raw content of Geometry. Consider using {@link GeomEditor} to edit
	 * OBJContent
	 * 
	 * @return OBJContent
	 */
	public OBJContent getObjContent();

	/**
	 * get GeomEditor to update geometries (add points / colors / etc)
	 * 
	 * @return GeomEditor
	 */
	public GeomEditor getGeomEditor();

	/**
	 * return the rendering parameters for current geom.
	 * 
	 * @return the rendering parameters for current geom.
	 */
	public RenderingParameters getRenderingParameters();
}
