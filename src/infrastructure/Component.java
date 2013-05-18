package infrastructure;

import java.util.ArrayList;

import infrastructure.interfaces.IDeathListener;
import infrastructure.interfaces.IDrawable;
import infrastructure.interfaces.IUpdateable;

/**
 * A component for use with {@link ManagedPApplet}
 * Provides simple interface for Enabled/Disabled and Visble/Invisble
 * as well as die event and method to add death listeners
 * @author Gadi
 *
 */
public abstract class Component implements IUpdateable, IDrawable 
{

	protected ManagedPApplet m_Parent;
	protected boolean m_Enabled = true;
	protected boolean m_Visible = true;
	protected final ArrayList<IDeathListener> m_DeathListeners = new ArrayList<IDeathListener>();
	
	public Component(ManagedPApplet i_Parent) 
	{		
		this.m_Parent = i_Parent;		
	}
	
	/**
	 * Returns True if this component is enabled
	 * Used by ManagedPApplet to decide whether to update this component
	 * @return true if component is Enabled
	 */
	public boolean Enabled()
	{
		return this.m_Enabled;
	}

	/**
	 * Returns true if component is Visible
	 * Used by ManagedPApplet to decide whether to draw this component
	 * @return true if component is Visible
	 */
	public boolean Visible() {

		return this.m_Visible;
	}
	
	/**
	 * Sets the component's Enabled state which controls
	 * whether it is updated or not by parent if registered
	 * @param i_Enabled true to Enable component (allow update)
	 */
	public void setEnabled(boolean i_Enabled)
	{
		this.m_Enabled = i_Enabled;
	}
	
	/**
	 * Sets the component's Visible state which controls
	 * whether it is drawn or not by parent if registered
	 * @param i_Visible true to show component
	 */
	public void setVisible(boolean i_Visible)
	{
		this.m_Visible = i_Visible;
	}
	
	/**
	 * Add a new listener for die() event
	 * @param i_Listener death listener
	 */
	public void addDeathListener(IDeathListener i_Listener)
	{
		if (!this.m_DeathListeners.contains(i_Listener))
		{
			this.m_DeathListeners.add(i_Listener);
		}
	}
	
	/**
	 * Trigger handleDeath event for all listeners
	 * currently registered to this component 
	 */
	protected void die()
	{
		for (IDeathListener listener : this.m_DeathListeners)
		{
			listener.handleDeath(this);
		}
	}

	public boolean isInView() {		
		
		return true;
	}

}
