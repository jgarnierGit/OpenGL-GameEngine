package renderEngine;

import models.RenderableGeom;

public interface DrawRenderer {

	public void process(RenderableGeom geom);

	public void cleanUp();

	public void clearGeom();

	/**
	 * Start rendering by parsing {@see RenderableGeom} list then calling
	 * {@see DrawRenderer.genericDrawRender(RenderableGeom geom)
	 */
	public void render();

	public void updateForRendering();
}
