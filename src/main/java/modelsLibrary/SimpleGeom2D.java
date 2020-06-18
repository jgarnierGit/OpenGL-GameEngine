package modelsLibrary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector4f;

import entities.Entity;
import entities.SimpleEntity;
import renderEngine.Draw2DRenderer;
import renderEngine.Loader;
import shaderManager.Draw2DShader;

public class SimpleGeom2D extends SimpleGeom {
	
	//FIXME 4 constructor is a bit too much.
	public SimpleGeom2D(Loader loader,Draw2DRenderer draw2DRenderer, String alias, Entity entity) {
		super(loader, 2, alias, entity);
		this.drawRenderer = draw2DRenderer;
	}
	
	private SimpleGeom2D(Loader loader, String alias, Entity entity) {
		super(loader, 2, alias, entity);
	}
	
	public SimpleGeom2D(Loader loader, Draw2DRenderer draw2DRenderer, String alias) {
		this(loader, draw2DRenderer, alias, SimpleEntity.createDefaultEntity());
	}

	@Override
	public SimpleGeom2D copy(String alias) {
		SimpleGeom2D copy = new SimpleGeom2D(this.loader, alias, SimpleEntity.createDefaultEntity());
		copy.setCopyParams(this, alias, SimpleEntity.createDefaultEntity());
		return copy;
	}
	@Override
	public void addPoint(Vector point) {
		duplicateLastColor();
		addPoint2f(point);
	}
	
	@Override
	public void addPoint(Vector point, Vector4f color) {
		addColor(color);
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
				super.updateColor(i, color);
			}
			i++;
		}
	}
	
	@Override
	public void reloadVao() {
		super.reloadVao(Draw2DShader.COLOR_INDEX);
	}
	
	private void addPoint2f(Vector point) {
		Vector2f v2f = getVector2f(point);
		float[] newPoints = ArrayUtils.addAll(points, v2f.x, v2f.y);
		points = newPoints;
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
		for (int i = 0; i < points.length; i += 2) {
			vectors.add(new Vector2f(points[i], points[i + 1]));
		}
		return vectors;
	}
}
