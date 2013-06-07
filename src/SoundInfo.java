import java.io.File;


public class SoundInfo 
{
	
	private File m_SoundFile;
	private boolean m_Recognized = true;
	private String m_RelativeName = null;
	private String m_ToString = null;
	private String m_User = "Guest";
	private String m_Date = "3/5/2013 12:32";
	
	public SoundInfo(File i_File, boolean i_Recognized, String i_User, String i_Date) 
	{
		this.m_SoundFile = i_File;
		this.m_Recognized = i_Recognized;
		this.m_User = i_User;
		this.m_Date = i_Date;
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
	
	@Override
	public String toString() {
		
		if (this.m_ToString == null)
		{
			this.m_ToString = "Recorded by: " + this.m_User + "\n";
			this.m_ToString += "On: " + this.m_Date;
		}
		
		return this.m_ToString;
	}

}
