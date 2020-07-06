package modelsManager;

import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.OBJModel;

/**
 * @deprecated
 * @author chezmoi
 *
 */
public class ModelUtils {
	private OBJContent objUtils;
	private MTLUtils mtlUtils;
	
	public ModelUtils(OBJContent generateTerrain, MTLUtils importTextures) {
		objUtils = generateTerrain;
		mtlUtils = importTextures;
	}
	
	public ModelUtils(OBJContent generateTerrain) {
		objUtils = generateTerrain;
		mtlUtils = MTLUtils.createEmpty();
	}
	public OBJContent getOBJUtils() {
		return objUtils;
	}
	public MTLUtils getMtlUtils() {
		return mtlUtils;
	}
}
