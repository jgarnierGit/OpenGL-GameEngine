package camera;

import inputListeners.InputInteractable;
import inputListeners.PlayerInputListener;

public abstract class Camera extends InputInteractable implements CameraBehavior {
	protected CameraEntity camera;

	public Camera(PlayerInputListener inputListener, CameraEntity camera) {
		super(inputListener);
		this.camera = camera;
	}

}
