package renderEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import modelsLibrary.ISimpleGeom;
import renderEngine.Loader.VBOIndex;
import shaderManager.Draw2DShader;
import shaderManager.Draw3DShader;

/**
 * Render as is geom
 * @author chezmoi
 *
 */
public class Draw2DRenderer extends DrawRenderer{
	private Draw2DShader draw2DShader;

	public Draw2DRenderer() throws IOException {
		super();
		this.draw2DShader = new Draw2DShader();
	}

	@Override
	public void render() {
		for(ISimpleGeom geom : geoms) {
			draw2DShader.start();
			prepare(geom, Draw2DShader.COLOR_INDEX, VBOIndex.POSITION_INDEX);
			
			// Disable distance filtering.
			GL11.glDisable(GL11.GL_DEPTH);
			renderByMode(geom);	
			unbindGeom(Draw2DShader.COLOR_INDEX,VBOIndex.POSITION_INDEX);
			// GL11.glLineWidth(1);
			GL11.glEnable(GL11.GL_DEPTH);
			draw2DShader.stop();
		}
	}
	
	@Override
	public void cleanUp() {
		draw2DShader.cleanUp();
	}
	
	@Override
	public void reloadAndprocess(ISimpleGeom geom, int renderingIndex) {
		geom.reloadPositions(Draw2DShader.COLOR_INDEX);
		process(geom, renderingIndex);
	}
}
