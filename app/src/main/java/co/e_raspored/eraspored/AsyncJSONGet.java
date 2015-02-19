package co.e_raspored.eraspored;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Nicba on 19.2.2015..
 */
public class AsyncJSONGet extends AsyncTask<String, Void, JSONObject> {
	View activity;

	public AsyncJSONGet(View activity) {
		super();
		this.activity = activity;
	}

	@Override
	protected JSONObject doInBackground(String... urls) {
		String response;

		try {

			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httppost = new HttpPost(urls[0]);

			HttpResponse responce = httpclient.execute(httppost);

			HttpEntity httpEntity = responce.getEntity();

			response = EntityUtils.toString(httpEntity);

			writeToFile(response, "data.json");

			return new JSONObject(response);

		} catch (Exception ex) {

			ex.printStackTrace();

		}

		return null;
	}

	private void writeToFile(String data, String fileName) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(activity.getContext().openFileOutput("data.json", Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("TESTT", "File write failed: " + e.toString());
		}
	}

	@Override
	protected void onPostExecute(JSONObject jsonData) {
		Spinner spinnerClasses = (Spinner) activity.findViewById(R.id.spinnerClasses);
		Spinner spinnerSchools = (Spinner) activity.findViewById(R.id.spinnerSchools);
		List<String> schoolArray = new ArrayList<String>();
		HashMap<String, ArrayList<String>> classMap = new HashMap<String, ArrayList<String>>();
		Iterator<?> keys = jsonData.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			schoolArray.add(StringEscapeUtils.unescapeHtml(key));
			try {
				ArrayList<String> classList = new ArrayList<String>();
				JSONObject jsonData1 = jsonData1 = jsonData.getJSONObject(key);
				JSONArray classes = jsonData1.getJSONArray("classData");
				for (int i = 0; i < classes.length(); i++) {
					classList.add(classes.getJSONObject(i).getString("class"));
				}
				classMap.put(StringEscapeUtils.unescapeHtml(key), classList);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				activity.getContext(), android.R.layout.simple_spinner_item, schoolArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSchools.setAdapter(adapter);
		ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(
				activity.getContext(), android.R.layout.simple_spinner_item, classMap.get(spinnerSchools.getSelectedItem().toString()));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerClasses.setAdapter(adapterClass);
		Toast.makeText(activity.getContext(), "Podatci uspješno učitani!", Toast.LENGTH_LONG).show();
	}
}