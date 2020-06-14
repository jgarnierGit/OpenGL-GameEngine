package entities;

import java.util.List;

import modelsLibrary.SimpleGeom;

/**
 * Simplify entities accessor
 * @author chezmoi
 *
 */
public interface EntityContainer {

	public List<SimpleGeom> getEntitiesGeom();
}
