package safenotepad;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class DataManager {
	private RecordStore stream;

	public DataManager(String name)
	{
		try
		{
			stream = RecordStore.openRecordStore(name, true);	
		}
		catch (RecordStoreException rse)
		{
			rse.printStackTrace();
		}
	}
	
	// this works because we assume to only have 1 note
	public int getNoteId() throws RecordStoreException
	{
		try {
			System.out.println("Number of records: "+stream.getNumRecords());
			RecordEnumeration renum = stream.enumerateRecords(null, null, false);
			if (renum.hasNextElement())
			{
				return renum.nextRecordId();
			}
			return -1000000;
		}
		catch (RecordStoreException rse)
		{
			rse.printStackTrace();
		}
		return -1000000;
	}
	
	public Note getOrCreateNote() throws RecordStoreException
	{
		int noteId = getNoteId();
		if (noteId == -1000000)
		{
			Note note = new Note("Example note");
			createNote(note);
			noteId = getNoteId();
		}
		return getNote(noteId);
	}
	
	public Note getNote(int noteId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException
	{
		try 
		{
			byte[] noteContentBytes = stream.getRecord(noteId);
			Note note = new Note(noteContentBytes);
			return note;
		}
		catch (RecordStoreNotOpenException rsnoe)
		{
			rsnoe.printStackTrace();
		}
		catch (InvalidRecordIDException iride)
		{
			iride.printStackTrace();
		}
		catch (RecordStoreException rse)
		{
			rse.printStackTrace();
		}
		return null;
	    
	}
	
	public int createNote(Note note) throws RecordStoreException
	{
		byte[] buffor = note.toByteArray();
		try
		{
			int recordId = stream.addRecord(buffor,0,buffor.length);
			return recordId;
		}
		catch (RecordStoreException rse) 
		{
			rse.printStackTrace();
		}
		return -1000000;
	}
	
	public void setNote(Note note, int noteId) 
			throws RecordStoreFullException, RecordStoreException	
	{
		byte[] buffor = note.toByteArray();
		try 
		{
			stream.setRecord(noteId, buffor, 0, buffor.length);
		}
		catch (RecordStoreFullException rsfe)
		{
			rsfe.printStackTrace();
		}
		catch (RecordStoreException rse)
		{
			rse.printStackTrace();
		}
	}
	
	public void editNote(Note note) throws RecordStoreException
	{
		try {
			int noteId = getNoteId();
			System.out.println("Id rekordu:"+noteId);
			setNote(note, noteId);				
		}
		catch (RecordStoreException rse)
		{
			rse.printStackTrace();
		}
	
	}
}