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

	private static int counter = 0;
	
	private final ArrayList<Component> m_ComponentsToAdd = new ArrayList<Component>();
	private final ArrayList<Component> m_ComponentsToRemove = new ArrayList<Component>();
	private final ArrayList<Component> m_Components = new ArrayList<Component>();
	protected int m_PrevUpdateTime;
	protected int m_PrevDrawTime;
	private Color m_BackgroundColor = Color.black;
	
	private float m_Zoom = 1.0f;
	private PVector m_CenterForZoom = new PVector(0, 0);
	private PVector m_CurrentCenter = new PVector(0, 0);
	private int m_PrevMouseX;
	private int m_PrevMouseY;
	private boolean m_ZoomEnabled = true;
	
	
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
		this.translate(this.m_CenterForZoom.x, this.m_CenterForZoom.y);
//		this.translate(this.width / 2, this.height / 2);
		
		// zoom in / out as needed
		this.scale(this.m_Zoom);
		
		this.translate(-this.m_CenterForZoom.x, -this.m_CenterForZoom.y);
		
		
		// translate center (accounting for zoom)
		this.translate(this.m_CurrentCenter.x / this.m_Zoom, this.m_CurrentCenter.y / this.m_Zoom);
		
		// draw all components if visible
		for (Component component : this.m_Components)
		{
			if (component.Visible())
			{
				component.draw(elapsedTime);
			}
		}
		
		this.popMatrix();
		
		this.m_PrevDrawTime = currentTime;	
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
		
		this.m_Zoom *= (1 - e.getWheelRotation() / 10f);
		System.out.println("new zoom is " + this.m_Zoom);
		
		
		this.m_CenterForZoom = new PVector(mouseX / this.m_Zoom, mouseY / this.m_Zoom);
		
	}
	
	@Override
	public void mouseDragged(MouseEvent event) 
	{
		// TODO Auto-generated method stub
		super.mouseDragged(event);
		
		System.out.println("MouseX " + mouseX);
		PVector translation = new PVector(mouseX - this.m_PrevMouseX, mouseY - this.m_PrevMouseY);
		System.out.println("translation is " + translation);
		this.m_CurrentCenter.add(translation);
		this.m_PrevMouseX = mouseX;
		this.m_PrevMouseY = mouseY;
	}
	
	@Override
	public void mouseMoved(MouseEvent event) {
		// TODO Auto-generated method stub
		super.mouseMoved(event);
		this.m_PrevMouseX = mouseX;
		this.m_PrevMouseY = mouseY;
	}


}
