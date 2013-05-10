package infrastructure;

import infrastructure.interfaces.IDrawable;
import infrastructure.interfaces.IUpdateable;

public abstract class Component implements IUpdateable, IDrawable {

	protected ManagedPApplet m_Parent;
	protected boolean m_Enabled = true;
	protected boolean m_Visible = true;
	
	public Component(ManagedPApplet i_Parent) {
		
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
	
	public void setEnabled(boolean i_Enabled)
	{
		this.m_Enabled = i_Enabled;
	}
	
	public void setVisible(boolean i_Visible)
	{
		this.m_Visible = i_Visible;
	}

}
