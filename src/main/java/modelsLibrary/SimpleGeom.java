package modelsLibrary;

import entities.SimpleEntity;
import modelsManager.OBJContent;
import renderEngine.RenderingParameters;

public abstract class SimpleGeom implements IRenderableGeom, IEditableGeom {
	protected VAOGeom vaoGeom;
	protected GeomEditor geomEditor;
	protected RenderingParameters renderingParameters;

	@Override
	public void clear() {
		this.vaoGeom.clear();
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
	public VAOGeom getVAOGeom() {
		return vaoGeom;
	}

	@Override
	public GeomEditor getGeomEditor() {
		return this.geomEditor;
	}

	@Override
	public void reloadVao() {
		vaoGeom.reloadVao();
	}

	@Override
	public OBJContent getObjContent() {
		return this.vaoGeom.objContent;
	}

	protected void copyValues(IRenderableGeom geomRef, String alias) {
		this.vaoGeom = VAOGeom.copy(geomRef.getVAOGeom());
		this.renderingParameters = RenderingParameters.copy(geomRef.getRenderingParameters(), geomRef.getVAOGeom(),
				alias, SimpleEntity.createDefaultEntity());
	}
}
