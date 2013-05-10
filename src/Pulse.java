import infrastructure.SelfRegisteringComponent;
import infrastructure.interfaces.IDeathListener;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;


/**
 * Pulsing circle effect
 * Self registers with parent app and removes itself automatically
 * with die() event
 * @author Gadi
 *
 */
public class Pulse extends SelfRegisteringComponent {

	private Main m_ParentApp;
	private PVector m_Origin;
	private float m_Diameter;
	private float m_OriginalDiameter;
	private Color m_Color;
	private long m_LifeTime;
	private int m_growthPerSecond = 120;
	private int m_fadePerSecond = 100;
	private long m_Alpha = 192;
	private boolean m_dead = false;
	
	/**
	 * Creates a new animated growing and fading circle
	 * @param i_Origin center of circle
	 * @param i_Diameter diameter of circle
	 * @param i_Color color of circle
	 * @param i_LifeTime lifetime of the circle
	 * @param i_Parent Parent application to draw on
	 */
	public Pulse(PVector i_Origin, float i_Diameter, Color i_Color, long i_LifeTime, Main i_Parent) 
	{
		super(i_Parent);
		
		this.m_Origin = i_Origin;
		this.m_Diameter = i_Diameter;
		this.m_OriginalDiameter = i_Diameter;
		this.m_Color = i_Color;
		this.m_ParentApp = i_Parent;
		
		// TODO: support lifetime in calculations of growthRate and fadePerSecond
		this.m_LifeTime = i_LifeTime;
	}
	
	@Override
	public void update(long elapsedTime)
	{
		this.m_LifeTime -= elapsedTime;
		
		// If circle is not too old and still visible (alpha > 0)
		if (this.m_LifeTime > 0 && this.m_Alpha > 0)
		{
			// grow and fade the circle
			this.grow(elapsedTime);
			this.fade(elapsedTime);
		} else
		{		
			// get rid of circle
			this.die();			
		}
	}
	
	@Override
	public void draw(long elapsedTime)
	{
		this.m_ParentApp.stroke(this.m_Color.getRGB(), this.m_Alpha);
		this.m_ParentApp.noFill();
		this.m_ParentApp.ellipse(this.m_Origin.x, this.m_Origin.y, this.m_Diameter, this.m_Diameter);
	}
	
	/**
	 * Returns true if pulse is dead
	 * @return true if pulse is dead
	 */
	public boolean isDead()
	{
		return this.m_dead;
	}
	
	
	/**
	 * Fade the circle according to fadePerSecond attribute
	 * @param elapsedTime time since last fade
	 */
	private void fade(float elapsedTime) {
		this.m_Alpha -= (this.m_fadePerSecond * elapsedTime / 1000f);
		
		// ensure alpha is not negative
		if (this.m_Alpha <= 0)
		{
			this.m_Alpha = 0;
		}
	}

	/**
	 * Grow circle according to growthPerSecond attribute
	 * @param elapsedTime
	 */
	private void grow(float elapsedTime) {
		this.m_Diameter += this.m_growthPerSecond * elapsedTime / 1000f;				
	}
	
}
