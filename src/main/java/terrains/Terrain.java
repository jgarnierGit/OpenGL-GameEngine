package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import com.mokiat.data.front.error.WFException;
import com.mokiat.data.front.parser.MTLLibrary;
import com.mokiat.data.front.parser.OBJDataReference;
import com.mokiat.data.front.parser.OBJFace;
import com.mokiat.data.front.parser.OBJMesh;
import com.mokiat.data.front.parser.OBJModel;
import com.mokiat.data.front.parser.OBJNormal;
import com.mokiat.data.front.parser.OBJObject;
import com.mokiat.data.front.parser.OBJTexCoord;
import com.mokiat.data.front.parser.OBJVertex;

import models.BlendedMaterialLibraryBuilder;
import models.MTLUtils;
import models.Model3D;
import models.ModelUtils;
import models.OBJUtils;
import renderEngine.Loader;

public class Terrain extends Model3D {
	private static final float SIZE = 800;
	private static final float MAX_HEIGHT = 40;
	private static final float MAX_PIXEL_COLOR = 256* 256 *256;
	private static int vertexCount = 0;
	
	static final String resourceTexturePath = Paths.get("./", "src", "main", "resources", "2D").toString();

	private static final String OBJ_NAME = "Terrain";
	private float x;
	private float z;

	public Terrain (int gridX, int gridZ, Loader loader) throws WFException, IOException {
		super(generateModel(), loader);
		this.x = gridX*SIZE;
		this.z = gridZ*SIZE;
	}

	private static ModelUtils generateModel() {
		return new ModelUtils(generateTerrain(), importTextures());
	}

	private static MTLUtils importTextures() {
		MTLLibrary mtlLibrary = BlendedMaterialLibraryBuilder.create()
				.addTexture(resourceTexturePath, "grass.png")
				.addTexture(resourceTexturePath, "mud.png")
				.addTexture(resourceTexturePath, "grassFlowers.png")
				.addTexture(resourceTexturePath, "path.png")
				.addBlendTexturesAndBuild(resourceTexturePath, "blendMap.png");
		
		return new MTLUtils(mtlLibrary);
	}

	private static OBJUtils generateTerrain(){
		ArrayList<Vector3f> positions = new ArrayList<>();
		ArrayList<Vector3f> normalsVector = new ArrayList<>();
		ArrayList<Vector2f> textures = new ArrayList<>();
		ArrayList<Integer> vertexIndices = new ArrayList<>();
		try {
			BufferedImage image = ImageIO.read(new File(Paths.get(resourceTexturePath, "heightmap.png").toString()));
			vertexCount = image.getHeight();
		for(int i=0;i<vertexCount;i++){
			for(int j=0;j<vertexCount;j++){
				//x+ is right, x- is left
				float x = -(float)j/((float)vertexCount - 1) * SIZE;
				// y+ is up, - is down
				float y = getHeight(j, i, image) -50;
				//z + is to the player, - is far from the screen
				float z = -(float)i/((float)vertexCount - 1) * SIZE;
				positions.add(new Vector3f(x, y, z));
				//TODO need to understand normals and how invert them
				normalsVector.add(new Vector3f(0, 1,0));
				float u = (float)j/((float)vertexCount - 1);
				float v = (float)i/((float)vertexCount - 1);
				textures.add(new Vector2f(u,v));
			}
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int gz=0;gz<vertexCount-1;gz++){
			for(int gx=0;gx<vertexCount-1;gx++){
				int topLeft = (gz*vertexCount)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*vertexCount)+gx;
				int bottomRight = bottomLeft + 1;
				vertexIndices.add(topLeft);
				vertexIndices.add(bottomLeft);
				vertexIndices.add(topRight);
				vertexIndices.add(topRight);
				vertexIndices.add(bottomLeft);
				vertexIndices.add(bottomRight);
			}
		}
		return OBJUtils.create(vertexIndices,positions,normalsVector,textures);
	}
	
	private static float getHeight(int x, int z, BufferedImage image) {
		if(x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
			return 0;
		}
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOR/2f;
		height /= MAX_PIXEL_COLOR/2f;
		height *= MAX_HEIGHT;
		return height;
	}

	public  float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}
}
