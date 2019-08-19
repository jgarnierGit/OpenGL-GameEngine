package models.bufferCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjglx.util.vector.Vector4f;

import models.MaterialMapper;
import models.OBJDataReferenceUtils;

public class ColorMaterialBufferCreator implements BufferCreator{
	ArrayList<Float> colorCoords;
	@Override
	public void setContent(OBJDataReferenceUtils objDataReferenceUtils) {
		colorCoords = new ArrayList<>();
		List<MaterialMapper> colorsList = objDataReferenceUtils.getColorsIndices().stream().map(indice -> {
			return objDataReferenceUtils.getMaterialsList().get(indice);
		}).collect(Collectors.toList());
		for(MaterialMapper color : colorsList) {
			colorCoords.add(color.getColor().x);
			colorCoords.add(color.getColor().y);
			colorCoords.add(color.getColor().z);
			colorCoords.add(color.getColor().w);
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
