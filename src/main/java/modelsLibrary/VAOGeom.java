package modelsLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjglx.util.vector.Vector4f;

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
	protected int dimension;
	protected int vaoId;
	protected Loader loader;
	protected DrawRenderer drawRenderer;
	protected VBOContent points;
	protected VBOContent colors;
	protected static final float[] DEFAULT_COLOR = new float[] { 1.0f, 0.0f, 1.0f, 1.0f };
	private static final int POSITION_INDEX = 0;
	private static final int COLOR_INDEX = 1;
	// TODO add checked that VBO index must not overlap ?

	public void copyRawValues(VAOGeom source) {
		if (source.getDimension() != this.getDimension()) {
			throw new IllegalArgumentException("Copy cannot perform over crossed dimension");
		}
		this.points = VBOContent.create(source.points.getShaderInputIndex(), source.points.getDimension(),
				new ArrayList<>(source.points.getContent()));
		this.colors = VBOContent.create(source.colors.getShaderInputIndex(), source.colors.getDimension(),
				new ArrayList<>(source.colors.getContent()));
	}

	private VAOGeom(Loader loader2, DrawRenderer drawRenderer, int dimension) {
		this.loader = loader2;
		this.dimension = dimension;
		this.drawRenderer = drawRenderer;
	}

	public static VAOGeom create(Loader loader2, DrawRenderer drawRenderer, int dimension) {
		VAOGeom vaoGeom = new VAOGeom(loader2, drawRenderer, dimension);
		vaoGeom.points = VBOContent.createEmpty(POSITION_INDEX, dimension);
		vaoGeom.colors = VBOContent.createEmpty(COLOR_INDEX, 4);
		// TODO try to not allocate vaoId to early.
		vaoGeom.vaoId = vaoGeom.loader.loadToVAO(vaoGeom.points);
		return vaoGeom;
	}

	public Vector4f getDefaultColor() {
		return new Vector4f(DEFAULT_COLOR[0], DEFAULT_COLOR[1], DEFAULT_COLOR[2], DEFAULT_COLOR[3]);
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
	 * @return VBOContent of points coordinates.
	 */
	public VBOContent getPoints() {
		return points;
	}

	/**
	 * return array of colors for each points.
	 * 
	 * @return VBOContent of colors for each points.
	 */
	public VBOContent getColors() {
		return colors;
	}

	public boolean hasTransparency() {
		List<Float> content = this.colors.getContent();
		for (int i = 3; i <= content.size(); i += 4) {
			if (content.get(i) < 1f) {
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

	protected void reloadVao() {
		loader.reloadVAO(vaoId, points);
		loader.reloadVAO(vaoId, colors);
	}

	@Override
	public String toString() {
		return "Ray " + vaoId + " [points=" + points.toString() + "]";
	}

	public void clear() {
		points = VBOContent.createEmpty(POSITION_INDEX, dimension);
		colors = VBOContent.createEmpty(COLOR_INDEX, dimension);
	}

	protected void updateColor(int index, Vector4f color) {
		index *= 4;
		if (index > this.colors.getContent().size() - 4) {
			throw new IllegalArgumentException(
					"incorrect index " + index + " : content size : " + this.colors.getContent().size());
		}
		List<Float> content = this.colors.getContent();
		content.set(index, color.x);
		content.set(index + 1, color.y);
		content.set(index + 2, color.z);
		content.set(index + 3, color.w);
	}

	public void updateColor(Vector4f color) {
		List<Float> content = this.colors.getContent();
		for (int i = 0; i < content.size(); i += 4) {
			if (color.x != -1) {
				content.set(i, color.x);
			}
			if (color.y != -1) {
				content.set(i + 1, color.y);
			}
			if (color.z != -1) {
				content.set(i + 2, color.z);
			}
			if (color.w != -1) {
				content.set(i + 3, color.w);
			}
		}
	}

	/**
	 * TODO add more check constraint : positions & color MUST have same amount of
	 * element.
	 */
	protected void duplicateLastColor() {
		ArrayList<Float> content = new ArrayList<>(this.colors.getContent());
		if (content.isEmpty()) {
			this.colors
					.setContent(Arrays.asList(DEFAULT_COLOR[0], DEFAULT_COLOR[1], DEFAULT_COLOR[2], DEFAULT_COLOR[3]));
		} else {
			int lastIndex = content.size();
			float xDuplicate = content.get(lastIndex - 4);
			float yDuplicate = content.get(lastIndex - 3);
			float zDuplicate = content.get(lastIndex - 2);
			float wDuplicate = content.get(lastIndex - 1);
			content.add(xDuplicate);
			content.add(yDuplicate);
			content.add(zDuplicate);
			content.add(wDuplicate);
			this.colors.setContent(content);
		}
	}

	protected void addColor(Vector4f color) {
		ArrayList<Float> content = new ArrayList<>(this.colors.getContent());
		content.add(color.x);
		content.add(color.y);
		content.add(color.z);
		content.add(color.w);
		this.colors.setContent(content);
	}

	public void updateRenderer(ISimpleGeom simpleGeom) {
		this.drawRenderer.process(simpleGeom);
	}
}
