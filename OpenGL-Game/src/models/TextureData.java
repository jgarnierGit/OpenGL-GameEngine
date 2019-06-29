package models;

import org.newdawn.slick.opengl.Texture;

public class TextureData{
	
	private int textureID;
	
	// specular lightning
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	
	public TextureData(int textureID) {
		this.textureID = textureID;
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

	public void addPNGTexture(Texture texture, int GL_TEXTURE) {
		this.textureID = texture.getTextureID();
	}

}
