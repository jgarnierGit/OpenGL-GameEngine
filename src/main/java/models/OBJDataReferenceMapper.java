package models;

import java.util.ArrayList;
import java.util.List;

import com.mokiat.data.front.parser.OBJDataReference;

public class OBJDataReferenceMapper {
	OBJDataReference dataRef;
	int materialIndex;
	List<OBJDataReference> children;
	boolean isCHild;
	OBJDataReferenceMapper parent;
	
	public OBJDataReferenceMapper(OBJDataReference objDataRef,int materialIndexRef) {
		dataRef = objDataRef;
		materialIndex = materialIndexRef;
		children = new ArrayList<OBJDataReference>();
	}
	
	public boolean hasChild() {
		return children != null;
	}
	
	public int getIndex() {
		return  dataRef.vertexIndex;
	}
	
	public int getOriginalIndex() {
		int index =  dataRef.vertexIndex;
		if(isCHild) {
			index = parent.getIndex();
		}
		return index;
	}
	
	public int getNormalIndex() {
		return dataRef.normalIndex;
	}
	
	public int getTextureCoordIndex() {
		return dataRef.texCoordIndex;
	}
	
	public int getMaterialIndex() {
		return materialIndex;
	}

	public void setParent(OBJDataReferenceMapper firstConfig) {
		isCHild = true;
		parent = firstConfig;
	}
}
