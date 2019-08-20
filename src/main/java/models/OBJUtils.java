package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.mokiat.data.front.parser.OBJModel;

import models.bufferCreator.BufferCreator;
import models.bufferCreator.ColorMaterialBufferCreator;
import models.bufferCreator.ImageMaterialBufferCreator;
import models.bufferCreator.NormalBufferCreator;
import models.bufferCreator.PositionBufferCreator;

public class OBJUtils {
	private OBJModel objModel;
	private final ArrayList<Integer> indices;
	private final PositionBufferCreator positions;
	private final BufferCreator material;
	private final NormalBufferCreator normals;
	
	/**
	 * @param objModel
	 * @param materialTypes 
	 * @param mtlUtils 
	 */
	public OBJUtils(OBJModel objModel, MTLUtils mtlUtils) {
		this.objModel= objModel;
		indices = new ArrayList<>();
		positions = new PositionBufferCreator();
		normals = new NormalBufferCreator();
		
		//TODO update to set type for each material of an object
		material = mtlUtils.getMaterials().get(0).getType() == MaterialType.IMAGE ? new ImageMaterialBufferCreator() : new ColorMaterialBufferCreator();
		OBJDataReferenceUtils objDataReferenceUtils = new OBJDataReferenceUtils(objModel,mtlUtils);

		objModel.getObjects().forEach(obj -> {
			obj.getMeshes().forEach(mesh -> {
				int materialIndex = mtlUtils.getMaterials().indexOf(mtlUtils.getMaterial(mesh.getMaterialName()));
				mesh.getFaces().forEach(face -> {
					face.getReferences().forEach(ref -> {
						if(ref.hasVertexIndex()) {
							// ref.vertexIndex only contains original ids.
							if(indices.contains(ref.vertexIndex)) {
								if(objDataReferenceUtils.isConfigRegistered(ref)) {
									indices.add(ref.vertexIndex);
								}
								else {
									objDataReferenceUtils.addChildConfig(ref, materialIndex);
									indices.add(ref.vertexIndex);
								}
							}
							else {
								indices.add(ref.vertexIndex);
								objDataReferenceUtils.addVertex(ref, materialIndex);
							}
						}
						else {
							System.out.println(obj.getName() +" has no vertexIndex");
						}
					});
				});
			});
		});
		positions.setContent(objDataReferenceUtils);
		normals.setContent(objDataReferenceUtils);
		material.setContent(objDataReferenceUtils);
		/**System.out.println(objModel.getObjects().get(0).getName() +" :");
		System.out.println(indices);
		System.out.println(positions.getContent());
		System.out.println(material.getContent());
		System.out.println(normals.getContent());**/
	}

	public ArrayList<Integer> getIndices() {
		return this.indices;
	}

	public BufferCreator getPositions() {
		return this.positions;
	}

	public BufferCreator getMaterial() {
		return this.material;
	}

	public BufferCreator getNormals() {
		return this.normals;
	}
	
	public OBJModel getObjModel() {
		return objModel;
	}
}
