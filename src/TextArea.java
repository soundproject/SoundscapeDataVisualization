import java.awt.Point;

import processing.core.PVector;
import infrastructure.ManagedPApplet;
import infrastructure.SelfRegisteringComponent;


public class TextArea extends SelfRegisteringComponent {
	
	private int m_width;
	private int m_height;
	private Point m_TopLeft;
	private Point m_BottomRight;
	private int m_DisplayTimeLeft;
	private String m_Title;
	private String[] m_Messages;
	private int m_Alpha = 0;
	private int m_introTime = 250;
	private int m_TotalDisplayTime;

	public TextArea(ManagedPApplet i_Parent, int width, int height, Point i_TopLeft) 
	{
		super(i_Parent);
		
		this.m_width = width;
		this.m_height = height;
		this.m_TopLeft = i_TopLeft; 
		
		setVisible(false);
		
	}
	
	@Override
	public void update(long ellapsedTime) 
	{
				
		this.m_DisplayTimeLeft -= ellapsedTime;
//		System.out.println("Display time left " + this.m_DisplayTimeLeft);
//		System.out.println("fade in time? " + (this.m_DisplayTimeLeft > this.m_TotalDisplayTime - this.m_introTime) );
		
		// fade out
		if (this.m_DisplayTimeLeft < this.m_introTime)
		{
			float t = (float) ellapsedTime;
			this.m_Alpha -= (t / this.m_introTime) * 255;
		}
		
		// fade in
		if (this.m_DisplayTimeLeft >= this.m_TotalDisplayTime - this.m_introTime)
		{
			float t = (float)ellapsedTime;
			this.m_Alpha += (t / this.m_introTime) * 255;
		}
		
		if (this.m_DisplayTimeLeft < 0)
		{
			this.setVisible(false);
			this.setEnabled(false);
			

			
		}
	}

	@Override
	public void draw(long ellapsedTime) 
	{
		this.m_Parent.rectMode(this.m_Parent.CORNER);
		this.m_Parent.fill(255, this.m_Alpha);
		System.out.println("Alpha is " + this.m_Alpha);
		this.m_Parent.rect(m_TopLeft.x, m_TopLeft.y, this.m_width, this.m_height, 7);
		
		this.m_Parent.stroke(0);
		this.m_Parent.fill(0);
		this.m_Parent.textAlign(this.m_Parent.TOP, this.m_Parent.LEFT);
		this.m_Parent.text(this.m_Title, this.m_TopLeft.x + 30, this.m_TopLeft.y + 30);
		
		for (int i = 0; i < this.m_Messages.length; i++)
		{
			this.m_Parent.text(this.m_Messages[i], this.m_TopLeft.x + 30, this.m_TopLeft.y + 30 * (i + 2));
		}
	}
	
	public void displayMessage(String i_Title, String[] i_Messages, int i_Time)
	{
		if (!this.m_Enabled)
		{
			this.m_DisplayTimeLeft = i_Time + this.m_introTime * 2;
			this.m_TotalDisplayTime = i_Time + this.m_introTime * 2;
			this.m_Title = i_Title;
			this.m_Messages = i_Messages;
			this.setVisible(true);
			this.setEnabled(true);
		}
	}

}
