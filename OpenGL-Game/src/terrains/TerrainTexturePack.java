package terrains;

import models.TextureContainer;

public class TerrainTexturePack{
	private TextureContainer backgroundTexture;
	private TextureContainer rTexture;
	private TextureContainer gTexture;
	private TextureContainer bTexture;
	private TextureContainer blendMap;
	
	
	public TerrainTexturePack(TextureContainer backgroundTexture, TextureContainer rTexture, TextureContainer gTexture,
			TextureContainer bTexture,TextureContainer blendMap) {
		this.backgroundTexture = backgroundTexture;
		this.rTexture = rTexture;
		this.gTexture = gTexture;
		this.bTexture = bTexture;
		this.blendMap = blendMap;
	}

}
