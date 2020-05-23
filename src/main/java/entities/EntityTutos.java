package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lwjglx.util.vector.Vector3f;

import modelsManager.Model3D;
import renderEngine.RenderingParameters;

public class EntityTutos extends Entity {
	private Model3D model;
	private boolean selected;

	
	public EntityTutos(Model3D model, Vector3f positions, float rotX, float rotY, float rotZ, float scale) {
		super(positions, rotX, rotY, rotZ, scale);
		this.model = model;
		this.selected = false;
	}
	
	public Model3D getModel() {
		return model;
	}
	public void setModel(Model3D model) {
		this.model = model;
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
}
