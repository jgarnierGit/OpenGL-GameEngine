package models.bufferCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjglx.util.vector.Vector4f;
import models.OBJDataReferenceUtils;

public class ColorMaterialBufferCreator implements BufferCreator{
	ArrayList<Float> colorCoords;
	@Override
	public void setContent(OBJDataReferenceUtils objDataReferenceUtils) {
		colorCoords = new ArrayList<>();
		List<Vector4f> colorsList = objDataReferenceUtils.getColorsIndices().stream().map(indice -> {
			return objDataReferenceUtils.getColorsList().get(indice);
		}).collect(Collectors.toList());
		for(Vector4f color : colorsList) {
			colorCoords.add(color.x);
			colorCoords.add(color.y);
			colorCoords.add(color.z);
			colorCoords.add(color.w);
		}
	}

	@Override
	public ArrayList<Float> getContent() {
		return colorCoords;
	}

	@Override
	public int getDimension() {
		return 4;
	}

}
