package modelsLibrary;

import java.util.List;

import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector4f;

import modelsManager.OBJUtils;

public interface IEditableGeom {

	void addPoint(Vector point);

	public void addPoint(Vector point, Vector4f color);

	public List<Integer> getPositionsToUpdate(Vector ref);

	public void invertNormals();

	public OBJUtils getObjContent();

}
