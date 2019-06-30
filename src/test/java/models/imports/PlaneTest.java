package models.imports;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import models.Model3D;
import renderEngine.Loader;

@RunWith(MockitoJUnitRunner.class)
class PlaneTest {
	@Mock
	Loader loader = new Loader();
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * TODO not working if mtl texture path is correcty configured, 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	void testConstructorSuccess() throws FileNotFoundException, IOException {
		//Mockito.when(loader.load3DContainerToVAO(container)).thenReturn(0);
		Model3D testModel = new Plane(loader);
		assertNotNull(testModel);
		//Mockito.verify(loader).load3DContainerToVAO(container);

	}
	
	@Test
	void testConstructorFail() throws FileNotFoundException, IOException {
		assertThrows(NullPointerException.class, () -> {new Plane(null); });
	}

}
