package models;

import java.io.IOException;

import renderEngine.DrawRenderer;
import renderEngine.MasterRenderer;
import shaderManager.Draw2DShader;
import shaderManager.IShader2D;

public class SimpleGeom2DBuilder {
	private SimpleGeom2DBuilder() {
		// hidden
	}

	public static class EmptySimpleGeom2DBuilder {
		public static class ShaderedSimpleGeom2DBuilder {
			IShader2D shader;
			MasterRenderer masterRenderer;
			DrawRenderer draw2DRenderer;
			String alias;

			public ShaderedSimpleGeom2DBuilder(MasterRenderer masterRenderer, DrawRenderer draw2DRenderer, String alias,
					IShader2D shader) {
				this.masterRenderer = masterRenderer;
				this.draw2DRenderer = draw2DRenderer;
				this.alias = alias;
				this.shader = shader;
			}

			public SimpleGeom2D build() throws IOException {
				return SimpleGeom2D.create(masterRenderer, draw2DRenderer, shader, alias);
			}
		}

		MasterRenderer masterRenderer;
		DrawRenderer draw2DRenderer;
		String alias;

		public EmptySimpleGeom2DBuilder(MasterRenderer masterRendererParam, DrawRenderer draw2DRendererParam,
				String aliasParam) {
			masterRenderer = masterRendererParam;
			draw2DRenderer = draw2DRendererParam;
			alias = aliasParam;
		}

		public ShaderedSimpleGeom2DBuilder withShader(IShader2D shader) throws IOException {
			return new ShaderedSimpleGeom2DBuilder(masterRenderer, draw2DRenderer, alias, shader);
		}

		public ShaderedSimpleGeom2DBuilder withDefaultShader() throws IOException {
			return new ShaderedSimpleGeom2DBuilder(masterRenderer, draw2DRenderer, alias, Draw2DShader.createDefault());
		}
	}

	public static EmptySimpleGeom2DBuilder create(MasterRenderer masterRenderer, DrawRenderer draw2DRenderer,
			String alias) {
		return new EmptySimpleGeom2DBuilder(masterRenderer, draw2DRenderer, alias);
	}
}
