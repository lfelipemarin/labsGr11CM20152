package co.edu.udea.cmovil.gr11.gcmdemogr11;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    EditText editText_mail_id;
    EditText editText_chat_message;
    ListView listView_chat_Messages;
    Button button_send_chat;
    List<ChatObject> chat_list;

    BroadcastReceiver recieve_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editText_mail_id = (EditText) findViewById(R.id.editText_email_id);
        editText_chat_message = (EditText) findViewById(R.id.editText_chat_message);
        listView_chat_Messages = (ListView) findViewById(R.id.listView_chat_messages);
        button_send_chat = (Button) findViewById(R.id.button_send_chat);
        button_send_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Enviar mensaje de chat al servicio
                String message = editText_chat_message.getText().toString();
                showChat("send", message);
                new SendMessage().execute();
                editText_chat_message.setText("");
            }
        });

        recieve_chat = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");

                Log.d("pavan", "in local broad "+ message);
                showChat("receive", message);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(recieve_chat, new IntentFilter("message_received"));
    }

    private void showChat (String type, String message){
        if (chat_list == null || chat_list.size()==0){
            chat_list = new ArrayList<ChatObject>();
        }

        chat_list.add(new ChatObject(message, type));
        ChatAdapter chatAdapter = new ChatAdapter(ChatActivity.this, R.layout.chat_view, chat_list);
        listView_chat_Messages.setAdapter(chatAdapter);
    }

    @Override
    protected void onDestroy(){

        super.onDestroy();
    }

    private class SendMessage extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute(){
            //TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params){
            //TODO Auto-generated method stub

            String url = Util.send_chat_url+"?email_id="+editText_mail_id.getText().toString()+"&message="+editText_chat_message.getText().toString();
            Log.i("pavan", "url" + url);

            OkHttpClient client_for_getMyFriends = new OkHttpClient();
            String response = null;

            try{
                url = url.replace(" ", "%20");
                response = callOkHttpResquest(new URL(url), client_for_getMyFriends);
                for(String subString : response.split("<script", 2)){
                    response = subString;
                    break;
                }
            }catch (MalformedURLException e){
                e.printStackTrace();

            }catch (IOException e){
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result){

            super.onPostExecute(result);
        }
    }

    //Peticion HTTP usando OKHttpClient
    String callOkHttpResquest (URL url, OkHttpClient tempClient)
        throws IOException{
        HttpURLConnection connection = tempClient.open(url);

        connection.setConnectTimeout(40000);
        InputStream in = null;
        try{
            in = connection.getInputStream();
            byte [] response = readFully(in);
            return new String (response, "UTF-8");
        } finally{
            if (in != null)
                in.close();
        }
    }

    byte[] readFully (InputStream in) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer))!=-1;){
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }
}
