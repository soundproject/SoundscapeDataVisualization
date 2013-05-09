import java.awt.Color;
import java.awt.geom.Ellipse2D;

import processing.core.PApplet;
import processing.core.PVector;


public class Pulse {

	private PApplet m_Parent;
	private PVector m_Origin;
	private float m_Diameter;
	private Color m_Color;
	private long m_LifeTime;
	private int m_growthPerSecond = 100;
	private int m_fadePerSecond = 50;
	private long m_Alpha = 192;
	private boolean m_dead = false;
	
	public Pulse(PVector i_Origin, float i_Diameter, Color i_Color, long i_LifeTime, PApplet i_Parent) 
	{
		this.m_Origin = i_Origin;
		this.m_Diameter = i_Diameter;
		this.m_Color = i_Color;
		this.m_LifeTime = i_LifeTime;
		this.m_Parent = i_Parent;
	}
	
	public void update(float ellapsedTime)
	{
		this.m_LifeTime -= ellapsedTime * 1000;
		System.out.println("time " + ellapsedTime * 1000);
		
		if (this.m_LifeTime > 0 && this.m_Alpha > 0)
		{
			this.grow(ellapsedTime);
			this.fade(ellapsedTime);
			System.out.println("lifetime " + this.m_LifeTime);
			System.out.println("alpha" + this.m_Alpha);
		} else
		{		
			this.m_Alpha = 0;
			this.m_dead = true;
			System.out.println("died");
		}
	}
	
	private void fade(float ellapsedTime) {
		this.m_Alpha -= (this.m_fadePerSecond * ellapsedTime);
		if (this.m_Alpha <= 0)
		{
			this.m_Alpha = 0;
		}
	}

	private void grow(float ellapsedTime) {
		this.m_Diameter += this.m_growthPerSecond * ellapsedTime;		
	}

	public void draw(float f)
	{
		this.m_Parent.stroke(this.m_Color.getRGB(), this.m_Alpha);
		this.m_Parent.noFill();
		this.m_Parent.ellipse(this.m_Origin.x, this.m_Origin.y, this.m_Diameter, this.m_Diameter);
	}
	
	public boolean isDead()
	{
		return this.m_dead;
	}
	
}
