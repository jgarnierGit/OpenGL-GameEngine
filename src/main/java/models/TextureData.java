package models;

import org.newdawn.slick.opengl.Texture;

public class TextureData{
	
	//TODO if is texture then use this param
	private int textureID;
	//TODO if is color then use this param
	private float red;
	private float blue;
	private float green;
	private float alpha;
	private String mtl_name;
	
	// specular lightning
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	
	public TextureData(int textureID, String mtl_name) {
		this.textureID = textureID;
		this.mtl_name = mtl_name;
	}
	
	public TextureData(float red, float green, float blue, float alpha, String mtl_name) {
		this.red = red;
		this.blue = blue;
		this.green = green;
		this.alpha = alpha;
		this.mtl_name = mtl_name;
	}
	
	public String getMtl_name() {
		return mtl_name;
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
	
	public float getRed() {
		return red;
	}

	public float getBlue() {
		return blue;
	}

	public float getGreen() {
		return green;
	}

	public float getAlpha() {
		return alpha;
	}

	public void addPNGTexture(Texture texture, int GL_TEXTURE) {
		this.textureID = texture.getTextureID();
	}
}
