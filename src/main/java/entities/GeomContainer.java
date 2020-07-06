package entities;

import models.GeomEditor;
import models.IEditableGeom;
import models.IRenderableGeom;

/**
 * Simplify entities accessor
 * 
 * @author chezmoi
 *
 */
public interface GeomContainer {

	public IEditableGeom getEditableGeom();

	public IRenderableGeom getRenderableGeom();

	public GeomEditor getGeomEditor();
}
