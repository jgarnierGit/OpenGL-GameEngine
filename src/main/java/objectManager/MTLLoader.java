package objectManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjglx.util.vector.Vector3f;
import org.newdawn.slick.opengl.TextureLoader;

import models.SimpleTextureContainer;
import models.TextureContainer;
import models.TextureData;

public class MTLLoader {
	 /**
     * @param file the file to be loaded
     * @return the loaded <code>Shader</code>
	 * @throws IOException 
     */
    public static TextureContainer loadModel(File file) throws IOException {
    	return MTLLoader.loadModel(new Scanner(file));
    }

    /**
     * @param stream the stream to be loaded
     * @return the loaded <code>Shader</code>
     * @throws IOException 
     */
    public static TextureContainer loadModel(InputStream stream) throws IOException {
    	return MTLLoader.loadModel(new Scanner(stream));
    }

    /**
     * TODO finish parser for multi texture and mixed textures and colors.
     * @param sc the <code>Shader</code> to be loaded
     * @return the loaded <code>Shader</code>
     * @throws IOException 
     */
    public static TextureContainer loadModel(Scanner sc) throws IOException {
    	ArrayList<Integer> textureIds = new ArrayList<>();
    	ArrayList<Vector3f> diffuseColor = new ArrayList<>();
        while (sc.hasNextLine()) {
            String ln = sc.nextLine();
            if (ln == null || ln.equals("") || ln.startsWith("#")) {
            } else {
                String[] split = ln.split(" ");
                String lineType = split[0];
                String[] lineContent = ArrayUtils.remove(split, 0);
                switch (lineType) {
                    case "kd": // diffuse color
                    	diffuseColor.add(new Vector3f(Float.parseFloat(lineContent[0]),
                        		Float.parseFloat(lineContent[1]),
                        		Float.parseFloat(lineContent[2])));
                        break;
                    case "map_Kd":
                		try {
                			textureIds.add(TextureLoader.getTexture("PNG", new FileInputStream(lineContent[0])).getTextureID());
                		} catch(FileNotFoundException e1) {
                			System.err.println("File not found "+ lineContent[0] +" specified in MTL file.");
                		}
                    	break;
                    default:
                        System.err.println("[SHADER] Unknown Line: " + ln);
                }
            }
        }
        sc.close();
    	TextureContainer textureData = SimpleTextureContainer.create()
    			.setTexture(textureIds)
    			.build();
        return textureData;
    }
}
