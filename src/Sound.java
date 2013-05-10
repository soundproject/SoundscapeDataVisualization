import infrastructure.ManagedPApplet;
import infrastructure.SelfRegisteringComponent;
import infrastructure.interfaces.IDeathListener;
import infrastructure.interfaces.IDrawable;
import infrastructure.interfaces.IUpdateable;

import java.awt.Color;
import java.io.File;
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
	public static final String[] WORDS = {"Truck", "Bird", "Dog", "Cat", "Laughter", "Rain", "Waves"};
	
	private Main m_Parent;
	private PVector m_Origin;
//	private PVector m_Velocity;
	private Color m_Color;
	private String m_Word;

	private float m_Diameter;
	private boolean m_mouseIsOn;
	private boolean m_active = true;
	private int m_pleasantness;

	private boolean m_Pulse = false;
	private int m_PulseStartingAlpha = 196;
	private int m_numberOfPulses = 8;
	private int m_TimeBetweenPulses = 500;
	private long m_totalAnimationTime = 4000;
	private long m_currentAnimationTime = 0;
	
	private int m_Pulses = 0;

	private int m_spacingTime;
	
	private String m_SoundFileName = "Sounds\\Pomeranian.mp3";
	

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
		this.m_SoundFileName = getRandomSound();
		System.out.println("Random Sound is " + m_SoundFileName);
		
	}
	
	private String getRandomSound() {
		File baseDirectory = new File("..\\Data\\Sounds");
		File directory = new File(baseDirectory, this.m_Word);
		File[] files = directory.listFiles();
		
		System.out.println("Number of sounds: " + files.length);
		
		return "Sounds\\" + this.m_Word + "\\"  + files[(int)this.m_Parent.random(0, files.length - 1)].getName();
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
		
		if (this.m_mouseIsOn && !this.m_Pulse) //!this.m_active)
		{
			this.activate();
		}
		
//		if (this.m_Pulse)
//		{
//			this.m_Pulse = true;
//		}

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
		if (this.m_Pulse)
		{
			this.m_currentAnimationTime += ellapsedTime;
			if (this.m_Pulses < this.m_numberOfPulses && this.m_spacingTime >= this.m_TimeBetweenPulses)
			{
				this.m_Pulses++;
				new Pulse(m_Origin, this.m_Diameter, Main.ACTIVE_COLOR, 3500, this.m_Parent).addDeathListener(this);
				this.m_spacingTime = 0;
			} else
			{
				this.m_spacingTime += ellapsedTime;
			}

			if (this.m_currentAnimationTime >= this.m_totalAnimationTime)
			{
				this.m_Pulse = false;
				this.m_currentAnimationTime = 0;
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
				// if we are selected and mouse was on and not anymore
				// update mouse status
				this.m_mouseIsOn = false;

				// notify parent we are no longer selected
				this.m_Parent.m_SelectedSound = null;
			}

		}

		return this.m_mouseIsOn;
	}

	private boolean isMouseOnCircle() 
	{
		
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
	}
	
	private String getRandomWord()
	{
		
		return WORDS[(int)(this.m_Parent.random(0, WORDS.length - 1))];
	}

	@Override
	public void handleDeath(Object object) 
	{		
		this.m_Pulses--;
	}
	
	public void activate()
	{
		this.m_Pulse = true;
		this.m_totalAnimationTime = this.m_Parent.m_SoundManager.loadAndPlaySound(getRandomSound());
		this.m_Parent.m_TextArea.displayMessage(this.m_Word, new String[] {"Name: " + this.m_SoundFileName}, 3500);
	}
	
	public void deactivate()
	{
		this.m_Pulse = false;
	}
}
