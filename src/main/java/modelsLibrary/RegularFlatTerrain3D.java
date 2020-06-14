package modelsLibrary;

import java.util.Optional;

import org.lwjglx.util.vector.Vector3f;

import entities.Entity;
import entities.SimpleEntity;
import renderEngine.Draw3DRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;

public class RegularFlatTerrain3D extends RegularTerrain3D {

	private float height;
	private static final int FLAT_DEFINITION = 1;
	private static final int X_INDEX = 0;
	private static final int Z_INDEX = 0;

	private RegularFlatTerrain3D(Loader loader, Draw3DRenderer draw3dRenderer, String alias, float size, Entity entity) {
		super(loader, draw3dRenderer, alias, size, FLAT_DEFINITION, entity);
		height = entity.getPositions().y;
	}

	public static RegularFlatTerrain3D generateRegular(MasterRenderer masterRenderer, String alias, float size, float x,
			float z, float elevation) {
		SimpleEntity entity = new SimpleEntity(new Vector3f(x,elevation,z), 0, 0, 0, 1);
		RegularFlatTerrain3D terrain = new RegularFlatTerrain3D(masterRenderer.getLoader(), masterRenderer.get3DRenderer(), alias, size,entity);
		Vector3f topLeft = new Vector3f(terrain.origineX + (X_INDEX / (float) terrain.definition * size), terrain.height,
				terrain.origineZ + (Z_INDEX / (float) terrain.definition * size));
		Vector3f topRight = new Vector3f(terrain.origineX + (X_INDEX / (float) terrain.definition * size), terrain.height,
				terrain.origineZ + ((Z_INDEX + 1) / (float) terrain.definition * size));
		Vector3f bottomLeft = new Vector3f(terrain.origineX + ((X_INDEX + 1) / (float) terrain.definition * size), terrain.height,
				terrain.origineZ + (Z_INDEX / (float) terrain.definition * size));
		Vector3f bottomRight = new Vector3f(terrain.origineX + ((X_INDEX + 1) / (float) terrain.definition * size), terrain.height,
				terrain.origineZ + ((Z_INDEX + 1) / (float) terrain.definition * size));
		terrain.addPoint(topLeft);
		terrain.addPoint(topRight);
		terrain.addPoint(bottomLeft);

		terrain.addPoint(topRight);
		terrain.addPoint(bottomRight);
		terrain.addPoint(bottomLeft);
		return terrain;
	}

	@Override
	public Optional<Float> getHeight(float worldX, float worldZ) {
		if (worldX < this.origineX || worldX > (this.origineX + this.size) || worldZ < this.origineZ
				|| worldZ > (this.origineZ + this.size)) {
			return Optional.empty();
		}
		return Optional.of(height);
	}
}
