package renderEngine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import models.RenderableGeom;
import models.data.OBJContent;
import toolbox.GLTextureIDIncrementer;

/**
 * Draw primitive 3D objects directly by drawArrays. For heavy objects use
 * Renderer with glDrawElements. gl_drawarrays does no benefits of indices
 * optimization while binding vertices into vao.
 * 
 * @author chezmoi
 *
 */
public abstract class DrawRendererCommon implements DrawRenderer {
	protected List<RenderableGeom> geoms;
	protected Logger logger = Logger.getLogger("DrawRenderer");

	public DrawRendererCommon() {
		this.geoms = new ArrayList<>();
	}

	@Override
	public void process(RenderableGeom geom) {
		this.geoms.add(geom);
	}

	@Override
	public void cleanUp() {
		for (RenderableGeom geom : geoms) {
			geom.getRenderingParameters().getShader().cleanUp();
		}
	}

	/**
	 * Before we can render a VAO it needs to be made active, and we can do this by
	 * binding it. We also need to enable the relevant attributes of the VAO, which
	 * in this case is just attribute 0 where we stored the position data.
	 * 
	 * <pre>
	 * Call prepare at first of render method.
	 * Hints: 
	 * GL30.glBindVertexArray
	 * GL20.glEnableVertexAttribArray
	 * GL13.glActiveTexture
	 * GL11.glBindTexture
	 * GL11.glEnable
	 * </pre>
	 * 
	 * @param VaoId
	 */
	protected abstract void prepare(int vaoId);

	/**
	 * <pre>
	 * Call unbindGeom before leaving render method.
	 * Hints:
	 * GL20.glDisableVertexAttribArray
	 * GL30.glBindVertexArray(0);
	 * GL11.glDisable
	 * </pre>
	 */
	protected abstract void unbindGeom();

	@Override
	public void updateForRendering() {
		updateOverridingColors();
		geoms = getOrderedRenderingParameters();
		geoms = checkForEntitiesPresence();
	}

	/**
	 * TODO is this still meaningful?
	 * 
	 * @return
	 */
	private LinkedList<RenderableGeom> checkForEntitiesPresence() {
		LinkedList<RenderableGeom> noEmptyGeomEntities = new LinkedList<>();
		for (RenderableGeom geom : geoms) {
			RenderingParameters param = geom.getRenderingParameters();
			if (!param.isNotUsingEntities() && param.getEntities().isEmpty()) {
				if (this.logger.isLoggable(Level.INFO)) {
					this.logger.info(param.getAlias()
							+ " has no entities set, will not be rendered. Tip : This filtering can be bypassed by setting doNotUseEntities to active RenderingParam");
				}
			} else {
				noEmptyGeomEntities.add(geom);
			}
		}
		return noEmptyGeomEntities;
	}

	/**
	 * Apply color override.
	 */
	private void updateOverridingColors() {
		for (RenderableGeom simpleGeom : this.geoms) {
			RenderingParameters rParam = simpleGeom.getRenderingParameters();
			boolean edited = rParam.applyColorOverriding(simpleGeom.getGeomEditor());
			if (edited) {
				simpleGeom.reloadVao();
			}
		}
	}

	@Override
	public void clearGeom() {
		this.geoms.clear();
	}

	protected void genericDrawRender(RenderableGeom geom) {
		// GL11.glEnable(GL11.GL_POINT_SMOOTH);
		GL11.glLineWidth(2); // seems to have a max cap unlike PointSize. for GL_LINES
		GL11.glPointSize(5); // GL_POINTS
		// must be binded before loading points / indices.
		bindTextures(geom.getVAOGeom().getTextures());
		if (geom.getObjContent().getIndices().isEmpty()) {
			renderByVertices(geom);
		} else {
			renderByIndices(geom.getObjContent().getIndicesAsPrimitiveArray());
		}
		GL11.glPointSize(1);
		GL11.glLineWidth(1);

	}

	private void renderByIndices(int[] indicesAsPrimitiveArray) {
		GL11.glDrawElements(GL11.GL_TRIANGLES, MasterRenderer.storeDataInIntBuffer(indicesAsPrimitiveArray));
	}

	private void bindTextures(Set<Integer> textures) {
		int i = 0;
		for (Integer textureIndex : textures) {
			if (i >= 33) {
				return;
			}
			/**
			 * TODO use RenderingParameters.enableRenderOptions(). if (texture.getDissolve()
			 * < 1.0f || mtlUtils.isHasTransparency()) { MasterRenderer.disableCulling(); }
			 **/
			// shader.loadShineVariable(texture.getSpecularExponent()); TODO reimplement
			// below link to sampler2D textureSampler in fragmentShader
			GL13.glActiveTexture(GLTextureIDIncrementer.GL_TEXTURE_IDS.get(i));
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIndex);
			i++;
		}
		// GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	private void renderByVertices(RenderableGeom geom) {
		// cf https://www.khronos.org/opengl/wiki/Primitive => internal gl logic, hidden
		// for DrawArrays usage;
		OBJContent geomContent = geom.getObjContent();
		int verticesCount = geomContent.getPositions().getContent().size() / geomContent.getPositions().getDimension();
		// Add default lineLoop rendering.
		// GL11.drawArrays can draw points with GL_POINTS, not GL_POINT
		GL11.glDrawArrays(geom.getRenderingParameters().getRenderMode().orElse(GL11.GL_POINTS), 0, verticesCount);
	}

	protected List<RenderableGeom> getOrderedRenderingParameters() {
		LinkedList<RenderableGeom> rawGeoms = new LinkedList<>();
		int i = 0;
		for (RenderableGeom simpleGeom : this.geoms) {
			RenderingParameters param = simpleGeom.getRenderingParameters();
			if (param.getAlias().isEmpty()) {
				param.setAlias("" + i);
			}
			rawGeoms.add(simpleGeom);
			i++;
		}
		LinkedHashSet<RenderableGeom> uniqueGeoms = rawGeoms.stream()
				.collect(Collectors.toCollection(LinkedHashSet::new));
		LinkedHashSet<RenderableGeom> cleanedGeomParams = removeMissConfiguratedReference(uniqueGeoms);
		LinkedList<RenderableGeom> geomsToMove = cleanedGeomParams.stream().filter((geom) -> {
			return geom.getRenderingParameters().isDestinationPositionAfter().isPresent()
					&& !geom.getRenderingParameters().getDestinationOrderAlias()
							.equals(geom.getRenderingParameters().getAlias());
		}).collect(Collectors.toCollection(LinkedList::new));
		List<RenderableGeom> finalsortedGeoms = transformRelativePositionToIndex(cleanedGeomParams, geomsToMove);
		return orderRawParams(rawGeoms, finalsortedGeoms);
	}

	private List<RenderableGeom> orderRawParams(LinkedList<RenderableGeom> rawParams,
			List<RenderableGeom> sortedUniqueParams) {
		LinkedList<RenderableGeom> finalList = new LinkedList<>();
		for (RenderableGeom param : sortedUniqueParams) {
			finalList.addAll(rawParams.stream().filter(rawParam -> rawParam.getRenderingParameters().getAlias()
					.equals(param.getRenderingParameters().getAlias())).collect(Collectors.toList()));
		}
		return finalList;
	}

	/**
	 * remove unknown reference destination. remove self reference destination.
	 * 
	 * @param geomsToRender
	 * @return
	 */
	private LinkedHashSet<RenderableGeom> removeMissConfiguratedReference(LinkedHashSet<RenderableGeom> geomsToRender) {
		LinkedHashSet<RenderableGeom> modifiedParams = new LinkedHashSet<>();
		modifiedParams.addAll(geomsToRender);
		for (RenderableGeom geom : modifiedParams) {
			RenderingParameters param = geom.getRenderingParameters();
			if (!param.getDestinationOrderAlias().isEmpty()) {
				boolean isPresent = checkReferencePresence(geomsToRender, param.getDestinationOrderAlias());
				if (!isPresent) {
					this.logger.log(Level.WARNING,
							"reference to " + param.getDestinationOrderAlias() + " is unknown. Will be removed");
					param.resetRenderingOrder();
				}
			}
		}
		return modifiedParams;
	}

	private boolean checkReferencePresence(LinkedHashSet<RenderableGeom> geomsToRender, String destinationOrderAlias) {
		for (RenderableGeom geom : geomsToRender) {
			RenderingParameters param = geom.getRenderingParameters();
			if (param.getAlias().equals(destinationOrderAlias)) {
				return true;
			}
		}
		return false;
	}

	private List<RenderableGeom> transformRelativePositionToIndex(LinkedHashSet<RenderableGeom> rawParams,
			LinkedList<RenderableGeom> geomsToMove) {
		LinkedList<RenderableGeom> sortedList = new LinkedList<>();
		sortedList.addAll(rawParams);
		for (RenderableGeom geom : geomsToMove) {
			RenderingParameters param = geom.getRenderingParameters();
			Boolean positionedAfter = param.isDestinationPositionAfter().get();
			sortedList.remove(geom);
			int index = 0;
			if (param.getDestinationOrderAlias().isEmpty()) {
				index = positionedAfter ? sortedList.size() : 0;
			} else {
				index = getIndexDestination(sortedList, param.getDestinationOrderAlias(), positionedAfter);
			}

			sortedList.add(index, geom);
		}
		return sortedList;
	}

	private int getIndexDestination(LinkedList<RenderableGeom> sortedParams, String destinationOrderAlias,
			boolean destinationPositionAfter) {
		Iterator<RenderableGeom> itParams = destinationPositionAfter ? sortedParams.descendingIterator()
				: sortedParams.iterator();
		int index = destinationPositionAfter ? sortedParams.size() : 0;
		boolean found = false;
		while (itParams.hasNext()) {

			RenderableGeom currentGeom = itParams.next();
			RenderingParameters currentParam = currentGeom.getRenderingParameters();
			if (destinationOrderAlias.equals(currentParam.getAlias())) {
				found = true;
				break;
			}
			index += destinationPositionAfter ? -1 : 1;
		}
		if (!found) {
			throw new IllegalArgumentException("unknown alias " + destinationOrderAlias);
		}
		return index;
	}
}
