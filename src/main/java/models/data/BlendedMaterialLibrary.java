package models.data;

import com.mokiat.data.front.parser.MTLLibrary;

public class BlendedMaterialLibrary  implements IMaterialLibrary{
	private MTLLibrary library;
	
	public  BlendedMaterialLibrary(MTLLibrary library) {
		this.library = library;
	}
	
	@Override
	public MTLLibrary getMaterialLibrary() {
		return library;
	}

}
