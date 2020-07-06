package models.data;

import java.util.Arrays;
import java.util.List;

public class CubeTexture {
	private int textureId;
	private String right;
	private String left;
	private String top;
	private String bottom;
	private String near;
	private String far;

	public CubeTexture(String left, String right, String top, String bottom, String near, String far) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		this.near = near;
		this.far = far;
	}
	
	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}
	
	public int getTextureId() {
		return this.textureId;
	}
	
	public List<String> getCubeTexture(){
		return Arrays.asList(right, left, top, bottom, far, near);
	}
}
