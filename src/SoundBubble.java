import infrastructure.Component;
import infrastructure.interfaces.IDeathListener;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import processing.core.PVector;
import processing.event.MouseEvent;


public class SoundBubble extends Component implements IDeathListener 
{

	private static final Color INACTIVE_COLOR = Color.gray;
	private float m_Radius;
	private Point m_Origin;
	private SoundInfo m_SoundInfo;
	private Main m_Parent;
	private float m_Size;
	private Color m_Color;
	private boolean m_MouseOver = false;
	private String m_Category;
	private int m_numberOfPulses = 4;
	private int m_Pulses = 0;
	private boolean m_Pulse;
	private long m_currentAnimationTime;
	private int m_spacingTime;
	private int m_TimeBetweenPulses = 250;
	private long m_totalAnimationTime;
	private boolean m_animating;
	private TextArea m_Popup;
	private boolean m_Outline;
	private Color m_OutlineColor;
	private boolean m_IsColor;
	private final ArrayList<Pulse> m_ActivePulses = new ArrayList<Pulse>();
	
	public SoundBubble(Main i_Parent, float i_Radius, Point i_Origin, String i_Category) 
	{
		super(i_Parent);
		
		this.m_Parent = i_Parent;
		this.m_Radius = i_Radius;
		this.m_Origin = i_Origin;
		this.m_Category = i_Category;
		
		this.m_Parent.registerMethod("mouseEvent", this);
		

		
	}
	
	public void setSoundInfo(SoundInfo i_SoundInfo)
	{
		this.m_SoundInfo = i_SoundInfo;
		this.m_Color = this.m_SoundInfo.isRecognized() ? Color.green : Color.red;
		
		
		float popupWidth = this.m_Parent.textWidth(this.m_SoundInfo.toString());
		System.out.println("Text width is: " + popupWidth);
		float popupHeight = 5 * 14;
		Point TopLeft = new Point(this.m_Origin);
		TopLeft.translate(0, (int)(- this.m_Radius - popupHeight));
		this.m_Popup = new TextArea(this.m_Parent, (int)(popupWidth * 1.2), (int)(popupHeight * 1.2), TopLeft, 14);
		this.m_Popup.displayMessage(this.m_SoundInfo.toString(), 2500);
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

	private void handleMouseClick(MouseEvent e) 
	{
		if (this.m_MouseOver)
		{
			
			this.activate();
		}
		
	}

	private void activate() 
	{
		this.m_animating = true;
		this.m_Pulse = true;
		m_totalAnimationTime = this.m_Parent.m_SoundManager.loadAndPlaySound(this.m_SoundInfo.getRelativeFileName());		
	}

	@Override
	public void update(long ellapsedTime) 
	{
		checkMouseOver();
		
		
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
				Pulse p = new Pulse(new PVector(this.m_Origin.x, this.m_Origin.y), this.m_Radius, this.m_Color, 3500, this.m_Parent);
				p.addDeathListener(this);
				this.m_ActivePulses.add(p);

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
		
		for (Pulse pulse : this.m_ActivePulses)
		{
			pulse.setColor(this.m_IsColor ? this.m_Color : INACTIVE_COLOR);
		}
		
	}

	private void deactivate() 
	{
		this.m_Pulse = false;
		this.m_currentAnimationTime = 0;

	}

	private void checkMouseOver() 
	{
		PVector worldMouse = this.m_Parent.getWorldMouse();
		Point worldMousePoint = new Point((int)worldMouse.x, (int)worldMouse.y);
		this.m_MouseOver = worldMousePoint.distance(m_Origin) < this.m_Radius / 2;
		
	}

	@Override
	public void draw(long ellapsedTime) 
	{
		if (this.m_Outline)
		{
			this.m_Parent.stroke(this.m_OutlineColor.getRGB());
			this.m_Parent.strokeWeight(4);
		} else
		{
			this.m_Parent.noStroke();			
		}
		
		Color color = this.m_IsColor ? this.m_Color : INACTIVE_COLOR;
		this.m_Parent.fill(color.getRGB());
		this.m_Parent.ellipse(this.m_Origin.x, this.m_Origin.y, this.m_Radius, this.m_Radius);
				
		if (this.m_MouseOver)
		{
			this.m_Popup.setVisible(true);
		} else
		{
			this.m_Popup.setVisible(false);
		}		
	}

	@Override
	public void handleDeath(Object object) 
	{
		this.m_Pulses--;
		Pulse p = (Pulse) object;
		this.m_ActivePulses.remove(p);
	}

	public void removeOutline() 
	{
		this.m_Outline = false;		
	}

	public void setOutline(Color i_OutlineColor) 
	{
		this.m_Outline = true;
		this.m_OutlineColor = i_OutlineColor;
		
	}

	public String getUser() {
		
		return this.m_SoundInfo.getUser();
	}

	public void colorize() 
	{		
		this.m_IsColor = true;
	}

	public void removeColor() 
	{
		this.m_IsColor = false;
		
	}

}
