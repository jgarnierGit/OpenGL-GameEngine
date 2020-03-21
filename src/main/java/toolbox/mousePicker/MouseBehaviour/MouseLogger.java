package toolbox.mousePicker.MouseBehaviour;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.UserInputHandler;
import renderEngine.MasterRenderer;
import renderEngine.RayRenderer;
import toolbox.Maths;

public class MouseLogger implements IMouseBehaviour {
	private List<Entity> entities;
	private Camera camera;
	private RayRenderer rayRenderer;
	private Matrix4f viewMatrix;

	public MouseLogger(Camera camera, RayRenderer rayRenderer) {
		this.entities = new ArrayList<>();
		this.camera = camera;
		this.rayRenderer = rayRenderer;
		this.viewMatrix = Maths.createViewMatrix(camera);
	}

	@Override
	public void process(Vector3f ray) {
		// FIXME why need many click to be interpreted
		if (UserInputHandler.activateOnPressOneTime(GLFW_MOUSE_BUTTON_LEFT)) {
			rayCasting(ray);
			//log();
			entities.clear();
		}
	}

	private void cleanSelected() {
		for (Entity entity : entities) {
			entity.unselect();
		}
	}

	/**private void log() {
		cleanSelected();
		float mouseX = UserInputHandler.getMouseXpos();
		float mouseY = UserInputHandler.getMouseYpos();
		System.out.println("ViewPort Space [" + mouseX + ", " + mouseY + "]");
		Vector2f normalizedCoords = getNormalizedDeviceCoords(mouseX, mouseY);
		System.out.println("Normalized device Space [" + normalizedCoords.x + ", " + normalizedCoords.y + "]");
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f);
		System.out.println("Homogeneous clip Space [" + clipCoords.x + ", " + clipCoords.y + ", " + clipCoords.z + ", "
				+ clipCoords.w + "]");
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		System.out.println(
				"Eye Space [" + eyeCoords.x + ", " + eyeCoords.y + ", " + eyeCoords.z + ", " + eyeCoords.w + "]");
		Vector3f worldCoords = toWorldCoords(eyeCoords);
		System.out.println("World Space [" + worldCoords.x + ", " + worldCoords.y + ", " + worldCoords.z + "]");
		System.out.println("-----");
		rayCasting(worldCoords);
	} **/

	private void rayCasting(Vector3f worldCoords) {
		Vector3f rayCasting = new Vector3f(worldCoords.x * 5, worldCoords.y * 5, worldCoords.z * 5);
		Vector3f orig = new Vector3f(this.camera.getPosition().x, this.camera.getPosition().y,
				this.camera.getPosition().z);
		rayRenderer.reloadPositions(orig, rayCasting);
		/**
		 * List<Entity> filteredEntities = this.entities.stream().filter(entity -> {
		 * return (entity.getPositions().x - 0.01 < worldCoords.x &&
		 * entity.getPositions().x + 0.01 > worldCoords.x) && (entity.getPositions().y -
		 * 0.01 < worldCoords.y && entity.getPositions().y + 0.01 > worldCoords.y);
		 * }).collect(Collectors.toList()); TODO decomment filtering when debug is done.
		 **/
		Entity selectedEntity = filterInDistance(this.entities, orig, rayCasting, 0, false);
		if (selectedEntity != null) {
			System.out.println(selectedEntity.getModel().getClass() + " is selected");
			selectedEntity.select();
			Vector3f objectWorld = objectToWorldCoord(selectedEntity.getPositions());
			System.out.println(objectWorld);
		} else {
			System.out.println("nothing selected");
		}
	}
	
	private Vector3f objectToWorldCoord(Vector3f objectPosition) {
		Vector4f objectPos4f = new Vector4f(objectPosition.x, objectPosition.y, objectPosition.z, 1f);
		Vector4f objectWorld = Matrix4f.transform(viewMatrix, objectPos4f, null);
		Vector3f objectToWorld = new Vector3f(objectWorld.x, objectWorld.y, objectWorld.z);
		objectToWorld.normalise();
		return objectToWorld;
	}

	public void processEntity(Entity entities) {
		this.entities.add(entities);
	}

	/**
	 * TODO use a start vector and a end vector : to avoid infinite loop if many
	 * entities have a too small range to be isolated
	 * 
	 * @param filteredEntities
	 * @param rayCasting
	 * @return
	 */
	private Entity filterInDistance(List<Entity> filteredEntities, Vector3f minRayCast, Vector3f maxRayCast,
			int iteration, boolean gotResult) {
		// TODO update filtering.
		List<Entity> filteredInZ = filteredEntities.stream().filter(entity -> {
			Vector3f objectWorld = objectToWorldCoord(entity.getPositions());
			return objectWorld.z < maxRayCast.z && objectWorld.z > minRayCast.z;
		}).collect(Collectors.toList());

		if (filteredInZ.size() == 1) {
			return filteredInZ.get(0);
		} else if (iteration >= RAY_RANGE) { // might never be reach but better safe than infinite
			return null;
		} else if (filteredInZ.isEmpty()) {
			// new max based on twice the distance of current vector.
			// while no match, simple double length of search
			float maxZ = maxRayCast.z * 4;
			float maxX = maxRayCast.x * 4;
			float maxY = maxRayCast.y * 4;
			if (gotResult) {
				maxZ = maxRayCast.z + (maxRayCast.z - minRayCast.z);
				maxX = maxRayCast.x + (maxRayCast.x - minRayCast.x);
				maxY = maxRayCast.y + (maxRayCast.y - minRayCast.y);
			}
			// cap to max rendered distance.
			if (maxZ > MasterRenderer.getFarPlane()) {
				maxZ = MasterRenderer.getFarPlane();
				maxX = (maxRayCast.x * (MasterRenderer.getFarPlane() / maxRayCast.z));
				maxY = (maxRayCast.y * (MasterRenderer.getFarPlane() / maxRayCast.z));
			}
			Vector3f rayCastingLonger = new Vector3f(maxX, maxY, maxZ);
			return filterInDistance(filteredEntities, maxRayCast, rayCastingLonger, ++iteration, gotResult);
		} else {
			float maxZ = minRayCast.z + (maxRayCast.z - minRayCast.z) / 2;
			float maxX = minRayCast.x + (maxRayCast.x - minRayCast.x) / 2;
			float maxY = minRayCast.y + (maxRayCast.y - minRayCast.y) / 2;
			Vector3f rayCastingShorter = new Vector3f(maxX, maxY, maxZ);
			return filterInDistance(filteredEntities, minRayCast, rayCastingShorter, ++iteration, true);
		}
	}

}
