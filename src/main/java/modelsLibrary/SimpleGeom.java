package modelsLibrary;

import org.lwjglx.util.vector.Vector4f;

import entities.SimpleEntity;
import renderEngine.RenderingParameters;

public abstract class SimpleGeom implements ISimpleGeom {
	protected VAOGeom rawGeom;
	protected RenderingParameters renderingParameters;

	@Override
	public void setColor(Vector4f color) {
		this.rawGeom.updateColor(color);
	}

	@Override
	public void clear() {
		this.rawGeom.clear();
		renderingParameters.reset();
	}

	@Override
	public RenderingParameters getRenderingParameters() {
		return renderingParameters;
	}

	@Override
	public boolean hasTransparency() {
		return this.rawGeom.hasTransparency();
	}

	@Override
	public void updateRenderer() {
		rawGeom.updateRenderer(this);
	}

	@Override
	public VAOGeom getVAOGeom() {
		return rawGeom;
	}

	@Override
	public void reloadVao() {
		rawGeom.reloadVao();
	}

	protected void copyValues(ISimpleGeom geomRef, String alias) {
		this.rawGeom.copyRawValues(geomRef.getVAOGeom());
		this.renderingParameters = RenderingParameters.copy(geomRef.getRenderingParameters(), geomRef, alias,
				SimpleEntity.createDefaultEntity());
	}
}
