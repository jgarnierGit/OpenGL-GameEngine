package toolbox.mousePicker.MouseBehaviour;

import org.lwjglx.util.vector.Vector3f;

public interface IMouseBehaviour {
	public static final float RAY_RANGE=  600;
	public static final int RECURSION_COUNT = 200;
	public void process(Vector3f ray);
}
