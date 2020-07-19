package models.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import utils.GeomUtils;

class VBOContentTest {

	VBOContent vboContent;

	@Nested
	class emptyVBO {
		@BeforeEach
		void setUp() throws Exception {
			vboContent = VBOContent.createEmpty(-1);
		}

		@Test
		@DisplayName("Empty VBOContent creator")
		void testInitEmpty() {
			assertNotNull(vboContent);
			assertEquals(0, vboContent.getDimension());
			assertEquals(-1, vboContent.getShaderInputIndex());
			assertNotNull(vboContent.getContent());
			assertEquals(0, vboContent.getContent().size());
		}

		@Test
		@DisplayName("Empty VBOContent creator must give status empty")
		void testIsEmptyTrue() {
			assertTrue(vboContent.isEmpty());
		}
	}

	@Nested
	@DisplayName("Create VBO of dimension 2")
	class VBO2f {
		@BeforeEach
		void setUp() throws Exception {
			vboContent = VBOContent.create2f(1, GeomUtils.createVector2fList(Arrays.asList(2f, 2f, 1f, 1f)));
		}

		@Test
		@DisplayName("2f VBOContent creator")
		void testInit2f() {
			assertNotNull(vboContent);
			assertEquals(2, vboContent.getDimension());
			assertEquals(1, vboContent.getShaderInputIndex());
			assertNotNull(vboContent.getContent());
			assertEquals(4, vboContent.getContent().size());
		}

		@Test
		@DisplayName("2f VBOContent must not give status empty")
		void testIsEmptyFalse() {
			assertFalse(vboContent.isEmpty());
		}

		@Test
		@DisplayName("2f VBOContent returns arrays of primitive float")
		void testgetAsPrimitiveArray() {
			float[] array = vboContent.getContentAsPrimitiveArray();
			assertEquals(vboContent.getContent().size(), array.length);
		}

		@Test
		@DisplayName("VBOContent replace content with 2f")
		void testReplaceContentSameDimension() {
			vboContent.setContent2f(GeomUtils.createVector2fList(Arrays.asList(3f, 3f, 4f, 4f)));
			assertNotNull(vboContent.getContent());
			assertEquals(4, vboContent.getContent().size());
			assertEquals((Float) 3f, vboContent.getContent().get(0));
		}

		@Test
		@DisplayName("VBOContent replace content with 3f")
		void testReplaceContent3fDimension() {
			vboContent.setContent3f(GeomUtils.createVector3fList(Arrays.asList(3f, 3f, 3f, 4f, 4f, 4f)));
			assertNotNull(vboContent.getContent());
			assertEquals(6, vboContent.getContent().size());
			assertEquals((Float) 3f, vboContent.getContent().get(0));
		}

		@Test
		@DisplayName("VBOContent replace content with 4f")
		void testReplaceContent4fDimension() {
			vboContent.setContent4f(GeomUtils.createVector4fList(Arrays.asList(3f, 3f, 3f, 3f, 4f, 4f, 4f, 4f)));
			assertNotNull(vboContent.getContent());
			assertEquals(8, vboContent.getContent().size());
			assertEquals((Float) 3f, vboContent.getContent().get(0));
		}
	}

	@Nested
	@DisplayName("Create VBO of dimension 3")
	class VBO3f {
		@BeforeEach
		void setUp() throws Exception {
			vboContent = VBOContent.create3f(1, GeomUtils.createVector3fList(Arrays.asList(2f, 2f, 2f, 1f, 1f, 1f)));
		}

		@Test
		@DisplayName("3f VBOContent creator")
		void testInit3f() {
			assertNotNull(vboContent);
			assertEquals(3, vboContent.getDimension());
			assertEquals(1, vboContent.getShaderInputIndex());
			assertNotNull(vboContent.getContent());
			assertEquals(6, vboContent.getContent().size());
		}

		@Test
		@DisplayName("3f VBOContent creator must not give status empty")
		void testIsEmptyFalse() {
			assertFalse(vboContent.isEmpty());
		}
	}

	@Nested
	@DisplayName("Create VBO of dimension 4")
	class VBO4f {
		@BeforeEach
		void setUp() throws Exception {
			vboContent = VBOContent.create4f(1,
					GeomUtils.createVector4fList(Arrays.asList(2f, 2f, 2f, 2f, 1f, 1f, 1f, 1f)));
		}

		@Test
		@DisplayName("4f VBOContent creator")
		void testInit4f() {
			assertNotNull(vboContent);
			assertEquals(4, vboContent.getDimension());
			assertEquals(1, vboContent.getShaderInputIndex());
			assertNotNull(vboContent.getContent());
			assertEquals(8, vboContent.getContent().size());
		}

		@Test
		@DisplayName("4f VBOContent creator must not give status empty")
		void testIsEmptyFalse() {
			assertFalse(vboContent.isEmpty());
		}
	}

}
