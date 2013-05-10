package infrastructure.interfaces;

/**
 * Updateable component
 * @author Gadi
 *
 */
public interface IUpdateable {
	
	/**
	 * Update this object
	 * @param ellapsedTime time in milliseconds since last update
	 */
	void update(long ellapsedTime);
	
}
