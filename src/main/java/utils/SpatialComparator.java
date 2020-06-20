package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import org.lwjglx.util.vector.Vector3f;

import entities.Entity;
import modelsLibrary.Face;

public class SpatialComparator {
	/**
	 * Default: left oriented
	 */
	public static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	/**
	 * Default: top oriented
	 */
	public static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	/**
	 * Default: far oriented
	 */
	public static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);

	private SpatialComparator() {
		// hidden constructor
	}
	
	public static List<Entity> filterEntitiesByValueEquality(float value, Axis axis, List<Entity> entities){
		BiPredicate<Vector3f, Vector3f> predicate = null;
		Vector3f position = null;
		switch(axis) {
		case X:
			predicate = (ref, test) -> ref.x == test.x;
			position = new Vector3f(value,0,0);
			break;
		case Y:
			predicate = (ref, test) -> ref.y == test.y;
			position = new Vector3f(0,value,0);
			break;
		case Z: 
			predicate = (ref, test) -> ref.z == test.z;
			position = new Vector3f(0,0,value);
			break;
		default:
			throw new IllegalArgumentException(axis + " is unknown");
		}
		return getFilteredEntity(predicate, position, entities);
	}

	/**
	 * 
	 * @param positions reference world position
	 * @param direction filter entities over this world direction
	 * @param operator
	 * @param entities  to filter over
	 * @return Entity if exists Optional.empty else
	 */
	public static List<Entity> filterEntitiesByDirection(Vector3f positions, Direction direction, Operator operator,
			List<Entity> entities) {
		BiPredicate<Vector3f, Vector3f> predicate = null;
		switch (operator) {
		case EXCLUSIVE:
			switch (direction) {
			case BOTTOM:
				predicate = (ref, test) -> ref.y > test.y;
				break;
			case TOP:
				predicate = (ref, test) -> ref.y < test.y;
				break;
			case WEST:
				predicate = (ref, test) -> ref.x < test.x;
				break;
			case EAST:
				predicate = (ref, test) -> ref.x > test.x;
				break;
			case SOUTH:
				predicate = (ref, test) -> ref.z > test.z;
				break;
			case NORTH:
				predicate = (ref, test) -> ref.z < test.z;
				break;
			default:
				throw new IllegalArgumentException(direction + " is unknown");
			}
			break;
		case INCLUSIVE:
			switch (direction) {
			case BOTTOM:
				predicate = (ref, test) -> ref.y >= test.y;
				break;
			case TOP:
				predicate = (ref, test) -> ref.y <= test.y;
				break;
			case WEST:
				predicate = (ref, test) -> ref.x <= test.x;
				break;
			case EAST:
				predicate = (ref, test) -> ref.x >= test.x;
				break;
			case SOUTH:
				predicate = (ref, test) -> ref.z >= test.z;
				break;
			case NORTH:
				predicate = (ref, test) -> ref.z <= test.z;
				break;
			default:
				throw new IllegalArgumentException(direction + " is unknown");
			}
			break;
		default:
			throw new IllegalArgumentException(operator + " is unknown");
		}
		
		return getFilteredEntity(predicate, positions, entities);
	}

	private static List<Entity> getFilteredEntity(BiPredicate<Vector3f, Vector3f> filteringPredicate,
			Vector3f refPosition, List<Entity> entities) {
		List<Entity> filteredEntities = new ArrayList<>();
		for (Entity entity : entities) {
			if (filteringPredicate.test(refPosition, entity.getPositions())) {
				filteredEntities.add(entity);
			}
		}
		return filteredEntities;
	}

	/**
	 * Get each intersection between refPosition (projected along axis) and faces
	 * (projected to worldPositionFaces)
	 * 
	 * @param refPosition        position to project on entity geom
	 * @param axis               axis of projection to apply
	 * @param faces              list of faces composing geom.
	 * @param worldPositionFaces world position of geom reference
	 * @return
	 */
	public static List<IntersectionResult> getProjectionOverEntity(Vector3f refPosition, Vector3f axis,
			List<Face> faces, Vector3f worldPositionFaces) {
		List<IntersectionResult> projectedPoints = new ArrayList<>();
		Vector3f axisNormalized = new Vector3f();
		axis.normalise(axisNormalized);
		for (Face face : faces) {
			Vector3f originFace = Vector3f.add(worldPositionFaces, face.p1, null);
			Vector3f uPoint = Vector3f.add(worldPositionFaces, face.p0, null);
			Vector3f vPoint = Vector3f.add(worldPositionFaces, face.p2, null);
			// getting length of a face from camera.
			Vector3f nearFromCamera = Vector3f.sub(originFace, refPosition, null);
			// getting normal from a face.
			Vector3f nearPlaneU2 = Vector3f.sub(uPoint, originFace, null);
			Vector3f nearPlaneV2 = Vector3f.sub(vPoint, originFace, null);
			Vector3f nearNormal = Vector3f.cross(nearPlaneU2, nearPlaneV2, null);
			nearNormal.normalise();
			// getting ratio to apply
			float cosThetaFromNormal = Vector3f.dot(nearFromCamera, nearNormal);
			float cosThetaFromRay = Vector3f.dot(axisNormalized, nearNormal);
			float k = cosThetaFromNormal / cosThetaFromRay;
			Vector3f intersect = new Vector3f(axisNormalized.x, axisNormalized.y, axisNormalized.z);
			intersect.scale(k);
			Vector3f rayIntersectToWorld = Vector3f.add(refPosition, intersect, null);

			if (k >= 0) {
				double roundedRayX = Math.floor(rayIntersectToWorld.x * 100);
				double roundedRayY = Math.floor(rayIntersectToWorld.y * 100);
				double roundedRayZ = Math.floor(rayIntersectToWorld.z * 100);
				double uPointX = Math.floor(uPoint.x * 100);
				double uPointY = Math.floor(uPoint.y * 100);
				double uPointZ = Math.floor(uPoint.z * 100);
				double vPointX = Math.floor(vPoint.x * 100);
				double vPointY = Math.floor(vPoint.y * 100);
				double vPointZ = Math.floor(vPoint.z * 100);
				if (roundedRayX >= Math.min(uPointX, vPointX) && roundedRayX <= Math.max(uPointX, vPointX)
						&& roundedRayY >= Math.min(uPointY, vPointY) && roundedRayY <= Math.max(uPointY, vPointY)
						&& roundedRayZ >= Math.min(uPointZ, vPointZ) && roundedRayZ <= Math.max(uPointZ, vPointZ)) {
					IntersectionResult result = new IntersectionResult();
					result.setProjectedPosition(rayIntersectToWorld);
					result.setFace(face);
					result.setSourcePosition(refPosition);
					projectedPoints.add(result);
				}
			}
		}

		return projectedPoints;
	}
}
