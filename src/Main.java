import infrastructure.ManagedPApplet;
import infrastructure.Sound.SoundManager;
import infrastructure.interfaces.IDrawable;
import infrastructure.interfaces.IUpdateable;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;


/**
 * Main class of Visualization
 * @author Gadi
 *
 */
public class Main extends ManagedPApplet 
{

	// -------------- Preferences ---------------------------------------------------------------------------
	// general graphics options
	public static final int FRAMERATE = 60;
	public static final int WIDTH = 1370;
	public static final int HEIGHT = 700;
	public static final boolean DRAW_STROKES = false;

	// resource locations (base path is ./data/)
	public static final String LEGEND_IMAGE = "Images\\COLORS.png";
	public static final String TITLE_IMAGE = "Images\\title.png";

	// number of sounds
	public static final int NUMBER_OF_SOUNDS = 30;

	// Colors:
	public static final Color BACKGROUND_COLOR = new Color(69, 70, 75, 200);
	public static final Color BACKGROUND_STROKE_COLOR = new Color(83, 83, 86);	
	public static final Color PLEASANT_COLOR = new Color(13, 0, 202);
	public static final Color UNPLEASANT_COLOR = new Color(250, 20, 202);	
	public static final Color INACTIVE_COLOR = new Color(128, 128, 128);
	public static final Color ACTIVE_COLOR = Color.orange.darker();

	// -------------- Preferences ---------------------------------------------------------------------------

	// General Services
	protected SoundManager m_SoundManager;
//	protected TextArea m_TextArea;
	protected PFont m_Font;	
	protected Sound m_SelectedSound = null;

	private Sound [] m_Sounds;
	private PImage m_LegendImage;
	private PImage m_TitleImage;

	private int m_IdleTime;
	private int m_MaxIdleTime = 6500;
	private AutoPlayingManager m_AutoPlayingManager;



	public void setup()
	{
		super.setup();

		// set size and framerate
		size (WIDTH, HEIGHT);
		frameRate(FRAMERATE);

		// load images, fonts, sounds...
		this.loadContent();

		// set graphics options
		ellipseMode(CENTER);	
		this.setBackgroundColor(BACKGROUND_COLOR);

		// initialize Sound Elements:
		m_Sounds = new Sound [NUMBER_OF_SOUNDS];
		for (int i = 0; i < this.m_Sounds.length; i++)
		{

			// generate new random sound
			// TODO: calculate sound parameters according to real info
			m_Sounds [i] = new Sound (new PVector(random(0, WIDTH), random(0, HEIGHT)),  
					random(10,90), this, i);
		}

		// Initialize services
		this.m_AutoPlayingManager = new AutoPlayingManager(this);
		this.m_SoundManager = new SoundManager(this);
//		this.m_TextArea = new TextArea(this, 250, 100, new Point(WIDTH - 250, HEIGHT - 100));
	}

	private void loadContent() {

		// load images:
		m_LegendImage = loadImage (LEGEND_IMAGE);
		m_TitleImage = loadImage(TITLE_IMAGE);

		// load font
		m_Font = createFont("Georgia", 18);
		textFont(m_Font);

	}


	@Override
	public void pre() {

		// if demo mode is not enabled, aggregate idleTime
		if (!this.m_AutoPlayingManager.Enabled())
		{
			this.m_IdleTime += (millis() - this.m_PrevUpdateTime);

			// Activate demo mode if max idle time reached
			if (this.m_IdleTime >= this.m_MaxIdleTime)
			{
				this.m_AutoPlayingManager.Activate();
			}
		}

		super.pre();		
	}


	public void draw()
	{

		drawBackground();		

		super.draw();

		// draw title images
		//		image(m_LegendImage, 10,530);
		image(m_TitleImage,10,20);

		// draw framerate
		fill(255);
		textAlign(LEFT, TOP);
		text(frameRate, 10, 10);
	}

	@Override
	protected void drawBackground() 
	{
		super.drawBackground();
		//		background(BACKGROUND_COLOR.getRed(), BACKGROUND_COLOR.getGreen(), 
		//				BACKGROUND_COLOR.getBlue(), BACKGROUND_COLOR.getAlpha());

		if (DRAW_STROKES)
		{
			for (int row=0; row<5000; row=row+20)
			{
				stroke(BACKGROUND_STROKE_COLOR.getRed(), BACKGROUND_STROKE_COLOR.getGreen(), 
						BACKGROUND_STROKE_COLOR.getBlue());
				strokeWeight(1);
				line(0,row,row,0);
			}
		}
	}	

	
// ******************* EVENT HANDLERS *************************************************************
	
	//-------------- mouse ------------------------------------------------------------------------
	@Override
	public void mouseClicked (MouseEvent e)
	{
		super.mouseClicked(e);
		this.m_AutoPlayingManager.setEnabled(false);
	}

	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		// TODO Auto-generated method stub
		super.mouseWheelMoved(e);				
		
	}
	
	/**
	 * MouseEvent handler
	 * @param e MouseEvent sent by Processing Framework to method
	 */
	protected void handleMouseEvent(MouseEvent e)
	{
		super.handleMouseEvent(e);
		// reset idle time if mouse event happened
		this.m_IdleTime = 0;

		// disable demo mode if needed
		if (this.m_AutoPlayingManager.Enabled())
		{
			this.m_AutoPlayingManager.Deactivate();
		}		
	}


// ******************* END OF EVENT HANDLERS ******************************************************

	/**
	 * Returns list of sounds
	 * @return list of sounds
	 */
	public Sound[] getSounds() 
	{

		return this.m_Sounds;
	}

	static public void main(String[] passedArgs) 
	{
		String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "sketch_130421a" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}
}
