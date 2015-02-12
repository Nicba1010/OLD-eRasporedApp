package co.e_raspored.eraspored;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import co.e_raspored.eraspored.service.widget.DayScheduleWidgetService;

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
			RemoteViews remoteViews = updateWidgetListView(context,
					appWidgetIds[i]);

			// Tell the AppWidgetManager to perform an update on the current app widget
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
	}

	private RemoteViews updateWidgetListView(Context context,
											 int appWidgetId) {
		//which layout to show on widget
		RemoteViews remoteViews = new RemoteViews(
				context.getPackageName(), R.layout.dayschedule_appwidget);

		//RemoteViews Service needed to provide adapter for ListView
		Intent svcIntent = new Intent(context, DayScheduleWidgetService.class);
		//passing app widget id to that RemoteViews Service
		svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		//setting a unique Uri to the intent
		//don't know its purpose to me right now
		svcIntent.setData(Uri.parse(
				svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
		//setting adapter to listview of the widget
		remoteViews.setRemoteAdapter(R.id.dayScheduleListView, svcIntent);
		//setting an empty view in case of no data
		remoteViews.setEmptyView(R.id.dayScheduleListView, R.id.empty_view);
		return remoteViews;
	}
}