package models.data;

import com.mokiat.data.front.parser.MTLLibrary;

public class MaterialLibraryImpl  implements MaterialLibrary {
	
	private MTLLibrary library;
	private int numberOfRows;
	
	public  MaterialLibraryImpl(MTLLibrary library) {
		this.library = library;
		// by default assuming there is just the texture to map.
		this.numberOfRows = 0;
	}

	@Override
	public MTLLibrary getMaterialLibrary() {
		return library;
	}

	@Override
	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}
}
