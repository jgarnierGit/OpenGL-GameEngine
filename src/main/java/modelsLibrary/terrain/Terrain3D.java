package modelsLibrary.terrain;

import java.util.logging.Logger;

import modelsLibrary.GeomEditor;
import modelsLibrary.IEditableGeom;
import modelsLibrary.IRenderableGeom;
import modelsLibrary.SimpleGeom3D;

public abstract class Terrain3D implements ITerrain {
	protected SimpleGeom3D terrain;

	protected Logger logger = Logger.getLogger("Terrain3D");

	public Terrain3D(SimpleGeom3D terrain) {
		this.terrain = terrain;
	}

	@Override
	public IEditableGeom getEditableGeom() {
		return terrain;
	}
	
	@Override
	public IRenderableGeom getRenderableGeom() {
		return terrain;
	}
	
	@Override
	public GeomEditor getGeomEditor() {
		return terrain.getGeomEditor();
	}
}
