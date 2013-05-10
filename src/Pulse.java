import infrastructure.SelfRegisteringComponent;
import infrastructure.interfaces.IDeathListener;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;


public class Pulse extends SelfRegisteringComponent {

	private Main m_ParentApp;
	private PVector m_Origin;
	private float m_Diameter;
	private Color m_Color;
	private long m_LifeTime;
	private int m_growthPerSecond = 100;
	private int m_fadePerSecond = 50;
	private long m_Alpha = 192;
	private boolean m_dead = false;
	private final ArrayList<IDeathListener> m_DeathListeners = new ArrayList<IDeathListener>();
	
	public Pulse(PVector i_Origin, float i_Diameter, Color i_Color, long i_LifeTime, Main i_Parent) 
	{
		super(i_Parent);
		this.m_Origin = i_Origin;
		this.m_Diameter = i_Diameter;
		this.m_Color = i_Color;
		this.m_LifeTime = i_LifeTime;
		this.m_ParentApp = i_Parent;
	}
	
	public void update(long elapsedTime)
	{
		this.m_LifeTime -= elapsedTime;
		if (this.m_LifeTime > 0 && this.m_Alpha > 0)
		{
			this.grow(elapsedTime);
			this.fade(elapsedTime);
		} else
		{		
			this.die();			
		}
	}
	
	public void addDeathListener(IDeathListener i_DeathListener)
	{
		this.m_DeathListeners.add(i_DeathListener);
	}
	
	public void draw(long elapsedTime)
	{
		this.m_ParentApp.stroke(this.m_Color.getRGB(), this.m_Alpha);
		this.m_ParentApp.noFill();
		this.m_ParentApp.ellipse(this.m_Origin.x, this.m_Origin.y, this.m_Diameter, this.m_Diameter);
	}
	
	public boolean isDead()
	{
		return this.m_dead;
	}
	
	private void fade(float elapsedTime) {
		this.m_Alpha -= (this.m_fadePerSecond * elapsedTime / 1000f);
		if (this.m_Alpha <= 0)
		{
			this.m_Alpha = 0;
		}
	}

	private void grow(float elapsedTime) {
		this.m_Diameter += this.m_growthPerSecond * elapsedTime / 1000f;		
	}

	
	private void die() {
		this.m_ParentApp.removeComponent(this);
		
		for (IDeathListener listener : this.m_DeathListeners)
		{
			listener.handleDeath(this);
		}
		
	}
	
}
