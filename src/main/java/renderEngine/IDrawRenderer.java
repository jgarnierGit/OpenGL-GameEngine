package renderEngine;

import models.IRenderableGeom;
import models.data.OBJContent;

public interface IDrawRenderer {

	public void process(IRenderableGeom geom);

	public void cleanUp();

	public void clearGeom();

	public void render();

	public void updateForRendering();

	public void reloadGeomToVAO(IRenderableGeom geom);
	
	public void bindContentToGeomVAO(IRenderableGeom geom, OBJContent geomContent);
}
