package renderEngine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import entities.Entity;
import modelsLibrary.SimpleGeom;

/**
 * * don't know when to use beforeAll... * Parameterized could have been
 * interesting if it would have taken Objects, not only primitives
 * * @InjectMocks is not needed anymore and should be a hint for bad practice if
 * used * used Whitebox.setInternalState (PowerMock) to initialize internal
 * state of SimpleGeom mocks in order to avoid npe on unset private
 * List<RenderingParameters> renderingParameters, (as constructor is not used in
 * mock abstract class) => field is protected but packages are not the same.
 * Whitebox doesn't have type check
 * 
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
	
	Logger spyLogger;

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
		Whitebox.setInternalState(firstGeomMock, "renderingParameters", new RenderingParameters(firstGeomMock, "",Mockito.mock(Entity.class)));
		Whitebox.setInternalState(secondGeomMock, "renderingParameters", new RenderingParameters(secondGeomMock, "",Mockito.mock(Entity.class)));
		Whitebox.setInternalState(thirdGeomMock, "renderingParameters", new RenderingParameters(thirdGeomMock, "",Mockito.mock(Entity.class)));
		geoms.add(firstGeomMock);
		geoms.add(secondGeomMock);
		geoms.add(thirdGeomMock);
		renderer = Mockito.mock(DrawRenderer.class, Mockito.CALLS_REAL_METHODS);
		renderer.geoms = geoms;
		
		Logger logger = Logger.getLogger("DrawRendererTests");
		this.spyLogger = Mockito.spy(logger);
		renderer.logger = spyLogger;
	}

	@Nested
	@DisplayName("Test ordering that must not affect order")
	class NotAffectingOrder {

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
					 * TODO check logger action
					 * A; B->?; C result: A;B;C
					 */
					@Test
					@DisplayName("Unknown alias should throws Exception")
					void testUnknownAliasException() {
						secondParam.renderBefore("unknown");
						renderingParams = renderer.getOrderedRenderingParameters();
						assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
						assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
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
						assertTrue(renderingParams.indexOf(secondGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(thirdGeomMock.getRenderingParameters()), renderingParams.toString());
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
					
					/**
					 * A->C(before); B; C result: [A,B,C]
					 */
					@Test
					@DisplayName("Moving first Params to middle : [A,B,C]")
					void testFirstBeforeLast() {
						firstParam.renderBefore(thirdAlias);
						renderingParams = renderer.getOrderedRenderingParameters();
						assertTrue(renderingParams.indexOf(firstGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(thirdGeomMock.getRenderingParameters()), renderingParams.toString());
					}

					/**
					 * A; B; C->A(after) result: [A,B,C]
					 */
					@Test
					@DisplayName("Moving last Params to middle : [A,B,C]")
					void testLastAfterFirst() {
						thirdParam.renderAfter(firstAlias);
						renderingParams = renderer.getOrderedRenderingParameters();
						assertTrue(renderingParams.indexOf(firstGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(thirdGeomMock.getRenderingParameters()), renderingParams.toString());
					}
				}

				@Nested
				@DisplayName("1 Param over 3 have no Alias")
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
						assertTrue(renderingParams.indexOf(secondGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(thirdGeomMock.getRenderingParameters()), renderingParams.toString());
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
		@DisplayName("Simple case [A,B,C]")
		class SimpleCases {
			
			@Nested
			@DisplayName("Single movement")
			class SingleMovement{
				/**
				 * A; B->A(before); C result: [B,A,C]
				 */
				@Test
				@DisplayName("Moving middle Params to first : [B,A,C]")
				void testReferencePreviousAsBefore() {
					secondParam.renderBefore(firstAlias);
					renderingParams = renderer.getOrderedRenderingParameters();
					assertEquals(secondGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
					assertEquals(firstGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
					assertEquals(thirdGeomMock.getVaoId(), renderingParams.get(2).getGeom().getVaoId());
				}

				/**
				 * A; B->C(after); C result: [A,C,B] or [C,B,A]
				 */
				@Test
				@DisplayName("Moving middle Params to last : [A,C,B] or [C,B,A]")
				void testReferenceNextAsAfter() {
					secondParam.renderAfter(thirdAlias);
					renderingParams = renderer.getOrderedRenderingParameters();
					assertTrue(renderingParams.indexOf(thirdGeomMock.getRenderingParameters()) < renderingParams
							.indexOf(secondGeomMock.getRenderingParameters()), renderingParams.toString());
				}
				
				/**
				 * A; B; C->B(before) result: [A,C,B]
				 */
				@Test
				@DisplayName("Moving last Params to middle : [A,C,B]")
				void testLastToMiddle() {
					thirdParam.renderBefore(secondAlias);
					renderingParams = renderer.getOrderedRenderingParameters();
					assertTrue(renderingParams.indexOf(thirdGeomMock.getRenderingParameters()) < renderingParams
							.indexOf(secondGeomMock.getRenderingParameters()), renderingParams.toString());
				}
				
				/**
				 * A->B(after); B; C result: [B,A,C]
				 */
				@Test
				@DisplayName("Moving first Params to middle : [B,A,C]")
				void testFirstToMiddle() {
					firstParam.renderAfter(secondAlias);
					renderingParams = renderer.getOrderedRenderingParameters();
					assertTrue(renderingParams.indexOf(secondGeomMock.getRenderingParameters()) < renderingParams
							.indexOf(firstGeomMock.getRenderingParameters()), renderingParams.toString());
				}
				
				/**
				 * A->last(); B; C result: [B,C,A]
				 */
				@Test
				@DisplayName("Moving first Params to last position : [B,C,A]")
				void testReferenceToLast() {
					firstParam.renderLast();
					renderingParams = renderer.getOrderedRenderingParameters();
					assertEquals(secondGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
					assertEquals(thirdGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
					assertEquals(firstGeomMock.getVaoId(), renderingParams.get(2).getGeom().getVaoId());
				}
				
				/**
				 * A; B; C->first() result: [C,A,B]
				 */
				@Test
				@DisplayName("Moving last Params to first position : [C,A,B]")
				void testReferenceToFirst() {
					thirdParam.renderFirst();
					renderingParams = renderer.getOrderedRenderingParameters();
					assertEquals(thirdGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
					assertEquals(firstGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
					assertEquals(secondGeomMock.getVaoId(), renderingParams.get(2).getGeom().getVaoId());
				}
			}

			@Nested
			@DisplayName("Chaining movement")
			class ChainingMovement {
				
				/**
				 * A->C(after); B->C(before); C result: [B,C,A]
				 */
				@Test
				@DisplayName("Multi reference to last ref : [B,C,A]")
				void testMultiReferenceToLastRef() {
					firstParam.renderAfter(thirdAlias);
					secondParam.renderBefore(thirdAlias);

					renderingParams = renderer.getOrderedRenderingParameters();
					assertTrue(renderingParams.indexOf(secondGeomMock.getRenderingParameters()) < renderingParams
							.indexOf(thirdGeomMock.getRenderingParameters()), renderingParams.toString());
					assertTrue(renderingParams.indexOf(thirdGeomMock.getRenderingParameters()) < renderingParams
							.indexOf(firstGeomMock.getRenderingParameters()), renderingParams.toString());
				}
			

				@Nested
				class ChainingAfters {
					/**
					 * A->B(after); B; C->A(after) result: [B,A,C]
					 */
					@Test
					@DisplayName("ReadFlow : Moving A after B and C after A gives : [B,A,C]")
					void testReferenceChainingAftersReadingFlow() {
						firstParam.renderAfter(secondAlias);
						thirdParam.renderAfter(firstAlias);

						renderingParams = renderer.getOrderedRenderingParameters();
						assertTrue(renderingParams.indexOf(secondGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(firstGeomMock.getRenderingParameters()), renderingParams.toString());
						assertTrue(renderingParams.indexOf(firstGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(thirdGeomMock.getRenderingParameters()), renderingParams.toString());
					}

					/**
					 * A->B(after); B->C(after); C result: [C,B,A]
					 */
					@Test
					@DisplayName("CrossFlow : Moving A after B and B after C gives : [C,B,A]")
					void testReferenceChainingAftersCrossFlow() {
						firstParam.renderAfter(secondAlias);
						secondParam.renderAfter(thirdAlias);

						renderingParams = renderer.getOrderedRenderingParameters();
						assertTrue(renderingParams.indexOf(thirdGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(secondGeomMock.getRenderingParameters()), renderingParams.toString());
						assertTrue(renderingParams.indexOf(secondGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(firstGeomMock.getRenderingParameters()), renderingParams.toString());
					}

					/**
					 * Cycles breaks on first detection TODO try to test logger here 
					 * apply last modification that forms a cycle
					 * A->C(after); B; C->A(after) result: [B,A,C] or [A,C,B]
					 */
					@Test
					@DisplayName("CycleFlow : Moving A after C and C after A gives : [B,A,C] or [A,C,B]")
					void testReferenceChainingAftersCycleFlow() {
						firstParam.renderAfter(thirdAlias);
						thirdParam.renderAfter(firstAlias);
						renderingParams = renderer.getOrderedRenderingParameters();
						assertTrue(renderingParams.indexOf(firstGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(thirdGeomMock.getRenderingParameters()), renderingParams.toString());
					}
					
					/**
					 * Cycles breaks on first detection TODO try to test logger here
					 * apply last modification that forms a cycle 
					 * A->B(after); B->C(after); C->A(after) result: [A,C,B]
					 */
					@Test
					@DisplayName("Complex CycleFlow : Moving A after B, B after C, C after A gives : [A,C,B]")
					void testReferenceChainingAftersComplexCycleFlow() {
						firstParam.renderAfter(secondAlias);
						secondParam.renderAfter(thirdAlias);
						thirdParam.renderAfter(firstAlias);
						renderingParams = renderer.getOrderedRenderingParameters();
						assertTrue(renderingParams.indexOf(firstGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(thirdGeomMock.getRenderingParameters()), renderingParams.toString());
						assertTrue(renderingParams.indexOf(thirdGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(secondGeomMock.getRenderingParameters()), renderingParams.toString());
					}
				}

				@Nested
				class ChainingBefores {
					/**
					 * A->C(before); B->A(before); C result: [B,A,C]
					 */
					@Test
					@DisplayName("ReadFlow : Moving A before C and B before A gives : [B,A,C]")
					void testReferenceChainingBeforesReadingFlow() {
						firstParam.renderBefore(thirdAlias);
						secondParam.renderBefore(firstAlias);

						renderingParams = renderer.getOrderedRenderingParameters();
						assertTrue(renderingParams.indexOf(secondGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(firstGeomMock.getRenderingParameters()), renderingParams.toString());
						assertTrue(renderingParams.indexOf(firstGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(thirdGeomMock.getRenderingParameters()), renderingParams.toString());
					}

					/**
					 * A->C(before); B; C->B(before) result: [A,C,B]
					 */
					@Test
					@DisplayName("CrossFlow : Moving A before C and C before B gives : [A,C,B]")
					void testReferenceChainingBeforesCrossFlow() {
						firstParam.renderBefore(thirdAlias);
						thirdParam.renderBefore(secondAlias);

						renderingParams = renderer.getOrderedRenderingParameters();
						assertTrue(renderingParams.indexOf(firstGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(thirdGeomMock.getRenderingParameters()), renderingParams.toString());
						assertTrue(renderingParams.indexOf(thirdGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(secondGeomMock.getRenderingParameters()), renderingParams.toString());
					}

					/**
					 * Hard to detect because A is effectively before C.
					 * apply last modification that forms a cycle
					 * A->C(before); B; C->A(before) result: [C,A,B]
					 */
					@Test
					@DisplayName("CycleFlow : Moving A before C and C before A gives : [C,A,B]")
					void testReferenceChainingBeforesCycleFlow() {
						firstParam.renderBefore(thirdAlias);
						thirdParam.renderBefore(firstAlias);
						renderingParams = renderer.getOrderedRenderingParameters();
						assertTrue(renderingParams.indexOf(thirdGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(firstGeomMock.getRenderingParameters()), renderingParams.toString());
					}
					
					/**
					 * Hard to detect because A is effectively before C.
					 * TODO try to test logger here 
					 * apply last modification that forms a cycle
					 * A->C(before);B->A(before); C->B(before) result: [C,B,A]
					 */
					@Test
					@DisplayName("Complex CycleFlow : Moving A before C, C before B, B before A  gives : [C,B,A]")
					void testReferenceChainingBeforesComplexCycleFlow() {
						firstParam.renderBefore(thirdAlias);
						secondParam.renderBefore(firstAlias);
						thirdParam.renderBefore(secondAlias);
						renderingParams = renderer.getOrderedRenderingParameters();
						assertTrue(renderingParams.indexOf(thirdGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(secondGeomMock.getRenderingParameters()), renderingParams.toString());
						assertTrue(renderingParams.indexOf(secondGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(firstGeomMock.getRenderingParameters()), renderingParams.toString());
					}
				}
				
					/**
					 * A->last(); B->last(); C result: [C,B,A] or [C,A,B]
					 */
					@Test
					@DisplayName("Moving 2 Params to last position : [C,B,A] or [C,A,B]")
					void testChainingLast() {
						firstParam.renderLast();
						secondParam.renderLast();
						renderingParams = renderer.getOrderedRenderingParameters();
						assertTrue(renderingParams.indexOf(thirdGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(secondGeomMock.getRenderingParameters()), renderingParams.toString());
						assertTrue(renderingParams.indexOf(thirdGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(firstGeomMock.getRenderingParameters()), renderingParams.toString());
					}
					
					/**
					 * A; B->first(); C->first() result: [C,B,A] or [B,C,A]
					 */
					@Test
					@DisplayName("Moving 2 Params to first position : [C,A,B] or [B,C,A]")
					void testChainingFirst() {
						thirdParam.renderFirst();
						secondParam.renderFirst();
						renderingParams = renderer.getOrderedRenderingParameters();
						assertTrue(renderingParams.indexOf(thirdGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(firstGeomMock.getRenderingParameters()), renderingParams.toString());
						assertTrue(renderingParams.indexOf(secondGeomMock.getRenderingParameters()) < renderingParams
								.indexOf(firstGeomMock.getRenderingParameters()), renderingParams.toString());
					}
			}
		}

		@Nested
		@DisplayName("Complex case [A,B,C,A,B,C]")
		class ComplexCases {
			SimpleGeom firstGeomMockBis;
			SimpleGeom secondGeomMockBis;
			SimpleGeom thirdGeomMockBis;

			@BeforeEach
			void setUpBeforeEach() throws Exception {
				firstGeomMockBis = Mockito.mock(SimpleGeom.class, Mockito.CALLS_REAL_METHODS);
				Mockito.when(firstGeomMockBis.getVaoId()).thenReturn(4);
				secondGeomMockBis = Mockito.mock(SimpleGeom.class, Mockito.CALLS_REAL_METHODS);
				Mockito.when(secondGeomMockBis.getVaoId()).thenReturn(5);
				thirdGeomMockBis = Mockito.mock(SimpleGeom.class, Mockito.CALLS_REAL_METHODS);
				Mockito.when(thirdGeomMockBis.getVaoId()).thenReturn(6);
				Whitebox.setInternalState(firstGeomMockBis, "renderingParameters",
						new RenderingParameters(firstGeomMockBis, firstAlias,Mockito.mock(Entity.class)));
				Whitebox.setInternalState(secondGeomMockBis, "renderingParameters",
						new RenderingParameters(secondGeomMockBis, secondAlias,Mockito.mock(Entity.class)));
				Whitebox.setInternalState(thirdGeomMockBis, "renderingParameters",
						new RenderingParameters(thirdGeomMockBis, thirdAlias, Mockito.mock(Entity.class)));

				geoms.add(firstGeomMockBis);
				geoms.add(secondGeomMockBis);
				geoms.add(thirdGeomMockBis);
			}

			@Nested
			@DisplayName("Single movement")
			class SingleMovement{
				/**
				 * A->B(after); B; C; A->B(after); B; C result: [B,B,A,A,C,C] (or more lazy
				 * ordering [B,C,B,A,A,C])
				 */
				@Test
				@DisplayName("Moving first Params after middle gives :  [B,B,A,A,C,C] ([B,C,B,A,A,C] also ok)")
				void testReferenceAGroupAfterBGroup() {
					RenderingParameters firstParamsBis = firstGeomMockBis.getRenderingParameters();
					firstParam.renderAfter(secondAlias);
					firstParamsBis.renderAfter(secondAlias);

					renderingParams = renderer.getOrderedRenderingParameters();
					int maxSecondIndex = Math.max(renderingParams.indexOf(secondGeomMockBis.getRenderingParameters()),
							renderingParams.indexOf(secondGeomMock.getRenderingParameters()));
					int minFirstIndex = Math.min(renderingParams.indexOf(firstGeomMockBis.getRenderingParameters()),
							renderingParams.indexOf(firstGeomMock.getRenderingParameters()));
					assertTrue(maxSecondIndex < minFirstIndex, renderingParams.toString());
				}

				/**
				 * A; B; C->B(before);A; B; C->B(before) result: [A,A,C,C,B,B] (or more lazy
				 * ordering [A,C,C,B,A,B])
				 */
				@Test
				@DisplayName("Moving last Params before middle gives : [A,A,C,C,B,B] ([A,C,C,B,A,B] also ok)")
				void testReferenceCGroupBeforeBGroup() {
					RenderingParameters thirdParamsBis = thirdGeomMockBis.getRenderingParameters();
					thirdParam.renderBefore(secondAlias);
					thirdParamsBis.renderBefore(secondAlias);

					renderingParams = renderer.getOrderedRenderingParameters();
					int minSecondIndex = Math.max(renderingParams.indexOf(secondGeomMockBis.getRenderingParameters()),
							renderingParams.indexOf(secondGeomMock.getRenderingParameters()));
					int maxThirdIndex = Math.min(renderingParams.indexOf(thirdGeomMockBis.getRenderingParameters()),
							renderingParams.indexOf(thirdGeomMock.getRenderingParameters()));
					assertTrue(maxThirdIndex < minSecondIndex, renderingParams.toString());
				}
				
			}	

			@Nested
			@DisplayName("Chaining movement")
			class ChainingMovement {
				
				@Nested
				class ChainingAfters{
					/**
					 * A->B(after); B; C->A(after); A->B(after); B; C->A(after) result: [B,B,A,A,C,C]
					 */
					@Test
					@DisplayName("ReadFlow : Moving As after Bs and Cs after As gives : [B,B,A,A,C,C]")
					void testReferenceChainingAftersReadFlow() {
						RenderingParameters firstParamsBis = firstGeomMockBis.getRenderingParameters();
						firstParam.renderAfter(secondAlias);
						firstParamsBis.renderAfter(secondAlias);

						RenderingParameters thirdParamsBis = thirdGeomMockBis.getRenderingParameters();
						thirdParamsBis.renderAfter(firstAlias);
						thirdParam.renderAfter(firstAlias);

						renderingParams = renderer.getOrderedRenderingParameters();
						int maxSecondIndex = Math.max(renderingParams.indexOf(secondGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(secondGeomMock.getRenderingParameters()));
						int minFirstIndex = Math.min(renderingParams.indexOf(firstGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(firstGeomMock.getRenderingParameters()));

						int maxFirstIndex = Math.max(renderingParams.indexOf(firstGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(firstGeomMock.getRenderingParameters()));
						int minThirdIndex = Math.min(renderingParams.indexOf(thirdGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(thirdGeomMock.getRenderingParameters()));
						assertTrue(maxSecondIndex < minFirstIndex, renderingParams.toString());
						assertTrue(maxFirstIndex < minThirdIndex, renderingParams.toString());
					}
					
					/**
					 * A->B(after); B->C(after); C; A->B(after); B->C(after); C result: [C,C,B,B,A,A]
					 */
					@Test
					@DisplayName("CrossFlow : Moving As after Bs and Bs after Cs gives : [C,C,B,B,A,A]")
					void testReferenceChainingAftersCrossFlow() {
						RenderingParameters firstParamsBis = firstGeomMockBis.getRenderingParameters();
						firstParam.renderAfter(secondAlias);
						firstParamsBis.renderAfter(secondAlias);

						RenderingParameters secondParamsBis = secondGeomMockBis.getRenderingParameters();
						secondParamsBis.renderAfter(thirdAlias);
						secondParam.renderAfter(thirdAlias);

						renderingParams = renderer.getOrderedRenderingParameters();
						int maxThirdIndex = Math.max(renderingParams.indexOf(thirdGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(thirdGeomMock.getRenderingParameters()));
						int minSecondIndex = Math.min(renderingParams.indexOf(secondGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(secondGeomMock.getRenderingParameters()));

						int maxSecondIndex = Math.max(renderingParams.indexOf(secondGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(secondGeomMock.getRenderingParameters()));
						int minFirstIndex = Math.min(renderingParams.indexOf(firstGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(firstGeomMock.getRenderingParameters()));
						assertTrue(maxThirdIndex < minSecondIndex, renderingParams.toString());
						assertTrue(maxSecondIndex < minFirstIndex, renderingParams.toString());
					}
				}
				
				@Nested
				class ChainingBefores{
					
					/**
					 * A->C(before); B->A(before); C; A->C(before); B->A(before); C result:
					 * [B,B,A,A,C,C]
					 */
					@Test
					@DisplayName("ReadFlow : Moving As before Cs and Bs before As gives : [B,B,A,A,C,C]")
					void testReferenceChainingBeforesReadFlow() {
						RenderingParameters firstParamsBis = firstGeomMockBis.getRenderingParameters();
						firstParam.renderBefore(thirdAlias);
						firstParamsBis.renderBefore(thirdAlias);
						
						RenderingParameters secondParamsBis = secondGeomMockBis.getRenderingParameters();
						secondParam.renderBefore(firstAlias);
						secondParamsBis.renderBefore(firstAlias);

						renderingParams = renderer.getOrderedRenderingParameters();
						int maxSecondIndex = Math.max(renderingParams.indexOf(secondGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(secondGeomMock.getRenderingParameters()));
						int minFirstIndex = Math.min(renderingParams.indexOf(firstGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(firstGeomMock.getRenderingParameters()));

						int maxFirstIndex = Math.max(renderingParams.indexOf(firstGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(firstGeomMock.getRenderingParameters()));
						int minThirdIndex = Math.min(renderingParams.indexOf(thirdGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(thirdGeomMock.getRenderingParameters()));
						
						assertTrue(maxSecondIndex < minFirstIndex, renderingParams.toString());
						assertTrue(maxFirstIndex < minThirdIndex, renderingParams.toString());
					}
					
					/**
					 * A->C(before); B; C->B(before);A->C(before); B; C->B(before) result:
					 * [A,A,C,C,B,B]
					 */
					@Test
					@DisplayName("CrossFlow : Moving As before Cs and Cs before Bs  gives : [A,A,C,C,B,B]")
					void testReferenceChainingBeforesCrossFlow() {
						RenderingParameters firstParamsBis = firstGeomMockBis.getRenderingParameters();
						firstParam.renderBefore(thirdAlias);
						firstParamsBis.renderBefore(thirdAlias);
						
						RenderingParameters thirdParamsBis = thirdGeomMockBis.getRenderingParameters();
						thirdParam.renderBefore(secondAlias);
						thirdParamsBis.renderBefore(secondAlias);

						renderingParams = renderer.getOrderedRenderingParameters();
						
						int maxfirstIndex = Math.max(renderingParams.indexOf(firstGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(firstGeomMock.getRenderingParameters()));
						int minThirdIndex = Math.min(renderingParams.indexOf(thirdGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(thirdGeomMock.getRenderingParameters()));

						int maxThirdIndex = Math.max(renderingParams.indexOf(thirdGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(thirdGeomMock.getRenderingParameters()));
						int minSecondIndex = Math.min(renderingParams.indexOf(secondGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(secondGeomMock.getRenderingParameters()));
						assertTrue(maxfirstIndex < minThirdIndex, renderingParams.toString());
						assertTrue(maxThirdIndex < minSecondIndex, renderingParams.toString());
					}
				}
			
			
				@Nested
				class SpecialCase{
					
					/**
					 * A->B(after); B; C; A->C(after); B; C result: [B,B,A,A,C,C] (or more lazy
					 * ordering [B,C,B,A,A,C])
					 */
					@Test
					@DisplayName("Different params for same alias are override by first configuration: Moving first Params after middle gives :  [B,B,A,A,C,C] ([B,C,B,A,A,C] also ok)")
					void testTwinsReferenceResolution() {
						RenderingParameters firstParamsBis = firstGeomMockBis.getRenderingParameters();
						firstParam.renderAfter(secondAlias);
						firstParamsBis.renderAfter(thirdAlias);

						renderingParams = renderer.getOrderedRenderingParameters();
						int maxSecondIndex = Math.max(renderingParams.indexOf(secondGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(secondGeomMock.getRenderingParameters()));
						int minFirstIndex = Math.min(renderingParams.indexOf(firstGeomMockBis.getRenderingParameters()),
								renderingParams.indexOf(firstGeomMock.getRenderingParameters()));
						assertTrue(maxSecondIndex < minFirstIndex, renderingParams.toString());
					}
				}
			}
		}
	
}

	
}