package models;

import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.OBJModel;

/**
 * @deprecated
 * useless class**/
public class ModelUtils {
	private OBJUtils objUtils;
	private MTLUtils mtlUtils;
	
	public ModelUtils(OBJUtils generateTerrain, MTLUtils importTextures) {
		objUtils = generateTerrain;
		mtlUtils = importTextures;
	}
	public OBJUtils getOBJUtils() {
		return objUtils;
	}
	public MTLUtils getMtlUtils() {
		return mtlUtils;
	}
	public static ModelUtils importModel(OBJModel importOBJ, MTLLibrary importMTL) {
		MTLUtils mtlUtils = new MTLUtils(importMTL);
		OBJUtils objUtils = new OBJUtils(importOBJ, mtlUtils);
		return new ModelUtils(objUtils, mtlUtils);
	}
	
	
}
