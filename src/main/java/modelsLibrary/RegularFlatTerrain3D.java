package modelsLibrary;

import java.util.List;
import java.util.Optional;

import org.lwjglx.util.vector.Vector3f;

import entities.Entity;
import entities.SimpleEntity;
import renderEngine.Draw3DRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import utils.Direction;
import utils.SpatialComparator;

public class RegularFlatTerrain3D extends RegularTerrain3D {

	private float height;
	private static final int FLAT_DEFINITION = 5;
	private static final int X_INDEX = 0;
	private static final int Z_INDEX = 0;

	private RegularFlatTerrain3D(Loader loader, Draw3DRenderer draw3dRenderer, String alias, float size,
			Entity entity) {
		super(loader, draw3dRenderer, alias, size, FLAT_DEFINITION, entity);
		height = entity.getPositions().y;
	}

	public static RegularFlatTerrain3D generateRegular(MasterRenderer masterRenderer, String alias, float size, float x,
			float z, float elevation) {
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
				if ((stepx + stepz) % 2 == 0) {
					terrain.addPoint(frontLeft);
					terrain.addPoint(frontRight);
					terrain.addPoint(farLeft);

					terrain.addPoint(frontRight);
					terrain.addPoint(farRight);
					terrain.addPoint(farLeft);
				} else {
					terrain.addPoint(frontLeft);
					terrain.addPoint(farLeft);
					terrain.addPoint(frontRight);

					terrain.addPoint(frontRight);
					terrain.addPoint(farLeft);
					terrain.addPoint(farRight);

				}

			}
		}

		return terrain;
	}

	@Override
	public Optional<Float> getHeight(Vector3f worldPosition) {
		List<Entity> filteredTerrainEntities = SpatialComparator.filterEntitiesByDirection(worldPosition,
				Direction.BOTTOM, this.getRenderingParameters().getEntities());
		if (filteredTerrainEntities.isEmpty()) {
			return Optional.empty();
		}
		Optional<Entity> nearestEntity = Optional.empty();
		for(Entity entityTerrain : filteredTerrainEntities) {
			List<Vector3f> vertices = this.buildVerticesList();
			Vector3f worldFrontLeft = Vector3f.add(entityTerrain.getPositions(), vertices.get(0), null);
			Vector3f worldFarRight = Vector3f.add(entityTerrain.getPositions(), vertices.get(this.buildVerticesList().size() - 1),
					null);
			if (worldPosition.x >= worldFrontLeft.x && worldPosition.x <= worldFarRight.x
					&& worldPosition.z >= worldFrontLeft.z && worldPosition.z <= worldFarRight.z) {
				if(!nearestEntity.isPresent()) {
					nearestEntity = Optional.of(entityTerrain);
					continue;
				}
				if(nearestEntity.get().getPositions().y < entityTerrain.getPositions().y) {
					nearestEntity = Optional.of(entityTerrain);
				}
			}
		}		
		return nearestEntity.map(entity -> entity.getPositions().y);
	}
}
