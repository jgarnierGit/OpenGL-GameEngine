package models.bufferCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mokiat.data.front.parser.OBJVertex;

import models.OBJDataReferenceUtils;

public class PositionBufferCreator implements BufferCreator{
	ArrayList<Float> positions;
	
	public PositionBufferCreator() {
		positions = new ArrayList<>();
	}
	@Override
	public int getDimension() {
		return 3;
	}

	@Override
	public void setContent(OBJDataReferenceUtils objDataReferenceUtils) {
		List<OBJVertex> vertices = objDataReferenceUtils.getPositionsIndices().stream().map(indice -> {
			return objDataReferenceUtils.getVerticesList().get(indice);
		}).collect(Collectors.toList());
		for(OBJVertex vertex : vertices) {
			positions.add(vertex.x);
			positions.add(vertex.y);
			positions.add(vertex.z);
		}
	}

	@Override
	public ArrayList<Float> getContent() {
		return positions;
	}

}
