package infrastructure;

import infrastructure.interfaces.IDrawable;
import infrastructure.interfaces.IUpdateable;
import processing.core.PApplet;

public abstract class SelfRegisteringComponent extends Component{
	
	protected boolean m_enabled = true;
	protected boolean m_visible = true;
	protected boolean m_alive = true;
	
	public SelfRegisteringComponent(ManagedPApplet i_Parent) 
	{
		super(i_Parent);
		
		this.m_Parent.addComponent(this);
	}
	

}
