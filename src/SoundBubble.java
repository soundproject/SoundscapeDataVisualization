import infrastructure.Component;

import java.awt.Point;


public class SoundBubble extends Component 
{

	private float m_Radius;
	private Point m_Origin;
	private SoundInfo m_SoundInfo;
	private Main m_Parent;
	
	public SoundBubble(Main i_Parent) {
		super(i_Parent);
		// TODO Auto-generated constructor stub
		
		this.m_Parent = i_Parent;
		
	}

	@Override
	public void update(long ellapsedTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(long ellapsedTime) {
		// TODO Auto-generated method stub

	}

}
