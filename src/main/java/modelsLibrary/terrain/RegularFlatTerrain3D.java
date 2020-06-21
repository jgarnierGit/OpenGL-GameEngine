package modelsLibrary.terrain;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.lwjglx.util.vector.Vector3f;

import entities.Entity;
import entities.SimpleEntity;
import modelsLibrary.SimpleGeom3D;
import renderEngine.Draw3DRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import utils.Direction;
import utils.Operator;
import utils.SpatialComparator;

public class RegularFlatTerrain3D extends RegularTerrain3D {

	private float height;
	private static final int FLAT_DEFINITION = 5;
	private static final int X_INDEX = 0;
	private static final int Z_INDEX = 0;

	private RegularFlatTerrain3D(Loader loader, Draw3DRenderer draw3dRenderer, String alias, float size, Entity entity)
			throws IOException {
		super(loader, draw3dRenderer, alias, size, FLAT_DEFINITION, entity);
		height = entity.getPositions().y;
	}

	public static RegularFlatTerrain3D generateRegular(MasterRenderer masterRenderer, String alias, float size, float x,
			float z, float elevation) throws IOException {
		SimpleEntity entity = new SimpleEntity(new Vector3f(x, elevation, z), 0, 0, 0, 1);
		RegularFlatTerrain3D terrain = new RegularFlatTerrain3D(masterRenderer.getLoader(),
				masterRenderer.get3DRenderer(), alias, size, entity);
		for (int stepz = 0; stepz < terrain.definition; stepz++) {
			for (int stepx = 0; stepx < terrain.definition; stepx++) {
				Vector3f frontLeft = new Vector3f(entity.getPositions().x + (stepx * (size / FLAT_DEFINITION)),
						terrain.height, entity.getPositions().z + (stepz * (size / FLAT_DEFINITION)));
				Vector3f frontRight = new Vector3f(entity.getPositions().x + ((stepx + 1) * (size / FLAT_DEFINITION)),
						terrain.height, entity.getPositions().z + (stepz * (size / FLAT_DEFINITION)));
				Vector3f farLeft = new Vector3f(entity.getPositions().x + (stepx * (size / FLAT_DEFINITION)),
						terrain.height, entity.getPositions().z + ((stepz + 1) * (size / FLAT_DEFINITION)));
				Vector3f farRight = new Vector3f(entity.getPositions().x + (stepx + 1) * (size / FLAT_DEFINITION),
						terrain.height, entity.getPositions().z + ((stepz + 1) * (size / FLAT_DEFINITION)));
				SimpleGeom3D terrainGeom = terrain.getSimpleGeom();
				if ((stepx + stepz) % 2 == 0) {

					terrainGeom.addPoint(frontLeft);
					terrainGeom.addPoint(frontRight);
					terrainGeom.addPoint(farLeft);

					terrainGeom.addPoint(frontRight);
					terrainGeom.addPoint(farRight);
					terrainGeom.addPoint(farLeft);
				} else {
					terrainGeom.addPoint(frontLeft);
					terrainGeom.addPoint(farLeft);
					terrainGeom.addPoint(frontRight);

					terrainGeom.addPoint(frontRight);
					terrainGeom.addPoint(farLeft);
					terrainGeom.addPoint(farRight);

				}

			}
		}

		return terrain;
	}

	@Override
	public Optional<Float> getHeight(Vector3f worldPosition) {
		List<Entity> filteredTerrainEntities = SpatialComparator.filterEntitiesByDirection(worldPosition,
				Direction.BOTTOM, Operator.INCLUSIVE, this.terrain.getRenderingParameters().getEntities());
		if (filteredTerrainEntities.isEmpty()) {
			return Optional.empty();
		}
		Optional<Entity> nearestEntity = Optional.empty();
		for (Entity entityTerrain : filteredTerrainEntities) {
			List<Vector3f> vertices = this.terrain.buildVerticesList();
			Vector3f worldFrontLeft = Vector3f.add(entityTerrain.getPositions(), vertices.get(0), null);
			Vector3f worldFarRight = Vector3f.add(entityTerrain.getPositions(), vertices.get(vertices.size() - 1),
					null);
			if (worldPosition.x >= worldFrontLeft.x && worldPosition.x <= worldFarRight.x
					&& worldPosition.z >= worldFrontLeft.z && worldPosition.z <= worldFarRight.z) {
				if (!nearestEntity.isPresent()) {
					nearestEntity = Optional.of(entityTerrain);
					continue;
				}
				if (nearestEntity.get().getPositions().y < entityTerrain.getPositions().y) {
					nearestEntity = Optional.of(entityTerrain);
				}
			}
		}
		return nearestEntity.map(entity -> entity.getPositions().y);
	}
}
