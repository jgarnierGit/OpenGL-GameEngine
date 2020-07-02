package modelsLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjglx.util.vector.Vector;
import org.lwjglx.util.vector.Vector4f;

import modelsManager.OBJUtils;

public class GeomEditor implements IGeomEditor {
	private IEditableGeom simpleGeom;

	private GeomEditor(IEditableGeom simpleGeom) {
		this.simpleGeom = simpleGeom;
	}

	public static GeomEditor create(IEditableGeom simpleGeom) {
		return new GeomEditor(simpleGeom);
	}

	@Override
	public void setColor(Vector4f color) {
		List<Float> content = this.simpleGeom.getObjContent().getColors().getContent();
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

	@Override
	public boolean hasTransparency() {
		return this.simpleGeom.getObjContent().hasTransparency();
	}

	protected void updateColor(int index, Vector4f color) {
		index *= 4;
		if (index > this.simpleGeom.getObjContent().getColors().getContent().size() - 4) {
			throw new IllegalArgumentException("incorrect index " + index + " : content size : "
					+ this.simpleGeom.getObjContent().getColors().getContent().size());
		}
		List<Float> content = this.simpleGeom.getObjContent().getColors().getContent();
		content.set(index, color.x);
		content.set(index + 1, color.y);
		content.set(index + 2, color.z);
		content.set(index + 3, color.w);
	}

	/**
	 * TODO add more check constraint : positions & color MUST have same amount of
	 * element.
	 */
	protected void duplicateLastColor() {
		ArrayList<Float> content = new ArrayList<>(this.simpleGeom.getObjContent().getColors().getContent());
		if (content.isEmpty()) {
			this.simpleGeom.getObjContent().getColors().setContent(Arrays.asList(OBJUtils.DEFAULT_COLOR[0],
					OBJUtils.DEFAULT_COLOR[1], OBJUtils.DEFAULT_COLOR[2], OBJUtils.DEFAULT_COLOR[3]));
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
			this.simpleGeom.getObjContent().getColors().setContent(content);
		}
	}

	protected void addColor(Vector4f color) {
		ArrayList<Float> content = new ArrayList<>(this.simpleGeom.getObjContent().getColors().getContent());
		content.add(color.x);
		content.add(color.y);
		content.add(color.z);
		content.add(color.w);
		this.simpleGeom.getObjContent().getColors().setContent(content);
	}

	@Override
	public void invertNormals() {
		simpleGeom.invertNormals();
	}

	@Override
	public void addPoint(Vector point) {
		duplicateLastColor();
		simpleGeom.addPoint(point);
	}

	@Override
	public void addPoint(Vector point, Vector4f color) {
		addColor(color);
		simpleGeom.addPoint(point, color);
	}

	@Override
	public void updateColorByPosition(Vector ref, Vector4f color) {
		List<Integer> positionIndexes = simpleGeom.getPositionsToUpdate(ref);
		positionIndexes.forEach(index -> {
			updateColor(index, color);
		});
	}
}
