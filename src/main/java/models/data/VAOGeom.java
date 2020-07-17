package models.data;

import java.io.NotActiveException;
import java.util.HashSet;
import java.util.Set;

import models.RenderableGeom;
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
	protected Set<Integer> textureIdsLoaded;
	protected Loader loader;
	protected DrawRenderer drawRenderer;
	protected OBJContent objContent;
	// TODO add checked that VBO index must not overlap ?

	public static VAOGeom copy(VAOGeom source) {
		VAOGeom vaoGeom = VAOGeom.create(source.loader, source.drawRenderer, source.objContent.getDimension());
		vaoGeom.objContent = OBJContent.copy(source.objContent);
		return vaoGeom;
	}

	private VAOGeom(Loader loader2, DrawRenderer drawRenderer) {
		this.loader = loader2;
		this.drawRenderer = drawRenderer;
		this.textureIdsLoaded = new HashSet<>();
	}

	public static VAOGeom create(Loader loader2, DrawRenderer drawRenderer, int dimension) {
		return new VAOGeom(loader2, drawRenderer);
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
		if(!objContent.getIndices().isEmpty()) {
			loader.bindIndicesBuffer(vaoId, objContent.getIndicesAsPrimitiveArray());
		}
	}
	
	protected void loadContent(OBJContent objContent) {
		this.objContent = objContent;
		loadToVAO();
		loadTextures();
	}

	public void loadTextures() {
		if(objContent.getMaterials().getType() == MaterialType.IMAGE) {
			for(String path :objContent.getMaterials().getUrl()) {
				this.textureIdsLoaded.add(loader.loadTexture(path));
			}
		}
	}

	@Override
	public String toString() {
		return "VAOGeom" + vaoId + " [" + this.objContent.toString() + "]";
	}

	public void clear() throws NotActiveException {
		throw new NotActiveException("maybe have to clean memory");
	}

	public void updateRenderer(RenderableGeom simpleGeom) {
		this.drawRenderer.process(simpleGeom);
	}
	
	public DrawRenderer getRenderer() {
		return this.drawRenderer;
	}

	public VBOContent getPositions() {
		return this.objContent.getPoints();
	}
	
	public OBJContent getObjContent(){
		return this.objContent;
	}

	public Set<Integer> getTextures() {
		return textureIdsLoaded;
	}
}
