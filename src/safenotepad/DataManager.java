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
	
	public void deleteExistingRecords() throws RecordStoreException
	{
		RecordEnumeration renum = stream.enumerateRecords(null, null, false);
		while (renum.hasNextElement())
		{
			int id = renum.nextRecordId();
			stream.deleteRecord(id);
		}
	}
	
	public void populateEmptyRecordStore() throws RecordStoreException
	{
		if (stream.getNumRecords() == 0)
		{
		    Note exampleNote = new Note("note=Somenote");
		    createNote(exampleNote);
		}
	}
	
	public int[] getNoteIdsFromRecordStore() throws RecordStoreNotOpenException, InvalidRecordIDException
	{
		int ids[] = new int[2];
		RecordEnumeration renum = stream.enumerateRecords(null, null, false);
		int i = 0;
		while (renum.hasNextElement())
		{
			ids[i] = renum.nextRecordId();
			i++;
		}
		return ids;
	}
	
	public Note[] getNotesFromRecordStore(int ids[]) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException
	{
		Note notes[] = new Note[ids.length];
		for (int i = 0; i < ids.length; i++)
		{
			notes[i] = getNote(ids[i]);
		}
		return notes;
	}
	
	public String getNoteStartingWithChars(Note[] notes, String chars)
	{
		for (int i = 0; i < notes.length; i++)
		{
			String noteString = notes[i].getNoteContent();
			if (noteString.startsWith(chars))
			{
				int start = chars.length();
				return noteString.substring(start);
			}
		}
		return "";
	}
	
	public int getIdOfNoteStartingWithChars(int ids[], String chars) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException
	{
		for (int i = 0; i < ids.length; i++)
		{
			if (getNote(ids[i]).getNoteContent().startsWith(chars))
			{
				return ids[i];
			}
		}
		return -1;
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
	
	public void editNote(Note note, int noteId) throws RecordStoreException
	{
		try {
			setNote(note, noteId);				
		}
		catch (RecordStoreException rse)
		{
			rse.printStackTrace();
		}
	
	}

	private int getNoteId() {
		// TODO Auto-generated method stub
		return 0;
	}
}