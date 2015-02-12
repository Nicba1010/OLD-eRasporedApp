package co.e_raspored.eraspored.provider;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

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
		remoteView.setTextViewText(R.id.num, listItem.num + ".");
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

	public DayScheduleItem createSchedulePart(int num, String subject) {
		return new DayScheduleItem(num, subject);
	}

	public void createSchedule() {
		array.add(createSchedulePart(1, "MAT"));
		array.add(createSchedulePart(2, "HRV"));
		array.add(createSchedulePart(3, "RAC"));
		array.add(createSchedulePart(4, "TZK"));
		array.add(createSchedulePart(5, "ESKL"));
	}
}