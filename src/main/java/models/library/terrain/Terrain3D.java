package models.library.terrain;

import java.util.logging.Logger;

import models.GeomEditor;
import models.EditableGeom;
import models.RenderableGeom;
import models.SimpleGeom3D;

public abstract class Terrain3D implements Terrain {
	protected SimpleGeom3D terrain;

	protected Logger logger = Logger.getLogger("Terrain3D");

	public Terrain3D(SimpleGeom3D terrain) {
		this.terrain = terrain;
	}

	@Override
	public EditableGeom getEditableGeom() {
		return terrain;
	}
	
	@Override
	public RenderableGeom getRenderableGeom() {
		return terrain;
	}
	
	@Override
	public GeomEditor getGeomEditor() {
		return terrain.getGeomEditor();
	}
}
