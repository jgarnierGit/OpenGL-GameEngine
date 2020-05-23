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

import entities.EntityTutos;
import renderEngine.Draw2DRenderer;
import renderEngine.Loader;
import renderEngine.RenderingParameters;

class SimpleGeom2DTest {

	public SimpleGeom2D geom;
	public String alias = "alias1";
	public Loader loader;
	@Mock
	public Draw2DRenderer draw2DRenderer;

	@BeforeEach
	void setUp() throws Exception {
		loader = Mockito.mock(Loader.class);
		
		geom = new SimpleGeom2D(loader, draw2DRenderer, alias);
		Mockito.when(loader.loadToVAO(geom.points, geom.dimension)).thenReturn(1);
	}
	
	@Nested
	class emptyGeom {
		
		@Test
		@DisplayName("Empty geom should be empty")
		void testEmptyGeom(){
			assertEquals(0, geom.colors.length);
			assertEquals(0, geom.points.length);
			assertNotNull(geom.renderingParameters);
		}
		
		@Test
		@DisplayName("Adding point copying default color")
		void testAddPoint() {
			geom.addPoint(new Vector2f(1, 1));
			assertEquals(4, geom.colors.length);
			assertEquals(2, geom.points.length);
			assertEquals(1.0f, geom.colors[0]);
		}
		
		@Test
		@DisplayName("Adding a point with color")
		void testaddPointWithColor() {
			geom.addPoint(new Vector2f(1, 1), new Vector4f(2,2,2,2));
			assertEquals(4, geom.colors.length);
			assertEquals(2, geom.points.length);
			assertEquals(2, geom.colors[0]);
		}
		
		@Test
		@DisplayName("Adding a vector3f throws exception")
		void testaddWrongPoint() {
			assertThrows(IllegalArgumentException.class, ()-> { geom.addPoint(new Vector3f(1, 1,1)); });
		}
	}
	
	@Nested
	class presetGeom {
		@BeforeEach
		void setUp() throws Exception {
			RenderingParameters params = geom.getRenderingParameters();
			params.addEntity(Mockito.mock(EntityTutos.class),new Vector3f(0, 0,0), 1, 1, 1, 1);
			geom.addPoint(new Vector2f(1, 1), new Vector4f(1, 1, 1, 1));

		}

		@Test
		void testInstanciationOk() {
			assertEquals(4, geom.colors.length);
			assertEquals(2, geom.points.length);
		}

		@Test
		void testReset() {
			geom.reset();
			assertEquals(0, geom.colors.length);
			assertEquals(0, geom.points.length);
		}
		
		@Test
		@DisplayName("Adding a point copying previous color")
		void testaddPoint() {
			geom.addPoint(new Vector2f(1, 1));
			assertEquals(8, geom.colors.length);
			assertEquals(4, geom.points.length);
		}
		
		@Test
		@DisplayName("Adding a point with color")
		void testaddPointWithColor() {
			geom.addPoint(new Vector2f(1, 1), new Vector4f(2,2,2,2));
			assertEquals(8, geom.colors.length);
			assertEquals(4, geom.points.length);
		}
		
		@Test
		@DisplayName("get Vectors vertices list")
		void testgetVertices() {
			assertEquals(1, geom.getVertices().size());
		}
	}
	

}
