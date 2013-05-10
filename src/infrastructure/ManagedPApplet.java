package infrastructure;

import infrastructure.interfaces.IDrawable;
import infrastructure.interfaces.IUpdateable;

import java.util.ArrayList;

import processing.core.PApplet;

public class ManagedPApplet extends PApplet {
	

	private final ArrayList<Component> m_ComponentsToAdd = new ArrayList<Component>();
	private final ArrayList<Component> m_ComponentsToRemove = new ArrayList<Component>();
	private final ArrayList<Component> m_Components = new ArrayList<Component>();
	private int m_PrevUpdateTime;
	private int m_PrevDrawTime;
	
	/**
	 * Add a new component to be automatically drawn and updated
	 * @param i_Component drawable object to add
	 */
	public void addComponent(Component i_Component)
	{
		this.m_ComponentsToAdd.add(i_Component);
	}

	
	/**
	 * Removes a componentfrom list of automatically updated objects 
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
		int currentTime = millis();
		int ellapsedTime = currentTime - this.m_PrevUpdateTime;
		
		
		addNewComponents();
		removeComponents();
		
		for (Component component : this.m_Components)
		{
			if (component.Enabled())
			{
				component.update(ellapsedTime);
			}
		}
		
		
		
		this.m_PrevUpdateTime = currentTime;	
	}
	
	private void removeComponents() {

		for (Component component : this.m_ComponentsToRemove) {
			this.m_Components.remove(component);		
		}
		
		this.m_ComponentsToRemove.clear();
	}

	private void addNewComponents() {

		for (Component component : this.m_ComponentsToAdd) {
			this.m_Components.add(component);
		}
		
		this.m_ComponentsToAdd.clear();
		
	}

	public void draw()
	{
		int currentTime = millis();
		int elapsedTime = currentTime - this.m_PrevDrawTime;
		
		for (Component component : this.m_Components)
		{
			if (component.Visible())
			{
				component.draw(elapsedTime);
			}
		}
		
		this.m_PrevDrawTime = currentTime;	
	}
	
}
