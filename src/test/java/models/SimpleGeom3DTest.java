package models;

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

import renderEngine.Draw3DRenderer;
import renderEngine.MasterRenderer;
import renderEngine.RenderingParameters;

class SimpleGeom3DTest {

	public SimpleGeom3D geom;
	public String alias = "alias1";
	@Mock
	public MasterRenderer masterRenderer;

	@BeforeEach
	void setUp() throws Exception {
		masterRenderer = Mockito.mock(MasterRenderer.class);
		Mockito.doNothing().when(masterRenderer).registerRenderer(Mockito.mock(Draw3DRenderer.class));
		geom = SimpleGeom3D.createWithDefaultEntity(masterRenderer, null, null, alias);
	}

	@Nested
	class emptyGeom {

		@Test
		@DisplayName("Empty geom should be empty")
		void testEmptyGeom() {
			assertEquals(0, geom.getObjContent().getMaterials().getVBOContent().getContent().size());
			assertEquals(0, geom.getObjContent().getPositions().getContent().size());
			assertNotNull(geom.getRenderingParameters());
		}

		@Test
		@DisplayName("Adding point copying default color")
		void testAddPoint() {
			geom.getGeomEditor().addPoint(new Vector3f(1, 1, 1));
			assertEquals(4, geom.getObjContent().getMaterials().getVBOContent().getContent().size());
			assertEquals(3, geom.getObjContent().getPositions().getContent().size());
			assertEquals(new Float(1), geom.getObjContent().getMaterials().getVBOContent().getContent().get(0));
		}

		@Test
		@DisplayName("Adding a point with color")
		void testaddPointWithColor() {
			geom.getGeomEditor().addPoint(new Vector3f(1, 1, 1), new Vector4f(2, 2, 2, 2));
			assertEquals(4, geom.getObjContent().getMaterials().getVBOContent().getContent().size());
			assertEquals(3, geom.getObjContent().getPositions().getContent().size());
			assertEquals(new Float(2), geom.getObjContent().getMaterials().getVBOContent().getContent().get(0));
		}

		@Test
		@DisplayName("Adding a vector2f throws exception")
		void testaddWrongPoint() {
			assertThrows(IllegalArgumentException.class, () -> {
				geom.getGeomEditor().addPoint(new Vector2f(1, 1));
			});
		}

		@Test
		@DisplayName("get copied geom from not initialized one")
		void testgetCopiedGeomFromEmpty() {
			SimpleGeom3D copied = geom.copy("newAlias");
			assertEquals(geom.getObjContent().getPositions().getContent(),
					copied.getObjContent().getPositions().getContent());
			assertEquals(geom.getObjContent().getMaterials().getVBOContent().getContent(),
					copied.getObjContent().getMaterials().getVBOContent().getContent());
		}
	}

	@Nested
	class presetGeom {
		@BeforeEach
		void setUp() throws Exception {
			RenderingParameters params = geom.getRenderingParameters();
			params.addEntity(new Vector3f(0, 0, 0), 1, 1, 1, 1);
			geom.getGeomEditor().addPoint(new Vector3f(1, 2, 3), new Vector4f(1, 2, 3, 4));

		}

		@Test
		void testInstanciationOk() {
			assertEquals(4, geom.getObjContent().getMaterials().getVBOContent().getContent().size());
			assertEquals(3, geom.getObjContent().getPositions().getContent().size());
		}

		@Test
		void testReset() {
			geom.clear();
			assertEquals(0, geom.getObjContent().getMaterials().getVBOContent().getContent().size());
			assertEquals(0, geom.getObjContent().getPositions().getContent().size());
		}

		@Test
		@DisplayName("Adding a point copying previous color")
		void testaddPoint() {
			geom.getGeomEditor().addPoint(new Vector3f(1, 2, 3));
			assertEquals(8, geom.getObjContent().getMaterials().getVBOContent().getContent().size());
			assertEquals(6, geom.getObjContent().getPositions().getContent().size());
		}

		@Test
		@DisplayName("Adding a point with color")
		void testaddPointWithColor() {
			geom.getGeomEditor().addPoint(new Vector3f(1, 2, 3), new Vector4f(2, 2, 2, 2));
			assertEquals(8, geom.getObjContent().getMaterials().getVBOContent().getContent().size());
			assertEquals(6, geom.getObjContent().getPositions().getContent().size());
		}

		@Test
		@DisplayName("get Vectors vertices list")
		void testgetVertices() {
			assertEquals(1, geom.getVertices().size());
		}

		@Test
		@DisplayName("get copied geom with same parameters")
		void testgetCopiedGeom() {
			SimpleGeom3D copied = geom.copy("newAlias");
			assertEquals(geom.getObjContent().getPositions().getContent(),
					copied.getObjContent().getPositions().getContent());
			assertEquals(geom.getObjContent().getMaterials().getVBOContent().getContent(),
					copied.getObjContent().getMaterials().getVBOContent().getContent());
		}
	}

}
