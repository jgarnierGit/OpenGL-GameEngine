package modelsLibrary;

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
	private float[] points = new float[] {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
	
	public PointGeom(Loader loader) {
		//faking point into quads as point sprite seems to be the longest way to go.
		vaoId = loader.loadToVAO(points,2); 
	}
	
	public int getVaoId() {
		return vaoId;
	}
	
	public float[] getPoints() {
		return points;
	}
	
}
