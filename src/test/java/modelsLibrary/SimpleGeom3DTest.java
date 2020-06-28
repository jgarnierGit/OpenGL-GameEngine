package modelsLibrary;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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

import modelsManager.bufferCreator.VBOContent;
import renderEngine.Draw3DRenderer;
import renderEngine.Loader;
import renderEngine.RenderingParameters;
import shaderManager.Draw3DShader;

class SimpleGeom3DTest {

	public SimpleGeom3D geom;
	public String alias = "alias1";
	public Loader loader;
	@Mock
	public Draw3DRenderer draw3DRenderer;
	@Mock
	public Draw3DShader shader;

	@BeforeEach
	void setUp() throws Exception {
		loader = Mockito.mock(Loader.class);
		geom = SimpleGeom3D.createWithDefaultEntity(loader, draw3DRenderer, shader, alias);
		Mockito.when(loader.loadToVAO(new VBOContent(1,  geom.rawGeom.dimension, geom.rawGeom.points))).thenReturn(1);
	}

	@Nested
	class emptyGeom {

		@Test
		@DisplayName("Empty geom should be empty")
		void testEmptyGeom() {
			assertEquals(0, geom.rawGeom.colors.length);
			assertEquals(0, geom.rawGeom.points.length);
			assertNotNull(geom.renderingParameters);
		}

		@Test
		@DisplayName("Adding point copying default color")
		void testAddPoint() {
			geom.addPoint(new Vector3f(1, 1, 1));
			assertEquals(4, geom.rawGeom.colors.length);
			assertEquals(3, geom.rawGeom.points.length);
			assertEquals(1.0f, geom.rawGeom.colors[0]);
		}

		@Test
		@DisplayName("Adding a point with color")
		void testaddPointWithColor() {
			geom.addPoint(new Vector3f(1, 1, 1), new Vector4f(2, 2, 2, 2));
			assertEquals(4, geom.rawGeom.colors.length);
			assertEquals(3, geom.rawGeom.points.length);
			assertEquals(2, geom.rawGeom.colors[0]);
		}

		@Test
		@DisplayName("Adding a vector2f throws exception")
		void testaddWrongPoint() {
			assertThrows(IllegalArgumentException.class, () -> {
				geom.addPoint(new Vector2f(1, 1));
			});
		}
	}

	@Nested
	class presetGeom {
		@BeforeEach
		void setUp() throws Exception {
			RenderingParameters params = geom.getRenderingParameters();
			params.addEntity(new Vector3f(0, 0, 0), 1, 1, 1, 1);
			geom.addPoint(new Vector3f(1, 2, 3), new Vector4f(1, 2, 3, 4));

		}

		@Test
		void testInstanciationOk() {
			assertEquals(4, geom.rawGeom.colors.length);
			assertEquals(3, geom.rawGeom.points.length);
		}

		@Test
		void testReset() {
			geom.reset();
			assertEquals(0, geom.rawGeom.colors.length);
			assertEquals(0, geom.rawGeom.points.length);
		}

		@Test
		@DisplayName("Adding a point copying previous color")
		void testaddPoint() {
			geom.addPoint(new Vector3f(1, 2, 3));
			assertEquals(8, geom.rawGeom.colors.length);
			assertEquals(6, geom.rawGeom.points.length);
		}

		@Test
		@DisplayName("Adding a point with color")
		void testaddPointWithColor() {
			geom.addPoint(new Vector3f(1, 2, 3), new Vector4f(2, 2, 2, 2));
			assertEquals(8, geom.rawGeom.colors.length);
			assertEquals(6, geom.rawGeom.points.length);
		}

		@Test
		@DisplayName("get Vectors vertices list")
		void testgetVertices() {
			assertEquals(1, geom.buildVerticesList().size());
		}

		@Test
		@DisplayName("get copied geom with same parameters")
		void testgetCopiedGeom() {
			SimpleGeom3D copied = geom.copy("newAlias");
			assertArrayEquals(geom.rawGeom.getPoints(), copied.rawGeom.getPoints());
			assertArrayEquals(geom.rawGeom.getColors(), copied.rawGeom.getColors());
		}
	}

}
