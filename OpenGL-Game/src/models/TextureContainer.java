package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class TextureContainer {
	private int textureID;
	// specular lightning
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	
	/** 
	 * TODO find a way to avoid empty constructor which is a really ugly thing.
	 */
	public TextureContainer() {
	}
	/**
	 * @param file
	 */
	public TextureContainer(File file) {
		try {
			textureID = TextureLoader.getTexture("PNG", new FileInputStream(file)).getTextureID();
		} catch (IOException e) {
			System.err.println("Texture "+ file.getPath() +" "+ file.getName() +" not found");
		}
	}
	
	public boolean isHasTransparency() {
		return hasTransparency;
	}

	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}
	
	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public int getTextureID() {
		return textureID;
	}

	public void addDiffuseColor(float parseFloat, float parseFloat2, float parseFloat3) {
		// TODO Auto-generated method stub
	}

	public void addPNGTexture(Texture texture) {
		this.textureID = texture.getTextureID();
	}
	
}
