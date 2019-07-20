package models;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lwjglx.util.vector.Vector3f;

public interface TextureContainer {
	static final Path resourceTexturePath = Paths.get("./", "src", "main", "resources", "2D");
	public ArrayList<TextureData> getTextures();
	public ArrayList<Float> getFlatColors(ArrayList<String> colorLinks);

}
