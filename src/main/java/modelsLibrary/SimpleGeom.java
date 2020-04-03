package modelsLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import modelsManager.Model3D;
import renderEngine.Loader;

//TODO class not using Model3D logic...
/**
 * Do not use GL_POINTS elsewhere than debugging. Causes many compatibilities issues with graphics cards when fragment used to render is larger than 1.
 * for example GL_POINT_SMOOTH doesn't work everywhere.
 * having big sized points also slows down rendering.
 * 
 * see also
 * GL11.glEnable(ARBPointSprite.GL_POINT_SPRITE_ARB);
 * 
 * Perfs are alike GL_POINTS.
 * @author chezmoi
 *
 */
public abstract class SimpleGeom implements ISimpleGeom{
	private int dimension;
	protected int vaoId;
	protected Loader loader;
	protected List<Integer> glRenderModes;
	protected float[] points;

	public SimpleGeom(Loader loader2, int dimension) {
		this.loader = loader2;
		this.dimension = dimension;
		this.glRenderModes = new ArrayList<>();
		this.points = new float[] {};
		this.vaoId = loader.loadToVAO(points, this.dimension);
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
	public int getDimension() {
		return this.dimension;
	}
	
	@Override
	public void reloadPositions() {
		loader.reloadVAOPosition(vaoId, points, this.dimension);
	}

	@Override
	public String toString() {
		return "Ray " + vaoId + " [points=" + Arrays.toString(points) + "]";
	}
	@Override
	public void resetGeom() {
		points = new float[] {};
	}
	@Override
	public List<Integer> getRenderModes() {
		return this.glRenderModes;
	}
	@Override
	public void addRenderMode(int glRenderMode) {
		this.glRenderModes.add(glRenderMode);
	}
}
