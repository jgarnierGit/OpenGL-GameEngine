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

/**
 * don't know when to use beforeAll...
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
		firstGeomMock = Mockito.mock(ISimpleGeom.class);
		Mockito.when(firstGeomMock.getVaoId()).thenReturn(1);
		secondGeomMock = Mockito.mock(ISimpleGeom.class);
		Mockito.when(secondGeomMock.getVaoId()).thenReturn(2);
		thirdGeomMock = Mockito.mock(ISimpleGeom.class);
		Mockito.when(secondGeomMock.getVaoId()).thenReturn(3);
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
			}

			@Nested
			@DisplayName("Params without alias (natural order)")
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
			@DisplayName("Params with alias")
			class ParamsWithChanges {
				@Nested
				@DisplayName("One change per ordering")
				class ParamsUnitaryChangePerOrderingOperation {

					@BeforeEach
					void setUpBeforeEach() throws Exception {
						geoms = new ArrayList<>();
						geoms.add(firstGeomMock);
						geoms.add(secondGeomMock);
						geoms.add(thirdGeomMock);
						firstParam = new RenderingParameters(firstGeomMock);
						secondParam = new RenderingParameters(secondGeomMock);
						thirdParam = new RenderingParameters(thirdGeomMock);
					}

					/**
					 * A; B->B(before); C result: A;B
					 */
					@Test
					@DisplayName("Refers to itself as Before")
					void testOneReferenceItselfBefore() {
						renderingParams = renderer.getOrderedRenderingParameters();
						fail("Not implemented");
					}

					/**
					 * A; B->B(after); C result: A;B
					 */
					@Test
					@DisplayName("Refers to itself as After")
					void testOneReferenceItselfAfter() {
						renderingParams = renderer.getOrderedRenderingParameters();
						fail("Not implemented");
					}

					/**
					 * A ; B -> C (before); C result: A;B; C
					 */
					@Test
					@DisplayName("Refers to Next as Before")
					void testReferenceNextAsBefore() {
						fail("Not implemented");
					}

					/**
					 * A ; B -> A (after); C result: A;B; C
					 */
					@Test
					@DisplayName("Refers to Previous as After")
					void testReferencePreviousAsAfter() {
						fail("Not implemented");
					}
				}
			}
		}
	}
}
