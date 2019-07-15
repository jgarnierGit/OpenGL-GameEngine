package models;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

public interface TextureContainer {
	static final Path resourceTexturePath = Paths.get("./", "src", "main", "resources", "2D");
	public ArrayList<TextureData> getTextures();
}
