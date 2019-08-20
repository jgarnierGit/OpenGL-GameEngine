package models;

import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.OBJModel;

/**
 * @deprecated
 * useless class**/
public class ModelUtils { 
	private MTLUtils mtlUtils;
	private OBJUtils objUtils;
	
	public ModelUtils(OBJModel model, MTLLibrary materialsLibrary) {
		mtlUtils = new MTLUtils(materialsLibrary);//TODO do the link between materials name and texture position;
		objUtils = new OBJUtils(model,mtlUtils);
		// if no textureCoordinates in objUtils, then mtlUtils gives only color.
		//if textureCoordinates, then only images for now. Can be updated to use both image and colors

	}

	public MTLUtils getMtlUtils() {
		return mtlUtils;
	}

	public OBJUtils getOBJUtils() {
		return objUtils;
	}
}
