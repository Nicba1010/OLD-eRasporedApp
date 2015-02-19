package co.e_raspored.eraspored.provider;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import co.e_raspored.eraspored.R;
import co.e_raspored.eraspored.helper.DayScheduleItem;

/**
 * Created by Nicba on 12.2.2015..
 */
public class DayScheduleProvider implements RemoteViewsService.RemoteViewsFactory {
	ArrayList<DayScheduleItem> array = new ArrayList<DayScheduleItem>();
	private Context context = null;
	private int appWidgetId;

	public DayScheduleProvider(Context context, Intent intent) {
		this.context = context;
		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		createSchedule();
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onDataSetChanged() {

	}

	@Override
	public void onDestroy() {

	}

	@Override
	public int getCount() {
		return array.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	/*
	*Similar to getView of Adapter where instead of View
	*we return RemoteViews
	*
	*/
	@Override
	public RemoteViews getViewAt(int position) {
		final RemoteViews remoteView = new RemoteViews(
				context.getPackageName(), R.layout.list_layout);
		DayScheduleItem listItem = array.get(position);
		remoteView.setTextViewText(R.id.num, listItem.num);
		remoteView.setTextViewText(R.id.subject, listItem.subject);
		return remoteView;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	public DayScheduleItem createSchedulePart(String num, String subject) {
		return new DayScheduleItem(num, subject);
	}

	private String readFromFile(String fileName) {

		String ret = "FILENOTFOUND";

		try {
			InputStream inputStream = context.openFileInput(fileName);

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

	public void createSchedule() {
		HashMap<String, String> subjectMap = new HashMap<String, String>();
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getString(R.string.pref), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		try {
			String json = readFromFile("data.json");
			if (!json.equalsIgnoreCase("FILENOTFOUND")) {
				JSONObject jsonData = new JSONObject(json);
				String key = sharedPref.getString("school", null);
				String keyClass = sharedPref.getString("class", null);
				if (key != null && keyClass != null) {
					JSONObject _jsonData = jsonData.getJSONObject(key);
					String key2 = _jsonData.getString("currentSchedule");
					JSONObject __jsonData = _jsonData.getJSONObject("scheduleData");
					JSONArray subjectData = _jsonData.getJSONArray("subjectData");
					for (int p = 0; p < subjectData.length(); p++) {
						subjectMap.put(subjectData.getJSONObject(p).getString("id"), StringEscapeUtils.escapeHtml(subjectData.getJSONObject(p).getString("subject")));
					}
					JSONObject scheduleJsonData = __jsonData.getJSONObject(key2);
					JSONObject _scheduleJsonData = scheduleJsonData.getJSONObject(keyClass);
					JSONObject __scheduleJsonData = _scheduleJsonData.getJSONObject("Popodne");
					JSONObject dayScheduleJsonData = __scheduleJsonData.getJSONObject("day" + (dayOfWeek - 2));
					for (int k = 0; k < dayScheduleJsonData.names().length(); k++) {
						array.add(createSchedulePart(dayScheduleJsonData.names().getString(k), StringEscapeUtils.unescapeHtml(subjectMap.get(dayScheduleJsonData.getString(dayScheduleJsonData.names().getString(k))))));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Collections.sort(array, new CustomComparator());
	}

	class CustomComparator implements Comparator<DayScheduleItem> {
		@Override
		public int compare(DayScheduleItem o1, DayScheduleItem o2) {
			Log.d("TESTT", Integer.parseInt(o1.num.replace(".", "")) + ">" + Integer.parseInt(o2.num.replace(".", "")) + ":" + (Integer.parseInt(o1.num.replace(".", "")) > Integer.parseInt(o1.num.replace(".", "")) ? "JE" : "NIJE"));
			return Integer.parseInt(o1.num.replace(".", "")) > Integer.parseInt(o1.num.replace(".", "")) ? -1 : 1;
		}
	}
}