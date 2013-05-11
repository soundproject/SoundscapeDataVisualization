package infrastructure;

import infrastructure.interfaces.IDeathListener;

import java.awt.Color;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

/**
 * Utility class extending PApplet functionality
 * by adding easier time tracking between draw or update cycles
 * Also has methods for adding and removing components to be
 * updated and drawn automatically as needed
 * @author Gadi
 *
 */
public class ManagedPApplet extends PApplet implements IDeathListener 
{
	private static final float ZOOM_BAR_TOP = 100;
	private static final float ZOOM_BAR_BOTTOM = 200;

	private final ArrayList<Component> m_ComponentsToAdd = new ArrayList<Component>();
	private final ArrayList<Component> m_ComponentsToRemove = new ArrayList<Component>();
	private final ArrayList<Component> m_Components = new ArrayList<Component>();
	protected int m_PrevUpdateTime;
	protected int m_PrevDrawTime;
	

	private float m_zoomFactor;
	private float m_MinZoomFactor = 1.0f;
	private float m_MaxZoomFactor = 50.0f;
	private float m_ZoomPercent = 0;
	private PVector m_PrevOffset;
	private PVector m_PreviousMouseLocation;
	private PVector m_Offset;
	private Color m_BackgroundColor = Color.black;
	

	@Override
	public void setup() {
		super.setup();

		this.m_PrevDrawTime = 0;
		this.m_PrevUpdateTime = 0;

		this.m_zoomFactor = 1.0f;
		this.m_PrevOffset = new PVector(0, 0);
		this.m_Offset = new PVector(0, 0);
		//		this.m_PreviousMouseLocation = ORIGIN;

		this.smooth(8);

		// register for pre event to manage elapsed time and call update()
		// on all updateables automatically
		registerMethod("pre", this);
	}

	/**
	 * Add a new component to be automatically drawn and updated
	 * Will only be added next update cycle in an orderly manner
	 * @param i_Component drawable object to add
	 */
	public void addComponent(Component i_Component)
	{
		this.m_ComponentsToAdd.add(i_Component);
	}


	/**
	 * Removes a component from list of automatically updated objects.
	 * Will only be removed on next update cycle in an orderly manner
	 * @param i_Component updateable object to remove
	 */
	public void removeComponent(Component i_Component)
	{
		this.m_ComponentsToRemove.add(i_Component);
	}

	public void pre()
	{	
		// keep track of time
		int currentTime = millis();
		int ellapsedTime = currentTime - this.m_PrevUpdateTime;

		// add and remove components as needed
		// separated from addComponent to allow subclasses of Component
		// to call addComponent at any time without 
		// risking ConcurrentModificationException
		addNewComponents();
		removeComponents();


		// update all components if they are enabled
		for (Component component : this.m_Components)
		{
			if (component.Enabled())
			{
				component.update(ellapsedTime);
			}
		}				

		this.m_ZoomPercent = (this.m_zoomFactor - this.m_MinZoomFactor) / (this.m_MaxZoomFactor - this.m_MinZoomFactor);
		this.m_ZoomPercent *= (ZOOM_BAR_BOTTOM - ZOOM_BAR_TOP);
		this.m_ZoomPercent = ZOOM_BAR_BOTTOM - this.m_ZoomPercent;		

		this.m_PrevUpdateTime = currentTime;	
	}

	@Override
	public void draw()
	{
		// keep track of time
		int currentTime = millis();
		int elapsedTime = currentTime - this.m_PrevDrawTime;
		
		this.drawBackground();

		// Allow scaling and zooming using matrix translations transparently
		// Push current matrix, allow all objects to draw to world coordinates
		// Then translate back to Camera coordinates after draw
		this.pushMatrix();

		//		 //Translate coordinates to center of screen
//		this.translate(this.width / 2, this.height /2);

		// "Zoom in" using scaling factor
		this.scale(this.m_zoomFactor);

		// Center on requested zoom target
//		this.translate(this.m_Offset.x / this.m_zoomFactor, this.m_Offset.y / this.m_zoomFactor);
		this.translate(this.m_Offset.x / this.m_zoomFactor, this.m_Offset.y / this.m_zoomFactor);

		//		translate(offset.x/zoom, offset.y/zoom);

		// draw all components if visible
		for (Component component : this.m_Components)
		{
			if (component.Visible())
			{
				component.draw(elapsedTime);
			}
		}

		this.popMatrix();
		
		this.fill(255);
		this.noStroke();
		this.textAlign(this.RIGHT);
		this.text("Current mouse is: " + new PVector(mouseX, mouseY), this.width, 30);
		this.text("Current offset is: " + this.m_Offset, this.width, 60);
		this.text("Previous offset is: " + this.m_PrevOffset, this.width, 90);
		this.text("Previous mouse is: " + this.m_PreviousMouseLocation, this.width, 120);
		this.text("Translation is: " + new PVector(this.m_Offset.x / this.m_zoomFactor, this.m_Offset.y / this.m_zoomFactor), this.width, 150);
		
		// draw zoom scale
		this.stroke(255);
		this.line(20, ZOOM_BAR_TOP, 20, ZOOM_BAR_BOTTOM);
		this.line(10, this.m_ZoomPercent, 30, this.m_ZoomPercent);
		
		// draw frame rate
		this.textAlign(this.LEFT, this.BOTTOM);
		this.text("Framrate: " + this.frameRate, 10, this.height);

		this.m_PrevDrawTime = currentTime;	
	}


	//	public void mouseEvent(MouseEvent e)
	//	{
	//		switch (e.getAction()) {
	//			case MouseEvent.PRESS:				
	//				break;
	//			case MouseEvent.DRAG:
	//
	//				break;
	//			default:
	//				break;
	//		}
	//	}

	/**
	 * Draw the background - default implementation
	 * simply redraws the background color set using
	 * setBackgroundColor
	 */
	protected void drawBackground() 
	{
		this.clear();
		this.setBackground(this.m_BackgroundColor);		
	}
	
	protected void setBackgroundColor(Color i_Color)
	{
		this.m_BackgroundColor = i_Color;
	}

	@Override
	public void mousePressed() 
	{
		super.mousePressed();
		this.m_PreviousMouseLocation = new PVector(mouseX, mouseY);
		this.m_PrevOffset.set(this.m_Offset);
		this.ellipseMode(CENTER);
		this.fill(150);
		this.ellipse(80, 80, 30, 50);
		
	};
	
	@Override
	public void mouseReleased() {
		super.mouseReleased();
	}

	@Override
	public void mouseDragged() 
	{
//		super.mouseDragged();
		this.m_Offset.x = this.mouseX + this.m_PreviousMouseLocation.x - this.m_PrevOffset.x;
		this.m_Offset.y = this.mouseY + this.m_PreviousMouseLocation.y - this.m_PrevOffset.y;

	};
	
	@Override
	public void mouseClicked()
	{
		super.mouseClicked();		
	};

	// Zoom in and out when the key is pressed
	public void keyPressed() {

		if (key == 'a') {
			this.m_zoomFactor += 0.1;
		} 
		else if (key == 'z') {
			this.m_zoomFactor -= 0.1;
		}
		else if (key == 'd')
		{
			this.m_Offset.add(-1, 0, 0);
		}
		else if (key == 's')
		{
			this.m_Offset.add(1, 0, 0);
		}
		else if (key == 'e')
		{
			this.m_Offset.add(0, 1, 0);
		}
		else if (key == 'x')
		{
			this.m_Offset.add(0, -1, 0);
		}				
		this.m_zoomFactor = constrain(this.m_zoomFactor,this.m_MinZoomFactor,this.m_MaxZoomFactor);
	}


	@Override
	public void handleDeath(Object object) 
	{
		this.removeComponent((Component)object);

	}


	/**
	 * Removes all components flagged to be removed since last pass
	 */
	private void removeComponents() 
	{

		// remove all components set to be removed in ComponentsToRemove
		for (Component component : this.m_ComponentsToRemove) 
		{
			this.m_Components.remove(component);		
		}

		// clear componentsToRemove list
		this.m_ComponentsToRemove.clear();
	}

	/**
	 * Adds all components flagged to be added since last pass
	 */
	private void addNewComponents() 
	{

		// Add all components set to be added in ComponentsToAdd
		for (Component component : this.m_ComponentsToAdd) 
		{
			this.m_Components.add(component);
		}

		// clear componentsToAdd list
		this.m_ComponentsToAdd.clear();

	}


}
