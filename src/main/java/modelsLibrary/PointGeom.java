package modelsLibrary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import renderEngine.Loader;


//TODO no need of a class...
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
public class PointGeom {
	private final int vaoId;
	private Loader loader;
	private List<Integer> glRenderModes;
	private float[] points = new float[] {};
	
	public PointGeom(Loader loader) {
		//faking point into quads as point sprite seems to be the longest way to go.
		this.loader = loader;
		vaoId = this.loader.loadToVAO(points,2); 
		this.glRenderModes = new ArrayList<>();
	}
	
	public int getVaoId() {
		return vaoId;
	}
	
	public float[] getPoints() {
		return points;
	}
	
	public void reloadPositions() {
		loader.reloadVAOPosition(vaoId, points, 2);
	}

	public void addPoint(Vector2f endRay) {
		float[] newPoints = ArrayUtils.addAll(points, endRay.x,endRay.y);//points
		points = newPoints;
	}
	
	public void resetGeom() {
		points = new float[] {};
	}

	public List<Integer> getRenderModes() {
		return this.glRenderModes;
	}

	public void addRenderMode(int glRenderMode2) {
		this.glRenderModes.add(glRenderMode2);
	}
}
