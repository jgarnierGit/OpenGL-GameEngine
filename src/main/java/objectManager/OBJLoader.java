
/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Matthew 'siD' Van der Bijl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package objectManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import models.Container3D;
import models.GeneratedModelContainer;

import org.apache.commons.lang3.ArrayUtils;

/**
 * OBJloader class. Loads in Wavefront .obj file in to the program.
 */
public class OBJLoader {

	/**
	 * @param file the file to be loaded
	 * @return the loaded <code>Obj</code>
	 * @throws java.io.FileNotFoundException thrown if the Obj file is not found
	 */
	public static Container3D loadModel(File file) throws FileNotFoundException {
		return loadModel(new FileReader(file));
	}

	/**
	 * TODO implements factory (with abstract if needed) to refactor Container3D creation
	 * @param sc the <code>Obj</code> to be loaded
	 * @return the loaded <code>Obj</code>
	 */
	public static Container3D loadModel(FileReader sc) {
		ArrayList<Integer> vertexIndices = new ArrayList<>();
		ArrayList<Vector3f> vertices = new ArrayList<>();
		ArrayList<Vector2f> textures = new ArrayList<>();

		ArrayList<Vertex> loaderVertexFaces= new ArrayList<>();
		ArrayList<Integer> loaderNormalIndices = new ArrayList<>();
		ArrayList<Integer> loaderTextureIndices = new ArrayList<>();

		ArrayList<Vector3f> normals = new ArrayList<>();

		BufferedReader reader = new BufferedReader(sc);
		String ln;
		try {
			while (true) {
				ln = reader.readLine();
				if (ln == null || ln.equals("") || ln.startsWith("#")) {
				} else {
					String[] split = ln.split(" ");
					String lineType = split[0];
					String[] lineContent = ArrayUtils.remove(split, 0);
					if(lineType.contentEquals("v")) {// geometric vertices
						vertices.add(new Vector3f(
								Float.parseFloat(lineContent[0]),
								Float.parseFloat(lineContent[1]),
								Float.parseFloat(lineContent[2])
								));
					}
					else if(lineType.contentEquals("vn")) { // vertex normals
						normals.add(new Vector3f(
								Float.parseFloat(lineContent[0]),
								Float.parseFloat(lineContent[1]),
								Float.parseFloat(lineContent[2])
								));
					}
					else if(lineType.contentEquals("vt")) { //texture coordinates
						textures.add(new Vector2f(
								Float.parseFloat(lineContent[0]),
								Float.parseFloat(lineContent[1])
								));
					}
					else if(lineType.contentEquals("f")) { //Polygonal face element
						//TODO generalize to any polygone.
						break;
					}
					else if(lineType.contentEquals("s")) { // TODO not used yet
						//  model.setSmoothShadingEnabled(!ln.contains("off"));
					}
					else { 
						System.err.println("[OBJ] Unknown Line: " + ln);
					}
				}
			}
			while (ln != null && ln.startsWith("f ")){
				String[] split = ln.split(" ");
				String[] lineContent = ArrayUtils.remove(split, 0);
				for(String vertexSetup : lineContent) {
					String vertice = vertexSetup.split("/")[0];
					String texture = vertexSetup.split("/")[1];
					String normal = vertexSetup.split("/")[2];

					if(vertice.isEmpty() || normal.isEmpty()) {
						throw new IllegalArgumentException("Face has a missed parameter. ("+ vertexSetup +")");
					}
					Vertex vertex;
					if(texture.isEmpty()) {
						vertex = new Vertex(Integer.parseInt(vertice) -1, 0, Integer.parseInt(normal) -1);
					}
					else {
						vertex = new Vertex(Integer.parseInt(vertice) -1, Integer.parseInt(texture) -1, Integer.parseInt(normal) -1);
					}
					Optional<Vertex> registeredVertex = findVertexAlreadyRegistered(loaderVertexFaces,vertex);
					if(!registeredVertex.isPresent()) {
						loaderVertexFaces.add(vertex);
						vertexIndices.add(vertex.getIndiceIndex());
						loaderNormalIndices.add(vertex.getNormalIndex());
						loaderTextureIndices.add(loaderVertexFaces.indexOf(vertex));
					}
					else {
						checkForDuplicatedVertex(loaderVertexFaces,registeredVertex.get(),vertex,vertexIndices,vertices);
					}
				}
				ln = reader.readLine();
			}
			sc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO use below for unit testing
		ArrayList<Vector2f> texturesTemp = new ArrayList<>();
		texturesTemp.add(new Vector2f(0,0));
		texturesTemp.add(new Vector2f(1f,0));
		texturesTemp.add(new Vector2f(0,1));
		texturesTemp.add(new Vector2f(1f,1));
		
		ArrayList<Vector3f> verticesFloat = new ArrayList<>();
		verticesFloat.add(new Vector3f(-0.5f,0.5f,0));
		verticesFloat.add(new Vector3f(-0.5f,-0.5f,0));
		verticesFloat.add(new Vector3f(0.5f,-0.5f,0));
		verticesFloat.add(new Vector3f(0.5f,0.5f,0));
		
				verticesFloat.add(new Vector3f(-0.5f,0.5f,1));
				verticesFloat.add(new Vector3f(-0.5f,-0.5f,1));
				verticesFloat.add(new Vector3f(0.5f,-0.5f,1));
				verticesFloat.add(new Vector3f(0.5f,0.5f,1));				

				verticesFloat.add(new Vector3f(0.5f,0.5f,0));
				verticesFloat.add(new Vector3f(0.5f,-0.5f,0));
				verticesFloat.add(new Vector3f(0.5f,-0.5f,1));
				verticesFloat.add(new Vector3f(0.5f,0.5f,1));

				verticesFloat.add(new Vector3f(-0.5f,0.5f,0));
				verticesFloat.add(new Vector3f(-0.5f,-0.5f,0));
				verticesFloat.add(new Vector3f(-0.5f,-0.5f,1));
				verticesFloat.add(new Vector3f(-0.5f,0.5f,1));

				verticesFloat.add(new Vector3f(-0.5f,0.5f,1));
				verticesFloat.add(new Vector3f(-0.5f,0.5f,0));
				verticesFloat.add(new Vector3f(0.5f,0.5f,0));
				verticesFloat.add(new Vector3f(0.5f,0.5f,1));

				verticesFloat.add(new Vector3f(-0.5f,-0.5f,1));
				verticesFloat.add(new Vector3f(-0.5f,-0.5f,0));
				verticesFloat.add(new Vector3f(0.5f,-0.5f,0));
				verticesFloat.add(new Vector3f(0.5f,-0.5f,1));
				
				
		
		ArrayList<Vector2f> textureCoords = new ArrayList<>();
		textureCoords.add(new Vector2f(0,0));
		textureCoords.add(new Vector2f(0,1));
		textureCoords.add(new Vector2f(1,1));
		textureCoords.add(new Vector2f(1,0));

		textureCoords.add(new Vector2f(0,0));
		textureCoords.add(new Vector2f(0,1));
		textureCoords.add(new Vector2f(1,1));
		textureCoords.add(new Vector2f(1,0));

		textureCoords.add(new Vector2f(0,0));
		textureCoords.add(new Vector2f(0,1));
		textureCoords.add(new Vector2f(1,1));
		textureCoords.add(new Vector2f(1,0));
		
		textureCoords.add(new Vector2f(0,0));
		textureCoords.add(new Vector2f(0,1));
		textureCoords.add(new Vector2f(1,1));
		textureCoords.add(new Vector2f(1,0));
		
		textureCoords.add(new Vector2f(0,0));
		textureCoords.add(new Vector2f(0,1));
		textureCoords.add(new Vector2f(1,1));
		textureCoords.add(new Vector2f(1,0));
		
		textureCoords.add(new Vector2f(0,0));
		textureCoords.add(new Vector2f(0,1));
		textureCoords.add(new Vector2f(1,1));
		textureCoords.add(new Vector2f(1,0));

		
		int[] indices = {
				0,1,3,	
				3,1,2,	
				4,5,7,
				7,5,6,
				8,9,11,
				11,9,10,
				12,13,15,
				15,13,14,	
				16,17,19,
				19,17,18,
				20,21,23,
				23,21,22

		};
		
		Container3D importedModel = new GeneratedModelContainer(vertexIndices,vertices, getOrderedVectors(textures,loaderVertexFaces, Vertex::getTextureIndex), getOrderedVectors(normals,loaderVertexFaces, Vertex::getNormalIndex));
		return importedModel;
	}

	private static void checkForDuplicatedVertex(ArrayList<Vertex> vertexFaces, Vertex currentVertex, Vertex vertexToAdd, ArrayList<Integer> indices, ArrayList<Vector3f> vertices) {
		if(vertexToAdd.hasSameConfig(currentVertex)) {
			indices.add(currentVertex.getIndiceIndex());
		}
		else {
			Optional<Vertex> previousVertex = currentVertex.getPreviousVertex();
			if(previousVertex.isPresent()) {
				checkForDuplicatedVertex(vertexFaces,previousVertex.get(),vertexToAdd,indices,vertices);
			}
			else {
				vertexFaces.add(vertexToAdd);
				Vector3f previousPoint = vertices.get(vertexToAdd.getIndiceIndex());
				vertices.add(new Vector3f(previousPoint.getX(), previousPoint.getY(), previousPoint.getZ()));
				vertexToAdd.setNewIndice(vertices.size() -1);
				indices.add(vertices.size() -1);
				currentVertex.setPreviousVertex(vertexToAdd);
			}
		}
	}

	private static Optional<Vertex> findVertexAlreadyRegistered(List<Vertex> vertexList, Vertex currentVertex) {
		Optional<Vertex> faceWithRegisteredVertex = vertexList.stream().filter(vertex -> vertex.getIndiceIndex() == currentVertex.getIndiceIndex()).findFirst();
		return faceWithRegisteredVertex;
	}

	/**
	 * 
	 * @param coordinates list of {Vector} to order following vertexOrderedByIndices order
	 * @param vertexOrderedByIndices list of {Vertex} already ordered for OpenGL rendering
	 * @param method {Vertex} attribute getter to process ordering
	 * @return <T> List of {Vector} ordered
	 */
	private static <T> ArrayList<T> getOrderedVectors(List<T> coordinates, ArrayList<Vertex> vertexOrderedByIndices, Function<Vertex, Integer> method) {
		ArrayList<T> orderedVectors = new ArrayList<>();
		if(coordinates.isEmpty()) {
			return orderedVectors;
		}
		
		List<Integer> sortedIndexes = vertexOrderedByIndices.stream().sorted(Comparator.comparingInt(Vertex::getIndiceIndex)).map(vertex -> method.apply(vertex)).collect(Collectors.toList());
		for(Integer index : sortedIndexes) {
			orderedVectors.add(coordinates.get(index));
		}
		return orderedVectors;
	}
}
