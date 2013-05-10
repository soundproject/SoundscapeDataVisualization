package infrastructure.interfaces;

/**
 * Drawable component
 * @author Gadi
 *
 */
public interface IDrawable {
	
	/**
	 * Draw this object
	 * @param ellapsedTime - Time (in milliseconds) since last draw
	 */
	void draw(long ellapsedTime);

}
