package safenotepad;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class SafeNotepad extends MIDlet implements CommandListener {
	private DataManager store;
	private Note note;
	private String noteText;
	private String newNoteText;
	private String placeholderNote = "Placeholder";
	private String notePrefix = "note=";
	private int[] ids = new int[2];
	private Note[] notes = new Note[2];
	private int noteId;
	private TextField noteField;
	private TextField newNoteField;
	private TextField passwordField;
	private TextField newPasswordField;
	private TextField currentPasswordField;
	private Ticker ticker;
	private Display screen;
	private Form loggedOutForm;
	private Form loggedInForm;
	private Form changeNoteForm;
	private Form changePasswordForm;
	
	public SafeNotepad() throws RecordStoreException
	{
		store = new DataManager("data");
		store.deleteExistingRecords();
		
		store.populateEmptyRecordStore();
		ids = store.getNoteIdsFromRecordStore();
		noteId = store.getIdOfNoteStartingWithChars(ids, notePrefix);
		note = store.getNote(noteId);
		
		screen = Display.getDisplay(this);
		loggedOutForm = new Form("Log in");
		passwordField = new TextField("Enter password", "", 30, TextField.PASSWORD);
		loggedOutForm.append(passwordField);
		Command login = new Command("Log in", Command.OK, 0);
		loggedOutForm.addCommand(login);
		Command quit = new Command("Exit", Command.EXIT, 0);
		loggedOutForm.addCommand(quit);
		loggedOutForm.setCommandListener(this);
		
		loggedInForm = new Form("Your note");
		ticker = new Ticker("Safe Notepad");
		noteField = new TextField("", placeholderNote, 128, TextField.UNEDITABLE);
		loggedInForm.append(noteField);
		Command logout = new Command("Log out", Command.EXIT, 0);
		loggedInForm.addCommand(logout);
		Command changeNote = new Command("Change note", Command.ITEM, 0);
		Command changePassword = new Command("Change password", Command.ITEM, 1);
		loggedInForm.addCommand(changeNote);
		loggedInForm.addCommand(changePassword);
		loggedInForm.setTicker(ticker);
		loggedInForm.setCommandListener(this);
		
		changeNoteForm = new Form("Change note");
		newNoteField = new TextField(
				"New note: ", "", 128, TextField.ANY);
		changeNoteForm.append(newNoteField);
		Command cancel = new Command("Log out", Command.EXIT, 0);
		changeNoteForm.addCommand(cancel);
		Command saveNote = new Command("Save", Command.OK, 0);
		changeNoteForm.addCommand(saveNote);
		changeNoteForm.setCommandListener(this);
		
		changePasswordForm = new Form("Change password");
		currentPasswordField = new TextField("Enter current password", "", 30, TextField.PASSWORD);
		changePasswordForm.append(currentPasswordField);
		newPasswordField = new TextField("Enter new password", "", 30, TextField.PASSWORD);
		changePasswordForm.append(newPasswordField);
		Command savePassword = new Command("Save", Command.OK, 0);
		changePasswordForm.addCommand(savePassword);
		Command cancelPasswordChange = new Command("Log out", Command.EXIT, 0);
		changePasswordForm.addCommand(cancelPasswordChange);
		changePasswordForm.setCommandListener(this);
		
		screen.setCurrent(loggedOutForm);
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub
		
	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		
	}
	
	public void updateNotePreview(String noteText)
	{
		TextField updatedNoteField = new TextField("", noteText, 128, TextField.UNEDITABLE);
		loggedInForm.deleteAll();
		loggedInForm.append(updatedNoteField);
	}
	
	public void updateIds()
	{
		try {
			ids = store.getNoteIdsFromRecordStore();
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRecordIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateNotes(int[] ids)
	{
		try {
			notes = store.getNotesFromRecordStore(ids);
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRecordIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printNotesFromRS()
	{
		try {
			notes = store.getNotesFromRecordStore(ids);
			System.out.println(notes.length);
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRecordIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < notes.length; i++)
		{
			System.out.println(notes[i].getNoteContent());
		}
	}
	
	public void commandAction(Command c, Displayable s)
	{
		if (s == loggedOutForm)
		{
			printNotesFromRS();
			updateIds();
			updateNotes(ids);
			
			if (c.getCommandType() == Command.OK)
			{
				/* TODO: verify password
				 * => check if the note can be decrypted using the provided password */
				if (passwordField.getString().equals("JAJA"))
				{
					noteText = store.getNoteStartingWithChars(notes, notePrefix);
					updateNotePreview(noteText);
					screen.setCurrent(loggedInForm);
					passwordField.setString("");
				}
			}
			if (c.getCommandType() == Command.EXIT)
				this.notifyDestroyed();
		}
		
		if (s == loggedInForm)
		{
			if (c.getCommandType() == Command.EXIT)
			{
				screen.setCurrent(loggedOutForm);
			}
			if (c.getCommandType() == Command.ITEM)
			{
				if (c.getPriority() == 0)
				{
					screen.setCurrent(changeNoteForm);
				}
				if (c.getPriority() == 1)
				{
					screen.setCurrent(changePasswordForm);
				}
			}			
		}
		
		if (s == changeNoteForm)
		{
			if (c.getCommandType() == Command.OK)
			{
				System.out.println(newNoteField.getString());
				note.setNoteContent(newNoteField.getString());
				try {
					store.editNote(note, noteId);
				} catch (RecordStoreException rse) {
					rse.printStackTrace();
				}
				screen.setCurrent(loggedOutForm);
				newNoteField.setString("");
				printNotesFromRS();
			 }
			if (c.getCommandType() == Command.EXIT)
			{
				screen.setCurrent(loggedOutForm);
			}
		}
		
		if (s == changePasswordForm)
		{
			if (c.getCommandType() == Command.OK)
			{
				if (currentPasswordField.getString() == "JAJA" && !newPasswordField.getString().equals(""))
				{
					/* TODO: update password 
					 * => reencrypt the note with the new password */
					screen.setCurrent(loggedInForm);
				}
			}
			
			if (c.getCommandType() == Command.EXIT)
				screen.setCurrent(loggedOutForm);
		}
	}

}
