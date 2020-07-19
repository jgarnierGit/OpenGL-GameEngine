package models.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

class OBJContentTest {
	OBJContent objContent;
	String alias;

	@BeforeEach
	void setUp() throws Exception {
		alias = "test";
		objContent = OBJContent.createEmpty(alias,-1,-1,-1,-1);
	}

	@Test
	@DisplayName("Empty ObjContent must not contains null")
	void testEmptyObjContent() {
		assertNotNull(objContent.getIndices());
		assertNotNull(objContent.getPositions());
		assertNotNull(objContent.getMaterials());
		assertNotNull(objContent.getNormals());
		assertEquals(alias, objContent.alias);
	}

	@Test
	@DisplayName("Empty indices as primitive array")
	void testEmptyIndicesAsPrimitiveArray() {
		assertNotNull(objContent.getIndicesAsPrimitiveArray());
		assertEquals(0, objContent.getIndicesAsPrimitiveArray().length);
	}

	@Test
	void testGetVBOs() {
		assertEquals(3, objContent.getVBOs().size());
	}

	@Test
	@DisplayName("Copy over empty ObjContent must not be the same")
	void testCopyEmpty() {
		OBJContent content2 = OBJContent.copy(objContent);
		assertNotSame(content2.getMaterials(), objContent.getMaterials());
		assertNotSame(content2.getIndices(), objContent.getIndices());
		assertNotSame(content2.getNormals(), objContent.getNormals());
		assertNotSame(content2.getPositions(), objContent.getPositions());
		assertEquals(alias, objContent.alias);
	}

	@Nested
	@DisplayName("Data ObjContent")
	class ObjContentWithData {

		List<Integer> indices = Arrays.asList(1, 2, 3);
		List<Vector3f> positions2 = Arrays.asList(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), new Vector3f(2, 2, 2));
		List<Vector3f> normals2 = Arrays.asList(new Vector3f(1, 0, 0), new Vector3f(1, 1, 1), new Vector3f(0, 0, 1));

		@Nested
		@DisplayName("With Color list")
		class WithColorList {
			MaterialContent materials = MaterialContent.createColorContent(0, Arrays.asList(new Vector4f(0, 0, 0, 0)));

			@BeforeEach
			void setUp() throws Exception {
				//objContent = OBJContent.create(indices, positions2, normals2, materials);
			}

			@Test
			void test() {
				fail("Not yet implemented");
			}
		}

	}
}
