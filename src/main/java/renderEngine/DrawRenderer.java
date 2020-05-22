package renderEngine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import modelsLibrary.ISimpleGeom;
import modelsLibrary.SimpleGeom;

/**
 * Draw primitive 3D objects directly by drawArrays. For heavy objects use
 * Renderer with glDrawElements. gl_drawarrays does no benefits of indices
 * optimization while binding vertices into vao.
 * 
 * @author chezmoi
 *
 */
public abstract class DrawRenderer implements IDrawRenderer {
	protected List<SimpleGeom> geoms;
	protected List<RenderingParameters> renderingParams;
	protected Logger logger;

	public DrawRenderer() {
		this.geoms = new ArrayList<>();
		this.renderingParams = new ArrayList<>();
		this.logger = Logger.getLogger("DrawRenderer");
	}

	@Override
	public void process(SimpleGeom geom) {
		this.geoms.add(geom);
	}

	@Override
	public void sendForRendering() {
		updateOverridingColors();
		renderingParams = getOrderedRenderingParameters();
		renderingParams = checkForEntitiesPresence();
	}

	private LinkedList<RenderingParameters> checkForEntitiesPresence() {
		LinkedList<RenderingParameters> noEmptyParameters = new LinkedList<>();
		for(RenderingParameters param : renderingParams) {
			if(!param.isNotUsingEntities() && param.getEntities().isEmpty()) {
				if(this.logger.isLoggable(Level.INFO)) {
					this.logger.info(param.getAlias() +" has no entities set, will not be rendered");
				}
			}
			else {
				noEmptyParameters.add(param);
			}
		}
		return noEmptyParameters;
	}

	/**
	 * Apply color override. If many RenderingParameters are set for a Geom, last
	 * override will be active.
	 */
	private void updateOverridingColors() {
		for (SimpleGeom simpleGeom : this.geoms) {
			RenderingParameters rParam = simpleGeom.getRenderingParameters();
			rParam.applyColorOverriding();

		}
	}

	@Override
	public void clearGeom() {
		this.geoms.clear();
	}

	/**
	 * Before we can render a VAO it needs to be made active, and we can do this by
	 * binding it. We also need to enable the relevant attributes of the VAO, which
	 * in this case is just attribute 0 where we stored the position data.
	 * 
	 * @param geom          Geom to render
	 * @param positionIndex GL vertex Attrib array position in VBO?
	 * @param colorIndex    GL vertex Attrib array position in VBO?
	 */
	protected void prepare(ISimpleGeom geom, int positionIndex, int colorIndex) {
		GL30.glBindVertexArray(geom.getVaoId());
		GL20.glEnableVertexAttribArray(positionIndex);
		GL20.glEnableVertexAttribArray(colorIndex);
	}

	protected void unbindGeom(int positionIndex, int colorIndex) {
		GL20.glDisableVertexAttribArray(colorIndex);
		GL20.glDisableVertexAttribArray(positionIndex);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	protected void genericDrawRender(RenderingParameters params) {
		int dataLength = 0;
		// GL11.glEnable(GL11.GL_POINT_SMOOTH);
		GL11.glLineWidth(2); // seems to have a max cap unlike PointSize. for GL_LINES
		GL11.glPointSize(5); // GL_POINTS
		renderByVertices(params);

		GL11.glPointSize(1);
		GL11.glLineWidth(1);

	}

	private void renderByVertices(RenderingParameters params) {
		// cf https://www.khronos.org/opengl/wiki/Primitive => internal gl logic, hidden
		// for DrawArrays usage;
		ISimpleGeom geom = params.getGeom();
		int verticesCount = geom.getPoints().length / geom.getDimension();
		// Add default lineLoop rendering.
		// GL11.drawArrays can draw points with GL_POINTS, not GL_POINT
		GL11.glDrawArrays(params.getRenderMode().orElse(GL11.GL_LINE_LOOP), 0, verticesCount);
	}

	protected List<RenderingParameters> getOrderedRenderingParameters() {
		LinkedList<RenderingParameters> rawParams = new LinkedList<>();
		int i = 0;
		for (SimpleGeom simpleGeom : this.geoms) {
			RenderingParameters param = simpleGeom.getRenderingParameters();
			if (param.getAlias().isEmpty()) {
				param.setAlias("" + i);
			}
			rawParams.add(param);
			i++;
		}
		LinkedHashSet<RenderingParameters> uniqueParams = rawParams.stream()
				.collect(Collectors.toCollection(LinkedHashSet::new));
		uniqueParams = removeMissConfiguratedReference(uniqueParams);
		LinkedList<RenderingParameters> paramsToMove = uniqueParams.stream().filter((renderingParam) -> {
			return !renderingParam.getDestinationOrderAlias().isEmpty()
					&& !renderingParam.getDestinationOrderAlias().equals(renderingParam.getAlias());
		}).collect(Collectors.toCollection(LinkedList::new));
		List<RenderingParameters> sortedUniqueParams = transformRelativePositionToIndex(uniqueParams,paramsToMove);
		System.out.println(sortedUniqueParams);
		return orderRawParams(rawParams, sortedUniqueParams);
	}

	private List<RenderingParameters> orderRawParams(LinkedList<RenderingParameters> rawParams,
			List<RenderingParameters> sortedUniqueParams) {
		LinkedList<RenderingParameters> finalList = new LinkedList<>();
		for (RenderingParameters param : sortedUniqueParams) {
			finalList.addAll(rawParams.stream().filter(rawParam -> rawParam.getAlias().equals(param.getAlias()))
					.collect(Collectors.toList()));
		}
		return finalList;
	}

	/**
	 * remove unknown reference destination. remove self reference destination.
	 * 
	 * @param uniqueParams
	 * @return
	 */
	private LinkedHashSet<RenderingParameters> removeMissConfiguratedReference(
			LinkedHashSet<RenderingParameters> uniqueParams) {
		LinkedHashSet<RenderingParameters> modifiedParams = new LinkedHashSet<>();
		modifiedParams.addAll(uniqueParams);
		for (RenderingParameters param : modifiedParams) {
			if (!param.getDestinationOrderAlias().isEmpty()) {
				boolean isPresent = checkReferencePresence(uniqueParams, param.getDestinationOrderAlias());
				if (!isPresent) {
					this.logger.log(Level.WARNING, "reference to " + param.getDestinationOrderAlias() + " is unknown. Will be removed");
					param.renderBefore("");
				} else {
					if (param.getAlias().equals(param.getDestinationOrderAlias())) {
						this.logger.log(Level.WARNING, "reference to itself " + param.getDestinationOrderAlias() + ". Will be removed");
						param.renderBefore("");
					}
				}
			}
		}
		return modifiedParams;
	}

	private boolean checkReferencePresence(LinkedHashSet<RenderingParameters> uniqueParams,
			String destinationOrderAlias) {
		for (RenderingParameters param : uniqueParams) {
			if (param.getAlias().equals(destinationOrderAlias)) {
				return true;
			}
		}
		return false;
	}
	
	private List<RenderingParameters> transformRelativePositionToIndex(LinkedHashSet<RenderingParameters> rawParams,
			LinkedList<RenderingParameters> paramsToMove) {
		LinkedList<RenderingParameters> sortedList = new LinkedList<>();
		sortedList.addAll(rawParams);
		for (RenderingParameters param : paramsToMove) {
			sortedList.remove(param);
			int index = getIndexDestination(sortedList, param.getDestinationOrderAlias(),
					param.isDestinationPositionAfter());
			sortedList.add(index, param);
		}
		return sortedList;
	}

	private int getIndexDestination(LinkedList<RenderingParameters> sortedParams, String destinationOrderAlias,
			boolean destinationPositionAfter) {
		Iterator<RenderingParameters> itParams = destinationPositionAfter ? sortedParams.descendingIterator()
				: sortedParams.iterator();
		int index = destinationPositionAfter ? sortedParams.size() : 0;
		boolean found = false;
		while (itParams.hasNext()) {

			RenderingParameters currentParam = itParams.next();
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
