package co.e_raspored.eraspored;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import java.util.ArrayList;

/**
 * Created by Nicba on 11.2.2015..
 */
public class DayScheduleAppWidgetProvider extends AppWidgetProvider {

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;

		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			// Get the layout for the App Widget and attach an on-click listener
			// to the button
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.dayschedule_appwidget);

			// Tell the AppWidgetManager to perform an update on the current app widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	public DayScheduleItem createSchedulePart(int num, String subject) {
		return new DayScheduleItem(num, subject);
	}

	public DayScheduleItem[] createSchedule() {
		ArrayList<DayScheduleItem> array = new ArrayList<DayScheduleItem>();
		array.add(createSchedulePart(1, "MAT"));
		array.add(createSchedulePart(2, "HRV"));
		array.add(createSchedulePart(3, "RAC"));
		array.add(createSchedulePart(4, "TZK"));
		array.add(createSchedulePart(5, "ESKL"));
		return array.toArray(new DayScheduleItem[array.size()]);
	}
}