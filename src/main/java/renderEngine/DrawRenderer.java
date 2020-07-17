package renderEngine;

import models.RenderableGeom;
import models.data.OBJContent;

public interface DrawRenderer {

	public void process(RenderableGeom geom);

	public void cleanUp();

	public void clearGeom();

	/**
	 * Start rendering by parsing {@see RenderingParameters} list
	 * then calling {@see DrawRenderer.genericDrawRender(RenderingParameters r)
	 */
	public void render();

	public void updateForRendering();

	public void reloadGeomToVAO(RenderableGeom geom);
	
	public void bindContentToGeomVAO(RenderableGeom geom, OBJContent geomContent);
}
