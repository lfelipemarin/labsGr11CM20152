package co.edu.udea.cmovil.gr11.yamba;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thenewcircle.yamba.client.YambaClient;
import com.thenewcircle.yamba.client.YambaClientException;


public class StatusFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = StatusFragment.class.getSimpleName();
    private Button mButtonTweet;
    private EditText mTextStatus;
    private TextView mTextCount;
    private int mDefaultColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status, container, false);

        //Inicializar
        mButtonTweet = (Button) v.findViewById(R.id.status_button_tweet);
        mButtonTweet.setEnabled(false);
        mButtonTweet.setOnClickListener(this);
        mTextStatus = (EditText) v.findViewById(R.id.status_text);
        mTextCount = (TextView) v.findViewById(R.id.status_text_count);

        mTextCount.setText(Integer.toString(140));

        mDefaultColor = mTextCount.getTextColors().getDefaultColor();

        mTextStatus.addTextChangedListener(new TextWatcher() {
            Integer count = Integer.valueOf(mTextCount.getText().toString());

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, charSequence.toString());

                mTextCount.setText(String.valueOf(count - mTextStatus.length()));
                if (mTextStatus.length() == 0)
                    mButtonTweet.setEnabled(false);
                else
                    mButtonTweet.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Log.d(TAG, "onCreated");

        return v;

    }

    @Override
    public void onClick(View view) {
        String status = mTextStatus.getText().toString();
        PostTask postTask = new PostTask();
        postTask.execute(status);
        Log.d(TAG, "onClicked");
    }

    class PostTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getActivity(), "Posting", "Please wait...");
            progress.setCancelable(true);
        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                YambaClient cloud = new YambaClient("student", "password");
                cloud.postStatus(strings[0]);

                Log.d(TAG, "Successfully posted on the cloud: " + strings[0]);
                return "Successfully posted";
            } catch (YambaClientException e) {
                Log.e(TAG, "Failed to post to the cloud", e);
                e.printStackTrace();
                return "Failed to post";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            if (getActivity() != null && result != null) {
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                if (result.equalsIgnoreCase("Successfully posted")) {
                    mTextStatus.setText("");
                }
                try {
                    // InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
    }
}
