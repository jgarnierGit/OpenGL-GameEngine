package modelsLibrary;

import java.util.Optional;

import org.apache.commons.lang3.NotImplementedException;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import renderEngine.Draw3DRenderer;
import renderEngine.Loader;
import toolbox.Maths;

public class RegularElevationTerrain3D extends RegularTerrain3D{
	private float[][] heights;

	public RegularElevationTerrain3D(Loader loader, Draw3DRenderer draw3dRenderer, String alias, int size,
			int definition, int x, int z) {
		super(loader, draw3dRenderer, alias, size, definition, x, z);
		heights = new float[definition][definition];
	}

	@Override
	protected void generateRegular() {
		throw new NotImplementedException("");
		/**for(int gz=0;gz<definition;gz++){
			for(int gx=0;gx<definition;gx++){
				heights[gx][gz] = 0;
				Vector3f topLeft = new Vector3f(this.origineX + (gx/(float)definition*size),height,this.origineZ + (gz/(float)definition*size));
				Vector3f topRight = new Vector3f(this.origineX + (gx/(float)definition*size),height,this.origineZ + ((gz+1)/(float)definition*size));
				Vector3f bottomLeft = new Vector3f(this.origineX + ((gx+1)/(float)definition*size),height,this.origineZ + (gz/(float)definition*size));
				Vector3f bottomRight = new Vector3f(this.origineX + ((gx+1)/(float)definition*size),height,this.origineZ + ((gz+1)/(float)definition*size));
				this.addPoint(topLeft);
				this.addPoint(topRight);
				this.addPoint(bottomLeft);

				this.addPoint(topRight);
				this.addPoint(bottomRight);
				this.addPoint(bottomLeft);
			}
		}**/
	}
	
	@Override
	public Optional<Float> getHeight(float worldX, float worldZ) {
		if(worldX < this.origineX || worldX > (this.origineX + this.size) || worldZ < this.origineZ || worldZ > (this.origineZ + this.size)) {
			return Optional.empty();
		}
		float terrainX = worldX - this.origineX;
		float terrainZ = worldZ - this.origineZ;
		float gridSquareSize = this.size / ((float)heights.length-1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
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
		return Optional.of(answer);
	}

}
