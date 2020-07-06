package utils;

import org.lwjglx.util.vector.Vector3f;

import models.data.Face;

public class IntersectionResult {
	private Face face;
	private Vector3f projectedPosition;
	private Vector3f sourcePosition;

	public Face getFace() {
		return face;
	}

	public void setFace(Face face) {
		this.face = face;
	}

	public Vector3f getProjectedPosition() {
		return projectedPosition;
	}

	public void setProjectedPosition(Vector3f projectedPosition) {
		this.projectedPosition = projectedPosition;
	}

	public Vector3f getSourcePosition() {
		return sourcePosition;
	}

	public void setSourcePosition(Vector3f sourcePosition) {
		this.sourcePosition = sourcePosition;
	}
	
}
