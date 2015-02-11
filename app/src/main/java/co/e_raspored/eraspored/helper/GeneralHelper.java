package co.e_raspored.eraspored.helper;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Nicba on 23.1.2015..
 */
public class GeneralHelper {
	Context ctx;
	Vibrator v;

	public GeneralHelper(Context ctx) {
		this.ctx = ctx;
		v = (Vibrator) this.ctx.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void vibrate() {
		if (v.hasVibrator()) {
			v.vibrate(3000);
		}
	}
}
