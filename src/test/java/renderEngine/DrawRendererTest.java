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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import entities.Entity;
import models.RenderableGeom;
import models.data.SimpleGeom;
import models.data.VAOGeom;
import shaderManager.IShader;

/**
 * 
 * @author chezmoi
 *
 */
class DrawRendererTest {

	List<RenderableGeom> geoms;
	DrawRendererCommon renderer;

	@Mock
	private Loader loader;

	SimpleGeom firstGeomMock;
	SimpleGeom secondGeomMock;
	SimpleGeom thirdGeomMock;
	Logger spyLogger;

	@BeforeEach
	void setUpBeforeEach() throws Exception {
		MockitoAnnotations.initMocks(this);
		geoms = new ArrayList<>();
		firstGeomMock = Mockito.mock(SimpleGeom.class, Mockito.CALLS_REAL_METHODS);
		secondGeomMock = Mockito.mock(SimpleGeom.class, Mockito.CALLS_REAL_METHODS);
		thirdGeomMock = Mockito.mock(SimpleGeom.class, Mockito.CALLS_REAL_METHODS);

		VAOGeom firstRawGeomMock = Mockito.mock(VAOGeom.class, Mockito.CALLS_REAL_METHODS);
		Mockito.doReturn(1).when(firstRawGeomMock).getVaoId();
		Whitebox.setInternalState(firstGeomMock, "vaoGeom", firstRawGeomMock);
		VAOGeom secondRawGeomMock = Mockito.mock(VAOGeom.class, Mockito.CALLS_REAL_METHODS);
		Mockito.doReturn(2).when(secondRawGeomMock).getVaoId();
		Whitebox.setInternalState(secondGeomMock, "vaoGeom", secondRawGeomMock);
		VAOGeom thirdRawGeomMock = Mockito.mock(VAOGeom.class, Mockito.CALLS_REAL_METHODS);
		Mockito.doReturn(3).when(thirdRawGeomMock).getVaoId();
		Whitebox.setInternalState(thirdGeomMock, "vaoGeom", thirdRawGeomMock);

		Whitebox.setInternalState(firstGeomMock, "renderingParameters",
				RenderingParameters.create(Mockito.mock(IShader.class), "", Mockito.mock(Entity.class)));
		Whitebox.setInternalState(secondGeomMock, "renderingParameters",
				RenderingParameters.create(Mockito.mock(IShader.class), "", Mockito.mock(Entity.class)));
		Whitebox.setInternalState(thirdGeomMock, "renderingParameters",
				RenderingParameters.create(Mockito.mock(IShader.class), "", Mockito.mock(Entity.class)));

		geoms.add(firstGeomMock);
		geoms.add(secondGeomMock);
		geoms.add(thirdGeomMock);
		// need to mock as it is an abstract class
		renderer = Mockito.mock(DrawRendererCommon.class, Mockito.CALLS_REAL_METHODS);
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
				geoms = renderer.getOrderedRenderingParameters();
			}

			/**
			 * A;B;C result list not empty
			 */
			@Test
			@DisplayName("Must not be empty")
			void testOrderingGeomsWithoutParamsNotEmpty() {
				assertFalse(geoms.isEmpty(), "output geoms is empty");
			}

			/**
			 * A;B;C result list.size() = 3
			 */
			@Test
			@DisplayName("Must have same elements")
			void testOrderingGeomsWithoutParamsSize() {
				assertEquals(3, geoms.size(), "output geoms miss geoms");
			}

			/**
			 * A;B;C result A first, B second
			 */
			@Test
			@DisplayName("Must have first geom first")
			void testOrderingGeomsWithoutParamsFirstGeomFirst() {
				assertEquals(firstGeomMock.getVAOGeom().getVaoId(), geoms.get(0).getVAOGeom().getVaoId(),
						"expected first geom to be first in output");
				assertEquals(secondGeomMock.getVAOGeom().getVaoId(), geoms.get(1).getVAOGeom().getVaoId(),
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
					geoms = renderer.getOrderedRenderingParameters();
				}

				/**
				 * A;B;C result list not empty
				 */
				@Test
				@DisplayName("Must not be empty")
				void testOrderingGeomsWithoutParamsNotEmpty() {
					assertFalse(geoms.isEmpty(), "output geoms is empty");
				}

				/**
				 * A;B;C result list.size() = 3
				 */
				@Test
				@DisplayName("Must have same elements")
				void testOrderingGeomsWithoutParamsSize() {
					assertEquals(3, geoms.size(), "output geoms miss geoms");
				}

				/**
				 * A;B;C result A first, B second
				 */
				@Test
				@DisplayName("Must have first geom first")
				void testOrderingGeomsWithoutParamsFirstGeomFirst() {
					assertEquals(firstGeomMock.getVAOGeom().getVaoId(), geoms.get(0).getVAOGeom().getVaoId(),
							"expected first geom to be first in output");
					assertEquals(secondGeomMock.getVAOGeom().getVaoId(), geoms.get(1).getVAOGeom().getVaoId(),
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
					 * TODO check logger action A; B->?; C result: A;B;C
					 */
					@Test
					@DisplayName("Unknown alias should throws Exception")
					void testUnknownAliasException() {
						secondParam.renderBefore("unknown");
						geoms = renderer.getOrderedRenderingParameters();
						assertEquals(firstGeomMock.getVAOGeom().getVaoId(), geoms.get(0).getVAOGeom().getVaoId());
						assertEquals(secondGeomMock.getVAOGeom().getVaoId(), geoms.get(1).getVAOGeom().getVaoId());
					}

					/**
					 * A; B->B(before); C result: A;B;C
					 */
					@Test
					@DisplayName("Refers to itself as Before")
					void testOneReferenceItselfBefore() {
						secondParam.renderBefore(secondAlias);
						geoms = renderer.getOrderedRenderingParameters();
						assertEquals(3, geoms.size());
						assertEquals(firstGeomMock.getVAOGeom().getVaoId(), geoms.get(0).getVAOGeom().getVaoId());
						assertEquals(secondGeomMock.getVAOGeom().getVaoId(), geoms.get(1).getVAOGeom().getVaoId());
					}

					/**
					 * A; B->B(after); C result: A;B;C
					 */
					@Test
					@DisplayName("Refers to itself as After")
					void testOneReferenceItselfAfter() {
						secondParam.renderAfter(secondAlias);
						geoms = renderer.getOrderedRenderingParameters();
						assertEquals(3, geoms.size());
						assertEquals(firstGeomMock.getVAOGeom().getVaoId(), geoms.get(0).getVAOGeom().getVaoId());
						assertEquals(secondGeomMock.getVAOGeom().getVaoId(), geoms.get(1).getVAOGeom().getVaoId());
					}

					/**
					 * A ; B -> C (before); C result: A;B;C
					 */
					@Test
					@DisplayName("Refers to Next as Before")
					void testReferenceNextAsBefore() {
						secondParam.renderBefore(thirdAlias);
						geoms = renderer.getOrderedRenderingParameters();
						assertEquals(3, geoms.size());
						assertTrue(geoms.indexOf(secondGeomMock) < geoms.indexOf(thirdGeomMock), geoms.toString());
					}

					/**
					 * A ; B -> A (after); C result: A;B;C
					 */
					@Test
					@DisplayName("Refers to Previous as After")
					void testReferencePreviousAsAfter() {
						secondParam.renderAfter(firstAlias);
						geoms = renderer.getOrderedRenderingParameters();
						assertEquals(3, geoms.size());
						assertEquals(firstGeomMock.getVAOGeom().getVaoId(), geoms.get(0).getVAOGeom().getVaoId());
						assertEquals(secondGeomMock.getVAOGeom().getVaoId(), geoms.get(1).getVAOGeom().getVaoId());
					}

					/**
					 * A->C(before); B; C result: [A,B,C]
					 */
					@Test
					@DisplayName("Moving first Params to middle : [A,B,C]")
					void testFirstBeforeLast() {
						firstParam.renderBefore(thirdAlias);
						geoms = renderer.getOrderedRenderingParameters();
						assertTrue(geoms.indexOf(firstGeomMock) < geoms.indexOf(thirdGeomMock), geoms.toString());
					}

					/**
					 * A; B; C->A(after) result: [A,B,C]
					 */
					@Test
					@DisplayName("Moving last Params to middle : [A,B,C]")
					void testLastAfterFirst() {
						thirdParam.renderAfter(firstAlias);
						geoms = renderer.getOrderedRenderingParameters();
						assertTrue(geoms.indexOf(firstGeomMock) < geoms.indexOf(thirdGeomMock), geoms.toString());
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
						geoms = renderer.getOrderedRenderingParameters();
						assertEquals(3, geoms.size());
						assertEquals(firstGeomMock.getVAOGeom().getVaoId(), geoms.get(0).getVAOGeom().getVaoId());
						assertEquals(secondGeomMock.getVAOGeom().getVaoId(), geoms.get(1).getVAOGeom().getVaoId());
					}

					/**
					 * x = no alias A:x; B->B(after); C:x result: A;B;C
					 */
					@Test
					@DisplayName("Refers to itself as After")
					void testOneReferenceItselfAfter() {
						secondParam.setAlias(secondAlias);
						secondParam.renderAfter(secondAlias);
						geoms = renderer.getOrderedRenderingParameters();
						assertEquals(3, geoms.size());
						assertEquals(firstGeomMock.getVAOGeom().getVaoId(), geoms.get(0).getVAOGeom().getVaoId());
						assertEquals(secondGeomMock.getVAOGeom().getVaoId(), geoms.get(1).getVAOGeom().getVaoId());
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
						geoms = renderer.getOrderedRenderingParameters();
						assertEquals(3, geoms.size());
						assertTrue(geoms.indexOf(secondGeomMock) < geoms.indexOf(thirdGeomMock), geoms.toString());
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
						geoms = renderer.getOrderedRenderingParameters();
						assertEquals(3, geoms.size());
						assertEquals(firstGeomMock.getVAOGeom().getVaoId(), geoms.get(0).getVAOGeom().getVaoId());
						assertEquals(secondGeomMock.getVAOGeom().getVaoId(), geoms.get(1).getVAOGeom().getVaoId());
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
			class SingleMovement {
				/**
				 * A; B->A(before); C result: [B,A,C]
				 */
				@Test
				@DisplayName("Moving middle Params to first : [B,A,C]")
				void testReferencePreviousAsBefore() {
					secondParam.renderBefore(firstAlias);
					geoms = renderer.getOrderedRenderingParameters();
					assertEquals(secondGeomMock.getVAOGeom().getVaoId(), geoms.get(0).getVAOGeom().getVaoId());
					assertEquals(firstGeomMock.getVAOGeom().getVaoId(), geoms.get(1).getVAOGeom().getVaoId());
					assertEquals(thirdGeomMock.getVAOGeom().getVaoId(), geoms.get(2).getVAOGeom().getVaoId());
				}

				/**
				 * A; B->C(after); C result: [A,C,B] or [C,B,A]
				 */
				@Test
				@DisplayName("Moving middle Params to last : [A,C,B] or [C,B,A]")
				void testReferenceNextAsAfter() {
					secondParam.renderAfter(thirdAlias);
					geoms = renderer.getOrderedRenderingParameters();
					assertTrue(geoms.indexOf(thirdGeomMock) < geoms.indexOf(secondGeomMock), geoms.toString());
				}

				/**
				 * A; B; C->B(before) result: [A,C,B]
				 */
				@Test
				@DisplayName("Moving last Params to middle : [A,C,B]")
				void testLastToMiddle() {
					thirdParam.renderBefore(secondAlias);
					geoms = renderer.getOrderedRenderingParameters();
					assertTrue(geoms.indexOf(thirdGeomMock) < geoms.indexOf(secondGeomMock), geoms.toString());
				}

				/**
				 * A->B(after); B; C result: [B,A,C]
				 */
				@Test
				@DisplayName("Moving first Params to middle : [B,A,C]")
				void testFirstToMiddle() {
					firstParam.renderAfter(secondAlias);
					geoms = renderer.getOrderedRenderingParameters();
					assertTrue(geoms.indexOf(secondGeomMock) < geoms.indexOf(firstGeomMock), geoms.toString());
				}

				/**
				 * A->last(); B; C result: [B,C,A]
				 */
				@Test
				@DisplayName("Moving first Params to last position : [B,C,A]")
				void testReferenceToLast() {
					firstParam.renderLast();
					geoms = renderer.getOrderedRenderingParameters();
					assertEquals(secondGeomMock.getVAOGeom().getVaoId(), geoms.get(0).getVAOGeom().getVaoId());
					assertEquals(thirdGeomMock.getVAOGeom().getVaoId(), geoms.get(1).getVAOGeom().getVaoId());
					assertEquals(firstGeomMock.getVAOGeom().getVaoId(), geoms.get(2).getVAOGeom().getVaoId());
				}

				/**
				 * A; B; C->first() result: [C,A,B]
				 */
				@Test
				@DisplayName("Moving last Params to first position : [C,A,B]")
				void testReferenceToFirst() {
					thirdParam.renderFirst();
					geoms = renderer.getOrderedRenderingParameters();
					assertEquals(thirdGeomMock.getVAOGeom().getVaoId(), geoms.get(0).getVAOGeom().getVaoId());
					assertEquals(firstGeomMock.getVAOGeom().getVaoId(), geoms.get(1).getVAOGeom().getVaoId());
					assertEquals(secondGeomMock.getVAOGeom().getVaoId(), geoms.get(2).getVAOGeom().getVaoId());
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

					geoms = renderer.getOrderedRenderingParameters();
					assertTrue(geoms.indexOf(secondGeomMock) < geoms.indexOf(thirdGeomMock), geoms.toString());
					assertTrue(geoms.indexOf(thirdGeomMock) < geoms.indexOf(firstGeomMock), geoms.toString());
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

						geoms = renderer.getOrderedRenderingParameters();
						assertTrue(geoms.indexOf(secondGeomMock) < geoms.indexOf(firstGeomMock), geoms.toString());
						assertTrue(geoms.indexOf(firstGeomMock) < geoms.indexOf(thirdGeomMock), geoms.toString());
					}

					/**
					 * A->B(after); B->C(after); C result: [C,B,A]
					 */
					@Test
					@DisplayName("CrossFlow : Moving A after B and B after C gives : [C,B,A]")
					void testReferenceChainingAftersCrossFlow() {
						firstParam.renderAfter(secondAlias);
						secondParam.renderAfter(thirdAlias);

						geoms = renderer.getOrderedRenderingParameters();
						assertTrue(geoms.indexOf(thirdGeomMock) < geoms.indexOf(secondGeomMock), geoms.toString());
						assertTrue(geoms.indexOf(secondGeomMock) < geoms.indexOf(firstGeomMock), geoms.toString());
					}

					/**
					 * Cycles breaks on first detection TODO try to test logger here apply last
					 * modification that forms a cycle A->C(after); B; C->A(after) result: [B,A,C]
					 * or [A,C,B]
					 */
					@Test
					@DisplayName("CycleFlow : Moving A after C and C after A gives : [B,A,C] or [A,C,B]")
					void testReferenceChainingAftersCycleFlow() {
						firstParam.renderAfter(thirdAlias);
						thirdParam.renderAfter(firstAlias);
						geoms = renderer.getOrderedRenderingParameters();
						assertTrue(geoms.indexOf(firstGeomMock) < geoms.indexOf(thirdGeomMock), geoms.toString());
					}

					/**
					 * Cycles breaks on first detection TODO try to test logger here apply last
					 * modification that forms a cycle A->B(after); B->C(after); C->A(after) result:
					 * [A,C,B]
					 */
					@Test
					@DisplayName("Complex CycleFlow : Moving A after B, B after C, C after A gives : [A,C,B]")
					void testReferenceChainingAftersComplexCycleFlow() {
						firstParam.renderAfter(secondAlias);
						secondParam.renderAfter(thirdAlias);
						thirdParam.renderAfter(firstAlias);
						geoms = renderer.getOrderedRenderingParameters();
						assertTrue(geoms.indexOf(firstGeomMock) < geoms.indexOf(thirdGeomMock), geoms.toString());
						assertTrue(geoms.indexOf(thirdGeomMock) < geoms.indexOf(secondGeomMock), geoms.toString());
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

						geoms = renderer.getOrderedRenderingParameters();
						assertTrue(geoms.indexOf(secondGeomMock) < geoms.indexOf(firstGeomMock), geoms.toString());
						assertTrue(geoms.indexOf(firstGeomMock) < geoms.indexOf(thirdGeomMock), geoms.toString());
					}

					/**
					 * A->C(before); B; C->B(before) result: [A,C,B]
					 */
					@Test
					@DisplayName("CrossFlow : Moving A before C and C before B gives : [A,C,B]")
					void testReferenceChainingBeforesCrossFlow() {
						firstParam.renderBefore(thirdAlias);
						thirdParam.renderBefore(secondAlias);

						geoms = renderer.getOrderedRenderingParameters();
						assertTrue(geoms.indexOf(firstGeomMock) < geoms.indexOf(thirdGeomMock), geoms.toString());
						assertTrue(geoms.indexOf(thirdGeomMock) < geoms.indexOf(secondGeomMock), geoms.toString());
					}

					/**
					 * Hard to detect because A is effectively before C. apply last modification
					 * that forms a cycle A->C(before); B; C->A(before) result: [C,A,B]
					 */
					@Test
					@DisplayName("CycleFlow : Moving A before C and C before A gives : [C,A,B]")
					void testReferenceChainingBeforesCycleFlow() {
						firstParam.renderBefore(thirdAlias);
						thirdParam.renderBefore(firstAlias);
						geoms = renderer.getOrderedRenderingParameters();
						assertTrue(geoms.indexOf(thirdGeomMock) < geoms.indexOf(firstGeomMock), geoms.toString());
					}

					/**
					 * Hard to detect because A is effectively before C. TODO try to test logger
					 * here apply last modification that forms a cycle A->C(before);B->A(before);
					 * C->B(before) result: [C,B,A]
					 */
					@Test
					@DisplayName("Complex CycleFlow : Moving A before C, C before B, B before A  gives : [C,B,A]")
					void testReferenceChainingBeforesComplexCycleFlow() {
						firstParam.renderBefore(thirdAlias);
						secondParam.renderBefore(firstAlias);
						thirdParam.renderBefore(secondAlias);
						geoms = renderer.getOrderedRenderingParameters();
						assertTrue(geoms.indexOf(thirdGeomMock) < geoms.indexOf(secondGeomMock), geoms.toString());
						assertTrue(geoms.indexOf(secondGeomMock) < geoms.indexOf(firstGeomMock), geoms.toString());
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
					geoms = renderer.getOrderedRenderingParameters();
					assertTrue(geoms.indexOf(thirdGeomMock) < geoms.indexOf(secondGeomMock), geoms.toString());
					assertTrue(geoms.indexOf(thirdGeomMock) < geoms.indexOf(firstGeomMock), geoms.toString());
				}

				/**
				 * A; B->first(); C->first() result: [C,B,A] or [B,C,A]
				 */
				@Test
				@DisplayName("Moving 2 Params to first position : [C,A,B] or [B,C,A]")
				void testChainingFirst() {
					thirdParam.renderFirst();
					secondParam.renderFirst();
					geoms = renderer.getOrderedRenderingParameters();
					assertTrue(geoms.indexOf(thirdGeomMock) < geoms.indexOf(firstGeomMock), geoms.toString());
					assertTrue(geoms.indexOf(secondGeomMock) < geoms.indexOf(firstGeomMock), geoms.toString());
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
				secondGeomMockBis = Mockito.mock(SimpleGeom.class, Mockito.CALLS_REAL_METHODS);
				thirdGeomMockBis = Mockito.mock(SimpleGeom.class, Mockito.CALLS_REAL_METHODS);

				VAOGeom firstRawGeomMockBis = Mockito.mock(VAOGeom.class, Mockito.CALLS_REAL_METHODS);
				Mockito.doReturn(4).when(firstRawGeomMockBis).getVaoId();
				Whitebox.setInternalState(firstGeomMockBis, "vaoGeom", firstRawGeomMockBis);
				VAOGeom secondRawGeomMockBis = Mockito.mock(VAOGeom.class, Mockito.CALLS_REAL_METHODS);
				Mockito.doReturn(5).when(secondRawGeomMockBis).getVaoId();
				Whitebox.setInternalState(secondGeomMockBis, "vaoGeom", secondRawGeomMockBis);
				VAOGeom thirdRawGeomMockBis = Mockito.mock(VAOGeom.class, Mockito.CALLS_REAL_METHODS);
				Mockito.doReturn(6).when(thirdRawGeomMockBis).getVaoId();
				Whitebox.setInternalState(thirdGeomMockBis, "vaoGeom", thirdRawGeomMockBis);

				Whitebox.setInternalState(firstGeomMockBis, "renderingParameters", RenderingParameters
						.create(Mockito.mock(IShader.class), firstAlias, Mockito.mock(Entity.class)));
				Whitebox.setInternalState(secondGeomMockBis, "renderingParameters", RenderingParameters
						.create(Mockito.mock(IShader.class), secondAlias, Mockito.mock(Entity.class)));
				Whitebox.setInternalState(thirdGeomMockBis, "renderingParameters", RenderingParameters
						.create(Mockito.mock(IShader.class), thirdAlias, Mockito.mock(Entity.class)));

				geoms.add(firstGeomMockBis);
				geoms.add(secondGeomMockBis);
				geoms.add(thirdGeomMockBis);
			}

			@Nested
			@DisplayName("Single movement")
			class SingleMovement {
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

					geoms = renderer.getOrderedRenderingParameters();
					int maxSecondIndex = Math.max(geoms.indexOf(secondGeomMockBis), geoms.indexOf(secondGeomMock));
					int minFirstIndex = Math.min(geoms.indexOf(firstGeomMockBis), geoms.indexOf(firstGeomMock));
					assertTrue(maxSecondIndex < minFirstIndex, geoms.toString());
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

					geoms = renderer.getOrderedRenderingParameters();
					int minSecondIndex = Math.max(geoms.indexOf(secondGeomMockBis), geoms.indexOf(secondGeomMock));
					int maxThirdIndex = Math.min(geoms.indexOf(thirdGeomMockBis), geoms.indexOf(thirdGeomMock));
					assertTrue(maxThirdIndex < minSecondIndex, geoms.toString());
				}

			}

			@Nested
			@DisplayName("Chaining movement")
			class ChainingMovement {

				@Nested
				class ChainingAfters {
					/**
					 * A->B(after); B; C->A(after); A->B(after); B; C->A(after) result:
					 * [B,B,A,A,C,C]
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

						geoms = renderer.getOrderedRenderingParameters();
						int maxSecondIndex = Math.max(geoms.indexOf(secondGeomMockBis), geoms.indexOf(secondGeomMock));
						int minFirstIndex = Math.min(geoms.indexOf(firstGeomMockBis), geoms.indexOf(firstGeomMock));

						int maxFirstIndex = Math.max(geoms.indexOf(firstGeomMockBis), geoms.indexOf(firstGeomMock));
						int minThirdIndex = Math.min(geoms.indexOf(thirdGeomMockBis), geoms.indexOf(thirdGeomMock));
						assertTrue(maxSecondIndex < minFirstIndex, geoms.toString());
						assertTrue(maxFirstIndex < minThirdIndex, geoms.toString());
					}

					/**
					 * A->B(after); B->C(after); C; A->B(after); B->C(after); C result:
					 * [C,C,B,B,A,A]
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

						geoms = renderer.getOrderedRenderingParameters();
						int maxThirdIndex = Math.max(geoms.indexOf(thirdGeomMockBis), geoms.indexOf(thirdGeomMock));
						int minSecondIndex = Math.min(geoms.indexOf(secondGeomMockBis), geoms.indexOf(secondGeomMock));

						int maxSecondIndex = Math.max(geoms.indexOf(secondGeomMockBis), geoms.indexOf(secondGeomMock));
						int minFirstIndex = Math.min(geoms.indexOf(firstGeomMockBis), geoms.indexOf(firstGeomMock));
						assertTrue(maxThirdIndex < minSecondIndex, geoms.toString());
						assertTrue(maxSecondIndex < minFirstIndex, geoms.toString());
					}
				}

				@Nested
				class ChainingBefores {

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

						geoms = renderer.getOrderedRenderingParameters();
						int maxSecondIndex = Math.max(geoms.indexOf(secondGeomMockBis), geoms.indexOf(secondGeomMock));
						int minFirstIndex = Math.min(geoms.indexOf(firstGeomMockBis), geoms.indexOf(firstGeomMock));

						int maxFirstIndex = Math.max(geoms.indexOf(firstGeomMockBis), geoms.indexOf(firstGeomMock));
						int minThirdIndex = Math.min(geoms.indexOf(thirdGeomMockBis), geoms.indexOf(thirdGeomMock));

						assertTrue(maxSecondIndex < minFirstIndex, geoms.toString());
						assertTrue(maxFirstIndex < minThirdIndex, geoms.toString());
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

						geoms = renderer.getOrderedRenderingParameters();

						int maxfirstIndex = Math.max(geoms.indexOf(firstGeomMockBis), geoms.indexOf(firstGeomMock));
						int minThirdIndex = Math.min(geoms.indexOf(thirdGeomMockBis), geoms.indexOf(thirdGeomMock));

						int maxThirdIndex = Math.max(geoms.indexOf(thirdGeomMockBis), geoms.indexOf(thirdGeomMock));
						int minSecondIndex = Math.min(geoms.indexOf(secondGeomMockBis), geoms.indexOf(secondGeomMock));
						assertTrue(maxfirstIndex < minThirdIndex, geoms.toString());
						assertTrue(maxThirdIndex < minSecondIndex, geoms.toString());
					}
				}

				@Nested
				class SpecialCase {

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

						geoms = renderer.getOrderedRenderingParameters();
						int maxSecondIndex = Math.max(geoms.indexOf(secondGeomMockBis), geoms.indexOf(secondGeomMock));
						int minFirstIndex = Math.min(geoms.indexOf(firstGeomMockBis), geoms.indexOf(firstGeomMock));
						assertTrue(maxSecondIndex < minFirstIndex, geoms.toString());
					}
				}
			}
		}

	}

}