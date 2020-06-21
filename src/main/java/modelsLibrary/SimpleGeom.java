package modelsLibrary;

import org.lwjglx.util.vector.Vector4f;

import entities.SimpleEntity;
import renderEngine.RenderingParameters;

public abstract class SimpleGeom implements ISimpleGeom {
	protected RawGeom rawGeom;
	protected RenderingParameters renderingParameters;
	
	@Override
	public void setColor(Vector4f color) {
		this.rawGeom.setColor(color);
	}

	@Override
	public void reset() {
		this.rawGeom.reset();
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
	public RawGeom getRawGeom() {
		return rawGeom;
	}
	
	protected void copyValues(ISimpleGeom geomRef,String alias) {
		this.rawGeom.copyRawValues(geomRef.getRawGeom());
		this.renderingParameters = RenderingParameters.copy(geomRef.getRenderingParameters(), geomRef, alias, SimpleEntity.createDefaultEntity());
	}
}
