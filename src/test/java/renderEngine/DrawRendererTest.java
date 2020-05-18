package renderEngine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import modelsLibrary.SimpleGeom;

/**
 *  * don't know when to use beforeAll...
 *  * Parameterized could have been interesting if it would have taken Objects, not
 * only primitives
 *  * @InjectMocks is not needed anymore and should be a hint for bad practice if used
 *  * used Whitebox.setInternalState (PowerMock) to initialize internal state of SimpleGeom mocks in order to avoid
 *  npe on unset private List<RenderingParameters> renderingParameters, (as constructor is not used in mock abstract class)
 *   => field is protected but packages are not the same.
 *   Whitebox doesn't have type check
 * @author chezmoi
 *
 */
class DrawRendererTest {

	List<SimpleGeom> geoms;
	DrawRenderer renderer;
	List<RenderingParameters> renderingParams;

	SimpleGeom firstGeomMock;
	SimpleGeom secondGeomMock;
	SimpleGeom thirdGeomMock;

	@BeforeEach
	void setUpBeforeEach() throws Exception {
		MockitoAnnotations.initMocks(this);
		geoms = new ArrayList<>();
		firstGeomMock = Mockito.mock(SimpleGeom.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(firstGeomMock.getVaoId()).thenReturn(1);
		secondGeomMock = Mockito.mock(SimpleGeom.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(secondGeomMock.getVaoId()).thenReturn(2);
		thirdGeomMock = Mockito.mock(SimpleGeom.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(thirdGeomMock.getVaoId()).thenReturn(3);
		Whitebox.setInternalState(firstGeomMock, "renderingParameters", new RenderingParameters(firstGeomMock, ""));
		Whitebox.setInternalState(secondGeomMock, "renderingParameters", new RenderingParameters(secondGeomMock, ""));
		Whitebox.setInternalState(thirdGeomMock, "renderingParameters", new RenderingParameters(thirdGeomMock, ""));
		geoms.add(firstGeomMock);
		geoms.add(secondGeomMock);
		geoms.add(thirdGeomMock);
		renderer = Mockito.mock(DrawRenderer.class, Mockito.CALLS_REAL_METHODS);
		renderer.geoms = geoms;
	}

	@Nested
	@DisplayName("Test ordering that must not affect order")
	class NotAffectingOrder {

		@Nested
		@DisplayName("Nor affect cardinality")
		class NorAffectingCardinality {

			@Nested
			@TestInstance(Lifecycle.PER_CLASS)
			@DisplayName("Geoms without Params")
			class NoParams {

				@BeforeEach
				void setUpBeforeEach() throws Exception {
					renderingParams = renderer.getOrderedRenderingParameters();
				}

				/**
				 * A;B;C result list not empty
				 */
				@Test
				@DisplayName("Must not be empty")
				void testOrderingGeomsWithoutParamsNotEmpty() {
					assertFalse(renderingParams.isEmpty(), "output renderingParams is empty");
				}

				/**
				 * A;B;C result list.size() = 3
				 */
				@Test
				@DisplayName("Must have same elements")
				void testOrderingGeomsWithoutParamsSize() {
					assertEquals(3, renderingParams.size(), "output renderingParams miss geoms");
				}

				/**
				 * A;B;C result A first, B second
				 */
				@Test
				@DisplayName("Must have first geom first")
				void testOrderingGeomsWithoutParamsFirstGeomFirst() {
					assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId(),
							"expected first geom to be first in output");
					assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId(),
							"expected second geom to be second in output");
				}
			}

			@Nested
			@DisplayName("Geoms with Params")
			class Params {
				RenderingParameters firstParam;
				RenderingParameters secondParam;
				RenderingParameters thirdParam;
				
				@BeforeEach
				void setUpBeforeEach() throws Exception {
					firstParam = firstGeomMock.getRenderingParameters();
					secondParam = secondGeomMock.getRenderingParameters();
					thirdParam = thirdGeomMock.getRenderingParameters();

					Mockito.when(firstGeomMock.getRenderingParameters()).thenReturn(firstParam);

					Mockito.when(secondGeomMock.getRenderingParameters()).thenReturn(secondParam);

					Mockito.when(thirdGeomMock.getRenderingParameters()).thenReturn(thirdParam);
				}

				@Nested
				@TestInstance(Lifecycle.PER_CLASS)
				@DisplayName("Params without alias reference (natural order)")
				class NaturalOrder {

					@BeforeEach
					void setUpBeforeEach() throws Exception {
						renderingParams = renderer.getOrderedRenderingParameters();
					}

					/**
					 * A;B;C result list not empty
					 */
					@Test
					@DisplayName("Must not be empty")
					void testOrderingGeomsWithoutParamsNotEmpty() {
						assertFalse(renderingParams.isEmpty(), "output renderingParams is empty");
					}

					/**
					 * A;B;C result list.size() = 3
					 */
					@Test
					@DisplayName("Must have same elements")
					void testOrderingGeomsWithoutParamsSize() {
						assertEquals(3, renderingParams.size(), "output renderingParams miss geoms");
					}

					/**
					 * A;B;C result A first, B second
					 */
					@Test
					@DisplayName("Must have first geom first")
					void testOrderingGeomsWithoutParamsFirstGeomFirst() {
						assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId(),
								"expected first geom to be first in output");
						assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId(),
								"expected second geom to be second in output");
					}
				}

				@Nested
				@DisplayName("Params with alias reference")
				class ParamsWithChanges {
					String firstAlias = "firstAlias";
					String secondAlias = "secondAlias";
					String thirdAlias = "thirdAlias";

					@Nested
					@DisplayName("All params have aliases, and each Geom have one Param")
					class AllParamsHaveAliasOneParamPerGeom {

						@BeforeEach
						void setUpBeforeEach() throws Exception {
							firstParam.setAlias(firstAlias);
							secondParam.setAlias(secondAlias);
							thirdParam.setAlias(thirdAlias);
						}

						/**
						 * A; B->?; C result: IllegalArgumentException
						 */
						@Test
						@DisplayName("Unknown alias should throws Exception")
						void testUnknownAliasException() {
							secondParam.renderBefore("unknown");
							assertThrows(IllegalArgumentException.class, () -> {
								renderer.getOrderedRenderingParameters();
							});
						}

						/**
						 * A; B->B(before); C result: A;B;C
						 */
						@Test
						@DisplayName("Refers to itself as Before")
						void testOneReferenceItselfBefore() {
							secondParam.renderBefore(secondAlias);
							renderingParams = renderer.getOrderedRenderingParameters();
							assertEquals(3, renderingParams.size());
							assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
							assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
						}

						/**
						 * A; B->B(after); C result: A;B;C
						 */
						@Test
						@DisplayName("Refers to itself as After")
						void testOneReferenceItselfAfter() {
							secondParam.renderAfter(secondAlias);
							renderingParams = renderer.getOrderedRenderingParameters();
							assertEquals(3, renderingParams.size());
							assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
							assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
						}

						/**
						 * A ; B -> C (before); C result: A;B;C
						 */
						@Test
						@DisplayName("Refers to Next as Before")
						void testReferenceNextAsBefore() {
							secondParam.renderBefore(thirdAlias);
							renderingParams = renderer.getOrderedRenderingParameters();
							assertEquals(3, renderingParams.size());
							assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
							assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
						}

						/**
						 * A ; B -> A (after); C result: A;B;C
						 */
						@Test
						@DisplayName("Refers to Previous as After")
						void testReferencePreviousAsAfter() {
							secondParam.renderAfter(firstAlias);
							renderingParams = renderer.getOrderedRenderingParameters();
							assertEquals(3, renderingParams.size());
							assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
							assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
						}
					}

					@Nested
					@DisplayName("1 Param over 3 have no Alias")
					@Deprecated
					class OneParamHaveNoAlias {

						/**
						 * x = no alias A:x; B->B(before); C:x result: A;B;C
						 */
						@Test
						@DisplayName("Refers to itself as Before")
						void testOneReferenceItselfBefore() {
							secondParam.setAlias(secondAlias);
							secondParam.renderBefore(secondAlias);
							renderingParams = renderer.getOrderedRenderingParameters();
							assertEquals(3, renderingParams.size());
							assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
							assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
						}

						/**
						 * x = no alias A:x; B->B(after); C:x result: A;B;C
						 */
						@Test
						@DisplayName("Refers to itself as After")
						void testOneReferenceItselfAfter() {
							secondParam.setAlias(secondAlias);
							secondParam.renderAfter(secondAlias);
							renderingParams = renderer.getOrderedRenderingParameters();
							assertEquals(3, renderingParams.size());
							assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
							assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
						}

						/**
						 * x = no alias A:x ; B -> C (before); C result: A;B;C
						 */
						@Test
						@DisplayName("Refers to Next as Before")
						void testReferenceNextAsBefore() {
							secondParam.setAlias(secondAlias);
							thirdParam.setAlias(thirdAlias);
							secondParam.renderBefore(thirdAlias);
							renderingParams = renderer.getOrderedRenderingParameters();
							assertEquals(3, renderingParams.size());
							assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
							assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
						}

						/**
						 * x = no alias A ; B -> A (after); C:x result: A;B;C
						 */
						@Test
						@DisplayName("Refers to Previous as After")
						void testReferencePreviousAsAfter() {
							firstParam.setAlias(firstAlias);
							secondParam.setAlias(secondAlias);
							secondParam.renderAfter(firstAlias);
							renderingParams = renderer.getOrderedRenderingParameters();
							assertEquals(3, renderingParams.size());
							assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
							assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
						}
					}
				}
			}
		}
	}
	
	@Nested
	@DisplayName("Test ordering that must affect order")
	class AffectingOrder {
		RenderingParameters firstParam;
		RenderingParameters secondParam;
		RenderingParameters thirdParam;
		
		String firstAlias = "firstAlias";
		String secondAlias = "secondAlias";
		String thirdAlias = "thirdAlias";
		
		@BeforeEach
		void setUpBeforeEach() throws Exception {
			firstParam = firstGeomMock.getRenderingParameters();
			firstParam.setAlias(firstAlias);
			secondParam = secondGeomMock.getRenderingParameters();
			secondParam.setAlias(secondAlias);
			thirdParam = thirdGeomMock.getRenderingParameters();
			thirdParam.setAlias(thirdAlias);

			Mockito.when(firstGeomMock.getRenderingParameters()).thenReturn(firstParam);
			Mockito.when(secondGeomMock.getRenderingParameters()).thenReturn(secondParam);
			Mockito.when(thirdGeomMock.getRenderingParameters()).thenReturn(thirdParam);
		}
		
		@Nested
		@DisplayName("But must not affect cardinality")
		class NotAffectingCardinality {
			/**
			 * A; B->A(before); C result: B;A;C
			 */
			@Test
			@DisplayName("Moving middle Params to first")
			void testReferencePreviousAsBefore() {
				secondParam.renderBefore(firstAlias);
				renderingParams = renderer.getOrderedRenderingParameters();
				assertEquals(secondGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
				assertEquals(firstGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
				assertEquals(thirdGeomMock.getVaoId(), renderingParams.get(2).getGeom().getVaoId());
			}
			
			/**
			 * A; B->C(after); C result: A;C;B
			 */
			@Test
			@DisplayName("Moving middle Params to last")
			void testReferenceNextAsAfter() {
				secondParam.renderAfter(thirdAlias);
				renderingParams = renderer.getOrderedRenderingParameters();
				assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
				assertEquals(thirdGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
				assertEquals(secondGeomMock.getVaoId(), renderingParams.get(2).getGeom().getVaoId());
			}
			
			/**
			 * A->C(before); B; C result: B;A;C
			 */
			@Test
			@DisplayName("Moving first Params to middle")
			void testInBetweenReferenceAsBefore() {
				firstParam.renderBefore(thirdAlias);
				renderingParams = renderer.getOrderedRenderingParameters();
				assertEquals(secondGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
				assertEquals(firstGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
				assertEquals(thirdGeomMock.getVaoId(), renderingParams.get(2).getGeom().getVaoId());
			}
			
			/**
			 * A; B; C->A(after) result: A;C;B
			 */
			@Test
			@DisplayName("Moving last Params to middle")
			void testInBetweenReferenceAsAfter() {
				thirdParam.renderAfter(firstAlias);
				renderingParams = renderer.getOrderedRenderingParameters();
				assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
				assertEquals(thirdGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
				assertEquals(secondGeomMock.getVaoId(), renderingParams.get(2).getGeom().getVaoId());
			}
		}
	}
}
