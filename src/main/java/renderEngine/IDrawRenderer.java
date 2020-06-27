package renderEngine;

import modelsLibrary.ISimpleGeom;

public interface IDrawRenderer {

	public void process(ISimpleGeom geom);

	public void cleanUp();
	public void clearGeom();

	public void render();

	public void sendForRendering();
	public void reloadAndprocess(ISimpleGeom geom);
}
