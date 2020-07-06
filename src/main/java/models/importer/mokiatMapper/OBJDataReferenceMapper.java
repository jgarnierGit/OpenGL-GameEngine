package models.importer.mokiatMapper;

import java.util.HashSet;

import com.mokiat.data.front.parser.OBJDataReference;

public class OBJDataReferenceMapper {
	final OBJDataReference dataRef;
	final int materialIndex;
	HashSet<OBJDataReference> children;
	boolean isCHild;
	final int mapperIndex;
	OBJDataReferenceMapper parent;
	
	public OBJDataReferenceMapper(OBJDataReference objDataRef,int materialIndexRef, int i) {
		dataRef = objDataRef;
		materialIndex = materialIndexRef;
		children = new HashSet<OBJDataReference>();
		mapperIndex = i;
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
	
	/**
	 * 
	 * @return parent if set, else itself
	 */
	public OBJDataReferenceMapper getParentIfPresent() {
		if(isCHild) {
			return parent;
		}
		return this;
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
