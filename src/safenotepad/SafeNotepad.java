package safenotepad;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStoreException;

public class SafeNotepad extends MIDlet implements CommandListener {
	private DataManager store;
	private Note note;
	private TextField noteField;
	private TextField passwordField;
	private TextField newPasswordField;
	private Ticker ticker;
	private Display screen;
	private Form loggedOutForm;
	private Form loggedInForm;
	private Form changeNoteForm;
	private Form changePasswordForm;
	
	public SafeNotepad() throws RecordStoreException
	{
		store = new DataManager("data");
		note = store.getOrCreateNote();
		
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
		noteField = new TextField("", note.getNoteContent(), 128, TextField.UNEDITABLE);
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
		noteField = new TextField(
				"Your decrypted note: ", note.getNoteContent(), 128, TextField.ANY);
		changeNoteForm.append(noteField);
		Command cancel = new Command("Cancel", Command.EXIT, 0);
		changeNoteForm.addCommand(cancel);
		Command saveNote = new Command("Save", Command.OK, 0);
		changeNoteForm.addCommand(saveNote);
		changeNoteForm.setCommandListener(this);
		
		changePasswordForm = new Form("Change password");
		newPasswordField = new TextField("Enter new password", "", 30, TextField.PASSWORD);
		changePasswordForm.append(newPasswordField);
		Command savePassword = new Command("Save", Command.OK, 0);
		changePasswordForm.addCommand(savePassword);
		Command cancelPasswordChange = new Command("Cancel", Command.EXIT, 0);
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
	
	public void updateNotePreview()
	{
		TextField updatedNoteField = new TextField("", note.getNoteContent(), 128, TextField.UNEDITABLE);
		loggedInForm.deleteAll();
		loggedInForm.append(updatedNoteField);
	}
	
	public void commandAction(Command c, Displayable s)
	{
		if (s == loggedOutForm)
		{
			if (c.getCommandType() == Command.OK)
			{
				/* TODO: verify password
				 * => check if the note can be decrypted using the provided password */
				if (passwordField.getString().equals("JAJA"))
				{
					note.getNoteContent();
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
				String newNoteText = noteField.getString();
				note.setNoteContent(newNoteText);
				try {
					store.editNote(note);
				} catch (RecordStoreException rse) {
					rse.printStackTrace();
				}
				updateNotePreview();
				screen.setCurrent(loggedInForm);
			}
			if (c.getCommandType() == Command.EXIT)
			{
				screen.setCurrent(loggedInForm);
			}
		}
		
		if (s == changePasswordForm)
		{
			if (c.getCommandType() == Command.OK)
			{
				if (!newPasswordField.getString().equals(""))
				{
					/* TODO: update password 
					 * => reencrypt the note with the new password */
					screen.setCurrent(loggedInForm);
				}
			}
			
			if (c.getCommandType() == Command.EXIT)
				screen.setCurrent(loggedInForm);
		}
	}

}
