package renderEngine;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import renderEngine.Loader.VBOIndex;
import shaderManager.Draw2DShader;

/**
 * Render as is geom
 * 
 * @author chezmoi
 *
 */
public class Draw2DRenderer extends DrawRenderer {
	public Draw2DRenderer() throws IOException {
		super();
	}

	@Override
	public void render() {
		for (RenderingParameters params : renderingParams) {
			Draw2DShader draw2DShader = (Draw2DShader) params.getShader();
			draw2DShader.start();
			prepare(params.getGeom(), Draw2DShader.COLOR_INDEX, VBOIndex.POSITION_INDEX);

			// Disable distance filtering.
			GL11.glDisable(GL11.GL_DEPTH);
			genericDrawRender(params);
			unbindGeom(Draw2DShader.COLOR_INDEX, VBOIndex.POSITION_INDEX);
			// GL11.glLineWidth(1);
			GL11.glEnable(GL11.GL_DEPTH);
			draw2DShader.stop();
		}
	}

	@Override
	public void cleanUp() {
		for (RenderingParameters params : renderingParams) {
			Draw2DShader draw2DShader = (Draw2DShader) params.getShader();
			draw2DShader.cleanUp();
		}
	}
}
