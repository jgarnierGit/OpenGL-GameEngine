package modelsLibrary.terrain;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.NotImplementedException;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import com.mokiat.data.front.parser.MTLLibrary;

import entities.Entity;
import entities.SimpleEntity;
import modelsManager.BlendedMaterialLibraryBuilder;
import modelsManager.MTLUtils;
import modelsManager.Model3DImporter;
import modelsManager.OBJUtils;
import renderEngine.Draw3DRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import toolbox.Maths;

public class RegularElevationTerrain3D extends RegularTerrain3D {
	private float[][] heights;
	private float maxHeight;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

	public RegularElevationTerrain3D(Loader loader, Draw3DRenderer draw3dRenderer, String alias, float size,
			int definition, float amplitude, Entity entity) throws IOException {
		super(loader, draw3dRenderer, alias, size, definition, entity);
		heights = new float[definition][definition];
		maxHeight = amplitude;
	}

	/**
	 * generate a point for each pixel of heightMap
	 * 
	 * @param masterRenderer
	 * @param alias
	 * @param x
	 * @param z
	 * @param elevation
	 * @param heightMap
	 * @return
	 * @throws IOException
	 */
	public static RegularElevationTerrain3D generateRegular(MasterRenderer masterRenderer, String alias, float size,
			float x, float z, float elevation, float amplitude, String heightMap) throws IOException {
		SimpleEntity entity = new SimpleEntity(new Vector3f(x, elevation, z), 0, 0, 0, 1);

		int definition = getDefinitionFromHeightMap(heightMap);

		RegularElevationTerrain3D terrain = new RegularElevationTerrain3D(masterRenderer.getLoader(),
				masterRenderer.get3DRenderer(), alias, size, definition, amplitude, entity);

		Optional<OBJUtils> obj = terrain.parseHeightMap(heightMap);
		MTLUtils textures = terrain.importTextures();
		obj.ifPresent(objUtils -> {
			OBJUtils objContent = obj.get();
			// terrain.getSimpleGeom().addPoint(point);
		});
		throw new NotImplementedException("");
		/**
		 * for(int gz=0;gz<definition;gz++){ for(int gx=0;gx<definition;gx++){
		 * heights[gx][gz] = 0; Vector3f topLeft = new Vector3f(this.origineX +
		 * (gx/(float)definition*size),height,this.origineZ +
		 * (gz/(float)definition*size)); Vector3f topRight = new Vector3f(this.origineX
		 * + (gx/(float)definition*size),height,this.origineZ +
		 * ((gz+1)/(float)definition*size)); Vector3f bottomLeft = new
		 * Vector3f(this.origineX + ((gx+1)/(float)definition*size),height,this.origineZ
		 * + (gz/(float)definition*size)); Vector3f bottomRight = new
		 * Vector3f(this.origineX + ((gx+1)/(float)definition*size),height,this.origineZ
		 * + ((gz+1)/(float)definition*size)); this.addPoint(topLeft);
		 * this.addPoint(topRight); this.addPoint(bottomLeft);
		 * 
		 * this.addPoint(topRight); this.addPoint(bottomRight);
		 * this.addPoint(bottomLeft); } }
		 **/
	}

	private MTLUtils importTextures() {
		MTLLibrary mtlLibrary = BlendedMaterialLibraryBuilder.create().addTexture("grass.png").addTexture("mud.png")
				.addTexture("grassFlowers.png").addTexture("path.png").addBlendTexturesAndBuild("blendMap.png");

		return new MTLUtils(mtlLibrary);
	}

	private static int getDefinitionFromHeightMap(String heightMap) {
		try (InputStream fileStream = Model3DImporter.class.getClassLoader().getResourceAsStream("2D/" + heightMap)) {
			BufferedImage image = ImageIO.read(fileStream);
			return image.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private Optional<OBJUtils> parseHeightMap(String heightMap) {
		ArrayList<Vector3f> positions = new ArrayList<>();
		ArrayList<Vector3f> normalsVector = new ArrayList<>();
		ArrayList<Vector2f> textures = new ArrayList<>();
		ArrayList<Integer> vertexIndices = new ArrayList<>();
		try (InputStream fileStream = Model3DImporter.class.getClassLoader().getResourceAsStream("2D/" + heightMap)) {
			BufferedImage image = ImageIO.read(fileStream);
			for (int i = 0; i < definition; i++) {
				for (int j = 0; j < definition; j++) {
					// x+ is right, x- is left
					float x = (float) j / ((float) definition - 1) * size;
					// y+ is up, - is down
					float y = extractHeightFromPixel(j, i, image);
					heights[j][i] = y;
					// z + is to the player, - is far from the screen
					float z = (float) i / ((float) definition - 1) * size;
					positions.add(new Vector3f(x, y, z));
					normalsVector.add(calculateNormal(j, i, image));
					float u = (float) j / ((float) definition - 1);
					float v = (float) i / ((float) definition - 1);
					textures.add(new Vector2f(u, v));
				}
			}

			for (int gz = 0; gz < definition - 1; gz++) {
				for (int gx = 0; gx < definition - 1; gx++) {
					int topLeft = (gz * definition) + gx;
					int topRight = topLeft + 1;
					int bottomLeft = ((gz + 1) * definition) + gx;
					int bottomRight = bottomLeft + 1;
					vertexIndices.add(topLeft);
					vertexIndices.add(bottomLeft);
					vertexIndices.add(topRight);
					vertexIndices.add(topRight);
					vertexIndices.add(bottomLeft);
					vertexIndices.add(bottomRight);
				}
			}
			return Optional.of(OBJUtils.create(vertexIndices, positions, normalsVector, textures));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	private Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = extractHeightFromPixel(x - 1, z, image);
		float heightR = extractHeightFromPixel(x + 1, z, image);
		float heightD = extractHeightFromPixel(x, z - 1, image);
		float heightU = extractHeightFromPixel(x, z + 1, image);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalise();
		return normal;
	}

	private float extractHeightFromPixel(int x, int z, BufferedImage image) {
		if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
			return 0;
		}
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOR / 2f;
		height /= MAX_PIXEL_COLOR / 2f;
		height *= maxHeight;
		return height;
	}

	@Override
	public Optional<Float> getHeight(Vector3f worldPosition) {
		if (worldPosition.x < this.origineX || worldPosition.x > (this.origineX + this.size)
				|| worldPosition.z < this.origineZ || worldPosition.z > (this.origineZ + this.size)) {
			return Optional.empty();
		}
		float terrainX = worldPosition.x - this.origineX;
		float terrainZ = worldPosition.z - this.origineZ;
		float gridSquareSize = this.size / ((float) heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		// once we found the current grid, have to get inside coord.
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float answer;
		// find out what triangle of current grid
		if (xCoord <= (1 - zCoord)) {
			answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		}
		return Optional.of(answer);
	}

}
