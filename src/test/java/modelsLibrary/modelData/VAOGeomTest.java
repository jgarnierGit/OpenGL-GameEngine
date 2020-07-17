package modelsLibrary.modelData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import models.data.OBJContent;
import models.data.VAOGeom;
import renderEngine.DrawRendererCommon;
import renderEngine.Loader;

class VAOGeomTest {
	private VAOGeom vaoGeom;
	private Loader loader;

	@BeforeEach
	void setUp() throws Exception {
		loader = Mockito.mock(Loader.class);
		vaoGeom = VAOGeom.create(loader, Mockito.mock(DrawRendererCommon.class), 3);
		Mockito.when(loader.loadToVAO(vaoGeom.getPositions())).thenReturn(1);
		vaoGeom.objContent = OBJContent.createEmpty(1);
	}

	@Test
	@DisplayName("Create VAOGeom")
	void TestCreate() {
		assertNotNull(vaoGeom.objContent);
	}

	@Test
	@DisplayName("Copy RawGeom must have fields if new signature reference")
	void testCopy() {
		VAOGeom copyGeom = VAOGeom.copy(vaoGeom);
		assertNotSame(vaoGeom.objContent, copyGeom);
	}

	@Test
	@DisplayName("Clear must empty VBOs")
	void testClear() {
		OBJContent content = vaoGeom.objContent;
		vaoGeom.clear();
		assertNotSame(content, vaoGeom.objContent);
		assertTrue(vaoGeom.getPositions().getContent().isEmpty());
		assertEquals(content.getDimension(), vaoGeom.objContent.getDimension());
	}

}
