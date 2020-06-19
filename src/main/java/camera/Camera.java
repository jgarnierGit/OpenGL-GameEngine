package camera;

import inputListeners.InputInteractable;
import inputListeners.PlayerInputListener;

public abstract class Camera extends InputInteractable implements CameraBehavior {
	protected CameraEntity cameraEntity;

	public Camera(PlayerInputListener inputListener, CameraEntity camera) {
		super(inputListener);
		this.cameraEntity = camera;
	}

}
