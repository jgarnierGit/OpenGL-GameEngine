package models;

import java.io.IOException;
import java.util.Optional;

import entities.Entity;
import renderEngine.DrawRenderer;
import renderEngine.MasterRenderer;
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
			MasterRenderer masterRenderer;
			DrawRenderer draw3DRenderer;
			String alias;

			// TODO create interface for 3DDrawrenderer.
			public ShaderedSimpleGeom3DBuilder(MasterRenderer masterRenderer, DrawRenderer draw3DRenderer, String alias,
					IShader3D shader) {
				this.masterRenderer = masterRenderer;
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
					simpleGeom = SimpleGeom3D.create(masterRenderer, draw3DRenderer, shader, alias, entity.get());
				} else {
					simpleGeom = SimpleGeom3D.createWithDefaultEntity(masterRenderer, draw3DRenderer, shader, alias);
				}
				shader.start();
				shader.loadProjectionMatrix(CoordinatesSystemManager.getProjectionMatrix());
				shader.stop();
				return simpleGeom;
			}
		}

		MasterRenderer masterRenderer;
		DrawRenderer draw3DRenderer;
		String alias;

		public EmptySimpleGeom3DBuilder(MasterRenderer masterRendererParam, DrawRenderer draw3DRendererParam,
				String aliasParam) {
			masterRenderer = masterRendererParam;
			draw3DRenderer = draw3DRendererParam;
			alias = aliasParam;
		}

		public ShaderedSimpleGeom3DBuilder withShader(IShader3D shader) {
			return new ShaderedSimpleGeom3DBuilder(masterRenderer, draw3DRenderer, alias, shader);
		}

		public ShaderedSimpleGeom3DBuilder withDefaultShader() throws IOException {
			return new ShaderedSimpleGeom3DBuilder(masterRenderer, draw3DRenderer, alias, Draw3DShader.createDefault());
		}
	}

	public static EmptySimpleGeom3DBuilder create(MasterRenderer masterRenderer, DrawRenderer draw3DRenderer,
			String alias) {
		return new EmptySimpleGeom3DBuilder(masterRenderer, draw3DRenderer, alias);
	}
}
