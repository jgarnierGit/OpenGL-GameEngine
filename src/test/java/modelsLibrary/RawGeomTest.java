package modelsLibrary;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import renderEngine.Loader;

class RawGeomTest {
	String alias2 = "alias2";
	RawGeom geom2;
	public RawGeom geom;
	public String alias = "alias1";
	public Loader loader;
	
	@BeforeEach
	void setUp() throws Exception {
		geom = Mockito.mock(RawGeom.class,Mockito.CALLS_REAL_METHODS);
		geom2 = Mockito.mock(RawGeom.class,Mockito.CALLS_REAL_METHODS);
		
		loader = Mockito.mock(Loader.class);
		geom.points = new float[] {1,1,1};
		geom.colors = new float[] {2,2,2};
		geom.dimension= 1;
		
	}
	
	@Test
	@DisplayName("Copy RawGeom must have fields if new signature reference")
	void testCopyRawValues() {
		Mockito.when(geom2.getDimension()).thenReturn(1);
		geom2.copyRawValues(geom);
		assertNotSame(geom.getColors(), geom2.getColors());
		assertNotSame(geom.getPoints(), geom2.getPoints());
	}
	
	@Test
	@DisplayName("Copy RawGeom fails if dimensions are not equals")
	void testCopyRawValuesFails() {
		Mockito.when(geom2.getDimension()).thenReturn(2);
		assertThrows(IllegalArgumentException.class, ()-> { geom2.copyRawValues(geom); });
	}
}
