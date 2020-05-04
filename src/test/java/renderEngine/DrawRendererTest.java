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

class DrawRendererTest {
	
	List<ISimpleGeom> geoms;
	DrawRenderer renderer;
	List<RenderingParameters> renderingParams;
	ISimpleGeom simpleGeomMock;
	ISimpleGeom simpleGeomMockLast;
	
	@Nested
	@TestInstance(Lifecycle.PER_CLASS)
	@DisplayName("Test ordering On Geoms without RenderinParameters")
	class noParams {
		
		@BeforeAll 
		void setUpBeforeClass() throws Exception {
			MockitoAnnotations.initMocks(this);
			geoms = new ArrayList<>();
			simpleGeomMock = Mockito.mock(
					ISimpleGeom.class);
			Mockito.when(simpleGeomMock.getVaoId()).thenReturn(1);
			simpleGeomMockLast = Mockito.mock(
					ISimpleGeom.class);
			Mockito.when(simpleGeomMockLast.getVaoId()).thenReturn(2);
			geoms.add(simpleGeomMock);
			geoms.add(simpleGeomMockLast);
			renderer = Mockito.mock(
					DrawRenderer.class, 
				      Mockito.CALLS_REAL_METHODS);
			renderer.geoms = geoms;
			renderingParams = renderer.getOrderedRenderingParameters();
		}
				
		@Test
		@DisplayName("Test ordering without renderingParameters must not be empty")
		void testOrderingGeomsWithoutParamsNotEmpty() {
			assertFalse(renderingParams.isEmpty(), "output renderingParams is empty");
		}
		
		@Test
		@DisplayName("Test ordering without renderingParameters must have same elements")
		void testOrderingGeomsWithoutParamsSize() {
			assertEquals(2, renderingParams.size(), "output renderingParams miss geoms");
		}
		
		@Test
		@DisplayName("Test ordering without renderingParameters must have first geom first")
		void testOrderingGeomsWithoutParamsFirstGeomFirst() {
			assertEquals(simpleGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId(), "expected first geom to be first in output");
		}
	}

	@Nested
	@TestInstance(Lifecycle.PER_CLASS)
	@DisplayName("Test ordering On Geoms with RenderinParameters")
	class withParamsNaturalOrder {
		
		@BeforeAll 
		void setUpBeforeClass() throws Exception {
			MockitoAnnotations.initMocks(this);
			geoms = new ArrayList<>();
			simpleGeomMock = Mockito.mock(
					ISimpleGeom.class);
			Mockito.when(simpleGeomMock.getVaoId()).thenReturn(1);
			RenderingParameters param1 = new RenderingParameters(simpleGeomMock);
			List<RenderingParameters> params1 = new ArrayList<>();
			params1.add(param1);
			Mockito.when(simpleGeomMock.getRenderingParameters()).thenReturn(params1);
			
			simpleGeomMockLast = Mockito.mock(
					ISimpleGeom.class);
			Mockito.when(simpleGeomMockLast.getVaoId()).thenReturn(2);
			RenderingParameters param2 = new RenderingParameters(simpleGeomMockLast);
			List<RenderingParameters> params2 = new ArrayList<>();
			params2.add(param2);
			Mockito.when(simpleGeomMockLast.getRenderingParameters()).thenReturn(params2);
			
			geoms.add(simpleGeomMock);
			geoms.add(simpleGeomMockLast);
			renderer = Mockito.mock(
					DrawRenderer.class, 
				      Mockito.CALLS_REAL_METHODS);
			renderer.geoms = geoms;
			renderingParams = renderer.getOrderedRenderingParameters();
		}

		@AfterEach
		void tearDown() throws Exception {
		}

		//TODO maybe parametrized test can avoid duplication
		@Test
		@DisplayName("Test ordering without renderingParameters must not be empty")
		void testOrderingGeomsWithoutParamsNotEmpty() {
			assertFalse(renderingParams.isEmpty(), "output renderingParams is empty");
		}
		
		@Test
		@DisplayName("Test ordering without renderingParameters must have same elements")
		void testOrderingGeomsWithoutParamsSize() {
			assertEquals(2, renderingParams.size(), "output renderingParams miss geoms");
		}
		
		@Test
		@DisplayName("Test ordering without renderingParameters must have first geom first")
		void testOrderingGeomsWithoutParamsFirstGeomFirst() {
			assertEquals(simpleGeomMock.getVaoId(), renderingParams.get(0).getGeom().getVaoId(), "expected first geom to be first in output");
		}
		
	}
	
	

}
