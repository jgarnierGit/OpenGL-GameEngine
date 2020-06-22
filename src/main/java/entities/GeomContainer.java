package entities;

import java.util.List;

import modelsLibrary.ISimpleGeom;

/**
 * Simplify entities accessor
 * @author chezmoi
 *
 */
public interface GeomContainer {

	public List<ISimpleGeom> getGeoms();
}
