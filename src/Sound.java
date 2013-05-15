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
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * Represents a sound category from the data visualization
 * @author Gadi
 *
 */
public class Sound extends SelfRegisteringComponent implements IDeathListener
{

	private static final float ZOOM_FILL_PERCENT = 0.95f;
	private static final int INACTIVE_LINE_WIDTH = 2;
	private static final int ACTIVE_LINE_WIDTH = 3;

	// TODO: remove these and get them from DB
	public static final String[] WORDS = {"Truck", "Bird", "Dog", "Cat", "Laughter", "Rain", "Waves"};
	private static final int TIME_FOR_ZOOM = 3500;

	public static Color[] COLORS = {Main.INACTIVE_COLOR};


	// need access to Main parent for sounds and other services
	private Main m_Parent;
	private PVector m_Origin;
	private Color m_Color;
	private Color m_InactiveColor;
	private String m_Word;
	private float m_Diameter;
	private boolean m_mouseIsOn;
	private boolean m_active = true;
	private int m_pleasantness;

	// Pulse animation related settings
	private boolean m_Pulse = false;
	private int m_PulseStartingAlpha = 196;
	private int m_TimeBetweenPulses = 500;
	private long m_totalAnimationTime = 4000;
	private long m_currentAnimationTime = 0;
	private int m_Pulses = 0;
	private int m_numberOfPulses = 8;
	private int m_spacingTime;

	// Sound file
	private String m_SoundFileName;

	private boolean m_ShowInfo = false;

	private int m_NumberOfPeople;
	private int m_TimeBeforeAnimation = 0;
	private boolean m_animating = false;

	//	private PVector m_Velocity;



	/**
	 * Create a new sound object in the visualization
	 * @param i_Origin center of circle
	 * @param i_Diameter diameter of circle
	 * @param i_Parent parrent application
	 * @param i pleasantness - TODO: REMOVE
	 */
	public Sound(PVector i_Origin, float i_Diameter, Main i_Parent, int i)
	{
		super(i_Parent);
		
		// initialize members
		this.m_Origin = i_Origin;
		m_Diameter = i_Diameter;
		//		this.m_Color = Main.INACTIVE_COLOR;
		this.m_Parent = i_Parent;
//		System.out.println("New sound at " + this.m_Origin + " radius: " + this.m_Diameter);
		this.m_InactiveColor = Sound.COLORS[(int)this.m_Parent.random(0, Sound.COLORS.length)];


		this.m_Color = new Color(this.m_InactiveColor.getRGB(), false);
		this.m_pleasantness = i;

		//		this.m_Velocity = PVector.div(i_Velocity, 5f);


		// register for mouse event
		this.m_Parent.registerMethod("mouseEvent", this);
		this.m_Parent.registerMethod("keyEvent", this);

		// TODO: get these from DB
		this.m_Word = getRandomWord();
		this.m_SoundFileName = getRandomSound();
		System.out.println("Random Sound is " + m_SoundFileName);

		this.m_NumberOfPeople = (int)this.m_Parent.random(1, this.m_Diameter / 5);
	}

	public void keyEvent(KeyEvent event)
	{
		if (event.getAction() == KeyEvent.PRESS)
		{
			if (this.m_mouseIsOn)
			{
				this.showInfo();
			}
		}
	}

	private void showInfo() 
	{
		this.m_ShowInfo = !this.m_ShowInfo;

	}

	// TODO: REMOVE THIS FOR DB
	private String getRandomSound() {
		File baseDirectory = new File("../Data/Sounds");
		File directory = new File(baseDirectory, this.m_Word);
		File[] files = directory.listFiles();

		System.out.println("Number of sounds: " + files.length);

		return "Sounds/" + this.m_Word + "/"  + files[(int)this.m_Parent.random(0, files.length - 1)].getName();
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


	/**
	 * Handles mouseClick event
	 * @param e MouseEvent which occured
	 */
	private void handleMouseClick(MouseEvent e) 
	{
		if (this.m_mouseIsOn && !this.m_Pulse) //!this.m_active)
		{
			this.activate(true);
			this.setToFillScreen();
		}
	}

	private void setToFillScreen() 
	{
		float zoomfactor = (this.m_Parent.height * ZOOM_FILL_PERCENT) / this.m_Diameter;
		this.m_Parent.moveToCenter(this.m_Origin, zoomfactor, TIME_FOR_ZOOM);

	}

	/**
	 * Mouseover effect for sound
	 */
	public void select()
	{
		this.m_Parent.strokeWeight(ACTIVE_LINE_WIDTH);
		this.m_Parent.stroke(203,214,208);
		this.m_Parent.stroke(this.m_Color.getRGB());
		this.m_Parent.noFill();
		this.m_Parent.ellipse(this.m_Origin.x ,this.m_Origin.y ,m_Diameter ,m_Diameter);

		// TODO: add glow?				
	}

	@Override
	public void update(long ellapsedTime)
	{
		// Check if mouse is hovering on circle:
		isMouseOn();

		// update color of circle if mouse is on the circle
		//		this.m_Color = this.m_mouseIsOn ? Main.ACTIVE_COLOR.brighter() : Main.INACTIVE_COLOR;
		this.m_Color = this.m_mouseIsOn ? Main.ACTIVE_COLOR.brighter() : this.m_InactiveColor;

		if (this.m_animating)
		{
			this.m_TimeBeforeAnimation -= ellapsedTime;
			
			if (this.m_TimeBeforeAnimation <= 0)
			{
				this.m_Pulse = true;
				this.m_totalAnimationTime = this.m_Parent.m_SoundManager.loadAndPlaySound(getRandomSound());
				this.m_animating = false;
				this.showInfo();
			}
		}

		// Pulse if needed
		if (this.m_Pulse)
		{
			// keep track of time
			this.m_currentAnimationTime += ellapsedTime;

			// if number of active pulses is not max and enough time passed since last pulse
			if (this.m_Pulses < this.m_numberOfPulses && this.m_spacingTime >= this.m_TimeBetweenPulses)
			{
				// create new pulse and increment number of active pulses
				this.m_Pulses++;

				// register for this pulses death event
				new Pulse(m_Origin, this.m_Diameter, Main.ACTIVE_COLOR, 3500, this.m_Parent).addDeathListener(this);

				// reset time since pulse
				this.m_spacingTime = 0;
			} else
			{
				// no pulse created - increment time since pulse
				this.m_spacingTime += ellapsedTime;
			}

			// If sound was active enough time stop animation and reset time
			if (this.m_currentAnimationTime >= this.m_totalAnimationTime)
			{
				this.deactivate();

			}
		}


	}

	/**
	 * Returns true if mouse is overing on this Sound
	 * only one sound at a time may be active in app
	 * @return true if mouse is hovering on this Sound
	 */
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

	/**
	 * Returns true is mouse is within circle bounds
	 * @return true is mouse is within circle bounds
	 */
	private boolean isMouseOnCircle() 
	{

		return Math.abs(this.m_Origin.x - this.m_Parent.getWorldMouse().x) < this.m_Diameter / 2
				&& Math.abs(this.m_Origin.y - this.m_Parent.getWorldMouse().y) < this.m_Diameter / 2;
	}


	// TODO: do we need this?
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

	@Override
	public void draw(long ellapsedTime)
	{

		// draw shape
		this.m_Parent.noFill();
		this.m_Parent.stroke(this.m_Color.getRGB());
		this.m_Parent.strokeWeight(INACTIVE_LINE_WIDTH);
		this.m_Parent.ellipse(this.m_Origin.x, this.m_Origin.y, m_Diameter, m_Diameter);

		// mouseover?
		if (this.m_mouseIsOn)
		{
			this.select();
		}

		if (this.m_ShowInfo)
		{
			this.drawInfo();
		}

	}

	private void drawInfo() 
	{
		this.m_Parent.fill(Color.white.getRGB());
		this.m_Parent.noStroke();
		this.m_Parent.textAlign(this.m_Parent.CENTER, this.m_Parent.BOTTOM);

		float originalFontSize = this.m_Parent.m_Font.getSize();
		float newSize = 50;
		this.m_Parent.textSize(newSize);

		//		System.out.println("Original size is " + originalFontSize);
		//		System.out.println("With is " + this.m_Parent.textWidth(this.m_Word));
		//		System.out.println("Diameter is " + this.m_Diameter);

		// calculate text size to fit in circle

		while (this.m_Parent.textWidth(this.m_Word) > this.m_Diameter * 0.6f)
		{
			this.m_Parent.textSize(--newSize);

			// just in case size is very small
			// TODO: Better handling
			if (newSize <= 3)
			{
				break;
			}
		}

		// Draw label
		this.m_Parent.text(this.m_Word, this.m_Origin.x, this.m_Origin.y);

		// Draw more info

		// calc size again
		String message = String.format("Recorded %d times by %d people", (int)this.m_Diameter / 5, this.m_NumberOfPeople);
		while (this.m_Parent.textWidth(message) > this.m_Diameter * 0.9f)
		{
			this.m_Parent.textSize(--newSize);

			// just in case size is very small
			// TODO: Better handling
			if (newSize == 1f)
			{
				break;
			}
		}

		this.m_Parent.text(message, this.m_Origin.x, this.m_Origin.y + newSize);


		// restore font size:
		this.m_Parent.textFont(this.m_Parent.m_Font, originalFontSize);

		//		System.out.println("size reset to " + this.m_Parent.m_Font.getSize());
	}

	// TODO: REMOVE FOR DB
	private String getRandomWord()
	{

		return WORDS[(int)(this.m_Parent.random(0, WORDS.length))];
	}

	@Override
	public void handleDeath(Object object) 
	{		
		this.m_Pulses--;
	}


	/**
	 * Activates this sound sending pulses and starts playing a random sound sample 
	 */
	public void activate(boolean i_zoom)
	{
		this.m_TimeBeforeAnimation = i_zoom ? TIME_FOR_ZOOM : 0;
		this.m_animating = true;
		//		this.m_Parent.m_TextArea.displayMessage(this.m_Word, new String[] {"Name: " + this.m_SoundFileName}, 3500);
	}

	/**
	 * Deactivate the pulse effect
	 */
	public void deactivate()
	{
//		this.m_Pulse = false;
		this.m_Parent.returnViewToPrevious(TIME_FOR_ZOOM);
		this.m_Pulse = false;
		this.m_currentAnimationTime = 0;
		this.showInfo();

	}

	/**
	 * Returns true if sound is currently playing
	 * @return true if sound is currently playing
	 */
	public boolean isPlayingSound() {

		return this.m_currentAnimationTime > 0;
	}
	
	public boolean hasIntersection(PVector i_OtherOrigin, float i_OtherDiameter)
	{
		boolean result = false;
		float distance = PVector.dist(this.m_Origin, i_OtherOrigin);
		float sumRadii = this.m_Diameter / 2 + i_OtherDiameter / 2;
//		System.out.println("Distance is: " + distance + " Radii are " + sumRadii );
		result = (distance < sumRadii);
		return result;
		
	}
}
