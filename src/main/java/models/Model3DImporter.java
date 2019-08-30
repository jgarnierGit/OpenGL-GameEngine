package models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.mokiat.data.front.error.WFException;
import com.mokiat.data.front.parser.IMTLParser;
import com.mokiat.data.front.parser.IOBJParser;
import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.MTLParser;
import com.mokiat.data.front.parser.OBJModel;
import com.mokiat.data.front.parser.OBJParser;

public class Model3DImporter {
	static final Path resourcePath = Paths.get("3D");
	
	public static OBJModel importOBJ(String objectDescriptor) throws WFException, FileNotFoundException, IOException {
		IOBJParser objParser = new OBJParser();
		OBJModel objModel = new OBJModel();
		try(InputStream fileStream = Model3DImporter.class.getClassLoader().getResourceAsStream("3D/"+ objectDescriptor)){
			objModel = objParser.parse(fileStream);
		}
		return objModel;
	}

	public static MTLLibrary importMTL(String textureDescriptor) throws WFException, FileNotFoundException, IOException {
		IMTLParser mtlParser = new MTLParser();
		MTLLibrary mtlLibrary = new MTLLibrary();
		try(InputStream fileStream = Model3DImporter.class.getClassLoader().getResourceAsStream("3D/"+ textureDescriptor)){
			mtlLibrary = mtlParser.parse(fileStream);
		}
		return mtlLibrary;
	}
}
