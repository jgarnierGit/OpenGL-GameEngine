package modelsLibrary;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lwjglx.util.vector.Vector4f;
import org.mockito.Mockito;

import models.GeomEditorImpl;
import models.EditableGeom;
import models.SimpleGeom2D;
import models.SimpleGeom3D;
import models.data.VBOContent;

class GeomEditorTest {
	GeomEditorImpl geomEditor;
	SimpleGeom3D geom3D;
	SimpleGeom2D geom2D;
	private static final Vector4f COLOR_ADDED = new Vector4f(1f, 2f, 3f, 4f);
	private static final List<Float> COLOR_GEOM1 = Arrays.asList(2f, 2f, 2f, 2f);

	@BeforeEach
	void setUp() throws Exception {
		geomEditor = GeomEditorImpl.create(Mockito.mock(EditableGeom.class));
	}

	@Test
	@DisplayName("test create")
	void testCreate() {
		fail("Not yet implemented");
	}

	@Test
	@DisplayName("Duplicate color empty")
	void TestDuplicateColorEmpty() {
		geom.duplicateLastColor();
		assertFalse(geom.colors.getVBOContent().isEmpty());
	}

	@Test
	@DisplayName("Add color on empty list")
	void TestAddColor() {
		geom.addColor(COLOR_ADDED);
		assertFalse(geom.colors.getVBOContent().isEmpty());
		assertEquals(4, geom.colors.getVBOContent().size());
		assertEquals(new Float(1), geom.colors.getVBOContent().get(0));
	}

	@Nested
	class WithValues {
		@BeforeEach
		void setUp() throws Exception {
			geom.getPositions() = VBOContent.create(0, 3, Arrays.asList(1f, 1f, 1f));
			geom.colors = VBOContent.create(1, 4, COLOR_GEOM1);
			geom.dimension = 3;
		}

		@Test
		@DisplayName("Add color on empty list")
		void TestAddColor() {
			geom.addColor(COLOR_ADDED);
			assertFalse(geom.colors.getVBOContent().isEmpty());
			assertEquals(8, geom.colors.getVBOContent().size());
			assertEquals(new Float(1), geom.colors.getVBOContent().get(4));
		}

		@Test
		@DisplayName("Duplicate color existing")
		void TestDuplicateColor() {
			geom.duplicateLastColor();
			assertFalse(geom.colors.getVBOContent().isEmpty());
			assertEquals(8, geom.colors.getVBOContent().size());
			assertEquals(new Float(2), geom.colors.getVBOContent().get(5));
		}

		@Test
		@DisplayName("update colors params at index")
		void testUpdateColorAtIndex() {
			vaoGeom.addColor(COLOR_ADDED);
			vaoGeom.updateColor(1, new Vector4f(6f, 5f, 4f, 3f));
			assertEquals(8, vaoGeom.colors.getVBOContent().size());
			assertEquals(new Float(6), vaoGeom.colors.getVBOContent().get(4));
		}

		@Test
		@DisplayName("update colors params at index exception")
		void testUpdateColorAtIndexFails() {
			vaoGeom.addColor(COLOR_ADDED);
			assertThrows(IllegalArgumentException.class, () -> {
				vaoGeom.updateColor(2, new Vector4f(6f, 5f, 4f, 3f));
			});
		}

		@Test
		@DisplayName("update all colors params")
		void testUpdateAllColors() {
			vaoGeom.addColor(COLOR_ADDED);
			vaoGeom.updateColor(new Vector4f(6f, 5f, 4f, 3f));
			assertEquals(8, vaoGeom.colors.getVBOContent().size());
			assertEquals(new Float(6), vaoGeom.colors.getVBOContent().get(0));
			assertEquals(new Float(3), vaoGeom.colors.getVBOContent().get(3));
			assertEquals(new Float(6), vaoGeom.colors.getVBOContent().get(4));
			assertEquals(new Float(3), vaoGeom.colors.getVBOContent().get(7));
		}

		@Test
		@DisplayName("update all colors params except x")
		void testUpdateColorsNotX() {
			vaoGeom.addColor(COLOR_ADDED);
			vaoGeom.updateColor(new Vector4f(-1f, 5f, 4f, 3f));
			assertEquals(8, vaoGeom.colors.getVBOContent().size());
			assertEquals(new Float(COLOR_GEOM1.get(0)), vaoGeom.colors.getVBOContent().get(0));
			assertEquals(new Float(3), vaoGeom.colors.getVBOContent().get(3));
			assertEquals(new Float(COLOR_ADDED.x), vaoGeom.colors.getVBOContent().get(4));
			assertEquals(new Float(3), vaoGeom.colors.getVBOContent().get(7));
		}

		@Test
		@DisplayName("update all colors params except y")
		void testUpdateColorsNotY() {
			vaoGeom.addColor(COLOR_ADDED);
			vaoGeom.updateColor(new Vector4f(6f, -1f, 4f, 3f));
			assertEquals(8, vaoGeom.colors.getVBOContent().size());
			assertEquals(new Float(COLOR_GEOM1.get(1)), vaoGeom.colors.getVBOContent().get(1));
			assertEquals(new Float(3), vaoGeom.colors.getVBOContent().get(3));
			assertEquals(new Float(COLOR_ADDED.y), vaoGeom.colors.getVBOContent().get(5));
			assertEquals(new Float(3), vaoGeom.colors.getVBOContent().get(7));
		}

		@Test
		@DisplayName("update all colors params except z")
		void testUpdateColorsNotZ() {
			vaoGeom.addColor(COLOR_ADDED);
			vaoGeom.updateColor(new Vector4f(6f, 5f, -1f, 3f));
			assertEquals(8, vaoGeom.colors.getVBOContent().size());
			assertEquals(new Float(COLOR_GEOM1.get(2)), vaoGeom.colors.getVBOContent().get(2));
			assertEquals(new Float(3), vaoGeom.colors.getVBOContent().get(3));
			assertEquals(new Float(COLOR_ADDED.z), vaoGeom.colors.getVBOContent().get(6));
			assertEquals(new Float(3), vaoGeom.colors.getVBOContent().get(7));
		}

		@Test
		@DisplayName("update all colors params except w")
		void testUpdateColorsNotW() {
			vaoGeom.addColor(COLOR_ADDED);
			vaoGeom.updateColor(new Vector4f(6f, 5f, 4f, -1f));
			assertEquals(8, vaoGeom.colors.getVBOContent().size());
			assertEquals(new Float(COLOR_GEOM1.get(3)), vaoGeom.colors.getVBOContent().get(3));
			assertEquals(new Float(4), vaoGeom.colors.getVBOContent().get(2));
			assertEquals(new Float(COLOR_ADDED.w), vaoGeom.colors.getVBOContent().get(7));
			assertEquals(new Float(4), vaoGeom.colors.getVBOContent().get(6));
		}
	}

}
