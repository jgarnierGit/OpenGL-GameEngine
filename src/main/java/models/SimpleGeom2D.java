package models;

import java.util.ArrayList;
import java.util.List;

import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector4f;

import entities.SimpleEntity;
import models.data.OBJContent;
import models.data.SimpleGeom;
import models.data.VAOGeom;
import renderEngine.DrawRenderer;
import renderEngine.MasterRenderer;
import renderEngine.RenderingParameters;
import shaderManager.IShader2D;
import utils.GeomUtils;

/**
 * hide direct access
 * 
 * @author chezmoi
 *
 */
public class SimpleGeom2D extends SimpleGeom {

	private SimpleGeom2D() {
		// hidden
	}

	protected static SimpleGeom2D create(MasterRenderer masterRenderer, DrawRenderer draw2DRenderer, IShader2D shader,
			String alias) {
		SimpleGeom2D simpleGeom2D = new SimpleGeom2D();
		masterRenderer.registerRenderer(draw2DRenderer);
		simpleGeom2D.vaoGeom = VAOGeom.create(masterRenderer.getLoader(), draw2DRenderer);
		simpleGeom2D.geomEditor = GeomEditorImpl.create(simpleGeom2D);
		// by-pass normal shader index.
		simpleGeom2D.objContent = OBJContent.createEmpty(alias, shader.getPositionShaderIndex(),
				shader.getColorShaderIndex(), shader.getTextureShaderIndex(), -1);
		simpleGeom2D.renderingParameters = RenderingParameters.create(shader, alias,
				SimpleEntity.createDefaultEntity());
		return simpleGeom2D;
	}

	@Override
	public SimpleGeom2D copy(String alias) {
		SimpleGeom2D copy = new SimpleGeom2D();
		copy.copyValues(this, alias);
		return copy;
	}

	@Override
	public void addPoint(Vector point) {
		addPoint2f(point);
	}

	@Override
	public void addPoint(Vector point, Vector4f color) {
		addPoint2f(point);
	}

	@Override
	public void invertNormals() {
		// nothing to do in 2D
	}

	@Override
	public List<Integer> getPositionsToUpdate(Vector ref) {
		ArrayList<Integer> positionsToUpdate = new ArrayList<>();
		Vector2f v2f = getVector2f(ref);
		int i = 0;
		for (Vector2f vertice : this.getVertices()) {
			if (vertice.x == v2f.x && vertice.y == v2f.y) {
				positionsToUpdate.add(i);
			}
			i++;
		}
		return positionsToUpdate;
	}

	private void addPoint2f(Vector point) {
		Vector2f v2f = getVector2f(point);
		List<Float> pointsContent = this.objContent.getPositions().getContent();
		pointsContent.add(v2f.x);
		pointsContent.add(v2f.y);
		this.objContent.getPositions().setContent2f(GeomUtils.createVector2fList(pointsContent));
	}

	private Vector2f getVector2f(Vector point) {
		if (!(point instanceof Vector2f)) {
			throw new IllegalArgumentException("Vector2f excepted, got " + point.getClass());
		}
		return (Vector2f) point;
	}

	@Override
	public List<Vector2f> getVertices() {
		List<Vector2f> vectors = new ArrayList<>();
		List<Float> content = this.objContent.getPositions().getContent();
		for (int i = 0; i < content.size(); i += 2) {
			vectors.add(new Vector2f(content.get(i), content.get(i + 1)));
		}
		return vectors;
	}

	public IShader2D getShader() {
		return (IShader2D) this.renderingParameters.getShader();
	}
}
