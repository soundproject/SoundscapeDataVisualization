package infrastructure;

import infrastructure.interfaces.IDeathListener;

import java.util.ArrayList;

public abstract class SelfRegisteringComponent extends Component 
{
	
	/**
	 * Creates a new Component and registers it with the parent
	 * for automatic update and draw, and also for die() event
	 * @param i_Parent Parent applet
	 */
	public SelfRegisteringComponent(ManagedPApplet i_Parent) 
	{
		super(i_Parent);
		
		// add component to parent
		this.m_Parent.addComponent(this);
		
		// set parent to listen for this components death
		this.addDeathListener(this.m_Parent);
	}



}
