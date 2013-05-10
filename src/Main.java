import infrastructure.ManagedPApplet;
import infrastructure.SoundManager;
import infrastructure.interfaces.IDrawable;
import infrastructure.interfaces.IUpdateable;

import java.awt.Color;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;


public class Main extends ManagedPApplet 
{

// -------------- Preferences ---------------------------------------------------------------------------
	// general graphics options
	public static final int FRAMERATE = 60;
	public static final int WIDTH = 1370;
	public static final int HEIGHT = 700;

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
	

	private Sound [] m_Sounds;
	private PImage m_LegendImage;
	private PImage m_TitleImage;
	protected PFont m_Font;	
	
	protected Sound m_SelectedSound = null;
	private AutoPlayingManager m_AutoPlayingManager;
	private int m_IdleTime;
	private int m_MaxIdleTime = 6500;
	
	protected SoundManager m_SoundManager;

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

		// initialize Sound Elements:
		m_Sounds = new Sound [NUMBER_OF_SOUNDS];
		for (int i = 0; i < this.m_Sounds.length; i++)
		{

			m_Sounds [i] = new Sound (new PVector(random(0, WIDTH), random(0, HEIGHT)), 
//									  new PVector(random(-15, 15), random (-15,15)), 
									  random(10,90), this, i);
		}
		
		// Initialize the "Demo Mode" manager
		this.m_AutoPlayingManager = new AutoPlayingManager(this);
		this.m_SoundManager = new SoundManager(this);
		this.registerMethod("mouseEvent", this);
	}
	
	private void loadContent() {
		
		// load images:
		m_LegendImage = loadImage (LEGEND_IMAGE);
		m_TitleImage = loadImage(TITLE_IMAGE);
		
		// load font
		m_Font = createFont("Georgia", 18);
		textFont(m_Font);
		
	}

	public void mouseEvent(MouseEvent e)
	{

		// reset idle time if mouse event happened
		this.m_IdleTime = 0;
		
		// disabled demo mode if needed
		if (this.m_AutoPlayingManager.Enabled())
		{
			this.m_AutoPlayingManager.Deactivate();
		}
				
		// handle mouse event
		switch (e.getAction()) {
			case MouseEvent.CLICK:
				this.mouseClicked(e);
				break;
			default:
				break;
		}		
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

		drawBackground(false);		
		
		super.draw();
		
		// draw title images
		image(m_LegendImage, 10,530);
//		image(m_TitleImage,10,20);
		
		// draw framerate
		fill(255);
		textAlign(LEFT, TOP);
		text(frameRate, 10, 10);
	}
	
	
	private void drawBackground(boolean drawStrokes) {
		
		background(BACKGROUND_COLOR.getRed(), BACKGROUND_COLOR.getGreen(), 
				BACKGROUND_COLOR.getBlue(), BACKGROUND_COLOR.getAlpha());

		if (drawStrokes)
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

	private ArrayList<Pulse> pulses = new ArrayList<Pulse>();	
	
	public void mouseClicked (MouseEvent e){
		
		this.m_AutoPlayingManager.setEnabled(!this.m_AutoPlayingManager.Enabled());
		
		
		
		
//		for (int n =0; n < this.m_Sounds.length; n++)
//		{
//			m_Sounds [n].select();
//		}
//		System.out.println("hi");
//		System.out.println("Origin is " + mouseX + "," + mouseY);
//		this.pulses.add(new Pulse(new PVector(mouseX, mouseY), 30f, Main.ACTIVE_COLOR, 3500, this));
	}


	static public void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "sketch_130421a" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}

	public Sound[] getSounds() {
		// TODO Auto-generated method stub
		
		return this.m_Sounds;
	}

}
