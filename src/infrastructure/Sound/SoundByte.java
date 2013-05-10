package infrastructure.Sound;

import infrastructure.ManagedPApplet;
import infrastructure.SelfRegisteringComponent;
import infrastructure.interfaces.IDeathListener;

import java.util.ArrayList;

import ddf.minim.AudioSample;

/**
 * Wrapper for AudioSample that reports its length and allows
 * simple playing and autoremoval from parent app.
 * Used by {@link SoundManager}
 * Relies on Minim.AudioSample
 * @author Gadi
 */
public class SoundByte extends SelfRegisteringComponent {
	
	private AudioSample m_Sample;
	private boolean m_Playing;
	private long m_PlayingTimeLeft;
	
	/**
	 * Create new SoundByte from sample
	 * @param i_Parent parent app
	 * @param i_Sample AudioSample to wrap
	 */
	public SoundByte(ManagedPApplet i_Parent, AudioSample i_Sample) 
	{
		super(i_Parent);		
		this.m_Sample = i_Sample;

		// disable draw() calls from parent
		this.setVisible(false);
	}

	@Override
	public void update(long ellapsedTime) 
	{
	
		// is sample is playing keep track of it
		if (this.m_Playing)
		{
			this.m_PlayingTimeLeft -= ellapsedTime;
			if (this.m_PlayingTimeLeft < 0)
			{
				// audio sample finished - trigger die() event
				this.die();
			}
		}		
	}
	
	@Override
	protected void die() 
	{
		super.die();
		
		// release audio resources
		this.release();
	}

	@Override
	public void draw(long ellapsedTime) 
	{
		// EMPTY		
	}
	
	/**
	 * Plays the audio sample and automatically removes it when done
	 */
	public void play()
	{
		this.m_Playing = true;
		this.m_PlayingTimeLeft = this.m_Sample.length();
		this.m_Sample.trigger();
	}

	/**
	 * release audio resources
	 */
	protected void release() 
	{
		this.m_Sample.close();		
		System.out.println("Sound byte closed: " + this.m_Sample.getMetaData().fileName());
	}

}
