package models.bufferCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mokiat.data.front.parser.OBJTexCoord;

import models.OBJDataReferenceUtils;

public class ImageMaterialBufferCreator implements BufferCreator {
	ArrayList<Float> texturesCoords;
	
	public ImageMaterialBufferCreator() {
		texturesCoords = new ArrayList<>();
	}
	@Override
	public void setContent(OBJDataReferenceUtils objDataReferenceUtils) {
		List<OBJTexCoord> textCoordList = objDataReferenceUtils.getTexturesCoordsIndices().stream().map(indice -> {
			return objDataReferenceUtils.getTextCoordsList().get(indice);
		}).collect(Collectors.toList());
		for(OBJTexCoord textCoord : textCoordList) {
			texturesCoords.add(textCoord.u);
			texturesCoords.add(textCoord.v);
		}
	}

	@Override
	public ArrayList<Float> getContent() {
		return texturesCoords;
	}

	@Override
	public int getDimension() {
		return 2;
	}

}
