package models.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.NotActiveException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import renderEngine.DrawRendererCommon;
import renderEngine.Loader;

class VAOGeomTest {
	private VAOGeom vaoGeom;
	private Loader loader;

	@BeforeEach
	void setUp() throws Exception {
		loader = Mockito.mock(Loader.class);
		vaoGeom = VAOGeom.create(loader, Mockito.mock(DrawRendererCommon.class));
	}

	@Test
	@DisplayName("Create VAOGeom")
	void TestCreate() {
		assertNotNull(vaoGeom.textureIdsLoaded);
		assertNotNull(vaoGeom.loader);
		assertNotNull(vaoGeom.drawRenderer);
		assertNull(vaoGeom.vaoId);
	}

	@Test
	@DisplayName("get vaoId throws exception if null")
	void TestGetVaoIdNull() {
		assertThrows(IllegalStateException.class, () -> {
			vaoGeom.getVaoId();
		});
	}

	@Test
	@DisplayName("Copy RawGeom must have fields if new signature reference")
	void testCopy() {
		VAOGeom copyGeom = VAOGeom.copy(vaoGeom);
		assertNotSame(vaoGeom.getTextures(), copyGeom.getTextures());
		assertNull(copyGeom.vaoId);
		assertEquals(0, vaoGeom.textureIdsLoaded.size());
		assertSame(vaoGeom.loader, copyGeom.loader);
		assertSame(vaoGeom.getRenderer(), copyGeom.getRenderer());
	}

	@Test
	@DisplayName("Clear must empty VBOs")
	void testClear() {
		assertThrows(NotActiveException.class, () -> {
			vaoGeom.clear();
		});

	}
}
