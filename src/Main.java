import java.awt.Color;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;


public class Main extends PApplet 
{
	public static final int FRAMERATE = 60;
	public static final int WIDTH = 1370;
	public static final int HEIGHT = 700;
	public static final int NUMBER_OF_SOUNDS = 1;

	public static final String LEGEND_IMAGE = "COLORS.png";
	public static final String TITLE_IMAGE = "title.png";

	public static final Color BACKGROUND_COLOR = new Color(69, 70, 75, 200);
	public static final Color BACKGROUND_STROKE_COLOR = new Color(83, 83, 86);
	
	public static final Color PLEASANT_COLOR = new Color(13, 0, 202);
	public static final Color UNPLEASANT_COLOR = new Color(250, 20, 202);
	
	public static final Color INACTIVE_COLOR = new Color(128, 128, 128);
	public static final Color ACTIVE_COLOR = Color.orange.darker();

	private Sound [] m_Sounds;
	private PImage m_LegendImage;
	private PImage m_TitleImage;
	
	protected Sound m_SelectedSound = null;

	public void setup()
	{
		size (WIDTH, HEIGHT, P2D);
		frameRate(FRAMERATE);
		ellipseMode(CENTER);

		m_LegendImage = loadImage (LEGEND_IMAGE);
		m_TitleImage = loadImage(TITLE_IMAGE);

		m_Sounds = new Sound [NUMBER_OF_SOUNDS];
		
		float redStep = (UNPLEASANT_COLOR.getRed() - PLEASANT_COLOR.getRed()) / this.m_Sounds.length;
		float greenStep = (UNPLEASANT_COLOR.getGreen() - PLEASANT_COLOR.getGreen()) / this.m_Sounds.length;
		float blueStep = (UNPLEASANT_COLOR.getBlue() - PLEASANT_COLOR.getBlue()) / this.m_Sounds.length;
		
		for (int i = 0; i < this.m_Sounds.length; i++)
		{

			m_Sounds [i] = new Sound (new PVector(random(0, WIDTH), random(0, HEIGHT)), 
//									  new PVector(random(-15, 15), random (-15,15)), 
									  random(10,90), this, i);
		}
		
		this.registerMethod("pre", this);
//		this.registerMethod("mouseEvent", this);
	}
	
	public void mouseEvent(MouseEvent e)
	{

		switch (e.getAction()) {
			case MouseEvent.CLICK:
				this.mouseClicked(e);
				break;
			default:
				break;
		}

	}
	
	public void pre()
	{
		long ellapsedTime = millis() - m_prevUpdateTime;
		
		for (Pulse pulse : pulses) {
			pulse.update(ellapsedTime / 1000f);
		}
		
		for (Sound sound : this.m_Sounds)
		{
			sound.pre(ellapsedTime);
		}
		
		m_prevUpdateTime = millis();
	}
	
	private long m_prevDrawTime;
	private long m_prevUpdateTime;


	public void draw()
	{
		long ellapsedTime = millis() - m_prevDrawTime; 
		clear();
		drawBackground();				
		
		for (Pulse pulse : pulses) {
			
			pulse.draw(ellapsedTime / 1000f);
		}
		
		for (Sound sound : this.m_Sounds)
		{
			sound.draw();
		}
		
		// draw title images
		image (m_LegendImage, 10,530);
		image (m_TitleImage,10,20);
		
		m_prevDrawTime = millis();
	}
	
	
	private void drawBackground() {
		
		background(BACKGROUND_COLOR.getRed(), BACKGROUND_COLOR.getGreen(), 
				BACKGROUND_COLOR.getBlue(), BACKGROUND_COLOR.getAlpha());

//		for (int row=0; row<5000; row=row+20)
//		{
//			stroke(BACKGROUND_STROKE_COLOR.getRed(), BACKGROUND_STROKE_COLOR.getGreen(), 
//					BACKGROUND_STROKE_COLOR.getBlue());
//			strokeWeight(1);
//			line(0,row,row,0);
//		}
	}

	private ArrayList<Pulse> pulses = new ArrayList<Pulse>();	
	
	public void mouseClicked (MouseEvent e){
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

}
