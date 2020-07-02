package entities;

import java.util.List;

import modelsLibrary.IRenderableGeom;

/**
 * Simplify entities accessor
 * 
 * @author chezmoi
 *
 */
public interface GeomContainer {

	public List<IRenderableGeom> getGeoms();
}
