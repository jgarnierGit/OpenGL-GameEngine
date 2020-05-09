package renderEngine;

import modelsLibrary.SimpleGeom;

public interface IDrawRenderer {

	public void process(SimpleGeom geom);

	public void reloadAndprocess(SimpleGeom geom);

	public void cleanUp();
	public void clearGeom();

	public void render();

	public void sendForRendering();
}
