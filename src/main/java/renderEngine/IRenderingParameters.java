package renderEngine;

public interface IRenderingParameters {
	/**
	 * set an alias
	 * @param alias
	 */
	public void setAlias(String alias);
	
	/**
	 * get alias
	 * @return alias
	 */
	public String getAlias();
	
	/**
	 * get destination reference alias for ordering
	 * @return
	 */
	public String getDestinationOrderAlias();
	
	/**
	 * get destination position relative to alias destination specified
	 * @return true if positioned after alias destination, false elsewhere
	 */
	public boolean isDestinationPositionAfter();
	
	/**
	 * set reference destination ordering as before
	 * @param alias
	 */
	public void renderBefore(String alias);
	
	/**
	 * set reference destination ordering as after
	 * @param alias
	 */
	public void renderAfter(String alias);
}
