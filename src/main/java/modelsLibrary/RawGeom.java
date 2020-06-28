package modelsLibrary;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector4f;

import entities.Entity;
import modelsManager.bufferCreator.VBOContent;
import renderEngine.DrawRenderer;
import renderEngine.Loader;
import renderEngine.RenderingParameters;

//TODO class not using Model3D logic...
/**
 * Do not use GL_POINTS elsewhere than debugging. Causes many compatibilities
 * issues with graphics cards when fragment used to render is larger than 1. for
 * example GL_POINT_SMOOTH doesn't work everywhere. having big sized points also
 * slows down rendering.
 * 
 * see also GL11.glEnable(ARBPointSprite.GL_POINT_SPRITE_ARB);
 * 
 * Perfs are alike GL_POINTS.
 * 
 * @author chezmoi
 *
 */
public class RawGeom {
	protected int dimension;
	protected int vaoId;
	protected Loader loader;
	protected DrawRenderer drawRenderer;
	protected float[] points;
	protected float[] colors;
	protected static final float[] DEFAULT_COLOR = new float[] { 1.0f, 0.0f, 1.0f, 1.0f };
	
	public void copyRawValues(RawGeom source) {
		if(source.getDimension() != this.getDimension()) {
			throw new IllegalArgumentException("Copy cannot perform over crossed dimension");
		}
		this.points = ArrayUtils.addAll(source.points);
		this.colors = ArrayUtils.addAll(source.colors);
	}

	protected RawGeom(Loader loader2, DrawRenderer drawRenderer, int dimension) {
		this.loader = loader2;
		this.dimension = dimension;
		this.points = new float[] {};
		this.colors = new float[] {};
		//TODO try to not allocate vaoId to early.
		this.vaoId = loader.loadToVAO(new VBOContent(1, dimension, this.points));
		this.drawRenderer = drawRenderer;
	}

	public Vector4f getDefaultColor() {
		return new Vector4f(DEFAULT_COLOR[0], DEFAULT_COLOR[1], DEFAULT_COLOR[2], DEFAULT_COLOR[3]);
	}

	protected void duplicateLastColor() {
		if (this.colors.length == 0) {
			this.colors = RawGeom.DEFAULT_COLOR.clone();
		} else {
			int lastIndex = this.colors.length;
			this.colors = ArrayUtils.addAll(this.colors, this.colors[lastIndex - 4], this.colors[lastIndex - 3],
					this.colors[lastIndex - 2], this.colors[lastIndex - 1]);
		}
	}


	protected void addColor(Vector4f color) {
		this.colors = ArrayUtils.addAll(this.colors, color.x, color.y, color.z, color.w);
	}

	/**
	 * returns Vertex Array Object Id already binded.
	 * 
	 * @return Vertex Array Object Id already binded.
	 */
	public int getVaoId() {
		return vaoId;
	}

	/**
	 * return array of points coordinates.
	 * 
	 * @return array of points coordinates.
	 */
	public float[] getPoints() {
		return points;
	}

	/**
	 * return array of colors for each points.
	 * 
	 * @return array of colors for each points.
	 */
	public float[] getColors() {
		return colors;
	}

	public boolean hasTransparency() {
		for (int i = 3; i <= this.colors.length; i += 4) {
			if (this.colors[i] < 1f) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return dimension count to apply for each vertice.
	 */
	public int getDimension() {
		return this.dimension;
	}

	protected void reloadVao(int colorIndex) {
		loader.reloadVAOPosition(vaoId, points, colors, colorIndex, this.dimension);
	}

	@Override
	public String toString() {
		return "Ray " + vaoId + " [points=" + Arrays.toString(points) + "]";
	}

	public void reset() {
		points = new float[] {};
		colors = new float[] {};
	}

	protected void updateColor(int index, Vector4f color) {
		index*=4;
		this.colors[index] = color.x;
		this.colors[index + 1] = color.y;
		this.colors[index + 2] = color.z;
		this.colors[index + 3] = color.w;
	}

	public void setColor(Vector4f boundingBoxInsideColor) {
		for (int i = 0; i < colors.length; i += 4) {
			this.colors[i] = boundingBoxInsideColor.x == -1 ? this.colors[i] : boundingBoxInsideColor.x;
			this.colors[i + 1] = boundingBoxInsideColor.y == -1 ? this.colors[i+1] : boundingBoxInsideColor.y;
			this.colors[i + 2] = boundingBoxInsideColor.z == -1 ? this.colors[i+2] : boundingBoxInsideColor.z;
			this.colors[i + 3] = boundingBoxInsideColor.w == -1 ? this.colors[i+3] : boundingBoxInsideColor.w;
		}
	}

	public void updateRenderer(ISimpleGeom simpleGeom) {
		this.drawRenderer.process(simpleGeom);
	}
}
