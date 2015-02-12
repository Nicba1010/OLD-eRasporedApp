package co.e_raspored.eraspored;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Nicba on 11.2.2015..
 */
public class DayScheduleArrayAdapter extends ArrayAdapter<DayScheduleItem> {
	private final Context context;
	private final DayScheduleItem[] items;

	public DayScheduleArrayAdapter(Context context, DayScheduleItem[] items) {
		super(context, R.layout.list_layout, items);
		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_layout, parent, false);
		TextView num = (TextView) rowView.findViewById(R.id.num);
		TextView subject = (TextView) rowView.findViewById(R.id.subject);
		num.setText(items[position].num);
		subject.setText(items[position].subject);
		return rowView;
	}
}
