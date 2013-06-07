import java.io.File;


public class SoundInfo 
{
	
	private File m_SoundFile;
	private boolean m_Recognized = true;
	private String m_RelativeName = null;
	
	public SoundInfo(File i_File, boolean i_Recognized) 
	{
		this.m_SoundFile = i_File;
		this.m_Recognized = i_Recognized;
	}
	
	public File getFile() 
	{
		return this.m_SoundFile;
	}
	
	public boolean isRecognized() 
	{
		return this.m_Recognized;
	}
	
	public String getRelativeFileName()
	{
		if (this.m_RelativeName == null)
		{
			String filename = this.m_SoundFile.getName();
			String recognized = this.m_SoundFile.getParentFile().getName(); 
			String category = this.m_SoundFile.getParentFile().getParentFile().getName();
			this.m_RelativeName = "Sounds/" + category + "/" + recognized + "/" + filename;
		}
		
//		System.out.println("Name is " + this.m_RelativeName);
		return this.m_RelativeName; 
	}

}
