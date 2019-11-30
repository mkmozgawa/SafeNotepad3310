package safenotepad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Note {
	private String content;
	
	public Note(String p_content)
	{
		content = p_content;
	}
	
	public String getNoteContent()
	{
		return content;
	}
	
	public void setNoteContent(String newContent)
	{
		content = newContent;
	}
	
	public Note (byte[] data)
	{
		try
		{
			DataInputStream in = new DataInputStream(
					new ByteArrayInputStream(data));
			this.content = in.readUTF();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public byte[] toByteArray()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);
		byte buffor[] = new byte[0];
		try
		{
			out.writeUTF(this.content);
			buffor = baos.toByteArray();
			baos.close();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		return buffor;
	}
}