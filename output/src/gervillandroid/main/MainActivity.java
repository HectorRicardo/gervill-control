package own.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.text.MessageFormat;

import gervill.com.sun.media.sound.DLSSoundbank;
import gervill.com.sun.media.sound.SF2Soundbank;
import gervill.javax.sound.midi.Soundbank;
import samplesynth.SynthesizerPlayer;

public class MainActivity extends AppCompatActivity {

    private Soundbank soundbank;
    private final SynthesizerPlayer player = new SynthesizerPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(() -> {
            // Select random soundbank
            try {
                switch ((int) (Math.random() * 3)) {
                    case 0:
                        soundbank = new SF2Soundbank(getAssets().open("gm.sf2"));
                        break;
                    case 1:
                        soundbank = new DLSSoundbank(getAssets().open("gm.dls"));
                        break;
                    case 2:
                        soundbank = null;
                        break;
                    default:
                        throw new RuntimeException("WTF just happened");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            player.init(soundbank);

            runOnUiThread(() -> {
                final TextView textView = findViewById(R.id.allInstrumentsTextView);
                textView.setText(player.getInstrumentInfo());
            });
        }).start();
    }

    public void onButtonPressed(final View view) {
        if (player.readyToStartPlaying()) {
            new Thread(() -> {
                switch (view.getId()) {
                    case R.id.playSampleButton:
                        final String[] instrumentInfo = player.sample1();
                        runOnUiThread(() -> {
                            final TextView textView = findViewById(R.id.currentInstrumentTextView);
                            textView.setText(MessageFormat.format(
                                    "{0}\n{1}\n{2}",
                                    soundbank == null ? "Default soundbank" : soundbank.getClass().getSimpleName(),
                                    instrumentInfo[0],
                                    instrumentInfo[1]
                            ));
                        });
                        break;
                    case R.id.playAllInstrumentsButton:
                        player.sample2();
                        break;
                }
            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        player.close();
        super.onDestroy();
    }
}
