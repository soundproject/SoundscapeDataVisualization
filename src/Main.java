import infrastructure.ManagedPApplet;
import infrastructure.Sound.SoundManager;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.io.File;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
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
	public static final String LEGEND_IMAGE = "data/Images/COLORS.png";
	public static final String TITLE_IMAGE = "data/Images/title.png";
	public static final String SEARCH_IMAGE = "data/Images/search_small.png";

	// number of sounds
	public static final int NUMBER_OF_SOUNDS = 0;

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

	private Sound[] m_Sounds;
	private SoundCategory m_SoundCategory;
	private PImage m_LegendImage;
	private PImage m_TitleImage;

	private int m_IdleTime;

//	private AutoPlayingManager m_AutoPlayingManager;
	private PImage m_SearchImage;
	private String m_UserInput ="";
	private boolean m_isZoomingOut;



	public void setup()
	{
		super.setup();

		// set size and framerate
		size (displayWidth, displayHeight);
//		frame.setResizable(true);
		frameRate(FRAMERATE);

		// load images, fonts, sounds...
		this.loadContent();

		// set graphics options
		ellipseMode(CENTER);	
		this.setBackgroundColor(BACKGROUND_COLOR);
		
		this.createSoundClouds();

		
		// Initialize services
		this.m_SoundManager = new SoundManager(this);
		//		this.m_TextArea = new TextArea(this, 250, 100, new Point(WIDTH - 250, HEIGHT - 100));
	}

	private void createSoundClouds() 
	{
		File[] soundCategoryList = (new File("Data/Sounds")).listFiles();
		int cellWidth = this.width / (soundCategoryList.length);
		int cellHeight = this.height / (soundCategoryList.length);
		int cellSize = 250;
		boolean[][] soundCategories = new boolean[soundCategoryList.length / 2][soundCategoryList.length / 2];
		
		for (int i = 0; i < soundCategories.length; i++)
		{
			for (int j = 0; j < soundCategories[i].length; j++)
			{
				soundCategories[i][j] = false;
			}
		}
		
		for (File soundCategory : soundCategoryList)
		{
			int xCell;
			int yCell;
			int i = 0;
			do
			{
				xCell  = (int) this.random(soundCategories.length);
				yCell  = (int) this.random(soundCategories.length);
			} while (soundCategories[xCell][yCell]);
			
			soundCategories[xCell][yCell] = true;
			int dx = 0;
			int dy = 0;
//			if ((xCell > 0 && soundCategories[xCell - 1][yCell]) || 
//					(xCell < soundCategories.length - 1 && soundCategories[xCell + 1][yCell]))
//			{
				dy = (int)this.random(-cellSize / 4, cellSize / 4);				
//			}
			
//			if ((yCell > 0 && soundCategories[xCell][yCell - 1]) || 
//					(yCell < soundCategories.length - 1 && soundCategories[xCell][yCell + 1]))
//			{
				dx = (int)this.random(-cellSize / 4, cellSize / 4);				
//			}

			
			Point topLeft = new Point(xCell * cellSize, yCell * cellSize);
			System.out.println("TopLeft is " + topLeft.x + " " + topLeft.y);

			topLeft.translate(dx, dy);
			System.out.println("Translation is: " + dx + " " + dy + " for sound " + soundCategory.getName());
			System.out.println("New topleft is " + topLeft.x + " " + topLeft.y);
			
			new SoundCategory(this, soundCategory.getName(), topLeft, cellSize);
		}
		
	}

	private void loadContent() {

		// load images:
		m_LegendImage = loadImage (LEGEND_IMAGE);
		m_TitleImage = loadImage(TITLE_IMAGE);
		m_SearchImage = loadImage(SEARCH_IMAGE);

		// load font
		m_Font = createFont("Georgia", 18);
		textFont(m_Font);

	}


	@Override
	public void pre() {

//		// if demo mode is not enabled, aggregate idleTime
//		if (!this.m_AutoPlayingManager.Enabled())
//		{
//			this.m_IdleTime += (millis() - this.m_PrevUpdateTime);
//
//			// Activate demo mode if max idle time reached
//			if (this.m_IdleTime >= AutoPlayingManager.IDLE_USER_TIME)
//			{
//				this.m_AutoPlayingManager.Activate();
//			}
//		}

		super.pre();		
	}


	public void draw()
	{		
		
		super.draw();
		
		if (this.m_TimeLeftForCameraAnimation < 0 && this.m_isZoomingOut)
		{
			this.m_isZoomingOut = false;
			for (Sound sound : this.m_Sounds) {
				sound.showInfo();
			}
		}
		
		// draw title images
		//		image(m_LegendImage, 10,530);
		image(m_TitleImage, 10, 20);
		image(m_SearchImage, -25, 50);
		
		// draw search:
		fill(0);
		textAlign(LEFT);
		text(m_UserInput, 30, 85);
	}

	@Override
	protected void drawBackground() 
	{
		super.drawBackground();

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
//		this.m_AutoPlayingManager.setEnabled(false);
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
//		if (this.m_AutoPlayingManager.Enabled())
//		{
//			this.m_AutoPlayingManager.Deactivate();
//		}		
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
	
	@Override
	public void keyPressed(KeyEvent event) 
	{
		// TODO Auto-generated method stub
		super.keyPressed(event);
		
		if (event.getAction() == KeyEvent.PRESS)
		{
			System.out.println("Key code is " + event.getKeyCode());
			System.out.println("Key is " + event.getKey());
			if (event.getKey() == '\n')
			{
				search(this.m_UserInput);
				this.m_UserInput = "";
				System.out.println("GOT IT");
			} else if (Character.isAlphabetic(event.getKey()))
			{
				this.m_UserInput += key;
			} else if (event.getKeyCode() == 8)
			{
				this.m_UserInput = this.m_UserInput.substring(0, this.m_UserInput.length() - 1);
			}
		}
	}
	 
	

	private void search(String i_UserInput) 
	{
		
		for (Sound sound : this.m_Sounds)
		{
//			System.out.println("Checking " + sound.getName().toLowerCase());
			if (sound.getName().toLowerCase().equals(i_UserInput.toLowerCase()))
			{
				sound.setColor(Color.green);
			} else
			{
				sound.setColor(INACTIVE_COLOR);
			}
		}
		
	}

	static public void main(String[] passedArgs) 
	{
		String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "Main" };
//		String[] appletArgs = new String[] { "Main" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}

	@Override
	public void moveToCenter(PVector i_Center, float i_ZoomFactor,
			int i_TimeToAnimate) {
		// TODO Auto-generated method stub
		super.moveToCenter(i_Center, i_ZoomFactor, i_TimeToAnimate);
		
		for (Sound sound : this.m_Sounds) {
			sound.hideInfo();
		}
	}
	
	@Override
	public void returnViewToPrevious(int i_TimeToAnimate) {
		// TODO Auto-generated method stub
		super.returnViewToPrevious(i_TimeToAnimate);
		
		this.m_isZoomingOut = true;
	}




}
