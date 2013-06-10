import infrastructure.ManagedPApplet;
import infrastructure.SelfRegisteringComponent;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class SoundCategory extends SelfRegisteringComponent 
{
	
	private String m_Name;
	private Rectangle m_BoundingRectangle;
	private boolean m_MouseOver;
	private final ArrayList<SoundBubble> m_SoundBubbles = new ArrayList<SoundBubble>();
	private Main m_Parent;
	private boolean m_Active;
	
	
	/**
	 * Create a new soundcategory for the given name in the location provided
	 * @param i_Parent parent app
	 * @param i_Name sound category name
	 * @param i_TopLeft coordinate for top left corner of bounding box
	 * @param i_Size width and height for bounding box
	 */
	public SoundCategory(Main i_Parent, String i_Name, Point i_TopLeft, int i_Size) 
	{
		super(i_Parent);
		this.m_Parent = i_Parent;
		this.m_Name = i_Name;
		this.m_BoundingRectangle = new Rectangle(i_TopLeft);
		this.m_BoundingRectangle.width = i_Size;
		this.m_BoundingRectangle.height = i_Size;
		
		this.createSoundBubbles();
	}
	
	private void createSoundBubbles() 
	{
		File baseDirectory = new File("Data/Sounds");
		System.out.println(baseDirectory.getAbsolutePath());
		File directory = new File(baseDirectory, this.m_Name);

		HashMap<String, SoundInfo> soundInfoDictInfoDict = readSoundInfoFromDirectory(directory);
		
		// Get recognized sounds:
		File[] recognizedSounds = new File(directory, "Recognized").listFiles();
		
		for (File file : recognizedSounds)
		{
			SoundInfo soundInfo = soundInfoDictInfoDict.get(file.getName());
			SoundBubble bubble = new SoundBubble(this.m_Parent, 25f, this.nextSoundLocation(), this.m_Name);
			bubble.setSoundInfo(soundInfo);
			this.m_SoundBubbles.add(bubble);
			this.m_Parent.registerSoundBubble(bubble);
		}			
		
		// Get unrecognized sounds
		File[] unrecognizedSounds = new File(directory, "Unrecognized").listFiles();
		
		for (File file : unrecognizedSounds)
		{
			SoundInfo soundInfo = soundInfoDictInfoDict.get(file.getName());
			SoundBubble bubble = new SoundBubble(this.m_Parent, 25f, this.nextSoundLocation(),  this.m_Name);
			bubble.setSoundInfo(soundInfo);
			this.m_SoundBubbles.add(bubble);
			this.m_Parent.registerSoundBubble(bubble);
		}
	}

	/**
	 * Reads csv file containing sound recording metadata
	 * and loads them into a dict. Format is:
	 * FileName, recognized/unrecognized, username, date (dd/mm/yy HH:MM 24 hour format)
	 * @param directory to load info from
	 * @return dictionary mapping filename to its soundInfo object
	 */
	private HashMap<String, SoundInfo> readSoundInfoFromDirectory(File directory) 
	{
		String infoFile = "Sounds/" + directory.getName() + "/sound.info";
		BufferedReader reader = this.m_Parent.createReader(infoFile);
		
		Scanner s = new Scanner(reader);
		
		HashMap<String, SoundInfo> result = new HashMap<String, SoundInfo>();
		
		while (s.hasNextLine())
		{
			String[] split = this.m_Parent.split(s.nextLine(), ',');
			File file = new File(directory, split[1].trim() + "/" + split[0].trim());
			boolean recognized = split[1].toLowerCase().trim().equals("recognized");
			SoundInfo info = new SoundInfo(file, recognized, split[2].trim(), split[3].trim());
			result.put(split[0].trim(), info);
		}
		
		return result;
	}

	/**
	 * Set the center of this category to (x, y)
	 * @param x coordinate to center on
	 * @param y coordinate to center on
	 */
	public void setCenter(int x, int y)
	{
		this.m_BoundingRectangle.setLocation(x, y);
	}
	

	/**
	 * returns a valid location for new sound bubble
	 * @return valid location for new sound bubble
	 */
	private Point nextSoundLocation() 
	{
		
		Point result = null;
		
		// bounding rectangle with the calculated angle and distance
		do
		{
			// Generate random angle
			float angle = this.m_Parent.random(0, this.m_Parent.TWO_PI);
			
			// Generate random distance within the bounding rectangle
			float distance = this.m_Parent.random(20, this.m_BoundingRectangle.width / 2 - 20);
			
			// Calculate result location based on a circle centered on the center of the 
			result = new Point((int)this.m_BoundingRectangle.getCenterX() + (int)(this.m_Parent.cos(angle) * distance), 
						   		(int)this.m_BoundingRectangle.getCenterY() + (int)(this.m_Parent.sin(angle) * distance));
		}
		while(!this.isBubbleLocationLegal(result));
		
		return result;
	}

	/**
	 * returns true iff location is valid for new sound bubble
	 * @return true iff location is valid for new sound bubble
	 */
	private boolean isBubbleLocationLegal(Point i_Location) 
	{

		this.m_Parent.textSize(35);
		int width = (int)this.m_Parent.textWidth(this.m_Name) + 25;
		int height = 60;
		Rectangle textBounds = new Rectangle((int)(this.m_BoundingRectangle.getCenterX() - width / 2), 
											 (int)(this.m_BoundingRectangle.getCenterY() - height / 2), 
											 width, height);
		return !textBounds.contains(i_Location);
	}

	@Override
	public void update(long ellapsedTime) 
	{
		// check if mouse is inside the bounding rectangle of the cloud		
		this.m_MouseOver = this.m_BoundingRectangle.contains(new Point((int)this.m_Parent.getWorldMouse().x, 
																	   (int)this.m_Parent.getWorldMouse().y));
		
		this.m_Active = m_MouseOver;
		
		for(SoundBubble bubble : this.m_SoundBubbles)
		{
			bubble.update(ellapsedTime);
			if (m_MouseOver || m_Active)
			{
				bubble.colorize();
			} else
			{
				bubble.removeColor();
			}
		}
	}

	@Override
	public void draw(long ellapsedTime) 
	{
		
		// draw outline of circle in debug mode
		if (ManagedPApplet.DEBUG)
		{
			this.m_Parent.ellipseMode(this.m_Parent.CENTER);
			this.m_Parent.noFill();
			this.m_Parent.strokeWeight(1.1f);
			Color fillColor = Color.yellow.darker();
			this.m_Parent.stroke(fillColor.getRGB());
			this.m_Parent.ellipse((float)this.m_BoundingRectangle.getCenterX(), (float)this.m_BoundingRectangle.getCenterY(),
								  this.m_BoundingRectangle.width, this.m_BoundingRectangle.height);
		}
		
		if (this.m_MouseOver)
		{
			drawGlow();
		}

		for (SoundBubble bubble : this.m_SoundBubbles)
		{
			bubble.draw(ellapsedTime);
		}
		

		this.m_Parent.fill(255);
		this.m_Parent.textAlign(this.m_Parent.CENTER, this.m_Parent.CENTER);
		this.m_Parent.textSize(35);
		this.m_Parent.text(this.m_Name, (float)this.m_BoundingRectangle.getCenterX(), (float)this.m_BoundingRectangle.getCenterY());

	}
	
	public void activate()
	{
		this.m_Active = true;
	}
	
	public void deactivate()
	{
		this.m_Active = false;
	}

	private void drawGlow() 
	{
		this.m_Parent.ellipseMode(this.m_Parent.CENTER);
		this.m_Parent.strokeWeight(1.1f);
		Color fillColor = Color.white.darker();
		this.m_Parent.fill(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), 50);
		this.m_Parent.noStroke();
		this.m_Parent.ellipse((float)this.m_BoundingRectangle.getCenterX(), (float)this.m_BoundingRectangle.getCenterY(), 
							  this.m_BoundingRectangle.width, this.m_BoundingRectangle.height);
	}
}
