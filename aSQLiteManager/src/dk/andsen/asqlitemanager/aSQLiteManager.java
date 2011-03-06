/**
 * Part of aSQLiteManager (http://sourceforge.net/projects/asqlitemanager/)
 * a a SQLite Manager by andsen (http://sourceforge.net/users/andsen)
 *
 * @author andsen
 *
 */
package dk.andsen.asqlitemanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import dk.andsen.utils.NewFilePicker;
import dk.andsen.utils.Utils;

public class aSQLiteManager extends Activity implements OnClickListener {
	
	/**
	 * True to enable functions under test
	 */
	private final boolean testing = false;
	private static final int MENU_OPT = 1;
	private static final int MENU_HLP = 2;
	private static final int MENU_RESET = 3;
	private Context _cont;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button open = (Button) this.findViewById(R.id.Open);
        open.setOnClickListener(this);
        Button about = (Button) this.findViewById(R.id.About);
        about.setOnClickListener(this);
        Button newDatabase = (Button) this.findViewById(R.id.NewDB);
        newDatabase.setOnClickListener(this);
        Button test = (Button) this.findViewById(R.id.Test);
        TextView tv = (TextView) this.findViewById(R.id.Version);
        tv.setText(getText(R.string.Version) + " " + getText(R.string.VersionNo));
        _cont = this;
        if (!testing) {
        	test.setVisibility(4);
        } else {
        	test.setOnClickListener(this);
        }
        String mh = MimeTypeMap.getSingleton().getExtensionFromMimeType("db");
        Utils.logD("MimeType for sqlite: " + mh);
        //Utils.showMessage("Debug", "Mime Type for .sqlite: " + mh, this);
        Utils.logD("Created");
    }

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		public void onClick(View v) {
			int key = v.getId();
			if (key == R.id.Open) {
				Intent i = new Intent(this, NewFilePicker.class);
				Utils.logD("Calling NewFilepicker");
//				Utils.logD("Calling NewFilepicker for result");
//				startActivityForResult(i, 1);
				startActivity(i);
			} else if (key == R.id.About) {
				Dialog dial = new Dialog(this);
				dial.setContentView(R.layout.about);
				dial.setTitle(getString(R.string.AboutHeader));
				TextView text = (TextView) dial.findViewById(R.id.text);
				text.setText(getString(R.string.AboutText));
				ImageView image = (ImageView) dial.findViewById(R.id.image);
				image.setImageResource(R.drawable.and);
				TextView mail = (TextView) dial.findViewById(R.id.mail);
				mail.setAutoLinkMask(Linkify.ALL);
				mail.setText(getString(R.string.MAIL));
				TextView www = (TextView) dial.findViewById(R.id.www);
				www.setAutoLinkMask(Linkify.ALL);
				www.setText(getString(R.string.WWW));
				dial.show();
			}  else if (key == R.id.NewDB) {
				Utils.logD("Create new database");
				newDatabase();
			} else if (key == R.id.Test) {
				Intent i = new Intent(this, NewFilePicker.class);
				startActivity(i);
			}
      
		}
		
		protected void onActivityResult(int requestCode, int resultCode, Intent data)
		{
			Utils.logD("MainDriver main-activity got result from sub-activity");
			if (resultCode == Activity.RESULT_CANCELED) {
				Utils.logD("WidgetActivity was cancelled or encountered an error. resultcode == result_cancelled");
				Utils.logD("WidgetActivity was cancelled - data =" + data);
			} else
				switch (requestCode) {
				case 1:
					 String msg = data.getStringExtra("returnedData");
					 Utils.showMessage("Returned file", msg, _cont);
					break;
				}
			Utils.logD("MainDriver main-activity got result from sub-activity");
		}		
		
		/**
		 * Create a new empty database
		 */
		private void newDatabase() {
			final Dialog newDatabaseDialog = new Dialog(this);
			newDatabaseDialog.setContentView(R.layout.new_database);
			newDatabaseDialog.setTitle(getText(R.string.NewDBSDCard));
			final EditText edNewDB = (EditText)newDatabaseDialog.findViewById(R.id.newCode);
			edNewDB.setHint(getText(R.string.NewDBPath));
			TextView tvMessage = (TextView) newDatabaseDialog.findViewById(R.id.newMessage);
			tvMessage.setText(getText(R.string.Database));
			newDatabaseDialog.show();
			final Button btnMOK = (Button) newDatabaseDialog.findViewById(R.id.btnMOK);
			btnMOK.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String path;
					if (v == btnMOK) {
						if (Utils.isSDAvailable()) {
							String fileName = edNewDB.getEditableText().toString();
							path = Environment.getExternalStorageDirectory().getAbsolutePath();
							path += "/" + fileName;
							if (fileName.equals("")) {
								Utils.showMessage((String)getText(R.string.Error), (String)getText(R.string.NoFileName), _cont);
							} else {
								if (!path.endsWith(".sqlite"))
									path += ".sqlite";
								SQLiteDatabase.openOrCreateDatabase(path, null);
								// Ask before??
								Intent i = new Intent(_cont, DBViewer.class);
								i.putExtra("db", path);
								newDatabaseDialog.dismiss();
								startActivity(i);
							}
						}
						Utils.logD("Path: " + edNewDB.getText().toString());
					}
				}
			});
		}

		/*
		 *  Creates the menu items
		 */
		public boolean onCreateOptionsMenu(Menu menu) {
			menu.add(0, MENU_OPT, 0, R.string.Option).setIcon(R.drawable.ic_menu_preferences);
			menu.add(0, MENU_HLP, 0, R.string.Help).setIcon(R.drawable.ic_menu_help);
			menu.add(0, MENU_RESET, 0, R.string.Reset).setIcon(R.drawable.ic_menu_close_clear_cancel);
			return true;
		}
		
		/* (non-Javadoc) Handles item selections
		 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
		 */
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
	    case MENU_OPT:
	      startActivity(new Intent(this, Prefs.class));
	      return true;
	    case MENU_HLP:
				Intent i = new Intent(this, Help.class);
				startActivity(i);
	      return true;
	    case MENU_RESET:
	    	// Reset all settings to default
	    	SharedPreferences settings = getSharedPreferences("aSQLiteManager", MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("FPJustOpen", false);
				editor.putBoolean("JustOpen", false);
				editor.commit();
				settings = getSharedPreferences("dk.andsen.asqlitemanager_preferences", MODE_PRIVATE);
				editor = settings.edit();
				editor.putString("PageSize", "20");
				editor.putBoolean("SaveSQL", false);
				editor.commit();
	    	return false;
			}
			return false;
		}

}