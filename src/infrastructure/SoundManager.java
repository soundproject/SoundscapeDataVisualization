package infrastructure;

import java.util.ArrayList;

import ddf.minim.AudioSample;
import ddf.minim.Minim;

public class SoundManager extends SelfRegisteringComponent 
{
	
	private ManagedPApplet m_Parent;
	private Minim m_Minim;
	
	private ArrayList<AudioSample> m_Samples = new ArrayList<AudioSample>();
	private AudioSample m_Sample;
	private boolean m_SampleIsPlaying;
	private int m_TimeLeftToPlay;
	
	public SoundManager(ManagedPApplet i_Parent) 
	{
		super(i_Parent);
//		this.m_Parent = i_Parent;
		this.m_Minim = new Minim(i_Parent);
//		this.m_Minim.debugOn();
	}
	
	public void loadAndPlaySound(String i_FileName)
	{
		if (!this.m_SampleIsPlaying)
		{
			AudioSample sample = this.loadSample(i_FileName);
			this.playSample(sample);
		}
	}

	private void playSample(AudioSample sample) {
		sample.trigger();
		int length = sample.length();
		this.m_SampleIsPlaying = true;
		this.m_TimeLeftToPlay = length;		
	}

	private AudioSample loadSample(String i_FileName) {
		
		AudioSample result = this.m_Minim.loadSample(i_FileName);
		if (!this.m_Samples.contains(result))
		{
			this.m_Samples.add(result);
		}
		this.m_Sample = result;
		return result;
	}

	@Override
	public void update(long ellapsedTime) {
		// TODO Auto-generated method stub
		if (this.isPlaying())
		{
			this.m_TimeLeftToPlay -= ellapsedTime;
			
			if (m_TimeLeftToPlay < 0)
			{
				System.out.println("donePlaying");
				this.m_SampleIsPlaying = false;
				this.m_Sample.stop();
			}
		}
		
	}

	private boolean isPlaying() {
		return this.m_SampleIsPlaying;
	}

	@Override
	public void draw(long ellapsedTime) 
	{
		// EMPTY		
	}

}
