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

public class AudioRecorder {
    private MediaRecorder mRecorder;
	private boolean started = false;

    public AudioRecorder() {
        super();
		mRecorder = new MediaRecorder();
	}

    public void start(String recordFilePath) {
        release();
		mRecorder = new MediaRecorder();
        try {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mRecorder.setOutputFile(recordFilePath);
            mRecorder.prepare();
            mRecorder.start();
            started = true;
        } catch (IllegalStateException | IOException e) {
            throw new RuntimeException(e);
        }

	}

    private void reset() {
        if (started) {
			mRecorder.stop();
			mRecorder.reset();
			started = false;
		}
	}

	public void release() {
		reset();
		mRecorder.release();
	}

	public double getAmplitude() {
		if (mRecorder != null && started) {
			return (mRecorder.getMaxAmplitude() / 2700.0);
		} else {
			return 0;
		}

	}
}