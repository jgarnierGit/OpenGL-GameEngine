package models;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	static final Path resourcePath = Paths.get("./", "src", "main", "resources", "3D");
	
	public static OBJModel importOBJ(String objectDescriptor) throws WFException, FileNotFoundException, IOException {
		IOBJParser objParser = new OBJParser();
		return objParser.parse(new FileInputStream(Paths.get(resourcePath.toString(), objectDescriptor).toFile()));
	}

	public static MTLLibrary importMTL(String textureDescriptor) throws WFException, FileNotFoundException, IOException {
		IMTLParser mtlParser = new MTLParser();
		return mtlParser.parse(new FileInputStream(Paths.get(resourcePath.toString(), textureDescriptor).toFile()));
	}
}
