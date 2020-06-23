package modelsLibrary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector4f;

import entities.SimpleEntity;
import renderEngine.Draw2DRenderer;
import renderEngine.Loader;
import renderEngine.RenderingParameters;
import shaderManager.Draw2DShader;
import shaderManager.Shader2D;

/**
 * hide direct access
 * @author chezmoi
 *
 */
public class SimpleGeom2D extends SimpleGeom {
	
	private SimpleGeom2D() {
		//hidden
	}
	
	protected static SimpleGeom2D create(Loader loader, Draw2DRenderer draw2DRenderer, Shader2D shader, String alias) {
		SimpleGeom2D simpleGeom2D = new SimpleGeom2D();
		simpleGeom2D.rawGeom = new RawGeom(loader,draw2DRenderer, 2);
		simpleGeom2D.renderingParameters = RenderingParameters.create(shader, simpleGeom2D, alias, SimpleEntity.createDefaultEntity());
		return simpleGeom2D;
	}

	@Override
	public SimpleGeom2D copy(String alias) {
		SimpleGeom2D copy = new SimpleGeom2D();
		copy.rawGeom = new RawGeom(rawGeom.loader,rawGeom.drawRenderer, 2);
		copy.copyValues(this, alias);
		return copy;
	}
	
	@Override
	public void addPoint(Vector point) {
		rawGeom.duplicateLastColor();
		addPoint2f(point);
	}
	
	@Override
	public void addPoint(Vector point, Vector4f color) {
		rawGeom.addColor(color);
		addPoint2f(point);
	}
	
	@Override
	public void invertNormals() {
		//nothing to do in 2D
	}
	
	@Override
	public void updateColorByPosition(Vector ref, Vector4f color) {
		Vector2f v2f = getVector2f(ref);
		int i=0;
		for(Vector2f vertice :this.buildVerticesList()) {
			if(vertice.x == v2f.x && vertice.y == v2f.y ) {
				rawGeom.updateColor(i, color);
			}
			i++;
		}
	}
	
	@Override
	public void reloadVao() {
		rawGeom.reloadVao(Draw2DShader.COLOR_INDEX);
	}
	
	private void addPoint2f(Vector point) {
		Vector2f v2f = getVector2f(point);
		float[] newPoints = ArrayUtils.addAll(rawGeom.points, v2f.x, v2f.y);
		rawGeom.points = newPoints;
	}
	
	private Vector2f getVector2f(Vector point) {
		if (!(point instanceof Vector2f)) {
			throw new IllegalArgumentException("Vector2f excepted, got " + point.getClass());
		}
		return (Vector2f) point;
	}

	@Override
	public List<Vector2f> buildVerticesList() {
		List<Vector2f> vectors = new ArrayList<>();
		for (int i = 0; i < rawGeom.points.length; i += 2) {
			vectors.add(new Vector2f(rawGeom.points[i], rawGeom.points[i + 1]));
		}
		return vectors;
	}
	
	public Draw2DShader getShader() {
		return (Draw2DShader) this.renderingParameters.getShader();
	}
}
