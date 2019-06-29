package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public interface TextureContainer {
	static final Path resourceTexturePath = Paths.get("./", "resources", "2D");
	public ArrayList<TextureData> getTextures();
}
