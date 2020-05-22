package modelsLibrary;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lwjglx.util.vector.Vector3f;
import org.mockito.Mockito;

import renderEngine.Loader;
import renderEngine.RenderingParameters;

/**
 * Mockito.mock(SimpleGeom.class, Mockito.withSettings().useConstructor(geom).defaultAnswer(Mockito.CALLS_REAL_METHODS)) did not work
 * @author chezmoi
 *
 */
class SimpleGeomTest {

	public SimpleGeom geom;
	public String alias = "alias1";
	public Loader loader;
	
	@BeforeEach
	void setUp() throws Exception {
		geom = Mockito.mock(SimpleGeom.class,Mockito.CALLS_REAL_METHODS);
		loader = Mockito.mock(Loader.class);
		geom.renderingParameters = new RenderingParameters(geom, alias);
		geom.points = new float[] {};
		geom.colors = new float[] {};
		geom.dimension= 0;
		Mockito.when(loader.loadToVAO(geom.points,geom.dimension)).thenReturn(1);
		geom.loader = loader;
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * TODO must be tested in appropriate class
	 * createRenderingParameters
	 */
	@Disabled
	@Test
	void testCreateRenderingParameters() {
		/**RenderingParameters params = geom.createRenderingParameters(alias);
		assertEquals(geom, params.getGeom());
		assertEquals(1, geom.getRenderingParameters());
		assertEquals(params, geom.getRenderingParameters().get(0));
		assertEquals(Optional.empty(),params.getRenderMode());
		assertEquals(this.alias, params.getAlias());
		assertEquals(false,  params.isDestinationPositionAfter());
		assertEquals("",  params.getDestinationOrderAlias());
		assertNotNull(params.getEntities());**/
	}
	
	@Nested
	@DisplayName("Copying geom or param must operate a deep copy")
	class copyingExistingGeomAndParam{
		String alias2 = "alias2";
		SimpleGeom geom2;
		@BeforeEach
		void setUp() throws Exception {
			geom2 = Mockito.mock(SimpleGeom.class,Mockito.CALLS_REAL_METHODS);
		}
		
		@Test
		@DisplayName("Copy geom using model must have fields if new signature reference")
		void testCopyGeomSignatures() {
			geom2.copy(geom, alias2);
			assertNotSame(geom.getColors(), geom2.getColors());
			assertNotSame(geom.getPoints(), geom2.getPoints());
			assertSame(geom.getDimension(), geom2.getDimension());
			assertNotSame(geom.getRenderingParameters(), geom2.getRenderingParameters());
		}
		
		@Test
		@DisplayName("Copy default RenderingParameter on new geom must have fields if new signature reference")
		void testCopyDefaultRenderingPamaterSignatures() {
			geom2.copy(geom, alias2);
			RenderingParameters geom2Params = geom2.getRenderingParameters();
			geom2Params.setRenderMode(1);
			geom2Params.renderAfter("toto");
			
			assertNotNull(geom2Params.getAlias());
			assertNotSame(geom.renderingParameters.getAlias(), geom2Params.getAlias());
			assertNotNull(geom2Params.getRenderMode());
			assertNotSame(geom.renderingParameters.getRenderMode(), geom2Params.getRenderMode());
			assertNotNull(geom2Params.getDestinationOrderAlias());
			assertNotSame(geom.renderingParameters.getDestinationOrderAlias(), geom2Params.getDestinationOrderAlias());
			assertNotNull(geom2Params.getStatesRendering());
			assertNotSame(geom.renderingParameters.getStatesRendering(), geom2Params.getStatesRendering());
			assertNotNull(geom2Params.getGeom());
			assertNotSame(geom.renderingParameters.getGeom(), geom2Params.getGeom());
			assertNotNull(geom2Params.isDestinationPositionAfter());
			assertNotSame(geom.renderingParameters.isDestinationPositionAfter(), geom2Params.isDestinationPositionAfter());
			assertNotNull(geom2Params.getEntities());
			assertNotSame(geom.renderingParameters.getEntities(), geom2Params.getEntities());
		}
		
		@Nested
		class duplicateArraysObject{
			RenderingParameters geom2Params;
			Vector3f positionEntity = new Vector3f(1,2,3);
			@BeforeEach
			void setUp() throws Exception {
				geom.renderingParameters.addGlState(2, true);
				geom.renderingParameters.setRenderMode(2);
				geom.renderingParameters.renderBefore("test");
				geom.renderingParameters.addEntity(positionEntity, 0, 0, 0, 1);
				geom2.copy(geom, alias2);
				geom2Params = geom2.getRenderingParameters();
				geom2Params.setRenderMode(1);
				geom2Params.renderAfter("toto");
			}
			
			@Test
			@DisplayName("Copy RenderingParameter on new geom must have fields if new signature reference")
			void testCopyRenderingPamaterSignatures() {
				assertNotNull(geom2Params.getAlias());
				assertNotSame(geom.renderingParameters.getAlias(), geom2Params.getAlias());
				assertNotNull(geom2Params.getRenderMode());
				assertNotSame(geom.renderingParameters.getRenderMode(), geom2Params.getRenderMode());
				assertNotNull(geom2Params.getDestinationOrderAlias());
				assertNotSame(geom.renderingParameters.getDestinationOrderAlias(), geom2Params.getDestinationOrderAlias());
				assertNotNull(geom2Params.getStatesRendering());
				assertNotSame(geom.renderingParameters.getStatesRendering(), geom2Params.getStatesRendering());
				assertNotNull(geom2Params.getGeom());
				assertNotSame(geom.renderingParameters.getGeom(), geom2Params.getGeom());
				assertNotNull(geom2Params.isDestinationPositionAfter());
				assertNotEquals(geom.renderingParameters.isDestinationPositionAfter(), geom2Params.isDestinationPositionAfter());
				assertNotNull(geom2Params.getEntities());
				assertNotSame(geom.renderingParameters.getEntities(), geom2Params.getEntities());
			}
		}
		
	}
}
