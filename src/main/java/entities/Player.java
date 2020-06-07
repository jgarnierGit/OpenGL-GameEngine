package entities;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Optional;

import org.lwjglx.util.vector.Vector3f;

import modelsLibrary.Terrain;
import modelsLibrary.ITerrain;
import modelsManager.Model3D;
import renderEngine.DisplayManager;

public class Player extends EntityTutos {
	private static final float RUN_SPEED = 200;
	private static final float TURN_FLOAT = 160;
	private static final float GRAVITY = 50;
	private static final float JUMP_POWER = 30;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardSpeed = 0;
	private float falling = 0;
	private boolean isInAir = false;

	public Player(Model3D model, Vector3f positions, float rotX, float rotY, float rotZ, float scale) {
		super(model, positions, rotX, rotY, rotZ, scale);
	}
	
	public void move(ITerrain terrain) {
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		upwardSpeed -= GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		Optional<Float> terrainHeight = terrain.getHeight(this.getPositions().x, this.getPositions().z);
		if(terrainHeight.isPresent()) {
			float elevation = terrainHeight.get();
			if(this.getPositions().y < elevation) {
				upwardSpeed = 0;
				this.getPositions().y = elevation;
				isInAir = false;
				falling = 0;
			}
		}
		else {
			falling += DisplayManager.getFrameTimeSeconds();
			if(falling > 3) {
				this.setPositions(new Vector3f(0,10,0));
			}
		}
		
	}
	
	private void jump() {
		if(!isInAir) {
			this.upwardSpeed = JUMP_POWER;
			isInAir = true;
		}
		
	}
	
	private void checkInputs() {
			if (UserInputHandler.activateOnPress(GLFW_KEY_W)) {
				currentSpeed = RUN_SPEED;
			}
			if(UserInputHandler.activateOnPress(GLFW_KEY_S)) {
				currentSpeed = -RUN_SPEED;
			}
			if(!UserInputHandler.activateOnPress(GLFW_KEY_W) && !UserInputHandler.activateOnPress(GLFW_KEY_S)){
				currentSpeed = 0;
			}
			if(UserInputHandler.activateOnPress(GLFW_KEY_A)) {
				currentTurnSpeed = - TURN_FLOAT;
			}
			if(UserInputHandler.activateOnPress(GLFW_KEY_D)) {
				currentTurnSpeed = TURN_FLOAT;
			}
			if(!UserInputHandler.activateOnPress(GLFW_KEY_A) && !UserInputHandler.activateOnPress(GLFW_KEY_D)){
				currentTurnSpeed = 0;
			}
			if(UserInputHandler.activateOnPress(GLFW_KEY_SPACE)) {
				jump();
			}
	}

}
