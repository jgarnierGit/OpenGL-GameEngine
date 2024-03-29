package shaderManager;

public interface IShader {

	public void start();

	public void stop();

	public void cleanUp();

	public int getColorShaderIndex();

	public int getTextureShaderIndex();

	public int getPositionShaderIndex();
}
