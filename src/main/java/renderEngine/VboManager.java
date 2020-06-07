package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL15;

public class VboManager {
	private final Map<Integer, Integer> vboIDs;
	private final List<Integer> unmutableVboIDs;

	public VboManager() {
		vboIDs = new HashMap<>();
		unmutableVboIDs = new ArrayList<>();
	}

	public void addVbo(Integer vertexAttributeIndex, Integer vboIndex) {
		vboIDs.put(vertexAttributeIndex, vboIndex);
	}

	public void addUnmutableVbo(int vboId) {
		this.unmutableVboIDs.add(vboId);
	}
	
	public void clean() {
		for(Entry<Integer, Integer> vboIDByAttrIndex : vboIDs.entrySet()) {
			GL15.glDeleteBuffers(vboIDByAttrIndex.getValue());
		}
		for(Integer vboIDUnmutable : unmutableVboIDs) {
			GL15.glDeleteBuffers(vboIDUnmutable);
		}
	}

	public Map<Integer, Integer> getVbos() {
		return this.vboIDs;
	}

	@Override
	public String toString() {
		return "VboManager [vboIDs=" + vboIDs + ", unmutableVboIDs=" + unmutableVboIDs + "]";
	}
	
	
}
