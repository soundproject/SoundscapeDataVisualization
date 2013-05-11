package infrastructure;

import infrastructure.interfaces.IDeathListener;
import infrastructure.interfaces.IDrawable;
import infrastructure.interfaces.IUpdateable;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.color.ColorSpace;
import java.awt.event.MouseWheelEvent;
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

	private static final float MIN_ZOOM = 0.1f;
	private static float MAX_ZOOM = 100f;

	private static final boolean DEBUG = true;

	private static int counter = 0;
	
	private final ArrayList<Component> m_ComponentsToAdd = new ArrayList<Component>();
	private final ArrayList<Component> m_ComponentsToRemove = new ArrayList<Component>();
	private final ArrayList<Component> m_Components = new ArrayList<Component>();
	protected int m_PrevUpdateTime;
	protected int m_PrevDrawTime;
	private Color m_BackgroundColor = Color.black;
	
	private float m_Zoom = 1.0f;
	private PVector m_CenterForZoom = new PVector(0, 0);
	private PVector m_WorldOrigin = new PVector(0, 0);
	private int m_PrevMouseX;
	private int m_PrevMouseY;
	private boolean m_ZoomEnabled = true;
	private Color m_ZoomBarColor = Color.GREEN.darker();

	private int m_ZoomBarHeight = 125;

	private int m_ZoomBarBottom = 150;
	private PVector m_PanDirection = new PVector(0, 0);
	private float m_PanSpeed = 0;
	private int m_PanningTime = 0;

	
	
	
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
	
	@Override
	public void setup() {
		super.setup();
		
		// register for pre event to manage elapsed time and call update()
		// on all updateables automatically
		registerMethod("pre", this);
	}
	
	public void pre()
	{	
		// keep track of time
		int currentTime = millis();
		int elapsedTime = currentTime - this.m_PrevUpdateTime;
		
		// add and remove components as needed
		// separated from addComponent to allow subclasses of Component
		// to call addComponent at any time without 
		// risking ConcurrentModificationException
		addNewComponents();
		removeComponents();
		
		// pan to target
		if (this.m_PanningTime > 0)
		{
			this.m_PanningTime -= elapsedTime;
			
			this.m_WorldOrigin.add(PVector.mult(m_PanDirection, this.m_PanSpeed * 7));
//			this.m_WorldOrigin.add(PVector.mult(m_PanDirection, this.m_PanSpeed));
		}
		
		// update all components if they are enabled
		for (Component component : this.m_Components)
		{
			if (component.Enabled())
			{
				component.update(elapsedTime);
			}
		}
		
		this.m_PrevUpdateTime = currentTime;	
	}
	
	@Override
	public void draw()
	{
		// keep track of time
		int currentTime = millis();
		int elapsedTime = currentTime - this.m_PrevDrawTime;
		
		drawBackground();
		
		// save world coordinates
		pushMatrix();
		
		// center on zoom target
//		this.translate(this.width / 2, this.height / 2);
//		this.translate(this.m_CenterForZoom.x, this.m_CenterForZoom.y);
		
		// zoom in / out as needed
		this.scale(this.m_Zoom);
		
		// move back
//		this.translate(-this.m_CenterForZoom.x, -this.m_CenterForZoom.y);		
		
		// translate center (accounting for zoom)
		this.translate(this.m_WorldOrigin.x / this.m_Zoom, this.m_WorldOrigin.y / this.m_Zoom);
		
		// draw all components if visible
		for (Component component : this.m_Components)
		{
			if (component.Visible())
			{
				component.draw(elapsedTime);
			}
		}
		
		// return to world coordinates
		this.popMatrix();
		
		if (this.m_ZoomEnabled)
		{
			drawZoomBar();
		}
		
		if (DEBUG)
		{
			// draw framerate
			fill(255);
			textAlign(LEFT, TOP);
			text(frameRate, 10, 10);
			
			// draw world transformation and mouse info
			textAlign(LEFT, BOTTOM);
			text("Mouse world coordinates " + getWorldMouse(), 10, this.height - 10);
			text("Mouse actual coordinates" + new PVector(mouseX, mouseY), 10, this.height - 40);
			text("World Origin is at " + this.m_WorldOrigin, 10, this.height - 70);
//			System.out.println("Mouse world coordinates" + result);
//			System.out.println("Actual mouse is on " + mouseX + " " + mouseY);
			
		}
		
		this.m_PrevDrawTime = currentTime;	
	}


	private void drawZoomBar() 
	{
		// set graphics settings for vertical zoom bar
		this.noFill();
		this.stroke(this.m_ZoomBarColor.getRGB());
		this.strokeWeight(5);
		
		// vertical part of zoom bar
		this.line(this.width - 30, this.m_ZoomBarBottom - this.m_ZoomBarHeight, this.width - 30, this.m_ZoomBarBottom);
		
		// TODO: draw scale on it?
		
		// draw current zoom "location"
		this.strokeWeight(3);
		
		float zoomPercent = (this.m_Zoom - MIN_ZOOM) / (MAX_ZOOM - MIN_ZOOM);
		float yLocation = this.m_ZoomBarBottom - (zoomPercent * this.m_ZoomBarHeight);
		
		this.line(this.width - 30 - 10, yLocation, this.width - 30 + 10, yLocation);						
	}


	/**
	 * Draw the background
	 */
	protected void drawBackground() 
	{
		clear();
		this.background(this.m_BackgroundColor.getRGB());
	}
	
	public void setBackgroundColor(Color i_Color)
	{
		this.m_BackgroundColor = new Color(i_Color.getColorSpace(), i_Color.getRGBColorComponents(null), i_Color.getAlpha() / 255f);
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
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		super.mouseWheelMoved(e);
		
		if (this.m_ZoomEnabled)
		{
			zoom(e.getWheelRotation() < 0);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent event) 
	{	
		super.mouseClicked(event);

		if (mouseButton == RIGHT)
		{
			this.toggleZoom();
		} else if (mouseButton == CENTER)
		{
			this.resetView();
		} else
		{
			this.getWorldMouse();
		}
		
	}


	/**
	 * Zooms the view in or out according to parameter
	 * @param i_zoomIn true for zooming in, false for zooming out
	 */
	public void zoom(boolean i_zoomIn) 
	{
		float zoomStep = 0.1f;
		
		if (!i_zoomIn)
		{
			zoomStep *= -1;
		}
		
		this.m_Zoom *= (1 + zoomStep);
		this.m_Zoom = constrain(m_Zoom, MIN_ZOOM, MAX_ZOOM);
		
		if (DEBUG)
		{
			System.out.println("new zoom is " + this.m_Zoom);
			System.out.println("zoom-in?" + i_zoomIn);
		}
		
		
		this.m_CenterForZoom = new PVector(mouseX / this.m_Zoom, mouseY / this.m_Zoom);
	}
	
	@Override
	public void mouseDragged(MouseEvent event) 
	{
		// TODO Auto-generated method stub
		super.mouseDragged(event);
		
		if (this.m_ZoomEnabled)
		{
			PVector translation = new PVector(mouseX - this.m_PrevMouseX, mouseY - this.m_PrevMouseY);
			
			if (DEBUG)
			{
				System.out.println("MouseX is " + mouseX);
				System.out.println("translation is " + translation);
			}
			
			this.m_WorldOrigin.add(translation);
		}
		
		this.m_PrevMouseX = mouseX;
		this.m_PrevMouseY = mouseY;
	}
	
	@Override
	public void mouseMoved(MouseEvent event) 
	{
		super.mouseMoved(event);
		
		// keep track of mouse location
		this.m_PrevMouseX = mouseX;
		this.m_PrevMouseY = mouseY;
	}
	
	/**
	 * Enables zooming and panning with mouse and mouse wheel
	 */
	public void enableZoom()
	{
		this.m_ZoomEnabled = true;
		
		if (DEBUG)
		{
			System.out.println("Zoom enabled");
		}
	}
	
	/**
	 * Disables zooming and panning with mouse and mouse wheel
	 */
	public void disableZoom()
	{
		this.m_ZoomEnabled = false;
		if (DEBUG)
		{
			System.out.println("Zoom disabled");
		}
	}
	
	/**
	 * Toggles zooming on or off
	 */
	public void toggleZoom()
	{
		if (this.m_ZoomEnabled)
		{
			this.disableZoom();
		} else
		{
			this.enableZoom();
		}
	}
	
	/**
	 * Resets view as if no zoom or pan was done
	 */
	public void resetView()
	{
		this.m_Zoom = 1.0f;
		this.m_WorldOrigin = new PVector(0, 0);
		
		if (DEBUG)
		{
			System.out.println("View reset");
		}
	}
	
	/**
	 * Coordinates of mouse in world after accounting 
	 * for zoom and translation of camera
	 * @return world mouse coordinates
	 */
	public PVector getWorldMouse()
	{
		PVector result = new PVector((mouseX - this.m_WorldOrigin.x) / this.m_Zoom, (mouseY - this.m_WorldOrigin.y) / this.m_Zoom);
		
		return result;
	}
	
	public void centerViewOnTarget(PVector i_NewCenter, float i_ZoomFactor, int i_TimeForAnimation)
	{
		float left = i_NewCenter.x - (this.width / i_ZoomFactor);
		float top = i_NewCenter.y - (this.height / i_ZoomFactor);
//		PVector calculatedCenter = new PVector(left, top);
//		if (i_ZoomFactor > 1)
//		{
//			i_ZoomFactor *= -1;
//		}
		PVector calculatedCenter = PVector.mult(i_NewCenter, i_ZoomFactor * -1);
		this.m_PanDirection = PVector.sub(this.m_WorldOrigin, calculatedCenter);
		this.m_PanSpeed = this.m_PanDirection.mag() / i_TimeForAnimation;
		this.m_PanDirection.mult(-1);
		this.m_PanDirection.normalize();
		
		this.m_PanningTime = i_TimeForAnimation;
		
		if (DEBUG)
		{
			System.out.println("Received new center: " + i_NewCenter);
			System.out.println("Received new zoom: " + i_ZoomFactor);
			System.out.println("Calculated center " + calculatedCenter);
			System.out.println("Time to animate: " + i_TimeForAnimation);
			System.out.println("Direction is " + this.m_PanDirection);
		}
	}


}
