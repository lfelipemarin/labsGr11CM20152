package apps.edu.udea.co.anywall_gr11;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;

public class DispatchActivity extends Activity {

    public DispatchActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser().getUsername() != null) {
            startActivity(new Intent(this, Welcome.class));
        } else {
            startActivity(new Intent(this, LoginSignupActivity.class));
        }
    }


}
