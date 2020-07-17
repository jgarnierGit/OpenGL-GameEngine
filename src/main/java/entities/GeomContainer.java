package entities;

import models.GeomEditor;
import models.EditableGeom;
import models.RenderableGeom;

/**
 * Simplify entities accessor
 * 
 * @author chezmoi
 *
 */
public interface GeomContainer {

	public EditableGeom getEditableGeom();

	public RenderableGeom getRenderableGeom();

	public GeomEditor getGeomEditor();
}
