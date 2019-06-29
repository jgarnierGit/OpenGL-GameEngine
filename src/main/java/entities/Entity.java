package entities;

import org.lwjglx.util.vector.Vector3f;

import models.Model3D;

public class Entity {
	private Model3D model;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	public Entity(Model3D model, Vector3f positions, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = positions;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}
	
	public void increasePosition(float dx, float dy, float dz) {
		this.position.x+=dx;
		this.position.y+=dy;
		this.position.z+=dz;
	}
	
	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX+= dx;
		this.rotY+=dy;
		this.rotZ+=dz;
	}
	
	public Model3D getModel() {
		return model;
	}
	public void setModel(Model3D model) {
		this.model = model;
	}
	public Vector3f getPositions() {
		return position;
	}
	public void setPositions(Vector3f positions) {
		this.position = positions;
	}
	public float getRotX() {
		return rotX;
	}
	public void setRotX(float rotX) {
		this.rotX = rotX;
	}
	public float getRotY() {
		return rotY;
	}
	public void setRotY(float rotY) {
		this.rotY = rotY;
	}
	public float getRotZ() {
		return rotZ;
	}
	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	
}
