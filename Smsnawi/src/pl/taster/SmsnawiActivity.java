package pl.taster;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class SmsnawiActivity extends Activity {
	public static final int PICK_CONTACT_CODE = 1;
	private EditText messageText, contactsText;
	private Button sendButton;
	private String number, name;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        messageText = (EditText)findViewById(R.id.editText);
        contactsText = (EditText)findViewById(R.id.contactsText);
        sendButton = (Button)findViewById(R.id.sendButton);
        contactsText.setOnClickListener(new OnClickListener() {
        	public void onClick(View arg0) {
                    Intent intent = new Intent(Intent.ACTION_PICK, 
                    		ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 0);
            }
        });
        //abrakadabra out of this world 123
        sendButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View arg0) {
                    sendSMS();
                    finish();
            }
        });
        messageText.setText(getString(R.string.location_text) + getLocation());
    }
    
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) return;
		Uri contactData = data.getData();
		Cursor contactsCursor = managedQuery(contactData, null, null, null, null);
		while (contactsCursor.moveToNext()) {
			name = contactsCursor.getString(contactsCursor
					.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
			String id = contactsCursor.getString(contactsCursor
					.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
			if (Integer
					.parseInt(contactsCursor.getString(contactsCursor
					.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0){
				Cursor phonesCursor = managedQuery(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
					"CONTACT_ID = " + id, null, null);
				while (phonesCursor.moveToNext()) {
					number = phonesCursor
							.getString(phonesCursor.getColumnIndexOrThrow(
									ContactsContract.CommonDataKinds.Phone.NUMBER));
				}
				phonesCursor.close();
			}
		}
		contactsCursor.close();
		contactsText.setText(name + "[" + number + "]");
	}

    private String getLocation() {
    	LocationManager locMngr =
    		(LocationManager) getSystemService(Context.LOCATION_SERVICE);  
    	List<String> providers = locMngr.getProviders(true);
    	Location loc = null;
    	for (int i=providers.size()-1; i>=0; i--) {
    		loc = locMngr.getLastKnownLocation(providers.get(i));
    		if (loc != null) break;
    	}
    	if(loc != null)	
    		return Location.convert(loc.getLatitude(), Location.FORMAT_DEGREES) 
    		+ "x" + Location.convert(loc.getLongitude(), Location.FORMAT_DEGREES);
    	return "0x0";
    }
    
    private void sendSMS() {
    	SmsManager smsMngr = SmsManager.getDefault();
    	smsMngr.sendTextMessage(number, null, 
    		messageText.getText().toString(), null, null);
    }

}