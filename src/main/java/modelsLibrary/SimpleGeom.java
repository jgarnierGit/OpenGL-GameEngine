package modelsLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import modelsManager.Model3D;
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
public abstract class SimpleGeom implements ISimpleGeom {
	private int dimension;
	protected int vaoId;
	protected Loader loader;
	private List<RenderingParameters> renderingParameters;
	protected float[] points;
	protected float[] colors;
	protected static final float[] DEFAULT_COLOR = new float[] { 1.0f, 0.0f, 1.0f, 1.0f };

	public SimpleGeom(Loader loader2, int dimension) {
		this.loader = loader2;
		this.dimension = dimension;
		this.points = new float[] {};
		this.colors = new float[] {};
		this.vaoId = loader.loadToVAO(points, this.dimension);
		this.renderingParameters = new ArrayList<>();
	}

	@Override
	public List<RenderingParameters> getRenderingParameters() {
		return this.renderingParameters;
	}

	public Vector4f getDefaultColor() {
		return new Vector4f(DEFAULT_COLOR[0], DEFAULT_COLOR[1], DEFAULT_COLOR[2], DEFAULT_COLOR[3]);
	}

	public void duplicateLastColor() {
		if (this.colors.length == 0) {
			this.colors = SimpleGeom.DEFAULT_COLOR.clone();
		} else {
			int lastIndex = this.colors.length;
			this.colors = ArrayUtils.addAll(this.colors, this.colors[lastIndex - 4], this.colors[lastIndex - 3],
					this.colors[lastIndex - 2], this.colors[lastIndex - 1]);
		}
	}

	public void addColor(Vector4f color) {
		this.colors = ArrayUtils.addAll(this.colors, color.x, color.y, color.z, color.w);
	}

	@Override
	public int getVaoId() {
		return vaoId;
	}

	@Override
	public float[] getPoints() {
		return points;
	}

	@Override
	public float[] getColors() {
		return colors;
	}

	@Override
	public boolean hasTransparency() {
		for (int i = 3; i <= this.colors.length; i += 4) {
			if (this.colors[i] < 1f) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getDimension() {
		return this.dimension;
	}

	@Override
	public void reloadPositions(int colorIndex) {
		loader.reloadVAOPosition(vaoId, points, colors, colorIndex, this.dimension);
	}

	@Override
	public String toString() {
		return "Ray " + vaoId + " [points=" + Arrays.toString(points) + "]";
	}

	@Override
	public void resetGeom() {
		points = new float[] {};
		colors = new float[] {};
	}

	@Override
	public void updateColor(int index, Vector4f color) {
		this.colors[index] = color.x;
		this.colors[index + 1] = color.y;
		this.colors[index + 2] = color.z;
		this.colors[index + 3] = color.w;
	}

}
