package org.zju.ese.market;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class MarketFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle icicle) {		
		super.onCreate(icicle);
		addPreferencesFromResource(R.layout.preference);
	}
}
