package entities;

import java.util.List;

import modelsLibrary.ISimpleGeom;

/**
 * Simplify entities accessor
 * @author chezmoi
 *
 */
public interface EntityContainer {

	public List<ISimpleGeom> getEntitiesGeom();
}
