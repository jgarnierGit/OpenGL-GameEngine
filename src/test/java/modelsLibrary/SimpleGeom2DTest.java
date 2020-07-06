package modelsLibrary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;
import org.mockito.Mock;
import org.mockito.Mockito;

import models.SimpleGeom2D;
import models.data.VBOContent;
import renderEngine.Draw2DRenderer;
import renderEngine.Loader;
import renderEngine.RenderingParameters;
import shaderManager.Draw2DShader;

class SimpleGeom2DTest {

	public SimpleGeom2D geom;
	public String alias = "alias1";
	public Loader loader;
	@Mock
	public Draw2DRenderer draw2DRenderer;
	@Mock
	public Draw2DShader shader;

	@BeforeEach
	void setUp() throws Exception {
		loader = Mockito.mock(Loader.class);

		geom = SimpleGeom2D.create(loader, draw2DRenderer, shader, alias);
		Mockito.when(loader.loadToVAO(VBOContent.createEmpty(0, 2))).thenReturn(1);
	}

	@Nested
	class EmptyGeom {

		@Test
		@DisplayName("Empty geom should be empty")
		void testEmptyGeom() {
			assertEquals(0, geom.getVAOGeom().getObjContent().getMaterialsContent().getContent().size());
			assertEquals(0, geom.getVAOGeom().getObjContent().getPoints().getContent().size());
			assertNotNull(geom.getRenderingParameters());
		}

		@Test
		@DisplayName("Adding point copying default color")
		void testAddPoint() {
			geom.getGeomEditor().addPoint(new Vector2f(1, 1));
			assertEquals(4, geom.getVAOGeom().getObjContent().getMaterialsContent().getContent().size());
			assertEquals(2, geom.getVAOGeom().getObjContent().getPoints().getContent().size());
			assertEquals(new Float(1), geom.getVAOGeom().getObjContent().getMaterialsContent().getContent().get(0));
		}

		@Test
		@DisplayName("Adding a point with color")
		void testaddPointWithColor() {
			geom.getGeomEditor().addPoint(new Vector2f(1, 1), new Vector4f(2, 2, 2, 2));
			assertEquals(4, geom.getVAOGeom().getObjContent().getMaterialsContent().getContent().size());
			assertEquals(2, geom.getVAOGeom().getObjContent().getPoints().getContent().size());
			assertEquals(new Float(2), geom.getVAOGeom().getObjContent().getMaterialsContent().getContent().get(0));
		}

		@Test
		@DisplayName("Adding a vector3f throws exception")
		void testaddWrongPoint() {
			assertThrows(IllegalArgumentException.class, () -> {
				geom.getGeomEditor().addPoint(new Vector3f(1, 1, 1));
			});
		}
	}

	@Nested
	class presetGeom {
		@BeforeEach
		void setUp() throws Exception {
			RenderingParameters params = geom.getRenderingParameters();
			params.addEntity(new Vector3f(0, 0, 0), 1, 1, 1, 1);
			geom.getGeomEditor().addPoint(new Vector2f(1, 1), new Vector4f(1, 1, 1, 1));

		}

		@Test
		void testInstanciationOk() {
			assertEquals(4, geom.getVAOGeom().getObjContent().getMaterialsContent().getContent().size());
			assertEquals(2, geom.getVAOGeom().getObjContent().getPoints().getContent().size());
		}

		@Test
		void testReset() {
			geom.clear();
			assertEquals(0, geom.getVAOGeom().getObjContent().getMaterialsContent().getContent().size());
			assertEquals(0, geom.getVAOGeom().getObjContent().getPoints().getContent().size());
		}

		@Test
		@DisplayName("Adding a point copying previous color")
		void testaddPoint() {
			geom.getGeomEditor().addPoint(new Vector2f(1, 1));
			assertEquals(8, geom.getVAOGeom().getObjContent().getMaterialsContent().getContent().size());
			assertEquals(4, geom.getVAOGeom().getObjContent().getPoints().getContent().size());
		}

		@Test
		@DisplayName("Adding a point with color")
		void testaddPointWithColor() {
			geom.getGeomEditor().addPoint(new Vector2f(1, 1), new Vector4f(2, 2, 2, 2));
			assertEquals(8, geom.getVAOGeom().getObjContent().getMaterialsContent().getContent().size());
			assertEquals(4, geom.getVAOGeom().getObjContent().getPoints().getContent().size());
		}

		@Test
		@DisplayName("get Vectors vertices list")
		void testgetVertices() {
			assertEquals(1, geom.getVertices().size());
		}

		@Test
		@DisplayName("get copied geom with same parameters")
		void testgetCopiedGeom() {
			SimpleGeom2D copied = geom.copy("newAlias");
			assertEquals(geom.getVAOGeom().getObjContent().getPoints().getContent(),
					copied.getVAOGeom().getObjContent().getPoints().getContent());
			assertEquals(geom.getVAOGeom().getObjContent().getMaterialsContent().getContent(),
					copied.getVAOGeom().getObjContent().getMaterialsContent().getContent());
		}
	}

}
