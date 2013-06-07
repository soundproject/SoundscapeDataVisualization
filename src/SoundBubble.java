import infrastructure.Component;

import java.awt.Color;
import java.awt.Point;

import processing.core.PVector;
import processing.event.MouseEvent;


public class SoundBubble extends Component 
{

	private float m_Radius;
	private Point m_Origin;
	private SoundInfo m_SoundInfo;
	private Main m_Parent;
	private float m_Size;
	private Color m_Color;
	private boolean m_MouseOver = false;
	private String m_Category;
	
	public SoundBubble(Main i_Parent, float i_Radius, Point i_Origin, String i_Category) 
	{
		super(i_Parent);
		
		this.m_Parent = i_Parent;
		this.m_Radius= i_Radius;
		this.m_Origin = i_Origin;
		this.m_Category = i_Category;
		
		this.m_Parent.registerMethod("mouseEvent", this);
		
	}
	
	public void setSoundInfo(SoundInfo i_SoundInfo)
	{
		this.m_SoundInfo = i_SoundInfo;
		this.m_Color = this.m_SoundInfo.isRecognized() ? Color.green : Color.red;
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
			
			this.m_Parent.m_SoundManager.loadAndPlaySound(this.m_SoundInfo.getRelativeFileName());
		}
		
	}

	@Override
	public void update(long ellapsedTime) 
	{
		checkMouseOver();
	}

	private void checkMouseOver() 
	{
		PVector worldMouse = this.m_Parent.getWorldMouse();
		Point worldMousePoint = new Point((int)worldMouse.x, (int)worldMouse.y);
		this.m_MouseOver = worldMousePoint.distance(m_Origin) < this.m_Radius;		
		
		if (m_MouseOver)
		{
			System.out.println("Found");
			this.m_Parent.exit();
		}
	}

	@Override
	public void draw(long ellapsedTime) 
	{
		
		this.m_Parent.noStroke();
		this.m_Parent.fill(this.m_Color.getRGB());
		
		this.m_Parent.ellipse(this.m_Origin.x, this.m_Origin.y, this.m_Radius, this.m_Radius);

	}

}
