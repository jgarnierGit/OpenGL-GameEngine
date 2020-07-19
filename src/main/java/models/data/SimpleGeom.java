package models.data;

import java.io.NotActiveException;

import entities.SimpleEntity;
import models.EditableGeom;
import models.Geom;
import models.GeomEditor;
import models.RenderableGeom;
import renderEngine.DrawRenderer;
import renderEngine.RenderingParameters;

public abstract class SimpleGeom implements RenderableGeom, EditableGeom {
	protected VAOGeom vaoGeom;
	protected OBJContent objContent;
	// TODO add checked that VBO index must not overlap ?
	protected GeomEditor geomEditor;
	protected RenderingParameters renderingParameters;

	@Override
	public void clear() {
		try {
			this.vaoGeom.clear();
		} catch (NotActiveException e) {
			e.printStackTrace();
		}
		// TODO pass shader index as a generic list. with type binded
		// TODO fixme those -1 with
		// renderingParameters.getShader().getPositionShaderIndex()
		objContent = OBJContent.createEmpty(renderingParameters.getAlias(), -1, -1, -1, -1);
		renderingParameters.reset();
	}

	@Override
	public String toString() {
		return "SimpleGeom [renderingParameters=" + renderingParameters + "]";
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
		vaoGeom.loadToVAO(objContent);
	}

	@Override
	public void bindContentToVAO(OBJContent geomContent) {
		this.objContent = geomContent;
		vaoGeom.loadContent(geomContent);
	}

	@Override
	public OBJContent getObjContent() {
		return this.objContent;
	}

	protected void copyValues(Geom geomRef, String alias) {
		this.vaoGeom = VAOGeom.copy(geomRef.getVAOGeom());
		this.objContent = OBJContent.copy(geomRef.getObjContent());
		this.renderingParameters = RenderingParameters.copy(geomRef.getRenderingParameters(), alias,
				SimpleEntity.createDefaultEntity());
	}
}
