import infrastructure.ManagedPApplet;
import infrastructure.SelfRegisteringComponent;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import android.graphics.Color;


public class SoundCategory extends SelfRegisteringComponent 
{
	
	private String m_Name;
	private final ArrayList<SoundBubble> m_Sounds = new ArrayList<SoundBubble>();
	private Rectangle m_BoundingRectangle;
	private boolean m_MouseOver;
	private float m_MaxAlpha = 70f;
	private float m_MinAlpha = 0f;
	
	public SoundCategory(Main i_Parent, String i_Name, Point i_TopLeft) 
	{
		super(i_Parent);
		this.m_Name = i_Name;
		this.m_BoundingRectangle = new Rectangle(i_TopLeft);
		this.m_BoundingRectangle.width = 300;
		this.m_BoundingRectangle.height = 300;
	}
	
	public void setCenter(int x, int y)
	{
		this.m_BoundingRectangle.setLocation(x, y);
	}
	
	public void addSound(SoundBubble i_Sound)
	{
		if (!this.m_Sounds.contains(i_Sound))
		{
			this.m_Sounds.add(i_Sound);
//			i_Sound.setLocation(this.nextSoundLocation());
			i_Sound.setVisible(true);
		}
	}

	private Point nextSoundLocation() 
	{
		// TODO Auto-generated method stub
		Point result = null;
		float angle = this.m_Parent.random(0, this.m_Parent.TWO_PI);
		float distance = this.m_Parent.random(20, this.m_BoundingRectangle.width / 2 - 20);
		result = new Point((int)this.m_BoundingRectangle.getCenterX() + (int)(this.m_Parent.cos(angle) * distance), 
						   (int)this.m_BoundingRectangle.getCenterY() + (int)(this.m_Parent.sin(angle) * distance));
		
		
		return result;
	}

	@Override
	public void update(long ellapsedTime) 
	{
		if (this.m_BoundingRectangle.contains(new Point((int)this.m_Parent.getWorldMouse().x, (int)this.m_Parent.getWorldMouse().y)))
		{
			this.m_MouseOver = true;
		} else
		{
			this.m_MouseOver = false;
		}
	}

	@Override
	public void draw(long ellapsedTime) 
	{
		if (ManagedPApplet.DEBUG)
		{
			this.m_Parent.ellipseMode(this.m_Parent.CENTER);
			this.m_Parent.noFill();
			this.m_Parent.strokeWeight(1.1f);
			java.awt.Color fillColor = java.awt.Color.yellow.darker();
			this.m_Parent.stroke(fillColor.getRGB());
			this.m_Parent.ellipse((float)this.m_BoundingRectangle.getCenterX(), (float)this.m_BoundingRectangle.getCenterY(), this.m_BoundingRectangle.width, this.m_BoundingRectangle.height);
		}
		
		if (this.m_MouseOver)
		{
			drawGlow();
		}

		
		this.m_Parent.fill(255);
		this.m_Parent.textAlign(this.m_Parent.CENTER, this.m_Parent.CENTER);
		this.m_Parent.text(this.m_Name, (float)this.m_BoundingRectangle.getCenterX(), (float)this.m_BoundingRectangle.getCenterY());
		
//		drawCircle();

	}

	private void drawCircle() 
	{
		Point result = nextSoundLocation();
		this.m_Parent.fill(java.awt.Color.green.getRGB());
		this.m_Parent.noStroke();
		this.m_Parent.ellipse(result.x, result.y, 20, 20);		
	}

	private void drawGlow() 
	{
		this.m_Parent.ellipseMode(this.m_Parent.CENTER);
		this.m_Parent.noFill();
		this.m_Parent.strokeWeight(1.1f);
		java.awt.Color fillColor = java.awt.Color.yellow.darker();
		this.m_Parent.stroke(fillColor.getRGB());
		this.m_Parent.ellipse((float)this.m_BoundingRectangle.getCenterX(), (float)this.m_BoundingRectangle.getCenterY(), this.m_BoundingRectangle.width, this.m_BoundingRectangle.height);
//		for (int i = 0; i < this.m_BoundingRectangle.width; i++)
//		{
//			java.awt.Color fillColor = java.awt.Color.yellow.darker();
//			float alpha = this.m_MaxAlpha - this.m_MinAlpha;
//			alpha /= this.m_BoundingRectangle.width;
//			alpha *= i;
//			alpha = this.m_MaxAlpha - alpha;
//			this.m_Parent.stroke(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), alpha);
//			this.m_Parent.ellipse((float)this.m_BoundingRectangle.getCenterX(), (float)this.m_BoundingRectangle.getCenterY(), i, i);
//		}
		
	}

}
