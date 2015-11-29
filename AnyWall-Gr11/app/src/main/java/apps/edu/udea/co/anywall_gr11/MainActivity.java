package apps.edu.udea.co.anywall_gr11;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity {

    private static final int MAX_POST_SEARCH_RESULTS = 20;

    private ParseQueryAdapter<AnyWallPost> postsQueryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseQueryAdapter.QueryFactory<AnyWallPost> factory =
                new ParseQueryAdapter.QueryFactory<AnyWallPost>() {
                    public ParseQuery<AnyWallPost> create() {
                        ParseQuery<AnyWallPost> query = AnyWallPost.getQuery();
                        query.include("user");
                        query.orderByDescending("createdAt");
                        query.setLimit(MAX_POST_SEARCH_RESULTS);
                        return query;
                    }
                };

        postsQueryAdapter = new ParseQueryAdapter<AnyWallPost>(this, factory) {
            @Override
            public View getItemView(AnyWallPost post, View v, ViewGroup parent) {
                if (v == null) {
                    v = View.inflate(getContext(), R.layout.anywall_post_item, null);
                }
                TextView contentView = (TextView) v.findViewById(R.id.content_view);
                TextView usernameView = (TextView) v.findViewById(R.id.username_view);
                contentView.setText(post.getText());
                usernameView.setText(post.getUser().getUsername());
                return v;
            }
        };

        postsQueryAdapter.setAutoload(false);

        postsQueryAdapter.setPaginationEnabled(false);

        ListView postsListView = (ListView) findViewById(R.id.post_listview);
        postsListView.setAdapter(postsQueryAdapter);

        Button postButton = (Button) findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });

        Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent intent = new Intent(MainActivity.this, LoginSignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        doListQuery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private void doListQuery() {
        postsQueryAdapter.loadObjects();
    }
}
