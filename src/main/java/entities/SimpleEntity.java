package entities;

import org.lwjglx.util.vector.Vector3f;

public class SimpleEntity extends Entity{

	public SimpleEntity(Vector3f positions, float rotX, float rotY, float rotZ, float scale) {
		super(positions, rotX, rotY, rotZ, scale);
	}

	public static SimpleEntity createDefaultEntity() {
		return new SimpleEntity(new Vector3f(0,0,0),0,0,0,1);
	}
}
