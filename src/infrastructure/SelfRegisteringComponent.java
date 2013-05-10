package infrastructure;

import infrastructure.interfaces.IDeathListener;

import java.util.ArrayList;

public abstract class SelfRegisteringComponent extends Component{
	
	protected boolean m_enabled = true;
	protected boolean m_visible = true;
	protected boolean m_alive = true;
	protected final ArrayList<IDeathListener> m_DeathListeners = new ArrayList<IDeathListener>();
	
	/**
	 * Creates a new Component and registers it with the parent
	 * for automatic update and draw, and also for die() event
	 * @param i_Parent Parent applet
	 */
	public SelfRegisteringComponent(ManagedPApplet i_Parent) 
	{
		super(i_Parent);
		
		this.m_Parent.addComponent(this);
		this.addDeathListener(this.m_Parent);
	}

	public void addDeathListener(IDeathListener i_Listener)
	{
		if (!this.m_DeathListeners.contains(i_Listener))
		{
			this.m_DeathListeners.add(i_Listener);
		}
	}

}
