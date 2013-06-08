import infrastructure.ManagedPApplet;
import infrastructure.SelfRegisteringComponent;

import java.awt.Color;
import java.awt.Point;


public class TextArea extends SelfRegisteringComponent {

	private int m_width;
	private int m_height;
	private Point m_TopLeft;
	private Point m_BottomRight;
	private int m_DisplayTimeLeft;
	private String m_Message;

	private int m_Alpha = 150;
	private int m_introTime = 250;
	private int m_TotalDisplayTime;
	private int m_TextSize;

	public TextArea(ManagedPApplet i_Parent, int width, int height, Point i_TopLeft, int i_textSize) 
	{
		super(i_Parent);

		this.m_width = width;
		this.m_height = height;
		this.m_TopLeft = i_TopLeft; 
		this.m_TextSize = i_textSize;

		setVisible(false);
		setEnabled(false);

	}

	@Override
	public void update(long ellapsedTime) 
	{

		this.m_DisplayTimeLeft -= ellapsedTime;

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
		this.m_Parent.strokeWeight(1);
		
		this.m_Parent.stroke(Color.blue.getRGB());
		this.m_Parent.rect(m_TopLeft.x, m_TopLeft.y, this.m_width, this.m_height, 7);

		this.m_Parent.noStroke();
		this.m_Parent.fill(0);
		this.m_Parent.textAlign(this.m_Parent.TOP, this.m_Parent.LEFT);
		this.m_Parent.textSize(m_TextSize);
		this.m_Parent.text(this.m_Message, this.m_TopLeft.x + 30, this.m_TopLeft.y + 30);
	}

	public void displayMessage(String i_Message, int i_Time)
	{

		this.m_DisplayTimeLeft = i_Time + this.m_introTime * 2;
		this.m_TotalDisplayTime = i_Time + this.m_introTime * 2;
		this.m_Message = i_Message;
		
		this.setVisible(true);
		this.setEnabled(true);
	}

}
