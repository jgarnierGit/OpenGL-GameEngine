package terrains;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import models.Container3D;
import models.Container3DImpl;
import models.GeneratedModelContainer;
import models.BlendedTexturesContainer;
import models.Model3D;
import models.TextureContainer;
import renderEngine.Loader;

public class Terrain extends Model3D {
	private static final float SIZE = 800;
	private static final int VERTEX_COUNT = 128;

	private float x;
	private float z;

	public Terrain (int gridX, int gridZ, Loader loader) throws FileNotFoundException {
		super(generateTerrain(), importTextures(), loader);
		this.x = gridX*SIZE;
		this.z = gridZ*SIZE;
	}

	private static TextureContainer importTextures() {
		TextureContainer mixedTextures = BlendedTexturesContainer.create()
				.addTexture("grassFlowers.png")
				.addTexture("mud.png")
				.addTexture("path.png")
				.addTexture("grass.png")
				.addBlendTexturesAndBuild("blendMap.png");
		return mixedTextures;
	}

	private static Container3D generateTerrain(){
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
		return new GeneratedModelContainer(vertexIndices, positions, textures, normalsVector);
	}

	public  float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}
}
