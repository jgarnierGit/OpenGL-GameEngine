package utils;

import org.lwjglx.util.vector.Vector3f;


public class MeasureResult {
	Direction direction;
	float distance;
	Vector3f source;
	Vector3f destination;
	
	
	public float getMeasure() {
		return distance;
	}


	public Vector3f getDestination() {
		return destination;
	}
	
	public Vector3f getSource() {
		return source;
	}
	
	
	public void setDestination(Vector3f destination) {
		this.destination= destination;
	}
	
	public void setSource(Vector3f source) {
		this.source =source;
	}
}
