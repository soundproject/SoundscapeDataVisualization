import infrastructure.ManagedPApplet;
import infrastructure.SelfRegisteringComponent;


public class AutoPlayingManager extends SelfRegisteringComponent {
	
	// Timing settings for transitions between sounds
	public static final int MINIMUM_TIME_BETWEEN_ACTIVATIONS = 3000;
	public static final int MAXIMUM_TIME_BETWEEN_ACTIVATIONS = 4500;
	public static final int MINIMUM_ACTIVATION_TIME = 1500;
	public static final int MAXIMUM_ACTIVATION_TIME = 2500;
	
	private Main m_MainParent;
	private long m_PlayingTime;
	private long m_TimeToPlay;
	private int m_StoppedTime;
	private int m_TimeToStop;
	private boolean m_Playing = false;
	private int m_ActiveSoundIndex;

	public AutoPlayingManager(Main i_Parent) 
	{
		super(i_Parent);
		
		this.m_MainParent = i_Parent;		
		
		generateNewStopTime();
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
	public void update(long ellapsedTime) {
		
		
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

	private void selectNextSound() {

		String SelectedSound = "";
		
		this.m_ActiveSoundIndex++;
		this.m_ActiveSoundIndex %= this.m_MainParent.getSounds().length;
		
		Sound selectedSound = this.m_MainParent.getSounds()[this.m_ActiveSoundIndex];
		selectedSound.activate();
		generateNewPlayTime();
		playSound(SelectedSound);
		this.m_PlayingTime = 0;
		this.m_Playing = true;
		
	}

	private void playSound(String selectedSound) {
		// TODO Auto-generated method stub
		
	}

	

	private void stopPlaying() {
		// TODO Auto-generated method stub
		generateNewStopTime();
		this.m_StoppedTime = 0;
		this.m_Playing = false;		
		
	}

	private boolean isPlaying() {
		// TODO Auto-generated method stub
		return this.m_Playing;
	}

	@Override
	public void draw(long ellapsedTime) {
		// EMPTY - not drawable
		
		this.m_MainParent.fill(155);
		this.m_MainParent.text("Demo Mode", this.m_MainParent.width / 2, 10);
	}

	public void Deactivate() 
	{		
		this.setEnabled(false);
		this.setVisible(false);
	}

	public void Activate() 
	{		
		this.setEnabled(true);
		this.setVisible(true);
	}
}
