import infrastructure.ManagedPApplet;
import infrastructure.SelfRegisteringComponent;
import infrastructure.interfaces.IDeathListener;
import infrastructure.interfaces.IDrawable;
import infrastructure.interfaces.IUpdateable;

import java.awt.Color;
import java.util.ArrayList;

import processing.core.PFont;
import processing.core.PVector;
import processing.event.MouseEvent;

/**
 * @author Gadi
 *
 */
public class Sound extends SelfRegisteringComponent implements IDeathListener
{
	public static final String[] WORDS = {"Trucks", "Birds", "Dog", "Cat", "Laughter", "Kids", "Rain", "Waves"};
	
	private Main m_Parent;
	private PVector m_Origin;
//	private PVector m_Velocity;
	private Color m_Color;
	private String m_Word;

	private float m_Diameter;
	private boolean m_mouseIsOn;
	private boolean m_active = true;
	private int m_pleasantness;

	private boolean m_pulse = false;
	private int m_pulseAlpha = 196;
	private int m_numberOfPulses = 8;
	private int m_spacing = 500;
	private long m_totalAnimationTime = 4000;
	private long m_currentAnimationTime = 0;
	
	private int m_Pulses = 0;

	private int m_spacingTime;
	

	/**
	 * @param i_xLocation
	 * @param i_yLocation
	 * @param i_speedx
	 * @param i_speedy
	 * @param i_Diameter
	 * @param i_Parent
	 * @param i
	 */
	Sound(PVector i_Origin, float i_Diameter, Main i_Parent, int i)
	{
		super(i_Parent);
		this.m_Origin = i_Origin;

		m_Diameter = i_Diameter;
		
//		this.m_Velocity = PVector.div(i_Velocity, 5f);

		this.m_Parent = i_Parent;

		this.m_Parent.registerMethod("mouseEvent", this);
		
		this.m_Color = Main.INACTIVE_COLOR;
		
		this.m_pleasantness = i;
		this.m_Word = getRandomWord();
		
	}
	
	public void mouseEvent(MouseEvent e)
	{

		switch (e.getAction()) {
			case MouseEvent.CLICK:

				handleMouseClick(e);
				break;
			default:
				break;
		}

	}

	private void handleMouseClick(MouseEvent e) {
		
		if (this.m_mouseIsOn || !this.m_active)
		{
			this.m_active = !this.m_active;
		}
		
		if (!this.m_active)
		{
			this.m_pulse = true;
		}

	}

	public void select()
	{
		this.m_Parent.strokeWeight(4);
		this.m_Parent.stroke(203,214,208);
		this.m_Parent.stroke(this.m_Color.getRGB());
		this.m_Parent.noFill();
		this.m_Parent.ellipse(this.m_Origin.x ,this.m_Origin.y ,m_Diameter,m_Diameter);
		
		// TODO: add glow
		
	}

	public void update(long ellapsedTime)
	{
		// Check if mouse is hovering on circle:
		isMouseOn();
		
		// update color of circle if mouse is on the circle
		this.m_Color = this.m_mouseIsOn ? Main.ACTIVE_COLOR.brighter() : Main.INACTIVE_COLOR;
		
		// Pulse if needed
		if (this.m_pulse)
		{
			this.m_currentAnimationTime += ellapsedTime;
			if (this.m_Pulses < this.m_numberOfPulses && this.m_spacingTime >= this.m_spacing)
			{
				this.m_Pulses++;
				new Pulse(m_Origin, this.m_Diameter, Main.ACTIVE_COLOR, 3500, this.m_Parent).addDeathListener(this);
				System.out.println("Pulse Added" + this.m_Pulses);
				this.m_spacingTime = 0;
			} else
			{
				this.m_spacingTime += ellapsedTime;
			}

			if (this.m_currentAnimationTime >= this.m_totalAnimationTime)
			{
				this.m_pulse = false;
			}
		} 						
		
		
	}

	private boolean isMouseOn() {

		if (isMouseOnCircle() && 
			// check if this circle is selected or no other circles selected
			(this.m_Parent.m_SelectedSound == null || this.m_Parent.m_SelectedSound == this))
		{
			this.m_mouseIsOn = true;
			
			// notify parent we are selected
			this.m_Parent.m_SelectedSound = this;
		} else
		{
			// check if this circle was selected
			if (this.m_mouseIsOn && this.m_Parent.m_SelectedSound == this)
			{
				this.m_mouseIsOn = false;

				// notify parent we are no longer selected
				this.m_Parent.m_SelectedSound = null;
			}

		}

		return this.m_mouseIsOn;
	}

	private boolean isMouseOnCircle() {
		
		return Math.abs(this.m_Origin.x - this.m_Parent.mouseX) < this.m_Diameter / 2
				&& Math.abs(this.m_Origin.y - this.m_Parent.mouseY) < this.m_Diameter / 2;
	}

	public void move()
	{
		
//		this.m_Origin.add(this.m_Velocity);
//		if ((this.m_Origin.x < 0)|| (this.m_Origin.y > this.m_Parent.width))
//		{
//			this.m_Velocity.x *= -1;
//		}
//		
//		if ((this.m_Origin.y < 0) || (this.m_Origin.y > this.m_Parent.height))
//		{
//			this.m_Velocity.y *= -1;
//		}
	}

	public void draw(long ellapsedTime)
	{
		
		// draw shape
		this.m_Parent.noFill();
		this.m_Parent.stroke(this.m_Color.getRGB());
		this.m_Parent.strokeWeight(2);
		this.m_Parent.ellipse(this.m_Origin.x, this.m_Origin.y, m_Diameter, m_Diameter);

		// mouseover?
		if (this.m_mouseIsOn)
		{
			this.select();
		}
		
		// pulse?
		if (this.m_pulse)
		{
//			this.m_Parent.stroke(this.m_Color.getRGB(), this.m_pulseAlpha);
//			this.m_Parent.ellipse(this.m_Origin.x ,this.m_Origin.y ,m_Diameter + (192 - this.m_pulseAlpha),m_Diameter + (192 - this.m_pulseAlpha));
//			for (Pulse pulse : this.m_Pulses) {
//				if (!pulse.isDead())
//					pulse.draw(ellapsedTime);
//				else
//				{
//					System.out.println("dead pulse found");
//				}
//			}
		}
		
//		if (!this.m_active)
//		{
//			this.m_Parent.fill(255);
//			this.m_Parent.noStroke();
//			int width = (int) Math.max((this.m_Parent.textWidth("Total: 24") + 10), (this.m_Parent.textWidth("Unpleasant: 89%") + 10));
//			int height = 24 * 3 + 15;
//			int x = (int) (this.m_Origin.x - width / 2);
//			int y = (int) (this.m_Origin.y - this.m_Diameter /2 - height - 3);
//			this.m_Parent.rect(x, y, width, height, 7);
//
//			this.m_Parent.textAlign(this.m_Parent.CENTER, this.m_Parent.TOP);
//			
//			this.m_Parent.fill(128);
//			this.m_Parent.text(this.m_Word, x + width / 2, y);
//			this.m_Parent.textAlign(this.m_Parent.LEFT, this.m_Parent.TOP);
//			this.m_Parent.text("Total: " + (int)this.m_Diameter, x + 5, y + 24);
//			
//			String message = this.m_pleasantness <= 50? "Pleasant ": "Unpleasant ";
//			int pleasant = this.m_pleasantness;
//			if (this.m_pleasantness <= 50)
//			{
//				pleasant = 100 - (this.m_pleasantness * 2);
//			}
//			message += pleasant + "%";
//			
//			this.m_Parent.text(message, x + 5, y + 24 * 2);
//			
//		}
	}
	
	private String getRandomWord()
	{
		
		return WORDS[(int)(this.m_Parent.random(0, WORDS.length - 1))];
	}

	@Override
	public void handleDeath(Object object) {
		
		this.m_Pulses--;
		System.out.println("Pulse died" + this.m_Pulses);
	}
}
