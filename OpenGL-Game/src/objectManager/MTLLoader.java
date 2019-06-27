package objectManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;
import org.newdawn.slick.opengl.TextureLoader;

import models.TextureContainer;

public class MTLLoader {
	 /**
     * @param file the file to be loaded
     * @return the loaded <code>Shader</code>
     * @throws java.io.FileNotFoundException thrown if the Shader file is not found
     */
    public static TextureContainer loadModel(File file) throws FileNotFoundException {
    	return MTLLoader.loadModel(new Scanner(file));
    }

    /**
     * @param stream the stream to be loaded
     * @return the loaded <code>Shader</code>
     */
    public static TextureContainer loadModel(InputStream stream) {
    	return MTLLoader.loadModel(new Scanner(stream));
    }

    /**
     * @param sc the <code>Shader</code> to be loaded
     * @return the loaded <code>Shader</code>
     */
    public static TextureContainer loadModel(Scanner sc) {
    	TextureContainer textureData = new TextureContainer();
        while (sc.hasNextLine()) {
            String ln = sc.nextLine();
            if (ln == null || ln.equals("") || ln.startsWith("#")) {
            } else {
                String[] split = ln.split(" ");
                String lineType = split[0];
                String[] lineContent = ArrayUtils.remove(split, 0);
                switch (lineType) {
                    case "kd": // diffuse color
                    	textureData.addDiffuseColor(Float.parseFloat(lineContent[0]),
                        		Float.parseFloat(lineContent[1]),
                        		Float.parseFloat(lineContent[2]));
                        break;
                    case "map_Kd":
                		try {
                			textureData.addPNGTexture(TextureLoader.getTexture("PNG", new FileInputStream(lineContent[0])));
                		} catch(FileNotFoundException e1) {
                			System.err.println("File not found "+ lineContent[0]);
                		}
                		catch (IOException e) {
                			e.printStackTrace();
                		}
                    	break;
                    default:
                        System.err.println("[SHADER] Unknown Line: " + ln);
                }
            }
        }
        sc.close();
        return textureData;
    }
}
