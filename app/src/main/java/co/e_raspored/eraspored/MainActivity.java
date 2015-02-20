package co.e_raspored.eraspored;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends ActionBarActivity {
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String SENDER_ID = "975096398491";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static MainActivity activity;
	private static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		activity = this;
		context = getApplicationContext();
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
		GoogleCloudMessaging gcm;
		AtomicInteger msgId = new AtomicInteger();
		SharedPreferences prefs;
		String regid;
		String TAG = "TESTT";
		TextView mDisplay;

		public MainFragment() {
		}

		/**
		 * @return Application's version code from the {@code PackageManager}.
		 */
		private static int getAppVersion(Context context) {
			try {
				PackageInfo packageInfo = context.getPackageManager()
						.getPackageInfo(context.getPackageName(), 0);
				return packageInfo.versionCode;
			} catch (PackageManager.NameNotFoundException e) {
				// should never happen
				throw new RuntimeException("Could not get package name: " + e);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			final Spinner spinnerClasses = (Spinner) rootView.findViewById(R.id.spinnerClasses);
			final Spinner spinnerSchools = (Spinner) rootView.findViewById(R.id.spinnerSchools);
			Button loadData = (Button) rootView.findViewById(R.id.loadData);
			Button save = (Button) rootView.findViewById(R.id.save);
			mDisplay = (TextView) rootView.findViewById(R.id.display);
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
			new DownloadFilesTask().execute();
			if (checkPlayServices()) {
				gcm = GoogleCloudMessaging.getInstance(MainActivity.activity);
				regid = getRegistrationId(context);

				if (regid.isEmpty()) {
					registerInBackground();
				}
			} else {
				Log.i("TESTT", "No valid Google Play Services APK found.");
			}
			return rootView;
		}

		/**
		 * Gets the current registration ID for application on GCM service.
		 * <p/>
		 * If result is empty, the app needs to register.
		 *
		 * @return registration ID, or empty string if there is no existing
		 * registration ID.
		 */
		private String getRegistrationId(Context context) {
			final SharedPreferences prefs = getGCMPreferences(context);
			String registrationId = prefs.getString(PROPERTY_REG_ID, "");
			if (registrationId.isEmpty()) {
				Log.i(TAG, "Registration not found.");
				return "";
			}
			// Check if app was updated; if so, it must clear the registration ID
			// since the existing registration ID is not guaranteed to work with
			// the new app version.
			int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
			int currentVersion = getAppVersion(context);
			if (registeredVersion != currentVersion) {
				Log.i(TAG, "App version changed.");
				return "";
			}
			return registrationId;
		}

		/**
		 * @return Application's {@code SharedPreferences}.
		 */
		private SharedPreferences getGCMPreferences(Context context) {
			// This sample app persists the registration ID in shared preferences, but
			// how you store the registration ID in your app is up to you.
			return activity.getSharedPreferences(MainActivity.class.getSimpleName(),
					Context.MODE_PRIVATE);
		}

		/**
		 * Registers the application with GCM servers asynchronously.
		 * <p/>
		 * Stores the registration ID and app versionCode in the application's
		 * shared preferences.
		 */
		private void registerInBackground() {
			new AsyncTask<Void, Void, String>() {

				@Override
				protected String doInBackground(Void... params) {
					String msg = "";
					try {
						if (gcm == null) {
							gcm = GoogleCloudMessaging.getInstance(context);
						}
						regid = gcm.register(SENDER_ID);
						msg = "Device registered, registration ID=" + regid;
						Log.d(TAG, msg);

						// You should send the registration ID to your server over HTTP,
						// so it can use GCM/HTTP or CCS to send messages to your app.
						// The request to your server should be authenticated if your app
						// is using accounts.
						new DownloadFilesTask().execute();

						// For this demo: we don't need to send it because the device
						// will send upstream messages to a server that echo back the
						// message using the 'from' address in the message.

						// Persist the registration ID - no need to register again.
						storeRegistrationId(context, regid);
					} catch (IOException ex) {
						msg = "Error :" + ex.getMessage();
						// If there is an error, don't just keep trying to register.
						// Require the user to click a button again, or perform
						// exponential back-off.
					}
					return msg;
				}

				@Override
				protected void onPostExecute(String msg) {
					mDisplay.append(msg + "\n");
				}
			}.execute(null, null, null);
		}

		private void sendRegistrationIdToBackend(String s) {
			postData(s);
		}

		/**
		 * Stores the registration ID and app versionCode in the application's
		 * {@code SharedPreferences}.
		 *
		 * @param context application's context.
		 * @param regId   registration ID
		 */
		private void storeRegistrationId(Context context, String regId) {
			final SharedPreferences prefs = getGCMPreferences(context);
			int appVersion = getAppVersion(context);
			Log.i(TAG, "Saving regId on app version " + appVersion);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(PROPERTY_REG_ID, regId);
			editor.putInt(PROPERTY_APP_VERSION, appVersion);
			editor.commit();
		}

		@Override
		public void onResume() {
			super.onResume();
			checkPlayServices();
		}

		private boolean checkPlayServices() {
			int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainActivity.activity);
			if (resultCode != ConnectionResult.SUCCESS) {
				if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
					GooglePlayServicesUtil.getErrorDialog(resultCode, MainActivity.activity,
							PLAY_SERVICES_RESOLUTION_REQUEST).show();
				} else {
					Log.i("TESTT", "This device is not supported.");
					MainActivity.activity.finish();
				}
				return false;
			}
			return true;
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

		public void postData(String s) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://e-raspored.co/addPhone.php");

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("phoneid", s));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				Log.d(TAG, EntityUtils.toString(response.getEntity()));
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}

		class DownloadFilesTask extends AsyncTask<Void, Void, Void> {
			@Override
			protected Void doInBackground(Void... params) {
				sendRegistrationIdToBackend(getRegistrationId(context));
				return null;
			}
		}
	}
}
