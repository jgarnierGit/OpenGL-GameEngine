package terrains;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import models.Container3D;
import models.Model3D;
import models.TextureContainer;
import renderEngine.Loader;

/**
 * not extending {GeneratedModel} to avoid surchage of import*() methods in Model3D.
 * Maybe refactor if generated models multiplicates...
 * @author chezmoi
 *
 */
public class Terrain {
	private static final float SIZE = 800;
	private static final int VERTEX_COUNT = 128;

	private float x;
	private float z;
	
	//TODO try to refactor with Model3D as sole endpoint.
	private int vaoID;
	private Container3D container3D;
	private TextureContainer backgroundTexture;
	private TextureContainer rTexture;
	private TextureContainer gTexture;
	private TextureContainer bTexture;
	private TextureContainer blendMap;

	public Terrain (int gridX, int gridZ, Loader loader) throws FileNotFoundException {
		this.backgroundTexture = new TextureContainer(Paths.get("./", "resources", "2D", "grass.png").toFile());
		this.rTexture = new TextureContainer(Paths.get("./", "resources", "2D", "grassFlowers.png").toFile());
		this.gTexture = new TextureContainer(Paths.get("./", "resources", "2D", "mud.png").toFile());
		this.bTexture = new TextureContainer(Paths.get("./", "resources", "2D", "path.png").toFile());
		this.blendMap = new TextureContainer(Paths.get("./", "resources", "2D", "blendMap.png").toFile());
		this.container3D = generateTerrain(loader);
		loader.load3DContainerToVAO(this.container3D);
		/**loader.loadTextureToVAO(this.backgroundTexture);
		loader.loadTextureToVAO(this.rTexture);
		loader.loadTextureToVAO(this.gTexture);
		loader.loadTextureToVAO(this.bTexture);
		loader.loadTextureToVAO(this.blendMap);**/
		this.x = gridX*SIZE;
		this.z = gridZ*SIZE;
	}

	private Container3D generateTerrain(Loader loader){
		ArrayList<Vector3f> positions = new ArrayList<>();
		ArrayList<Vector3f> normalsVector = new ArrayList<>();
		ArrayList<Vector2f> textures = new ArrayList<>();
		ArrayList<Integer> vertexIndices = new ArrayList<>();

		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				//x+ is right, x- is left
				float x = -(float)j/((float)VERTEX_COUNT - 1) * SIZE;
				// y+ is up, - is down
				float y = -5;
				//z + is to the player, - is far from the screen
				float z = -(float)i/((float)VERTEX_COUNT - 1) * SIZE;
				positions.add(new Vector3f(x, y, z));
				//TODO need to understand normals and how invert them
				normalsVector.add(new Vector3f(0, 1,0));
				float u = (float)j/((float)VERTEX_COUNT - 1);
				float v = (float)i/((float)VERTEX_COUNT - 1);
				textures.add(new Vector2f(u,v));
			}
		}
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				vertexIndices.add(topLeft);
				vertexIndices.add(bottomLeft);
				vertexIndices.add(topRight);
				vertexIndices.add(topRight);
				vertexIndices.add(bottomLeft);
				vertexIndices.add(bottomRight);
			}
		}
		return new Container3D(vertexIndices, positions, textures, normalsVector);
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}
	
	public TextureContainer getBackgroundTexture() {
		return backgroundTexture;
	}
	public TextureContainer getrTexture() {
		return rTexture;
	}
	public TextureContainer getgTexture() {
		return gTexture;
	}
	public TextureContainer getbTexture() {
		return bTexture;
	}
	public TextureContainer getBlendMap() {
		return blendMap;
	}
	
	/**
	 * @return The ID of the VAO which contains the data about all the geometry
	 *         of this model.
	 */
	public int getVaoID() {
		return vaoID;
	}

	public Container3D getContainer3D() {
		return this.container3D;
	}


}
