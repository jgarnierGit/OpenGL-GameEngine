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
	private List<Vector3f> boundingBox;
	
	private RenderingParameters renderingParameters;
	public EntityTutos(Model3D model, Vector3f positions, float rotX, float rotY, float rotZ, float scale) {
		super(positions, rotX, rotY, rotZ, scale);
		this.model = model;
		this.selected = false;
		this.boundingBox = new ArrayList<>();
	}
	
	public Model3D getModel() {
		return model;
	}
	public void setModel(Model3D model) {
		this.model = model;
	}
	
	public void setRenderingParameters(RenderingParameters renderingParameters) {
		this.renderingParameters = renderingParameters;
	}
	
	public Optional<RenderingParameters> getRenderingParameters() {
		return Optional.ofNullable(this.renderingParameters);
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

	public void setBoundingBox(List<Vector3f> boundingBox) {
		this.boundingBox = boundingBox;
	}
	
	public List<Vector3f> getBoundingBox(){
		return this.boundingBox;
	}
	
	
}
