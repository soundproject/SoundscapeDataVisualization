import infrastructure.ManagedPApplet;
import infrastructure.SelfRegisteringComponent;


/**
 * Class that manages Demo Mode for visualization
 * @author Gadi
 *
 */
public class AutoPlayingManager extends SelfRegisteringComponent {
	
	// Timing settings for transitions between sounds
	public static final int MINIMUM_TIME_BETWEEN_ACTIVATIONS = 3000;
	public static final int MAXIMUM_TIME_BETWEEN_ACTIVATIONS = 4500;
	public static final int MINIMUM_ACTIVATION_TIME = 7500;
	public static final int MAXIMUM_ACTIVATION_TIME = 15000;
	public static final int IDLE_USER_TIME = 20000;
	
	// need direct reference to Main parent to work with Sounds
	private Main m_MainParent;
	private long m_PlayingTime;
	private long m_TimeToPlay;
	private int m_StoppedTime;
	private int m_TimeToStop;
	private boolean m_Playing = false;
	private int m_ActiveSoundIndex;
	private long m_delayTime;

	public AutoPlayingManager(Main i_Parent) 
	{
		super(i_Parent);
		
		this.m_MainParent = i_Parent;		
		
		// Set initial wait time from starting Demo mode 
		// to playing first sound
		generateNewStopTime();
		
		this.m_delayTime = 2000;
	}

	private void generateNewStopTime() 
	{		
		this.m_TimeToStop = (int) this.m_MainParent.random(MINIMUM_TIME_BETWEEN_ACTIVATIONS, MAXIMUM_TIME_BETWEEN_ACTIVATIONS);		
	}
	
	private void generateNewPlayTime() 
	{
		this.m_TimeToPlay = (int) this.m_MainParent.random(MINIMUM_ACTIVATION_TIME, MAXIMUM_ACTIVATION_TIME);		
	}

	@Override
	public void update(long ellapsedTime) 
	{				
		// If a sound is playing
		if (isPlaying())
		{
			// keep track of playing time
			this.m_PlayingTime += ellapsedTime;
			
			// check if need to stop playing:
			if (this.m_PlayingTime > this.m_TimeToPlay)
			{
				stopPlaying();
			}			
			// If still supposed to play and no sound is playing
			else if (!this.m_MainParent.getSounds()[this.m_ActiveSoundIndex].isPlayingSound())
			{
				this.m_delayTime -= ellapsedTime;
				
				// check if enough time has passed (delayTime)
				if (this.m_delayTime < 0)
				{
					// trigger next sound and reset delayTime
					this.m_MainParent.getSounds()[this.m_ActiveSoundIndex].activate();
					this.m_delayTime = 2000;
				}
			}
		}
		else
		{	
			// keep tracked of stopped time
			this.m_StoppedTime += ellapsedTime;
			
			// Check if new sound needs to be selected:
			if (this.m_StoppedTime > this.m_TimeToStop)
			{
				selectNextSound();
			}
		}		
	}

	private void selectNextSound() 
	{
		// Move to next sound in Sounds - cycle back if needed
		this.m_ActiveSoundIndex++;
		this.m_ActiveSoundIndex %= this.m_MainParent.getSounds().length;
		
		// activate the sound and generate random playing time within defined values
		Sound selectedSound = this.m_MainParent.getSounds()[this.m_ActiveSoundIndex];
		selectedSound.activate();
		generateNewPlayTime();
		
		// set playing flag and reset playing time counter
		this.m_PlayingTime = 0;
		this.m_Playing = true;
		
	}

	private void stopPlaying() 
	{
		generateNewStopTime();
		this.m_StoppedTime = 0;
		this.m_Playing = false;		
		
	}

	/**
	 * Return True if a sound is currently selected
	 * @return True if a sound is currently selected
	 */
	public boolean isPlaying() 
	{
		return this.m_Playing;
	}

	@Override
	public void draw(long ellapsedTime) {

		// Draw Demo Mode message - DEBUG ONLY
		this.m_MainParent.fill(155);
		this.m_MainParent.text("Demo Mode", this.m_MainParent.width / 2, 10);
	}

	/**
	 * Deactivate the Demo Mode
	 */
	public void Deactivate() 
	{		
		this.setEnabled(false);
		this.setVisible(false);
	}

	/**
	 * Activate Demo Mode
	 */
	public void Activate() 
	{		
		this.setEnabled(true);
		this.setVisible(true);
	}
}
