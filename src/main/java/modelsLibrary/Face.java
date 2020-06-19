package modelsLibrary;

import org.lwjglx.util.vector.Vector3f;

public class Face {
	public Vector3f p0;
	public Vector3f p1;
	public Vector3f p2;
	public Face(Vector3f p0, Vector3f p1, Vector3f p2) {
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
	}
}
