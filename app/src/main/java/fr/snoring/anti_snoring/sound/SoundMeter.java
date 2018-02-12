/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.snoring.anti_snoring.sound;

import android.media.MediaRecorder;

import java.io.IOException;

public class SoundMeter {
	private MediaRecorder mRecorder;

	public void start() {
		if (mRecorder == null) {
			try {
                mRecorder = new MediaRecorder();
                mRecorder.reset();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
				mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
				mRecorder.setOutputFile("/dev/null");
				mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                mRecorder.prepare();
				mRecorder.start();
			} catch (IllegalStateException | IOException e) {
				throw new RuntimeException("MediaRecorder: "+mRecorder, e);
			}
		}
	}

	public void release() {
		mRecorder.release();
		mRecorder = null;
	}

	public double getAmplitude() {
		if (mRecorder != null) {
			return (mRecorder.getMaxAmplitude() / 2700.0);
		} else {
			return 0;
		}

	}
}