package com.akshay.panda;

import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements
		TextToSpeech.OnInitListener {

	protected static final int RESULT_SPEECH = 1;

	private ImageButton btnSpeak;
	private TextView txtText;
	private String output;
	private TextToSpeech speaker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		speaker = new TextToSpeech(this, this);
		txtText = (TextView) findViewById(R.id.txtText);

		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

				try {
					startActivityForResult(intent, RESULT_SPEECH);
					txtText.setText("");
				} catch (ActivityNotFoundException a) {
					Toast t = Toast.makeText(getApplicationContext(),
							"Opps! Your device doesn't support Speech to Text",
							Toast.LENGTH_SHORT);
					t.show();
				}
			}
		});

	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (speaker != null) {
			speaker.stop();
			speaker.shutdown();
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SPEECH: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				output = text.get(0);
				speakOut();
				txtText.setText(text.get(0));
			}
			break;
		}

		}
	}

	@SuppressWarnings("deprecation")
	private void speakOut() {
		if (output != null) {
			speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onInit(int status) {

		if (status == TextToSpeech.SUCCESS) {

			int result = speaker.setLanguage(Locale.ROOT);
			speaker.setEngineByPackageName("com.google.android.tts");
			//speaker.setPitch((float) 0.6);
			speaker.setSpeechRate((float) 0.9);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
				btnSpeak.setEnabled(true);
				speakOut();
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}

	}

}