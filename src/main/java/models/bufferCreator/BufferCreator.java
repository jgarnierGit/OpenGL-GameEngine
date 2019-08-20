package models.bufferCreator;

import java.util.ArrayList;

import models.OBJDataReferenceUtils;

public interface BufferCreator {
	
	public int getDimension();
	public void setContent(OBJDataReferenceUtils objDataReferenceUtils);
	public ArrayList<Float> getContent();
}
