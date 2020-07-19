package utils;

import java.util.ArrayList;
import java.util.List;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

public class GeomUtils {

	private GeomUtils() {
		// hidden
	}

	public static List<Vector3f> createVector3fList(List<Float> content) {
		if (content.size() % 3 != 0) {
			throw new IllegalArgumentException("expected a content of dimension 3, got " + content.size());
		}
		ArrayList<Vector3f> pos3f = new ArrayList<>();
		for (int i = 0; i < content.size(); i += 3) {
			pos3f.add(new Vector3f(content.get(i), content.get(i + 1), content.get(i + 2)));
		}
		return pos3f;
	}

	public static List<Vector4f> createVector4fList(List<Float> content) {
		if (content.size() % 4 != 0) {
			throw new IllegalArgumentException("expected a content of dimension 4, got " + content.size());
		}
		ArrayList<Vector4f> pos4f = new ArrayList<>();
		for (int i = 0; i < content.size(); i += 4) {
			pos4f.add(new Vector4f(content.get(i), content.get(i + 1), content.get(i + 2), content.get(i + 3)));
		}
		return pos4f;
	}

	public static List<Vector2f> createVector2fList(List<Float> content) {
		if (content.size() % 2 != 0) {
			throw new IllegalArgumentException("expected a content of dimension 2, got " + content.size());
		}
		ArrayList<Vector2f> pos2f = new ArrayList<>();
		for (int i = 0; i < content.size(); i += 2) {
			pos2f.add(new Vector2f(content.get(i), content.get(i + 1)));
		}
		return pos2f;
	}

	public static List<Float> createListFromVector4f(List<Vector4f> content) {
		List<Float> contentFloat = new ArrayList<>();
		for (Vector4f v : content) {
			contentFloat.add(v.x);
			contentFloat.add(v.y);
			contentFloat.add(v.z);
			contentFloat.add(v.w);
		}
		return contentFloat;
	}

	public static List<Float> createListFromVector3f(List<Vector3f> content) {
		List<Float> contentFloat = new ArrayList<>();
		for (Vector3f v : content) {
			contentFloat.add(v.x);
			contentFloat.add(v.y);
			contentFloat.add(v.z);
		}
		return contentFloat;
	}

	public static List<Float> createListFromVector2f(List<Vector2f> content) {
		List<Float> contentFloat = new ArrayList<>();
		for (Vector2f v : content) {
			contentFloat.add(v.x);
			contentFloat.add(v.y);
		}
		return contentFloat;
	}
}
