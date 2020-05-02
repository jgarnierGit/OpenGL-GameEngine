package renderEngine;

import modelsLibrary.ISimpleGeom;

public interface IDrawRenderer {

	public void process(ISimpleGeom geom);
	public void process(ISimpleGeom geom, int renderingIndex);
	
	public void reloadAndprocess(ISimpleGeom geom);
	public void reloadAndprocess(ISimpleGeom geom, int renderingIndex);
	public void cleanUp();
	public void render();
}
