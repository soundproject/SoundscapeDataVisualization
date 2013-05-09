import processing.event.MouseEvent;

import processing.core.PApplet;
import processing.core.PFont;

public class Sound
{
	public static final String[] WORDS = {"Trucks", "Birds", "Dog", "Cat", "Laughter", "Kids", "Rain", "Waves"};
	float m_xLocation;
	float m_yLocation;

	float m_Diameter;
	int r;
	int g;
	int b;
	float m_ySpeed;
	float m_xSpeed;
	private Main m_Parent;
	private boolean m_mouseIsOn;
	private boolean m_active = true;
	
	private PFont font;
	private int m_pleasantness;
	private String m_Word;

	/**
	 * @param i_xLocation
	 * @param i_yLocation
	 * @param i_speedx
	 * @param i_speedy
	 * @param i_red
	 * @param i_green
	 * @param i_blue
	 * @param i_diameter
	 * @param parent
	 */
	Sound(float i_xLocation, float i_yLocation, float i_speedx,float i_speedy,
//			int i_red, int i_green, int i_blue, 
			float i_diameter, Main parent, int i)
			{
		m_xLocation=i_xLocation;
		m_yLocation=i_yLocation;

		m_Diameter=i_diameter;
//		r=i_red;
//		g=i_green;
//		b=i_blue;
		m_xSpeed= i_speedx / 5;
		m_ySpeed=i_speedy / 5;

		this.m_Parent = parent;
		this.m_Parent.registerMethod("draw", this);
		this.m_Parent.registerMethod("pre", this);
		this.m_Parent.registerMethod("mouseEvent", this);
		
		font = this.m_Parent.createFont("Georgia", 24);
		this.m_Parent.textFont(font);
		
		int redStep = (Main.UNPLEASANT_COLOR.getRed() - Main.PLEASANT_COLOR.getRed()) / 100;
		int greenStep = (Main.UNPLEASANT_COLOR.getGreen() - Main.PLEASANT_COLOR.getGreen()) / 100;
		int blueStep = (Main.UNPLEASANT_COLOR.getBlue() - Main.PLEASANT_COLOR.getBlue()) / 100;
		
		
		r = i * redStep + Main.PLEASANT_COLOR.getRed();
		g = i * greenStep + Main.PLEASANT_COLOR.getGreen();
		b = i * blueStep + Main.PLEASANT_COLOR.getBlue();
		
		this.m_pleasantness = i;
		this.m_Word = getRandomWord();
		
			}

	public void mouseEvent(MouseEvent e)
	{

		switch (e.getAction()) {
			case MouseEvent.CLICK:
//									System.out.println("Mouse clicked");
				handleMouseClick(e);
				break;
			case MouseEvent.MOVE:
//									System.out.println("Mouse moved");
				handleMouseMove(e);
			default:
//				System.out.println("Event: " + e.getAction());
				break;
		}

	}

	private void handleMouseMove(MouseEvent e) {
		// TODO Auto-generated method stub


	}

	private void handleMouseClick(MouseEvent e) {
		
		if (this.m_mouseIsOn || !this.m_active)
		{
			this.m_active = !this.m_active;
		}

	}

	public void select()
	{
		this.m_Parent.strokeWeight(4);
		this.m_Parent.stroke(203,214,208);
		this.m_Parent.noFill();
		this.m_Parent.ellipse(m_xLocation,m_yLocation,m_Diameter,m_Diameter);
	}

	public void pre()
	{
		if (!isMouseOn() || !this.m_Parent.mousePressed)
		{}
		
		if (this.m_active)
		{			
			this.move();
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
		
		return Math.abs(this.m_xLocation - this.m_Parent.mouseX) < this.m_Diameter / 2
				&& Math.abs(this.m_yLocation - this.m_Parent.mouseY) < this.m_Diameter / 2;
	}

	public void move()
	{
		m_xLocation=m_xLocation+m_xSpeed;
		if ((m_xLocation<0)|| (m_xLocation>this.m_Parent.width))
		{
			m_xSpeed=m_xSpeed*-1;
		}
		m_yLocation=m_yLocation+m_ySpeed;
		if ((m_yLocation<0)|| (m_yLocation>this.m_Parent.height))
		{
			m_ySpeed=m_ySpeed*-1;
		}
	}

	public void draw()
	{
		// draw shape
		this.m_Parent.fill(r, g, b);
		this.m_Parent.noStroke();
		this.m_Parent.ellipse(m_xLocation, m_yLocation, m_Diameter, m_Diameter);

		// mouseover?
		if (this.m_mouseIsOn)
		{
			this.select();
		}
		
		if (!this.m_active)
		{
			this.m_Parent.fill(255);
			this.m_Parent.noStroke();
			int width = (int) Math.max((this.m_Parent.textWidth("Total: 24") + 10), (this.m_Parent.textWidth("Unpleasant: 89%") + 10));
			int height = 24 * 3 + 15;
			int x = (int) (this.m_xLocation - width / 2);
			int y = (int) (this.m_yLocation - this.m_Diameter /2 - height - 3);
			this.m_Parent.rect(x, y, width, height, 7);

			this.m_Parent.textAlign(this.m_Parent.CENTER, this.m_Parent.TOP);
			
			this.m_Parent.fill(128);
			this.m_Parent.text(this.m_Word, x + width / 2, y);
			this.m_Parent.textAlign(this.m_Parent.LEFT, this.m_Parent.TOP);
			this.m_Parent.text("Total: " + (int)this.m_Diameter, x + 5, y + 24);
			
			String message = this.m_pleasantness <= 50? "Pleasant ": "Unpleasant ";
			int pleasant = this.m_pleasantness;
			if (this.m_pleasantness <= 50)
			{
				pleasant = 100 - (this.m_pleasantness * 2);
			}
			message += pleasant + "%";
			
			this.m_Parent.text(message, x + 5, y + 24 * 2);
			
		}
	}
	
	private String getRandomWord()
	{
		
		return WORDS[(int)(this.m_Parent.random(0, WORDS.length - 1))];
	}
}
