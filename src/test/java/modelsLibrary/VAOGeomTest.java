package modelsLibrary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lwjglx.util.vector.Vector4f;
import org.mockito.Mockito;

import modelsManager.bufferCreator.VBOContent;
import renderEngine.DrawRenderer;
import renderEngine.Loader;

class VAOGeomTest {
	private VAOGeom geom2;
	private VAOGeom geom;
	private Loader loader;
	private static final Vector4f COLOR_ADDED = new Vector4f(1f, 2f, 3f, 4f);
	private static final List<Float> COLOR_GEOM1 = Arrays.asList(2f, 2f, 2f, 2f);

	@BeforeEach
	void setUp() throws Exception {
		loader = Mockito.mock(Loader.class);
		geom = VAOGeom.create(loader, Mockito.mock(DrawRenderer.class), 3);
		geom2 = VAOGeom.create(loader, Mockito.mock(DrawRenderer.class), 3);
		Mockito.when(loader.loadToVAO(geom.points)).thenReturn(1);
		Mockito.when(loader.loadToVAO(geom2.points)).thenReturn(2);
		geom.dimension = 1;
		geom.points = VBOContent.createEmpty(0, 1);
		geom.colors = VBOContent.createEmpty(1, 4);

	}

	@Test
	@DisplayName("Create tester")
	void TestCreate() {
		assertNotNull(geom.points);
		assertNotNull(geom.colors);
		assertTrue(geom.points.getContent().isEmpty());
		assertTrue(geom.colors.getContent().isEmpty());
	}

	@Test
	@DisplayName("Duplicate color empty")
	void TestDuplicateColorEmpty() {
		geom.duplicateLastColor();
		assertFalse(geom.colors.getContent().isEmpty());
	}

	@Test
	@DisplayName("Add color on empty list")
	void TestAddColor() {
		geom.addColor(COLOR_ADDED);
		assertFalse(geom.colors.getContent().isEmpty());
		assertEquals(4, geom.colors.getContent().size());
		assertEquals(new Float(1), geom.colors.getContent().get(0));
	}

	@Nested
	class WithValues {
		@BeforeEach
		void setUp() throws Exception {
			geom.points = VBOContent.create(0, 3, Arrays.asList(1f, 1f, 1f));
			geom.colors = VBOContent.create(1, 4, COLOR_GEOM1);
			geom.dimension = 3;
		}

		@Test
		@DisplayName("Add color on empty list")
		void TestAddColor() {
			geom.addColor(COLOR_ADDED);
			assertFalse(geom.colors.getContent().isEmpty());
			assertEquals(8, geom.colors.getContent().size());
			assertEquals(new Float(1), geom.colors.getContent().get(4));
		}

		@Test
		@DisplayName("Duplicate color existing")
		void TestDuplicateColor() {
			geom.duplicateLastColor();
			assertFalse(geom.colors.getContent().isEmpty());
			assertEquals(8, geom.colors.getContent().size());
			assertEquals(new Float(2), geom.colors.getContent().get(5));
		}

		@Test
		@DisplayName("Copy RawGeom must have fields if new signature reference")
		void testCopyRawValues() {
			geom2.dimension = 3;
			geom2.copyRawValues(geom);
			assertNotSame(geom.getColors(), geom2.getColors());
			assertNotSame(geom.getPoints(), geom2.getPoints());
		}

		@Test
		@DisplayName("Copy RawGeom fails if dimensions are not equals")
		void testCopyRawValuesFails() {
			geom2.dimension = 2;
			assertThrows(IllegalArgumentException.class, () -> {
				geom2.copyRawValues(geom);
			});
		}

		@Test
		@DisplayName("Clear must empty VBOs")
		void testClear() {
			geom.clear();
			assertTrue(geom.colors.getContent().isEmpty());
			assertTrue(geom.points.getContent().isEmpty());
		}

		@Test
		@DisplayName("update colors params at index")
		void testUpdateColorAtIndex() {
			geom.addColor(COLOR_ADDED);
			geom.updateColor(1, new Vector4f(6f, 5f, 4f, 3f));
			assertEquals(8, geom.colors.getContent().size());
			assertEquals(new Float(6), geom.colors.getContent().get(4));
		}

		@Test
		@DisplayName("update colors params at index exception")
		void testUpdateColorAtIndexFails() {
			geom.addColor(COLOR_ADDED);
			assertThrows(IllegalArgumentException.class, () -> {
				geom.updateColor(2, new Vector4f(6f, 5f, 4f, 3f));
			});
		}

		@Test
		@DisplayName("update all colors params")
		void testUpdateAllColors() {
			geom.addColor(COLOR_ADDED);
			geom.updateColor(new Vector4f(6f, 5f, 4f, 3f));
			assertEquals(8, geom.colors.getContent().size());
			assertEquals(new Float(6), geom.colors.getContent().get(0));
			assertEquals(new Float(3), geom.colors.getContent().get(3));
			assertEquals(new Float(6), geom.colors.getContent().get(4));
			assertEquals(new Float(3), geom.colors.getContent().get(7));
		}

		@Test
		@DisplayName("update all colors params except x")
		void testUpdateColorsNotX() {
			geom.addColor(COLOR_ADDED);
			geom.updateColor(new Vector4f(-1f, 5f, 4f, 3f));
			assertEquals(8, geom.colors.getContent().size());
			assertEquals(new Float(COLOR_GEOM1.get(0)), geom.colors.getContent().get(0));
			assertEquals(new Float(3), geom.colors.getContent().get(3));
			assertEquals(new Float(COLOR_ADDED.x), geom.colors.getContent().get(4));
			assertEquals(new Float(3), geom.colors.getContent().get(7));
		}

		@Test
		@DisplayName("update all colors params except y")
		void testUpdateColorsNotY() {
			geom.addColor(COLOR_ADDED);
			geom.updateColor(new Vector4f(6f, -1f, 4f, 3f));
			assertEquals(8, geom.colors.getContent().size());
			assertEquals(new Float(COLOR_GEOM1.get(1)), geom.colors.getContent().get(1));
			assertEquals(new Float(3), geom.colors.getContent().get(3));
			assertEquals(new Float(COLOR_ADDED.y), geom.colors.getContent().get(5));
			assertEquals(new Float(3), geom.colors.getContent().get(7));
		}

		@Test
		@DisplayName("update all colors params except z")
		void testUpdateColorsNotZ() {
			geom.addColor(COLOR_ADDED);
			geom.updateColor(new Vector4f(6f, 5f, -1f, 3f));
			assertEquals(8, geom.colors.getContent().size());
			assertEquals(new Float(COLOR_GEOM1.get(2)), geom.colors.getContent().get(2));
			assertEquals(new Float(3), geom.colors.getContent().get(3));
			assertEquals(new Float(COLOR_ADDED.z), geom.colors.getContent().get(6));
			assertEquals(new Float(3), geom.colors.getContent().get(7));
		}

		@Test
		@DisplayName("update all colors params except w")
		void testUpdateColorsNotW() {
			geom.addColor(COLOR_ADDED);
			geom.updateColor(new Vector4f(6f, 5f, 4f, -1f));
			assertEquals(8, geom.colors.getContent().size());
			assertEquals(new Float(COLOR_GEOM1.get(3)), geom.colors.getContent().get(3));
			assertEquals(new Float(4), geom.colors.getContent().get(2));
			assertEquals(new Float(COLOR_ADDED.w), geom.colors.getContent().get(7));
			assertEquals(new Float(4), geom.colors.getContent().get(6));
		}

	}

}
