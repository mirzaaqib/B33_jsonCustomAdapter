package com.mirzaaqibbeg.b33_jsoncustomadapter;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {
    //step 10 declare all variables here
    Button b;
    ListView lv;
    ArrayList<Contacts> al;
    MyAdapter ma;
    MyTask m;


    //step 9 create 2 inner class
    //for async task
    public class MyTask extends AsyncTask<String, Void, String> {

        URL myurl;
        HttpURLConnection connection;
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        String line;
        StringBuilder result;

        protected String doInBackground(String... p1) {
            try {
                myurl = new URL(p1[0]);
                connection = (HttpURLConnection) myurl.openConnection();
                inputStream = connection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);


                result = new StringBuilder();
                line = bufferedReader.readLine();
                while (line != null){
                    result.append(line);
                    line = bufferedReader.readLine();

                }
                return result.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("B33", "Message..."+e.getMessage()); //these are the exception methods
                Log.d("B33", "cause..."+e.getCause());
                e.printStackTrace();//prints complete info about error
            } finally {
                //clean impo resources -eg: closing all network connections
                if(connection != null){
                    connection.disconnect();
                    if(inputStream != null)
                    {
                        try {
                            inputStream.close();
                            inputStreamReader.close();
                            bufferedReader.close();
                        } catch (IOException e) {
                            Log.d("B33" , "problem in closing connection");
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(String s){
            if(s == null)
            {
                Toast.makeText(getActivity(), "Network issue, Fix", Toast.LENGTH_SHORT).show();
                return;
            }

            //start json parsing

            try {
                JSONObject j = new JSONObject(s);   //it actually conatining the actual logic

                JSONArray arr = j.getJSONArray("contacts");
                for(int i=0; i<arr.length();i++ ){
                    JSONObject temp = arr.getJSONObject(i);
                    String name = temp.getString("name");
                    String email = temp.getString("email");
                    JSONObject phone = temp.getJSONObject("phone");
                    String mobile = phone.getString("mobile");
                    // with tbis we got one object

                    Contacts c = new Contacts();
                    c.setCno(""+"(i+1)");
                    c.setCname(name);
                    c.setEmail(email);
                    c.setCmobile(mobile);

                    //push to array lsit

                    al.add(c);

                    ma.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                Log.d("B33", "json exception"+e.getMessage());
                e.printStackTrace();
            }
        }
        //step 14-onpost execute for parsing null
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return al.size();
        }

        @Override
        public Object getItem(int i) {
            return al.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            //a.based on position read contact objects from array list
            Contacts c= al.get(i);
            //b.load row xml & all other views

                View v = getActivity().getLayoutInflater().inflate(R.layout.row,null);
            TextView tv1 = (TextView) v.findViewById(R.id.textview1);
            TextView tv2 = (TextView) v.findViewById(R.id.textview2);
            TextView tv3 = (TextView) v.findViewById(R.id.textview3);
            TextView tv4 = (TextView) v.findViewById(R.id.textview4);
            //c fill data onto text views using getters

            tv1.setText(c.getCno());
            tv2.setText(c.getCname());
            tv3.setText(c.getCmobile());
            tv4.setText(c.getEmail());

            //
            return v;


        }
    }

    public MyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //step 11  initialise variable and button clicklistner
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my, container, false);
        b = (Button) v.findViewById(R.id.button1);
        lv = (ListView) v.findViewById(R.id.listview1);
        al = new ArrayList<Contacts>();
        ma = new MyAdapter();
        m = new MyTask();
        lv.setAdapter(ma);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternet()==true){
                    m.execute("http://api.androidhive.info/contacts/");
                }else{
                    //Display a dialog saying No internet plz check
                    Toast.makeText(getActivity(), "NO INTERNET", Toast.LENGTH_SHORT).show();
                }
            }


        });
        return v;

    }
    public boolean checkInternet(){
        ConnectivityManager conn = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                if(conn!=null){
                    NetworkInfo info = conn.getActiveNetworkInfo();
                 if(info != null && info.isConnected()){
                        return true;
                    }else {
                        return false;
                    }

                }else {
                        return false;
                    }
    }
    }

