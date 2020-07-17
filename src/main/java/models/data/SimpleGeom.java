package models.data;

import java.io.NotActiveException;

import entities.SimpleEntity;
import models.GeomEditor;
import models.EditableGeom;
import models.RenderableGeom;
import renderEngine.DrawRenderer;
import renderEngine.RenderingParameters;

public abstract class SimpleGeom implements RenderableGeom, EditableGeom {
	protected VAOGeom vaoGeom;
	protected GeomEditor geomEditor;
	protected RenderingParameters renderingParameters;

	@Override
	public void clear() {
		try {
			this.vaoGeom.clear();
		} catch (NotActiveException e) {
			e.printStackTrace();
		}
		renderingParameters.reset();
	}

	@Override
	public RenderingParameters getRenderingParameters() {
		return renderingParameters;
	}

	@Override
	public void updateRenderer() {
		vaoGeom.updateRenderer(this);
	}
	
	@Override
	public DrawRenderer getRenderer() {
		return vaoGeom.getRenderer();
	}

	@Override
	public VAOGeom getVAOGeom() {
		return vaoGeom;
	}

	@Override
	public GeomEditor getGeomEditor() {
		return this.geomEditor;
	}

	@Override
	public void reloadVao() {
		vaoGeom.loadToVAO();
	}
	
	public void bindContentToVAO(OBJContent geomContent) {
		vaoGeom.loadContent(geomContent);
	}

	@Override
	public OBJContent getObjContent() {
		return this.vaoGeom.objContent;
	}

	protected void copyValues(RenderableGeom geomRef, String alias) {
		this.vaoGeom = VAOGeom.copy(geomRef.getVAOGeom());
		this.renderingParameters = RenderingParameters.copy(geomRef.getRenderingParameters(), geomRef.getVAOGeom(),
				alias, SimpleEntity.createDefaultEntity());
	}
}
