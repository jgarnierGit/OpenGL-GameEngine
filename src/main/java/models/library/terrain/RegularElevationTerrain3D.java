package models.library.terrain;

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
import models.SimpleGeom3D;
import models.data.BlendedMaterialLibraryBuilder;
import models.data.IMaterialLibrary;
import models.data.MaterialContent;
import models.data.OBJContent;
import models.importer.MTLUtils;
import models.importer.OBJImporter;
import toolbox.Maths;

public class RegularElevationTerrain3D extends RegularTerrain3D {
	private float[][] heights;
	private float maxHeight;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

	public RegularElevationTerrain3D(SimpleGeom3D terrainGeom, Entity entity, int size, int definition, int amplitude) {
		super(terrainGeom, entity, size, definition);
		heights = new float[definition][definition];
		maxHeight = amplitude;
	}
	
	/**
	 * generate a point for each pixel of heightMap
	 * @param terrainGeom
	 * @param mtlLibrary 
	 * @param entity
	 * @param size
	 * @param amplitude
	 * @param heightMap
	 * @param shaderTextureInputIndex TODO extract
	 * @return
	 */
	public static RegularElevationTerrain3D generateRegular(SimpleGeom3D terrainGeom, Optional<IMaterialLibrary> mtlLibrary, Entity entity, int size, int amplitude,
			String heightMap, int shaderTextureInputIndex) {
		int definition = getDefinitionFromHeightMap(heightMap);
		RegularElevationTerrain3D terrain = new RegularElevationTerrain3D(terrainGeom, entity, size,definition, amplitude);
		
		Optional<OBJContent> obj = terrain.parseHeightMap(shaderTextureInputIndex, heightMap);
		obj.ifPresent(objUtils -> {
			OBJContent objContent = obj.get();
			mtlLibrary.ifPresent(materials -> {
				objContent.setMaterials(materials);
			});
			terrain.getRenderableGeom().getVAOGeom().loadContent(objContent);
		});
		return terrain;
	}

	private static int getDefinitionFromHeightMap(String heightMap) {
		try (InputStream fileStream = OBJImporter.class.getClassLoader().getResourceAsStream("2D/" + heightMap)) {
			BufferedImage image = ImageIO.read(fileStream);
			return image.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private Optional<OBJContent> parseHeightMap(int shaderTextureInputIndex, String heightMap) {
		ArrayList<Vector3f> positions = new ArrayList<>();
		ArrayList<Vector3f> normalsVector = new ArrayList<>();
		ArrayList<Vector2f> textures = new ArrayList<>();
		ArrayList<Integer> vertexIndices = new ArrayList<>();
		try (InputStream fileStream = OBJImporter.class.getClassLoader().getResourceAsStream("2D/" + heightMap)) {
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
			MaterialContent content = MaterialContent.createImageContent(shaderTextureInputIndex, textures, heightMap);
			return Optional.of(OBJContent.create(vertexIndices, positions, normalsVector, content));
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
