package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
		//List<RenderingParameters> sortedUniqueParams = sortPositionsAliases(uniqueParams);
		LinkedList<RenderingParameters> paramsToMove = rawParams.stream().filter((renderingParam) -> {
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

	/**
	 * sort every aliases, then add each unsorted param in the end list ?
	 * 
	 * @param uniqueParams
	 * @return
	 */
	private List<RenderingParameters> sortPositionsAliases(LinkedHashSet<RenderingParameters> uniqueParams) {
		LinkedList<RenderingParameters> workingList = new LinkedList<>();
		workingList.addAll(uniqueParams);
		LinkedList<RenderingParameters> sortedList = new LinkedList<>();
		for (RenderingParameters param : uniqueParams) {
			int indexDestination = -1;
			workingList.remove(param);
			if (!param.getDestinationOrderAlias().isEmpty()) {
				indexDestination = getDestinationRef(workingList, param);
			}

			indexDestination = findIndexBasedOnDestinationUsages(workingList, param, indexDestination);

			if (indexDestination == -1 && !param.getDestinationOrderAlias().isEmpty()) {
				// if current param not used yet as destination, neither it as same destination
				// as another param.
				indexDestination = getDestinationBasedOnSameDestinationUsage(workingList, param);
			}

			if (indexDestination == -1 || indexDestination >= workingList.size()) {
				workingList.addLast(param);
			} else if (indexDestination < workingList.size()) {
				workingList.add(indexDestination, param);
			} else {
				workingList.addFirst(param);
			}

		}
		return workingList;
	}

	private int getDestinationBasedOnSameDestinationUsage(LinkedList<RenderingParameters> sortedList,
			RenderingParameters currentParam) {
		int sameDestinationIndex = -1;
		int currentIndex = 0;
		for (RenderingParameters param : sortedList) {
			// find if any other param have same destination as itself
			// avoid "before" to be after "after" and vice-versa.
			if (param.getDestinationOrderAlias().equals(currentParam.getDestinationOrderAlias())) {
				if (currentParam.isDestinationPositionAfter()) {
					// set as before; current param must be after this one.
					if (!param.isDestinationPositionAfter()) {
						sameDestinationIndex = Math.max(sameDestinationIndex, currentIndex);
					}
				} else {
					// set as after; current param must be before this one.
					if (param.isDestinationPositionAfter()) {
						sameDestinationIndex = Math.min(sameDestinationIndex, currentIndex);
					}
				}
			}
			currentIndex++;
		}
		return sameDestinationIndex;
	}

	/**
	 * returns destinationAlias param used if already known
	 * 
	 * @param sortedList
	 * @param currentParam
	 * @return destinationAlias param used if already known
	 */
	private int getDestinationRef(LinkedList<RenderingParameters> sortedList, RenderingParameters currentParam) {
		Optional<RenderingParameters> found = Optional.empty();
		for (RenderingParameters param : sortedList) {
			if (param.getAlias().equals(currentParam.getDestinationOrderAlias())) {
				found = Optional.ofNullable(param);
				break;
			}
		}
		int indexDestination = -1;
		if (found.isPresent()) {
			if (currentParam.isDestinationPositionAfter()) {
				indexDestination = sortedList.indexOf(found.get()) + 1;
			} else {
				indexDestination = sortedList.indexOf(found.get());
			}
		}
		return indexDestination;
	}

	/**
	 * returns destination index taking care of destinationAliasIndex and usages of
	 * current param as destination of others known params
	 * 
	 * @param sortedList
	 * @param currentParam
	 * @param destinationAliasIndex
	 * @return destination index taking care of destinationAliasIndex and usages of
	 *         current param as destination of others known params
	 */
	private int findIndexBasedOnDestinationUsages(LinkedList<RenderingParameters> sortedList,
			RenderingParameters currentParam, int destinationAliasIndex) {
		int minIndex = -1;
		int maxIndex = -1;
		int currentIndex = 0;
		for (RenderingParameters param : sortedList) {
			// find interval where current param belongs as destination usage
			if (param.getDestinationOrderAlias().equals(currentParam.getAlias())) {
				if (param.isDestinationPositionAfter()) {
					maxIndex = Math.max(maxIndex, currentIndex);
				} else {
					minIndex = Math.min(minIndex, currentIndex);
				}
			}
			currentIndex++;
		}

		if (minIndex > maxIndex) {
			this.logger.log(Level.WARNING, "impossible configuration : minIndex > maxIndex");
		}

		if (destinationAliasIndex != -1) {
			if (minIndex > -1 && destinationAliasIndex < minIndex) {
				this.logger.log(Level.WARNING, "Cyclic configuration detected with MIN in conflict with ["+ currentParam +"->"+ currentParam.getDestinationOrderAlias() +"], naturally broke it");
			}else if(maxIndex > -1 && destinationAliasIndex > maxIndex) {
			//	RenderingParameters conflictParam = sortedList.get(destinationAliasIndex);
				this.logger.log(Level.WARNING, "Cyclic configuration detected with MAX in conflict with ["+ currentParam +"->"+ currentParam.getDestinationOrderAlias() +"], naturally broke it");
			} else {
				if (currentParam.isDestinationPositionAfter()) {
					minIndex = destinationAliasIndex;
				} else {
					maxIndex = destinationAliasIndex;
				}
			}
		}

		int returnIndex = -1;
		returnIndex = maxIndex != -1 ? maxIndex : returnIndex;
		returnIndex = minIndex != -1 ? minIndex : returnIndex;

		return returnIndex;
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
