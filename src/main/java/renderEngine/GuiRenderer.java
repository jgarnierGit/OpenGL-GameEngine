package renderEngine;

import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Matrix4f;

import entities.GuiTexture;
import renderEngine.Loader.VBOIndex;
import shaderManager.GuiShader;
import toolbox.Maths;

public class GuiRenderer {
	private final int vaoId;
	private final float[] positions = {-1,1,-1,-1,1,1,1,-1};
	private GuiShader shader;

	public GuiRenderer(Loader loader) throws IOException {
		vaoId = loader.loadGUIToVAO(positions);
		shader = new GuiShader();
		
	}
	
	public void render(List<GuiTexture> gui) {
		shader.start();
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(VBOIndex.POSITION_INDEX);
		// set transparency can be refactored.
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH);
		for(GuiTexture texture : gui) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(texture.getPosition(), texture.getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, positions.length);
		}
		GL11.glEnable(GL11.GL_DEPTH);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(VBOIndex.POSITION_INDEX);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}
}
