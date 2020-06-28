package modelsLibrary;

import java.io.IOException;
import java.util.Optional;

import entities.Entity;
import renderEngine.DrawRenderer;
import renderEngine.Loader;
import shaderManager.Draw3DShader;
import shaderManager.IShader3D;
import toolbox.CoordinatesSystemManager;

public class SimpleGeom3DBuilder {
	private SimpleGeom3DBuilder() {
		// hidden
	}

	public static class EmptySimpleGeom3DBuilder {
		public static class ShaderedSimpleGeom3DBuilder {
			IShader3D shader;
			Optional<Entity> entity;
			Loader loader;
			DrawRenderer draw3DRenderer;
			String alias;

			// TODO create interface for 3DDrawrenderer.
			public ShaderedSimpleGeom3DBuilder(Loader loader, DrawRenderer draw3DRenderer, String alias,
					IShader3D shader) {
				this.loader = loader;
				this.draw3DRenderer = draw3DRenderer;
				this.alias = alias;
				this.entity = Optional.empty();
				this.shader = shader;
			}

			public ShaderedSimpleGeom3DBuilder withEntity(Entity entity) {
				this.entity = Optional.of(entity);
				return this;
			}

			public SimpleGeom3D build() {
				SimpleGeom3D simpleGeom;
				if (this.entity.isPresent()) {
					simpleGeom = SimpleGeom3D.create(loader, draw3DRenderer, shader, alias, entity.get());
				} else {
					simpleGeom = SimpleGeom3D.createWithDefaultEntity(loader, draw3DRenderer, shader, alias);
				}
				shader.start();
				shader.loadProjectionMatrix(CoordinatesSystemManager.getProjectionMatrix());
				shader.stop();
				return simpleGeom;
			}
		}

		Loader loader;
		DrawRenderer draw3DRenderer;
		String alias;

		public EmptySimpleGeom3DBuilder(Loader loaderParam, DrawRenderer draw3DRendererParam, String aliasParam) {
			loader = loaderParam;
			draw3DRenderer = draw3DRendererParam;
			alias = aliasParam;
		}

		public ShaderedSimpleGeom3DBuilder withShader(IShader3D shader) {
			return new ShaderedSimpleGeom3DBuilder(loader, draw3DRenderer, alias, shader);
		}

		public ShaderedSimpleGeom3DBuilder withDefaultShader() throws IOException {
			return new ShaderedSimpleGeom3DBuilder(loader, draw3DRenderer, alias, Draw3DShader.createDefault());
		}
	}

	public static EmptySimpleGeom3DBuilder create(Loader loader, DrawRenderer draw3DRenderer, String alias) {
		return new EmptySimpleGeom3DBuilder(loader, draw3DRenderer, alias);
	}
}
