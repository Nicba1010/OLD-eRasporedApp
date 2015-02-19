package co.e_raspored.eraspored;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends ActionBarActivity {
	public static MainActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		activity = this;
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new MainFragment())
					.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class MainFragment extends Fragment {

		public MainFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			final Spinner spinnerClasses = (Spinner) rootView.findViewById(R.id.spinnerClasses);
			final Spinner spinnerSchools = (Spinner) rootView.findViewById(R.id.spinnerSchools);
			Button loadData = (Button) rootView.findViewById(R.id.loadData);
			Button save = (Button) rootView.findViewById(R.id.save);
			loadData.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new AsyncJSONGet(rootView).execute(new String[]{"http://e-raspored.co/ajax/getSchools.php"});
				}
			});
			save.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Context context = getActivity();
					SharedPreferences sharedPref = context.getSharedPreferences(
							getString(R.string.pref), Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putString("school", StringEscapeUtils.escapeHtml(spinnerSchools.getSelectedItem().toString()));
					editor.putString("class", spinnerClasses.getSelectedItem().toString());
					editor.commit();
				}
			});
			List<String> schoolArray = new ArrayList<String>();
			HashMap<String, ArrayList<String>> classMap = new HashMap<String, ArrayList<String>>();
			new AsyncJSONGet(rootView).execute(new String[]{"http://e-raspored.co/ajax/getSchools.php"});
			try {
				String json = readFromFile("data.json");
				if (!json.equalsIgnoreCase("FILENOTFOUND")) {
					JSONObject jsonData = new JSONObject(json);
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
							rootView.getContext(), android.R.layout.simple_spinner_item, schoolArray);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					spinnerSchools.setAdapter(adapter);
					ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(
							rootView.getContext(), android.R.layout.simple_spinner_item, classMap.get(spinnerSchools.getSelectedItem().toString()));
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerClasses.setAdapter(adapterClass);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return rootView;
		}

		private String readFromFile(String fileName) {

			String ret = "FILENOTFOUND";

			try {
				InputStream inputStream = getActivity().openFileInput(fileName);

				if (inputStream != null) {
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					String receiveString = "";
					StringBuilder stringBuilder = new StringBuilder();

					while ((receiveString = bufferedReader.readLine()) != null) {
						stringBuilder.append(receiveString);
					}

					inputStream.close();
					ret = stringBuilder.toString();
				}
			} catch (FileNotFoundException e) {
				Log.e("login activity", "File not found: " + e.toString());
			} catch (IOException e) {
				Log.e("login activity", "Can not read file: " + e.toString());
			}

			return ret;
		}
	}
}
