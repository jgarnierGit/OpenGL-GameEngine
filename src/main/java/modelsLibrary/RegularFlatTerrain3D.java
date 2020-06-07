package modelsLibrary;

import java.util.Optional;
import java.util.logging.Level;

import org.lwjglx.util.vector.Vector3f;

import renderEngine.Draw3DRenderer;
import renderEngine.Loader;

public class RegularFlatTerrain3D  extends RegularTerrain3D{
	
	private float height;

	public RegularFlatTerrain3D(Loader loader, Draw3DRenderer draw3dRenderer, String alias, int size, int definition,
			int x, int z, float elevation) {
		super(loader, draw3dRenderer, alias, size, definition, x, z);
		height = elevation;
	}
	
	@Override
	protected void generateRegular() {
		for(int gz=0;gz<definition;gz++){
			for(int gx=0;gx<definition;gx++){
				Vector3f topLeft = new Vector3f(this.origineX + (gx/(float)definition*size),height,this.origineZ + (gz/(float)definition*size));
				Vector3f topRight = new Vector3f(this.origineX + (gx/(float)definition*size),height,this.origineZ + ((gz+1)/(float)definition*size));
				Vector3f bottomLeft = new Vector3f(this.origineX + ((gx+1)/(float)definition*size),height,this.origineZ + (gz/(float)definition*size));
				Vector3f bottomRight = new Vector3f(this.origineX + ((gx+1)/(float)definition*size),height,this.origineZ + ((gz+1)/(float)definition*size));
				this.addPoint(topLeft);
				this.addPoint(topRight);
				this.addPoint(bottomLeft);

				this.addPoint(topRight);
				this.addPoint(bottomRight);
				this.addPoint(bottomLeft);

			}
		}
	}
	
	@Override
	public Optional<Float> getHeight(float worldX, float worldZ) {
		if(worldX < this.origineX || worldX > (this.origineX + this.size) || worldZ < this.origineZ || worldZ > (this.origineZ + this.size)) {
			return Optional.empty();
		}
		return Optional.of(height);
	}
}
