package modelsLibrary;

import modelsManager.OBJUtils;
import modelsManager.bufferCreator.VBOContent;
import renderEngine.DrawRenderer;
import renderEngine.Loader;

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
public class VAOGeom {
	protected Integer vaoId;
	protected Loader loader;
	protected DrawRenderer drawRenderer;
	protected OBJUtils objContent;
	// TODO add checked that VBO index must not overlap ?

	public static VAOGeom copy(VAOGeom source) {
		VAOGeom vaoGeom = VAOGeom.create(source.loader, source.drawRenderer, source.objContent.getDimension());
		vaoGeom.objContent = OBJUtils.copy(source.objContent);
		return vaoGeom;
	}

	private VAOGeom(Loader loader2, DrawRenderer drawRenderer) {
		this.loader = loader2;
		this.drawRenderer = drawRenderer;
	}

	public static VAOGeom create(Loader loader2, DrawRenderer drawRenderer, int dimension) {
		VAOGeom vaoGeom = new VAOGeom(loader2, drawRenderer);
		vaoGeom.objContent = OBJUtils.createEmpty(dimension);
		// TODO try to not allocate vaoId to early.
		vaoGeom.loadToVAO();
		return vaoGeom;
	}

	/**
	 * returns Vertex Array Object Id already binded.
	 * 
	 * @return Vertex Array Object Id already binded.
	 */
	public int getVaoId() {
		return vaoId;
	}

	protected void loadToVAO() {
		objContent.getVBOs().forEach(vboContent -> {
			if (this.vaoId == null) {
				this.vaoId = loader.loadToVAO(vboContent);
			} else {
				loader.reloadVAO(vaoId, vboContent);
			}
		});
	}

	protected void reloadVao() {
		objContent.getVBOs().forEach(vboContent -> {
			loader.reloadVAO(vaoId, vboContent);
			// point, color, material...
		});
	}

	@Override
	public String toString() {
		return "VAOGeom" + vaoId + " [" + this.objContent.toString() + "]";
	}

	public void clear() {
		this.objContent = OBJUtils.createEmpty(this.objContent.getDimension());
	}

	public void updateRenderer(IRenderableGeom simpleGeom) {
		this.drawRenderer.process(simpleGeom);
	}

	public VBOContent getPositions() {
		return this.objContent.getPoints();
	}
}
