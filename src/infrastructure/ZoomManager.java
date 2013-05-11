package infrastructure;

import infrastructure.interfaces.IDrawable;
import processing.core.PShape;
import processing.core.PVector;

public class ZoomManager extends SelfRegisteringComponent
{
//	private ManagedPApplet m_Parent;
	private PVector m_CurrentCenter;
	private PVector m_TargetCenter;
	private boolean m_isMoving;
	private PVector translationVector;


	public ZoomManager(ManagedPApplet i_Parent) 
	{
		super(i_Parent);
		this.m_CurrentCenter = new PVector();
		this.m_TargetCenter = new PVector();
		this.setVisible(false);

	}
	
	public void fillScreenWithShape(IDrawable i_Drawable)
	{
		System.out.println("Filling screen with shape...");
		System.out.println("Shape center is " + i_Drawable.getCenter());
		System.out.println("Current center is " + this.m_CurrentCenter);
		this.setTargetCenter(i_Drawable.getCenter());
		translationVector = calcTranslationVector(i_Drawable);
		System.out.println("Vector is " + translationVector);
		
		// distance to center:
		float distanceToCenter = translationVector.mag();
		System.out.println("Length is " + distanceToCenter);
		
		// normalize vector
		translationVector.normalize();
		System.out.println("normalized? " + translationVector);
		
		// TODO: calculate needed speed:		
		
		// move center
//		this.setCurrentCenter(PVector.mult(translationVector, distanceToCenter));
		
	}

	private PVector calcTranslationVector(IDrawable i_Drawable) 
	{
		// find direction to center of shape from current center		
		PVector directionToCenter = PVector.sub(i_Drawable.getCenter(), this.m_CurrentCenter);				
		
		return directionToCenter;
	}
	
	public void setTargetCenter(PVector i_Vector)
	{
		this.m_TargetCenter.set(i_Vector);
		this.setEnabled(true);
	}
	
	public void setCurrentCenter(PVector i_Vector)
	{
		System.out.println("Previous center was: " +  this.m_CurrentCenter);
		this.m_CurrentCenter.set(i_Vector);
		this.setEnabled(true);
		System.out.println("Center set to " + this.m_CurrentCenter);
	}
	
	public boolean isMoving()
	{
//		System.out.println("Current center is: " + this.m_CurrentCenter);
//		System.out.println("Target Center is " + this.m_TargetCenter);
		boolean result = this.m_CurrentCenter.x != this.m_TargetCenter.x 
				&& this.m_CurrentCenter.y != this.m_TargetCenter.y;
		System.out.println("Result is " + result);
		return this.m_CurrentCenter.x != this.m_TargetCenter.x 
				&& this.m_CurrentCenter.y != this.m_TargetCenter.y;
	}

	@Override
	public void update(long ellapsedTime) 
	{
		if (!this.isMoving())
		{
			System.out.println("Done moving - disabling");
			this.setEnabled(false);
		} else
		{
			this.setCurrentCenter(PVector.add(m_CurrentCenter, PVector.mult(translationVector, 10)));
			this.m_Parent.setCenter(this.m_CurrentCenter);
//			System.out.println("Current center is " + this.m_CurrentCenter);
//			System.out.println("Target center is " + this.m_TargetCenter);
		}
		
	}

	@Override
	public void draw(long ellapsedTime) 
	{
		// TODO Auto-generated method stub
		
	}
	
}
