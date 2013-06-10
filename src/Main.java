import infrastructure.ManagedPApplet;
import infrastructure.Sound.SoundManager;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.ArrayList;

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
	public static final String TITLE_IMAGE = "data/Images/title.png";
	public static final String SEARCH_IMAGE = "data/Images/search_small.png";

	// Colors:
	public static final Color BACKGROUND_COLOR = new Color(69, 70, 75, 200);
	public static final Color BACKGROUND_STROKE_COLOR = new Color(83, 83, 86);	


	// -------------- Preferences ---------------------------------------------------------------------------

	// General Services
	protected SoundManager m_SoundManager;
	protected PFont m_Font;

	// Resources
	private PImage m_TitleImage;
	private PImage m_SearchImage;
	
	// Members
	private String m_UserInput ="";
	private boolean m_isZoomingOut;
	private final ArrayList<SoundBubble> m_Sounds = new ArrayList<SoundBubble>();
	private final ArrayList<SoundCategory> m_SoundCategories = new ArrayList<SoundCategory>();

	public void setup()
	{
		super.setup();

		// set size and framerate - full screen
		size (displayWidth, displayHeight);
		frameRate(FRAMERATE);

		// load images, fonts, sounds...
		this.loadContent();

		// set graphics options
		ellipseMode(CENTER);	
		this.setBackgroundColor(BACKGROUND_COLOR);
				
		// Initialize services
		this.m_SoundManager = new SoundManager(this);

		this.createSoundClouds();
	}

	/**
	 * Creates all sound categories
	 */
	private void createSoundClouds() 
	{
		File[] soundCategoryList = (new File("Data/Sounds")).listFiles();

		int cellSize = 350;
		
		// bitmap used to track used locations of sound categories
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
			
			// search for random unused location to place cloud
			do
			{
				xCell  = (int) this.random(soundCategories.length);
				yCell  = (int) this.random(soundCategories.length);
			} while (soundCategories[xCell][yCell]);
			
			// mark place as taken
			soundCategories[xCell][yCell] = true;
			
			// add small random movement to prevent "grid" look
			int dx = (int)this.random(-cellSize / 4, cellSize / 4);
			int dy = (int)this.random(-cellSize / 4, cellSize / 4);	
			
			Point topLeft = new Point(xCell * cellSize, yCell * cellSize);
			System.out.println("TopLeft is " + topLeft.x + " " + topLeft.y);

			topLeft.translate(dx, dy);
			System.out.println("Translation is: " + dx + " " + dy + " for sound " + soundCategory.getName());
			System.out.println("New topleft is " + topLeft.x + " " + topLeft.y);
			
			this.m_SoundCategories .add(new SoundCategory(this, soundCategory.getName(), topLeft, cellSize));
		}
	}

	private void loadContent() 
	{
		// load images:
		m_TitleImage = loadImage(TITLE_IMAGE);
		m_SearchImage = loadImage(SEARCH_IMAGE);

		// load font
		m_Font = createFont("Georgia", 18);
		textFont(m_Font);
	}

	@Override
	public void draw()
	{		
		super.draw();
		
		if (this.m_TimeLeftForCameraAnimation < 0 && this.m_isZoomingOut)
		{
			this.m_isZoomingOut = false;
		}
		
		// draw title images
		this.image(m_TitleImage, 10, 20);
		this.image(m_SearchImage, -25, 50);
		
		// draw search:
		this.fill(0);
		this.textAlign(LEFT);
		this.textSize(14);
		this.text(m_UserInput, 30, 85);
	}

	@Override
	protected void drawBackground() 
	{
		super.drawBackground();

		if (DRAW_STROKES)
		{
			for (int row = 0; row < 5000; row += 20)
			{
				this.stroke(BACKGROUND_STROKE_COLOR.getRGB());
				this.strokeWeight(1);
				this.line(0,row,row,0);
			}
		}
	}	
	
	@Override
	public void keyPressed(KeyEvent event) 
	{
		super.keyPressed(event);
		
		if (event.getAction() == KeyEvent.PRESS)
		{
			System.out.println("Key code is " + event.getKeyCode());
			System.out.println("Key is " + event.getKey());
			if (event.getKey() == '\n')
			{
				searchByUser(this.m_UserInput);
				this.m_UserInput = "";
			} else if (Character.isAlphabetic(event.getKey()) || event.getKey() == ' ')
			{
				this.m_UserInput += key;
			} else if (event.getKeyCode() == 8)
			{
				this.m_UserInput = this.m_UserInput.substring(0, this.m_UserInput.length() - 1);
			}
		}
	}

	/**
	 * Highlights all sound bubbles recorded by
	 * the username matching the input
	 * @param i_UserInput user to search for
	 */
	private void searchByUser(String i_UserInput) 
	{
		
		for (SoundBubble sound : this.m_Sounds)
		{
			if (sound.getUser().toLowerCase().equals(i_UserInput.toLowerCase()))
			{
				sound.setOutline(Color.blue);
			} else
			{
				sound.removeOutline();
			}
		}
		
	}

	static public void main(String[] passedArgs) 
	{
		String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "Main" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}
	
	@Override
	public void returnViewToPrevious(int i_TimeToAnimate) 
	{
		super.returnViewToPrevious(i_TimeToAnimate);
		
		this.m_isZoomingOut = true;
	}

	/**
	 * Adds a new bubble to the searchable sound bubbles
	 * @param bubble to be added
	 */
	public void registerSoundBubble(SoundBubble bubble) 
	{
		
		this.m_Sounds.add(bubble);		
	}
}
