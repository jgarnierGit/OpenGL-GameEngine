package models;

import org.lwjglx.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;

public class TextureData{
	
	//TODO if is texture then use this param
	private int textureID;
	//TODO if is color then use this param
	private Vector4f color;
	private String mtl_name;
	
	// specular lightning
	private float shineDamper = 1;
	private float reflectivity = 0;

	
	public TextureData(int textureID, String mtl_name) {
		this.textureID = textureID;
		this.mtl_name = mtl_name;
	}
	
	public TextureData(float red, float green, float blue, float alpha, String mtl_name) {
		this.color = new Vector4f(red,green,blue,alpha);
		this.mtl_name = mtl_name;
	}
	
	public String getMtl_name() {
		return mtl_name;
	}

	/**
	 * @deprecated replaced by getSpecularExponent()
	 * @return
	 */
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

	public void addPNGTexture(Texture texture, int GL_TEXTURE) {
		this.textureID = texture.getTextureID();
	}

	public Vector4f getColor() {
		return this.color;
	}
}
