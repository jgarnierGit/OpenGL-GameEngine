package modelsLibrary;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import renderEngine.Loader;
import renderEngine.RenderingParameters;

/**
 * Mockito.mock(SimpleGeom.class, Mockito.withSettings().useConstructor(geom).defaultAnswer(Mockito.CALLS_REAL_METHODS)) did not work
 * @author chezmoi
 *
 */
class SimpleGeomTest {

	public SimpleGeom geom;
	public String alias;
	public Loader loader;
	
	@BeforeEach
	void setUp() throws Exception {
		geom = Mockito.mock(SimpleGeom.class,Mockito.CALLS_REAL_METHODS);
		loader = Mockito.mock(Loader.class);
		geom.renderingParameters = new ArrayList<>();
		geom.points = new float[] {};
		geom.colors = new float[] {};
		geom.dimension= 0;
		Mockito.when(loader.loadToVAO(geom.points,geom.dimension)).thenReturn(1);
		geom.loader = loader;
		alias = "alias1";
		
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * createRenderingPamater
	 */
	@Test
	void testCreateRenderingPamater() {
		RenderingParameters params = geom.createRenderingPamater(alias);
		assertEquals(geom, params.getGeom());
		assertEquals(1, geom.getRenderingParameters().size());
		assertEquals(params, geom.getRenderingParameters().get(0));
		assertEquals(Optional.empty(),params.getRenderMode());
		assertEquals(this.alias, params.getAlias());
		assertEquals(false,  params.isDestinationPositionAfter());
		assertEquals("",  params.getDestinationOrderAlias());
	}
	
	@Nested
	class existingGeomAndParam{
		RenderingParameters params;
		SimpleGeom geom2;
		@BeforeEach
		void setUp() throws Exception {
			params = geom.createRenderingPamater(alias);
			geom2 = Mockito.mock(SimpleGeom.class,Mockito.CALLS_REAL_METHODS);
			//geom2.renderingParameters = new ArrayList<>();
		}
		
		@Test
		@DisplayName("Copy geom using model must have fields if new signature reference")
		void testCopyGeomSignatures() {
			geom2.copy(geom);
			assertNotSame(geom.getColors(), geom2.getColors());
			assertNotSame(geom.getPoints(), geom2.getPoints());
			assertSame(geom.getDimension(), geom2.getDimension());
			assertNotSame(geom.getRenderingParameters(), geom2.getRenderingParameters());
		}
		
		@Test
		@DisplayName("Copy RenderingParameter on new geom must have fields if new signature reference")
		void testCopyRenderingPamaterSignatures() {
			geom2.copy(geom);
			geom2.createRenderingPamater(params, "alias2");
			RenderingParameters geom2Params = geom2.getRenderingParameters().get(0);
			geom2Params.setRenderMode(1);
			geom2Params.renderAfter("toto");
			assertNotSame(params.getAlias(), geom2Params.getAlias());
			assertNotSame(params.getRenderMode(), geom2Params.getRenderMode());
			assertNotSame(params.getDestinationOrderAlias(), geom2Params.getDestinationOrderAlias());
			assertNotSame(params.getStatesRendering(), geom2Params.getStatesRendering());
			assertNotSame(params.getGeom(), geom2Params.getGeom());
			assertNotSame(params.isDestinationPositionAfter(), geom2Params.isDestinationPositionAfter());
		}
		
		@Test
		@DisplayName("Duplicate RenderingParameter on new geom must have fields set as original")
		void testDuplicateRenderingPamaterValues() {
			// createRenderingPamater(RenderingParameters frustrumPlainParams, String alias)
			// geom => this.geom2
			// this.renderingParameters.get(this.renderingParameters) => size 1
			//this.glStatesRendering => frustrumPlainParams.glStatesRendering
			//this.glRenderMode => frustrumPlainParams.glRenderMode
			// this.renderAfter => frustrumPlainParams.renderAfter;
			// this.destinationOrderAlias => frustrumPlainParams.destinationOrderAlias;
			
			fail("Not yet implemented");
		}
		/**
		@Test
		@DisplayName("Modifying duplicated RenderingParameter must apply changes on duplicated and leave original unmodified")
		void testModifyDuplicateRenderingPamater() {
			// createRenderingPamater(RenderingParameters frustrumPlainParams, String alias)
			// geom => this.geom2
			// this.renderingParameters.get(this.renderingParameters) => size 1
			//this.glStatesRendering => frustrumPlainParams.glStatesRendering
			//this.glRenderMode => frustrumPlainParams.glRenderMode
			// this.renderAfter => frustrumPlainParams.renderAfter;
			// this.destinationOrderAlias => frustrumPlainParams.destinationOrderAlias;
			
			fail("Not yet implemented");
		}**/
	}
	
	
	
	 

}
