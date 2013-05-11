package infrastructure.interfaces;

import processing.core.PVector;

/**
 * Drawable component
 * @author Gadi
 *
 */
public interface IDrawable 
{
	
	/**
	 * Draw this object
	 * @param ellapsedTime - Time (in milliseconds) since last draw
	 */
	void draw(long ellapsedTime);
	
	/**
	 * @return width of object
	 */
	float getWidth();
	
	/**
	 * @return height of object
	 */
	float getHeight();
	
	/**
	 * @return center of object
	 */
	PVector getCenter();

}
