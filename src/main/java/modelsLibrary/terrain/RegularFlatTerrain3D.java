package modelsLibrary.terrain;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.lwjglx.util.vector.Vector3f;

import entities.Entity;
import entities.SimpleEntity;
import modelsLibrary.IEditableGeom;
import modelsLibrary.IGeomEditor;
import modelsLibrary.IRenderableGeom;
import modelsLibrary.SimpleGeom3D;
import renderEngine.Draw3DRenderer;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import utils.Direction;
import utils.Operator;
import utils.SpatialComparator;

public class RegularFlatTerrain3D extends RegularTerrain3D {

	private float height;
	private static final int FLAT_DEFINITION = 1;
	private static final int X_INDEX = 0;
	private static final int Z_INDEX = 0;

	private RegularFlatTerrain3D(SimpleGeom3D terrain, Entity defaultEntity, float size) {
		super(terrain, defaultEntity, size, FLAT_DEFINITION);
		height = defaultEntity.getPositions().y;
	}

	public static RegularFlatTerrain3D generateRegular(SimpleGeom3D terrainGeom, Entity defaultEntity, float size) throws IOException {
		RegularFlatTerrain3D terrain = new RegularFlatTerrain3D(terrainGeom, defaultEntity, size);
		for (int stepz = 0; stepz < terrain.definition; stepz++) {
			for (int stepx = 0; stepx < terrain.definition; stepx++) {
				Vector3f frontLeft = new Vector3f(defaultEntity.getPositions().x + (stepx * (size / FLAT_DEFINITION)),
						terrain.height, defaultEntity.getPositions().z + (stepz * (size / FLAT_DEFINITION)));
				Vector3f frontRight = new Vector3f(defaultEntity.getPositions().x + ((stepx + 1) * (size / FLAT_DEFINITION)),
						terrain.height, defaultEntity.getPositions().z + (stepz * (size / FLAT_DEFINITION)));
				Vector3f farLeft = new Vector3f(defaultEntity.getPositions().x + (stepx * (size / FLAT_DEFINITION)),
						terrain.height, defaultEntity.getPositions().z + ((stepz + 1) * (size / FLAT_DEFINITION)));
				Vector3f farRight = new Vector3f(defaultEntity.getPositions().x + (stepx + 1) * (size / FLAT_DEFINITION),
						terrain.height, defaultEntity.getPositions().z + ((stepz + 1) * (size / FLAT_DEFINITION)));
				IGeomEditor terrainGeomEditor = terrain.getGeomEditor();
				if ((stepx + stepz) % 2 == 0) {

					terrainGeomEditor.addPoint(frontLeft);
					terrainGeomEditor.addPoint(frontRight);
					terrainGeomEditor.addPoint(farLeft);

					terrainGeomEditor.addPoint(frontRight);
					terrainGeomEditor.addPoint(farRight);
					terrainGeomEditor.addPoint(farLeft);
				} else {
					terrainGeomEditor.addPoint(frontLeft);
					terrainGeomEditor.addPoint(farLeft);
					terrainGeomEditor.addPoint(frontRight);

					terrainGeomEditor.addPoint(frontRight);
					terrainGeomEditor.addPoint(farLeft);
					terrainGeomEditor.addPoint(farRight);

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
			List<Vector3f> vertices = this.terrain.getVertices();
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
