package renderEngine;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import modelsLibrary.ISimpleGeom;
import modelsLibrary.SimpleGeom3D;

/**
 * don't know when to use beforeAll...
 * @Nested can't be used to factorize beforeEach, because each Nested has its own context
 * @author chezmoi
 *
 */
class DrawRendererTest {

	List<ISimpleGeom> geoms;
	DrawRenderer renderer;
	List<RenderingParameters> renderingParams;
	ISimpleGeom firstGeomMock;
	ISimpleGeom secondGeomMock;
	ISimpleGeom thirdGeomMock;

	@BeforeEach
	void setUpBeforeClass() throws Exception {
		MockitoAnnotations.initMocks(this);
		geoms = new ArrayList<>();
		firstGeomMock = Mockito.mock(ISimpleGeom.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(firstGeomMock.getVaoId()).thenReturn(1);
		secondGeomMock = Mockito.mock(ISimpleGeom.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(secondGeomMock.getVaoId()).thenReturn(2);
		thirdGeomMock = Mockito.mock(ISimpleGeom.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(thirdGeomMock.getVaoId()).thenReturn(3);
		geoms.add(firstGeomMock);
		geoms.add(secondGeomMock);
		geoms.add(thirdGeomMock);
		renderer = Mockito.mock(DrawRenderer.class, Mockito.CALLS_REAL_METHODS);
		renderer.geoms = geoms;
	}

	@Nested
	@DisplayName("Test ordering that must not affect order")
	class NotAffectingOrder {

		/**
		 * Parameterized could have been interesting if it would have taken Objects, not
		 * only primitives
		 * 
		 * @author chezmoi
		 *
		 */
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
			List<RenderingParameters> firstParams;
			List<RenderingParameters> secondParams;
			List<RenderingParameters> thirdParams;
			
			RenderingParameters firstParam;
			RenderingParameters secondParam;
			RenderingParameters thirdParam;

			@Nested
			@DisplayName("Params without alias reference (natural order)")
			class NaturalOrder {
				
				@BeforeEach
				void setUpBeforeEach() throws Exception {
					firstParam = new RenderingParameters(firstGeomMock);
					firstParams = new ArrayList<>();
					firstParams.add(firstParam);
					Mockito.when(firstGeomMock.getRenderingParameters()).thenReturn(firstParams);
					
					secondParam = new RenderingParameters(secondGeomMock);
					secondParams = new ArrayList<>();
					secondParams.add(secondParam);
					Mockito.when(secondGeomMock.getRenderingParameters()).thenReturn(secondParams);
					
					thirdParam = new RenderingParameters(thirdGeomMock);
					thirdParams = new ArrayList<>();
					thirdParams.add(thirdParam);
					Mockito.when(thirdGeomMock.getRenderingParameters()).thenReturn(thirdParams);

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
				String thirdAlias ="thirdAlias";
				
				@Nested
				@DisplayName("All params have aliases, and each Geom have one Param")
				class AllParamsHaveAliasOneParamPerGeom {

					@BeforeEach
					void setUpBeforeEach() throws Exception {
						geoms = new ArrayList<>();
						geoms.add(firstGeomMock);
						geoms.add(secondGeomMock);
						geoms.add(thirdGeomMock);
						firstParam = new RenderingParameters(firstGeomMock);
						secondParam = new RenderingParameters(secondGeomMock);
						thirdParam = new RenderingParameters(thirdGeomMock);
						firstParam.setAlias(firstAlias);
						secondParam.setAlias(secondAlias);
						thirdParam.setAlias(thirdAlias);
						
						firstParams = new ArrayList<>();
						firstParams.add(firstParam);
						Mockito.when(firstGeomMock.getRenderingParameters()).thenReturn(firstParams);
						
						secondParams = new ArrayList<>();
						secondParams.add(secondParam);
						Mockito.when(secondGeomMock.getRenderingParameters()).thenReturn(secondParams);
						
						thirdParams = new ArrayList<>();
						thirdParams.add(thirdParam);
						Mockito.when(thirdGeomMock.getRenderingParameters()).thenReturn(thirdParams);
					}
					
					/**
					 * A; B->?; C result: IllegalArgumentException
					 */
					@Test
					@DisplayName("Unknown alias should throws Exception")
					void testUnknownAliasException() {
						secondParam.renderBefore("unknown");
						assertThrows(IllegalArgumentException.class, () -> { renderer.getOrderedRenderingParameters();});
					}

					/**
					 * A; B->B(before); C result: A;B;C
					 */
					@Test
					@DisplayName("Refers to itself as Before")
					void testOneReferenceItselfBefore() {
						secondParam.renderBefore(secondAlias);
						renderingParams = renderer.getOrderedRenderingParameters();
						assertEquals(3,renderingParams.size());
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
						assertEquals(3,renderingParams.size());
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
						assertEquals(3,renderingParams.size());
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
						assertEquals(3,renderingParams.size());
						assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
						assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
					}
				}
				
				@Nested
				@DisplayName("1 Param over 3 have no Alias")
				class OneParamHaveNoAlias {

					@BeforeEach
					void setUpBeforeEach() throws Exception {
						geoms = new ArrayList<>();
						geoms.add(firstGeomMock);
						geoms.add(secondGeomMock);
						geoms.add(thirdGeomMock);
						firstParam = new RenderingParameters(firstGeomMock);
						secondParam = new RenderingParameters(secondGeomMock);
						thirdParam = new RenderingParameters(thirdGeomMock);
						
						firstParams = new ArrayList<>();
						firstParams.add(firstParam);
						Mockito.when(firstGeomMock.getRenderingParameters()).thenReturn(firstParams);
						
						secondParams = new ArrayList<>();
						secondParams.add(secondParam);
						Mockito.when(secondGeomMock.getRenderingParameters()).thenReturn(secondParams);
						
						thirdParams = new ArrayList<>();
						thirdParams.add(thirdParam);
						Mockito.when(thirdGeomMock.getRenderingParameters()).thenReturn(thirdParams);
					}

					/**
					 * x = no alias
					 * A:x; B->B(before); C:x result: A;B;C
					 */
					@Test
					@DisplayName("Refers to itself as Before")
					void testOneReferenceItselfBefore() {
						secondParam.setAlias(secondAlias);
						secondParam.renderBefore(secondAlias);
						renderingParams = renderer.getOrderedRenderingParameters();
						assertEquals(3,renderingParams.size());
						assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
						assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
					}

					/**
					 * x = no alias
					 * A:x; B->B(after); C:x result: A;B;C
					 */
					@Test
					@DisplayName("Refers to itself as After")
					void testOneReferenceItselfAfter() {
						secondParam.setAlias(secondAlias);
						secondParam.renderAfter(secondAlias);
						renderingParams = renderer.getOrderedRenderingParameters();
						assertEquals(3,renderingParams.size());
						assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
						assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
					}

					/**
					 * x = no alias
					 * A:x ; B -> C (before); C result: A;B;C
					 */
					@Test
					@DisplayName("Refers to Next as Before")
					void testReferenceNextAsBefore() {
						secondParam.setAlias(secondAlias);
						thirdParam.setAlias(thirdAlias);
						secondParam.renderBefore(thirdAlias);
						renderingParams = renderer.getOrderedRenderingParameters();
						assertEquals(3,renderingParams.size());
						assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
						assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
					}

					/**
					 * x = no alias
					 * A ; B -> A (after); C:x result: A;B;C
					 */
					@Test
					@DisplayName("Refers to Previous as After")
					void testReferencePreviousAsAfter() {
						firstParam.setAlias(firstAlias);
						secondParam.setAlias(secondAlias);
						secondParam.renderAfter(firstAlias);
						renderingParams = renderer.getOrderedRenderingParameters();
						assertEquals(3,renderingParams.size());
						assertEquals(firstGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId());
						assertEquals(secondGeomMock.getVaoId(), renderingParams.get(1).getGeom().getVaoId());
					}
				}
			}
		}
	}
}
