package toolbox;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL13;

public final class GLTextureIDIncrementer {
	public static final List<Integer> GL_TEXTURE_IDS = Collections.unmodifiableList(Arrays.asList(GL13.GL_TEXTURE0,
			GL13.GL_TEXTURE1,GL13.GL_TEXTURE2,GL13.GL_TEXTURE3,GL13.GL_TEXTURE4));
}
