package whu.iss.sric.android;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

public class SoundPlay {

	// ��Ч������
	int streamVolume;

	// ����SoundPool ����
	private SoundPool soundPool;

	// ����HASH��
	private HashMap<Integer, Integer> soundPoolMap;

	/***************************************************************
	 * Function: initSounds(); Parameters: null Returns: None. Description:
	 * ��ʼ������ϵͳ Notes: none.
	 ***************************************************************/
	public void initSounds(Context context) {
		// ��ʼ��soundPool ����,��һ�������������ж��ٸ�������ͬʱ����,��2����������������,�����������������Ʒ��
		soundPool = new SoundPool(25, AudioManager.STREAM_MUSIC, 100);

		// ��ʼ��HASH��
		soundPoolMap = new HashMap<Integer, Integer>();

		// ��������豸���豸����
		AudioManager mgr = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	/**
	 * ����Դ�е���Ч���ص�ָ����ID(���ŵ�ʱ��Ͷ�Ӧ�����ID���ž�����)
	 * Function: loadSfx(); Parameters: null Returns: None. Description: ������Ч��Դ
	 * Notes: none.
	 */
	public void loadSfx(Context context, int raw, int ID) {
		soundPoolMap.put(ID, soundPool.load(context, raw, 1));
	}

	/***************************************************************
	 * Function: play(); Parameters: sound:Ҫ���ŵ���Ч��ID, loop:ѭ������ Returns: None.
	 * Description: �������� Notes: none.
	 ***************************************************************/
	public void play(int sound, int uLoop) {
		soundPool.play(soundPoolMap.get(sound), streamVolume, streamVolume, 1,
				uLoop, 1f);
	}
}
