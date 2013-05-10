package infrastructure.Sound;

import infrastructure.ManagedPApplet;
import infrastructure.SelfRegisteringComponent;
import infrastructure.interfaces.IDeathListener;

import java.util.ArrayList;

import ddf.minim.AudioSample;

public class SoundByte extends SelfRegisteringComponent {
	
	private AudioSample m_Sample;
	private boolean m_Playing;
	private long m_PlayingTimeLeft;
	
	public SoundByte(ManagedPApplet i_Parent, AudioSample i_Sample) 
	{
		super(i_Parent);		
		this.m_Sample = i_Sample;
	}

	@Override
	public void update(long ellapsedTime) {
		if (this.m_Playing)
		{
			this.m_PlayingTimeLeft -= ellapsedTime;
			if (this.m_PlayingTimeLeft < 0)
			{
				this.die();
			}
		}		
	}
	

	private void die() 
	{
		for (IDeathListener listener : this.m_DeathListeners)
		{
			listener.handleDeath(this);
		}
		this.release();
	}

	@Override
	public void draw(long ellapsedTime) {
		// EMPTY		
	}
	
	public void play()
	{
		this.m_Playing = true;
		this.m_PlayingTimeLeft = this.m_Sample.length();
		this.m_Sample.trigger();
	}

	public void release() {
		this.m_Sample.close();		
		System.out.println("Sound byte closed: " + this.m_Sample.getMetaData().fileName());
	}

}
