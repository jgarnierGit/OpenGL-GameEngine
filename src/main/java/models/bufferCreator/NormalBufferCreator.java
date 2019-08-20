package models.bufferCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mokiat.data.front.parser.OBJNormal;

import models.OBJDataReferenceUtils;

public class NormalBufferCreator implements BufferCreator{
	ArrayList<Float> normals;
	
	public NormalBufferCreator() {
		normals = new ArrayList<>();
	}
	@Override
	public int getDimension() {
		return 3;
	}

	@Override
	public void setContent(OBJDataReferenceUtils objDataReferenceUtils) {
		List<OBJNormal> normalsList = objDataReferenceUtils.getNormalsIndices().stream().map(indice -> {
			return objDataReferenceUtils.getNormalsList().get(indice);
		}).collect(Collectors.toList());
		for(OBJNormal normal : normalsList) {
			normals.add(normal.x);
			normals.add(normal.y);
			normals.add(normal.z);
		}
	}

	@Override
	public ArrayList<Float> getContent() {
		// TODO Auto-generated method stub
		return normals;
	}

}
