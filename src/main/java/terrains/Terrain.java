package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import com.mokiat.data.front.error.WFException;
import com.mokiat.data.front.parser.MTLLibrary;

import models.BlendedMaterialLibraryBuilder;
import models.MTLUtils;
import models.Model3D;
import models.Model3DImporter;
import models.ModelUtils;
import models.OBJUtils;
import renderEngine.Loader;
import toolbox.Maths;

public class Terrain extends Model3D {
	private static final float SIZE = 800;
	private static final float MAX_HEIGHT = 40;
	private static final float MAX_PIXEL_COLOR = 256* 256 *256;
	private int vertexCount = 0; //TODO refactor to move those in TerrainData class or similar.
	private float[][] heights;
	
	static final String resourceTexturePath = Paths.get("2D").toString();

	private static final String OBJ_NAME = "Terrain";
	private float x;
	private float z;

	public Terrain (int gridX, int gridZ, Loader loader) throws WFException, IOException {
		super(); //TODO find a way to avoid empty constructor.
		createModel(generateModel(), loader);
		this.x = gridX*SIZE;
		this.z = gridZ*SIZE;
	}
	
	public float getHeight(float worldX, float worldZ) {
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		float gridSquareSize = SIZE / ((float)heights.length-1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		//TODO buggy for terrain in negative coordinates?
		if(gridX >= heights.length -1 || gridZ >= heights.length -1 || gridX < 0 || gridZ < 0) {
			return 0;
		}
		// once we found the current grid, have to get inside coord.
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float answer;
		// find out what triangle of current grid 
		if (xCoord <= (1-zCoord)) {
			answer = Maths
					.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ], 0), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths
					.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		return answer;
	}

	private ModelUtils generateModel() {
		return new ModelUtils(generateTerrain(), importTextures());
	}

	private MTLUtils importTextures() {
		MTLLibrary mtlLibrary = BlendedMaterialLibraryBuilder.create()
				.addTexture("grass.png")
				.addTexture("mud.png")
				.addTexture("grassFlowers.png")
				.addTexture("path.png")
				.addBlendTexturesAndBuild("blendMap.png");
		
		return new MTLUtils(mtlLibrary);
	}

	private OBJUtils generateTerrain(){
		ArrayList<Vector3f> positions = new ArrayList<>();
		ArrayList<Vector3f> normalsVector = new ArrayList<>();
		ArrayList<Vector2f> textures = new ArrayList<>();
		ArrayList<Integer> vertexIndices = new ArrayList<>();
		try(InputStream fileStream = Model3DImporter.class.getClassLoader().getResourceAsStream("2D/heightmap.png")){
			BufferedImage image = ImageIO.read(fileStream);
			vertexCount = image.getHeight();
			heights = new float[vertexCount][vertexCount];
		for(int i=0;i<vertexCount;i++){
			for(int j=0;j<vertexCount;j++){
				//x+ is right, x- is left
				float x = (float)j/((float)vertexCount - 1) * SIZE;
				// y+ is up, - is down
				float y = getHeight(j, i, image);
				heights[j][i] = y;
				//z + is to the player, - is far from the screen
				float z = (float)i/((float)vertexCount - 1) * SIZE;
				positions.add(new Vector3f(x, y, z));
				normalsVector.add(calculateNormal(j,i,image));
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
	
	private static Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = getHeight(x-1,z,image);
		float heightR = getHeight(x+1,z,image);
		float heightD = getHeight(x,z-1,image);
		float heightU = getHeight(x,z+1,image);
		Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
		normal.normalise();
		return normal;
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
