package co.e_raspored.eraspored.service.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

import co.e_raspored.eraspored.provider.DayScheduleProvider;

/**
 * Created by Nicba on 12.2.2015..
 */
public class DayScheduleWidgetService extends RemoteViewsService {
/*
* So pretty simple just defining the Adapter of the listview
* here Adapter is ListProvider
* */

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		int appWidgetId = intent.getIntExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		Log.d("APP", Integer.toString(appWidgetId));
		return (new DayScheduleProvider(this.getApplicationContext(), intent));
	}

}
