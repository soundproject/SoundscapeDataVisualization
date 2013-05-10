package infrastructure.Sound;

import infrastructure.ManagedPApplet;
import infrastructure.SelfRegisteringComponent;
import infrastructure.interfaces.IDeathListener;

import java.util.ArrayList;

import ddf.minim.AudioSample;
import ddf.minim.Minim;

public class SoundManager extends SelfRegisteringComponent implements IDeathListener
{
	
	private Minim m_Minim;
	
	private ArrayList<SoundByte> m_SoundBytes = new ArrayList<SoundByte>();
	private boolean m_SampleIsPlaying;
	private int m_TimeLeftToPlay;
	
	public SoundManager(ManagedPApplet i_Parent) 
	{
		super(i_Parent);
//		this.m_Parent = i_Parent;
		this.m_Minim = new Minim(i_Parent);
		this.m_Minim.debugOn();
		this.setVisible(false);
		this.setEnabled(false);
	}
	
	public int loadAndPlaySound(String i_FileName)
	{
		AudioSample sample = this.loadSample(i_FileName);
		SoundByte soundByte = new SoundByte(m_Parent, sample);
		this.m_SoundBytes.add(soundByte);
		soundByte.addDeathListener(this);
//		this.playSample(sample);
		soundByte.play();
		return sample.length();
	}

	private void playSample(AudioSample sample) {
		sample.trigger();
		int length = sample.length();
		this.m_SampleIsPlaying = true;
		this.m_TimeLeftToPlay = length;		
	}

	private AudioSample loadSample(String i_FileName) 
	{					
		return this.m_Minim.loadSample(i_FileName);
	}

	@Override
	public void update(long ellapsedTime) {
		// EMPTY - all work done by SoundByte
		
	}

	private boolean isPlaying() {
		return this.m_SampleIsPlaying;
	}

	@Override
	public void draw(long ellapsedTime) 
	{
		// EMPTY - component is not drawable		
	}

	@Override
	public void handleDeath(Object object) 
	{
		SoundByte soundByte = (SoundByte) object;
		this.m_SoundBytes.remove(soundByte);
		System.out.println("Releasing soundByte");
		
	}

}