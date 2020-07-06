package entities;

import modelsLibrary.GeomEditor;
import modelsLibrary.IEditableGeom;
import modelsLibrary.IRenderableGeom;

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
