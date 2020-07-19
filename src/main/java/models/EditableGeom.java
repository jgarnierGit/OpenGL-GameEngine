package models;

import java.util.List;

import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector4f;

/**
 * TODO try to hide this interface from API. Is useful for internal process. or
 * delete it.
 * 
 * @author chezmoi
 *
 */
public interface EditableGeom extends Geom {

	void addPoint(Vector point);

	public void addPoint(Vector point, Vector4f color);

	public List<Integer> getPositionsToUpdate(Vector ref);

	public void invertNormals();

}
