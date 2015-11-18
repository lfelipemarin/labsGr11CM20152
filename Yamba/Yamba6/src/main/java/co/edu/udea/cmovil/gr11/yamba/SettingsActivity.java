package co.edu.udea.cmovil.gr11.yamba;

import android.os.Bundle;

/**
 * Created by toughbook on 29/09/15.
 */
public class SettingsActivity extends SubActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null){
            SettingsFragment fragment = new SettingsFragment();
            getFragmentManager().beginTransaction().add(android.R.id.content, fragment,
                    fragment.getClass().getSimpleName()).commit();
        }
    }
}
