package org.zju.ese.market;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class MarketPreferenceActivity extends Activity 
{
	public static final int RESULT_PREF_UPDATE = 1;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 FragmentManager fragmentManager = getFragmentManager();
         FragmentTransaction fragmentTransaction =
              fragmentManager.beginTransaction();
         MarketFragment fragment1 = new MarketFragment();
         fragmentTransaction.replace(android.R.id.content, fragment1);       
         fragmentTransaction.addToBackStack(null);
         fragmentTransaction.commit();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		this.finishActivity(RESULT_PREF_UPDATE);
	}
}
