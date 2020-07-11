package renderEngine;

import models.IRenderableGeom;

public interface IDrawRenderer {

	public void process(IRenderableGeom geom);

	public void cleanUp();

	public void clearGeom();

	public void render();

	public void updateForRendering();

	public void loadToVAO(IRenderableGeom geom);
}
