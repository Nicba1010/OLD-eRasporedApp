package co.e_raspored.eraspored.helper;

import android.content.Context;
import android.media.AudioManager;

import co.e_raspored.eraspored.SoundMode;

/**
 * Created by Nicba on 23.1.2015..
 */
public class SoundHelper {
	Context ctx;

	public SoundHelper(Context ctx) {
		this.ctx = ctx;
	}

	public void setSound(SoundMode mode) {
		AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		switch (mode) {
			case VIBRATE:
				audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
				break;
			case NORMAL:
				audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
				break;
		}
	}
}
