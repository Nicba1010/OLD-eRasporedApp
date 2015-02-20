package co.e_raspored.eraspored.helper;

/**
 * Created by Nicba on 11.2.2015..
 */
public class DayScheduleItem {
	public String num;
	public String subject;
	public String time;

	public DayScheduleItem(String num, String subject, String time) {
		this.num = num;
		this.subject = subject;
		this.time = time;
	}

	public DayScheduleItem() {
	}
}
