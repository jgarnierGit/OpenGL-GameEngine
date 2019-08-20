package entities;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjglx.util.vector.Vector3f;

import models.Model3D;
import renderEngine.DisplayManager;

public class Player extends Entity {
	private static final float RUN_SPEED = 20;
	private static final float TURN_FLOAT = 160;
	private static final float GRAVITY = 50;
	private static final float JUMP_POWER = 30;
	private static final float TERRAIN_HEIGHT = -5;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardSpeed = 0;
	private boolean isInAir = false;

	public Player(Model3D model, Vector3f positions, float rotX, float rotY, float rotZ, float scale) {
		super(model, positions, rotX, rotY, rotZ, scale);
		// TODO Auto-generated constructor stub
	}
	
	public void move() {
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		upwardSpeed -= GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		if(super.getPositions().y < TERRAIN_HEIGHT) {
			upwardSpeed = 0;
			super.getPositions().y = TERRAIN_HEIGHT;
			isInAir = false;
		}
	}
	
	private void jump() {
		if(!isInAir) {
			this.upwardSpeed = JUMP_POWER;
			isInAir = true;
		}
		
	}
	
	private void checkInputs() {
			if (UserInputHandler.isActive(GLFW_KEY_W)) {
				currentSpeed = RUN_SPEED;
			}
			if(UserInputHandler.isActive(GLFW_KEY_S)) {
				currentSpeed = -RUN_SPEED;
			}
			if(!UserInputHandler.isActive(GLFW_KEY_W) && !UserInputHandler.isActive(GLFW_KEY_S)){
				currentSpeed = 0;
			}
			if(UserInputHandler.isActive(GLFW_KEY_A)) {
				currentTurnSpeed = - TURN_FLOAT;
			}
			if(UserInputHandler.isActive(GLFW_KEY_D)) {
				currentTurnSpeed = TURN_FLOAT;
			}
			if(!UserInputHandler.isActive(GLFW_KEY_A) && !UserInputHandler.isActive(GLFW_KEY_D)){
				currentTurnSpeed = 0;
			}
			if(UserInputHandler.isActive(GLFW_KEY_SPACE)) {
				jump();
			}
	}

}
