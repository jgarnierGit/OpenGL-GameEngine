package renderEngine;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import models.RenderableGeom;
import renderEngine.Loader.VBOIndex;
import shaderManager.Draw2DShader;

/**
 * Render as is geom
 * 
 * @author chezmoi
 *
 */
public class Draw2DRenderer extends DrawRendererCommon {
	public Draw2DRenderer() throws IOException {
		super();
	}

	@Override
	public void render() {
		for (RenderableGeom geom : geoms) {
			RenderingParameters params = geom.getRenderingParameters();
			Draw2DShader draw2DShader = (Draw2DShader) params.getShader();
			draw2DShader.start();
			prepare(geom.getVAOGeom().getVaoId());

			// Disable distance filtering.
			GL11.glDisable(GL11.GL_DEPTH);
			genericDrawRender(geom);
			// GL11.glLineWidth(1);
			GL11.glEnable(GL11.GL_DEPTH);
			draw2DShader.stop();
		}
	}

	@Override
	protected void prepare(int vaoId) {
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(Draw2DShader.COLOR_INDEX);
		GL20.glEnableVertexAttribArray(VBOIndex.POSITION_INDEX);
	}

	@Override
	protected void unbindGeom() {
		GL20.glDisableVertexAttribArray(VBOIndex.POSITION_INDEX);
		GL20.glDisableVertexAttribArray(Draw2DShader.COLOR_INDEX);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

}
