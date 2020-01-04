package safenotepad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;

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
		catch (DataLengthException e)
		{
			System.out.println("what");
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
	
	public Note getOrCreateNote(String key) throws RecordStoreException
	{
		int noteId = getNoteId();
		if (noteId == -1000000)
		{
			Note note = new Note("Example note");
			createNote(note, key);
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
	
	public int createNote(Note note, String key) throws RecordStoreException
	{
		try
		{
			byte[] buffor = note.toByteArray();
			byte[] keyBytes = key.getBytes();
			StringCipher cs = new StringCipher(keyBytes);
			InputStream inputStream;
			inputStream = new ByteArrayInputStream(buffor);
			OutputStream encodedOutputStream = new ByteArrayOutputStream();
			cs.encrypt(inputStream, encodedOutputStream);
			ByteArrayOutputStream bais = (ByteArrayOutputStream) encodedOutputStream;
	    	byte[] outputBytes = bais.toByteArray();
			int recordId = stream.addRecord(outputBytes,0,outputBytes.length);
			return recordId;
		}
		catch (RecordStoreException rse) 
		{
			rse.printStackTrace();
		} catch (DataLengthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShortBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCipherTextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1000000;
	}
	
	public void setNote(Note note, int noteId, String key) 
			throws RecordStoreFullException, RecordStoreException	
	{
		try 
		{
			byte[] buffor = note.toByteArray();
			byte[] keyBytes = key.getBytes();
			StringCipher cs = new StringCipher(keyBytes);
			InputStream inputStream;
			inputStream = new ByteArrayInputStream(buffor);
			OutputStream encodedOutputStream = new ByteArrayOutputStream();
			cs.encrypt(inputStream, encodedOutputStream);
			ByteArrayOutputStream bais = (ByteArrayOutputStream) encodedOutputStream;
	    	byte[] outputBytes = bais.toByteArray();
			stream.setRecord(noteId, outputBytes, 0, outputBytes.length);
		}
		catch (RecordStoreFullException rsfe)
		{
			rsfe.printStackTrace();
		}
		catch (RecordStoreException rse)
		{
			rse.printStackTrace();
		} catch (DataLengthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShortBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCipherTextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void editNote(Note note, String key) throws RecordStoreException
	{
		try {
			int noteId = getNoteId();
			System.out.println("Id rekordu:"+noteId);
			setNote(note, noteId, key);				
		}
		catch (RecordStoreException rse)
		{
			rse.printStackTrace();
		}
	
	}
}