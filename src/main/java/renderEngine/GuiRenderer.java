package renderEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Matrix4f;

import entities.GuiTexture;
import models.data.VBOContent;
import renderEngine.Loader.VBOIndex;
import shaderManager.GuiShader;
import toolbox.Maths;

public class GuiRenderer {
	private final int vaoId;
	private final List<Float> positions = Arrays.asList(-1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f); // construction of quad
																								// assuming
																								// triangle_strip
	private GuiShader shader;
	private List<GuiTexture> guis;

	public GuiRenderer(Loader loader) throws IOException {
		vaoId = loader.loadToVAO(VBOContent.create(0, 2, positions));
		shader = new GuiShader();
		guis = new ArrayList<>();
	}

	public void addGui(GuiTexture gui) {
		this.guis.add(gui);
	}

	public void render() {
		shader.start();
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(VBOIndex.POSITION_INDEX);
		// set transparency can be refactored.
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH);
		for (GuiTexture texture : guis) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(texture.getPosition(), texture.getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, positions.size());
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
