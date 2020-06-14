package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lwjglx.util.vector.Vector3f;

import renderEngine.RenderingParameters;

/**
 * Model acn be attached here. Model will be equals to unique points. Model must implements methods to use for as many rendering type as needed;
 * @author chezmoi
 *
 */
public abstract class Entity {
	private Vector3f position;
	private float rotX;
	private float rotY;
	private float rotZ;
	private float scale;
	
	private List<Vector3f> boundingBox;
	
	public Entity(Vector3f positions, float rotX, float rotY, float rotZ, float scale) {
		this.position = positions;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.boundingBox = new ArrayList<>();
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
	
	public void setBoundingBox(List<Vector3f> boundingBox) {
		this.boundingBox = boundingBox;
	}
	
	public List<Vector3f> getBoundingBox(){
		return this.boundingBox;
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
