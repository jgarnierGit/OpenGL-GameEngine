package objectManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;
import org.newdawn.slick.opengl.TextureLoader;

import models.SimpleTextureContainer;
import models.TextureContainer;

public class MTLLoader {
	 /**
     * @param file the file to be loaded
     * @return the loaded <code>Shader</code>
	 * @throws IOException 
     */
    public static TextureContainer loadModel(File file, Logger logger) throws IOException {
    	return MTLLoader.loadModel(new Scanner(file),logger);
    }

    /**
     * @param stream the stream to be loaded
     * @return the loaded <code>Shader</code>
     * @throws IOException 
     */
    public static TextureContainer loadModel(InputStream stream, Logger logger) throws IOException {
    	return MTLLoader.loadModel(new Scanner(stream), logger);
    }

    /**
     * TODO finish parser for multi texture and mixed textures and colors.
     * @param sc the <code>Shader</code> to be loaded
     * @return the loaded <code>Shader</code>
     * @throws IOException 
     */
    public static TextureContainer loadModel(Scanner sc, Logger logger) throws IOException {
    	HashMap<String, ArrayList<Integer>> textureIdsMTL = new HashMap<>();
    	HashMap<String, ArrayList<Vector4f>> diffuseColorMTL = new HashMap<>();
    	String currentMTL = "";
        while (sc.hasNextLine()) {
            String ln = sc.nextLine();
            if (ln == null || ln.equals("") || ln.startsWith("#")) {
            } else {
                String[] split = ln.split(" ");
                String lineType = split[0];
                String[] lineContent = ArrayUtils.remove(split, 0);
                switch (lineType) {
                	case "newmtl":
                		currentMTL = lineContent[0];
                		textureIdsMTL.put(lineContent[0], new ArrayList<>());
                		diffuseColorMTL.put(lineContent[0], new ArrayList<>());
                	break;
                    case "Kd": // diffuse color
                    	diffuseColorMTL.get(currentMTL).add(new Vector4f(Float.parseFloat(lineContent[0]),
                        		Float.parseFloat(lineContent[1]),
                        		Float.parseFloat(lineContent[2]),
                        		1f));
                        break;
                    case "map_Kd":
                		try {
                			textureIdsMTL.get(currentMTL).add(TextureLoader.getTexture("PNG", new FileInputStream(lineContent[0])).getTextureID());
                		} catch(FileNotFoundException e1) {
                			System.err.println("["+ logger.getName() +"] File not found "+ lineContent[0] +" specified in MTL file. ");
                		}
                    	break;
                    default:
                        System.err.println("[SHADER] Unknown Line: " + ln);
                }
            }
        }
        sc.close();
    	TextureContainer textureData = SimpleTextureContainer.create()
    			.setColors(diffuseColorMTL)
    			.addTextureIDs(textureIdsMTL) //TODO builder is not appropriate since i may instanciate TextureContainer with textures (int) or colors (Vector4f)
    			.build();
        return textureData;
    }
}
