package edu.pitt.designs1635.ParkIt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.maps.GeoPoint;

public class Add extends Activity
{
	private ParkingLocation pl;
	private Button next;
	public final int ADD_ACTIVITY = 0;
    private SharedPreferences prefs;
    private EditText name, limit, rate;
    private Spinner type, payment;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add);

		prefs = this.getSharedPreferences("parkItPrefs", Activity.MODE_PRIVATE);

		next = (Button) findViewById(R.id.next_button);
		name = (EditText) findViewById(R.id.nv_add);

		type = (Spinner) findViewById(R.id.tv_add);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
			this, R.array.park_type, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		type.setAdapter(adapter);

		payment = (Spinner) findViewById(R.id.pt_add);
		ArrayAdapter<CharSequence> payment_adapter = ArrayAdapter.createFromResource(
			this, R.array.park_pay_type, android.R.layout.simple_spinner_item);
		payment_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		payment.setAdapter(payment_adapter);

		limit = (EditText) findViewById(R.id.lv_add);
		rate = (EditText) findViewById(R.id.rv_add);

		name.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){
				if(s.length() > 0)
					next.setEnabled(true);
				else
					next.setEnabled(false);
			}
		}); 

		next.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(getApplicationContext(), AddPointMapActivity.class);
            	            	
            	intent.putExtra("edu.pitt.designs1635.ParkIt.center.lat", prefs.getInt("last_location_lat", 0));
            	intent.putExtra("edu.pitt.designs1635.ParkIt.center.long", prefs.getInt("last_location_lon", 0));
            	
                startActivityForResult(intent, ADD_ACTIVITY);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		case ADD_ACTIVITY:
			Bundle extras = data.getExtras();
		
			GeoPoint newPoint = new GeoPoint(extras.getInt("edu.pitt.designs1635.ParkIt.Add.lat"),
											extras.getInt("edu.pitt.designs1635.ParkIt.Add.long"));
			
			ParkingLocation newPl = new ParkingLocation();
			dbAdapter mDbHelper = new dbAdapter(this);
       		mDbHelper.open();
			switch(resultCode)
			{
			case AddPointMapActivity.SAVE_TO_PHONE:
				newPl.setName(name.getText().toString());
				newPl.setType(type.getSelectedItem().toString());
				newPl.setPayment(payment.getSelectedItem().toString());
				if(limit.getText().toString().compareTo("") != 0)
					newPl.setLimit(Integer.parseInt(limit.getText().toString()));
				if(rate.getText().toString().compareTo("") != 0)
					newPl.setRate(Float.parseFloat(rate.getText().toString()));
				newPl.setLatitude(newPoint.getLatitudeE6());
				newPl.setLongitude(newPoint.getLongitudeE6());
				mDbHelper.addPoint(newPl);
				mDbHelper.close();
				finish();
				break;
			case AddPointMapActivity.SAVE_TO_SERVER:
				newPl.setName(name.getText().toString());
				newPl.setType(type.getSelectedItem().toString());
				newPl.setPayment(payment.getSelectedItem().toString());
				if(limit.getText().toString().compareTo("") != 0)
					newPl.setLimit(Integer.parseInt(limit.getText().toString()));
				if(rate.getText().toString().compareTo("") != 0)
					newPl.setRate(Float.parseFloat(rate.getText().toString()));
				newPl.setLatitude(newPoint.getLatitudeE6());
				newPl.setLongitude(newPoint.getLongitudeE6());
				mDbHelper.addPoint(newPl);

				mDbHelper.close();
				finish();
				break;
			default:

				mDbHelper.close();
				break;
			}
			
			
			break;
		default:
			break;
		}
		
		
		
	}
}