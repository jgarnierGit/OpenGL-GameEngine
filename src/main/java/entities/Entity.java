package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lwjglx.util.vector.Vector3f;

import modelsLibrary.SimpleGeom;
import modelsLibrary.SimpleGeom3D;
import modelsManager.Model3D;

public class Entity {
	private Model3D model;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	private boolean selected;
	private Optional<SimpleGeom3D> boundingBox;
	public Entity(Model3D model, Vector3f positions, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = positions;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.selected = false;
		this.boundingBox = Optional.empty();
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

	public void select() {
		selected = true;
	}

	public boolean isSelected() {
		return selected;
	}

	public void unselect() {
		selected = false;
	}

	public void setBoundingBox(SimpleGeom3D boundingBox) {
		this.boundingBox = Optional.of(boundingBox);
	}
	
	public Optional<SimpleGeom3D> getBoundingBox(){
		return this.boundingBox;
	}
	
	
}
