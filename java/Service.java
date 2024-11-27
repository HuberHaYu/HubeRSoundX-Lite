package hub.studio.hsxtools;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Handler;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import java.util.UUID;

public class TileS extends TileService {
    /*
      仅部分开源，实际功能以Release版本为主
      Partially open-sourced; actual functionality is based on the release version.
    */
    private final Handler handler = new Handler();
    private Runnable deviceMonitorTask;
    public static Tile tile;
    private AudioManager audioManager;
    public Equalizer equalizer;
    public LoudnessEnhancer loudnessEnhancer;
    public boolean isOn = false;

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        tile = getQsTile();
        tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        startDeviceMonitorTask();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onClick() {
        if (equalizer == null) {
            equalizer = new Equalizer(0,0);
        }
        if (loudnessEnhancer == null) {
            loudnessEnhancer = new LoudnessEnhancer(0);
        }
        equalizer.setBandLevel((short) 0, (short) 1500);
        equalizer.setBandLevel((short) 1, (short) 500);
        equalizer.setBandLevel((short) 2, (short) -250);
        equalizer.setBandLevel((short) 3, (short) 150);
        equalizer.setBandLevel((short) 4, (short) 300);
        loudnessEnhancer.setTargetGain(1750);
        Runnable task = () -> {
            tile = getQsTile();
            if (tile != null) {
                if (audioManager.isWiredHeadsetOn()) {
                    // Wired headset connected
                    equalizer.setBandLevel((short) 0, (short) -100);
                    equalizer.setBandLevel((short) 1, (short) 0);
                    equalizer.setBandLevel((short) 2, (short) -120);
                    equalizer.setBandLevel((short) 3, (short) 150);
                    equalizer.setBandLevel((short) 4, (short) 300);
                    loudnessEnhancer.setTargetGain(950);
                } else if (audioManager.isBluetoothScoOn() || audioManager.isBluetoothA2dpOn()) {
                    // Bluetooth connected
                    equalizer.setBandLevel((short) 0, (short) 100);
                    equalizer.setBandLevel((short) 1, (short) 50);
                    equalizer.setBandLevel((short) 2, (short) -120);
                    equalizer.setBandLevel((short) 3, (short) 0);
                    equalizer.setBandLevel((short) 4, (short) 300);
                    loudnessEnhancer.setTargetGain(950);
                } else {
                    // Speaker
                }

                new Thread(() -> {
                    if (tile.getState() == Tile.STATE_INACTIVE) {
                        try {
                            UUID System_SoundFX_UUID = UUID.fromString("fx_uuid");
                            AudioEffect System_SoundFX = AudioEffect.class.getConstructor(UUID.class, UUID.class, int.class, int.class)
                                    .newInstance(System_SoundFX_UUID, System_SoundFX_UUID, 0, 0);
                            System_SoundFX.setEnabled(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tile.setState(Tile.STATE_ACTIVE);
                        isOn = true;
                        equalizer.setEnabled(true);
                        loudnessEnhancer.setEnabled(true);
                    } else {
                        try {
                            UUID System_SoundFX_UUID = UUID.fromString("fx_uuid");
                            AudioEffect System_SoundFX = AudioEffect.class.getConstructor(UUID.class, UUID.class, int.class, int.class)
                                    .newInstance(System_SoundFX_UUID, System_SoundFX_UUID, 0, 0);
                            System_SoundFX.setEnabled(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tile.setState(Tile.STATE_INACTIVE);
                        isOn = false;
                        equalizer.setEnabled(false);
                        loudnessEnhancer.setEnabled(false);
                    }
                    tile.updateTile();
                }).start();
            }
        };
        new Thread(task).start();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        stopDeviceMonitorTask();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDeviceMonitorTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void startDeviceMonitorTask() {
        deviceMonitorTask = new Runnable() {
            @Override
            public void run() {
                monitorAudioDevice();
                handler.postDelayed(this, 600);
            }
        };
        handler.post(deviceMonitorTask);
    }

    private void stopDeviceMonitorTask() {
        if (deviceMonitorTask != null) {
            handler.removeCallbacks(deviceMonitorTask);
            deviceMonitorTask = null;
        }
    }
    private void monitorAudioDevice() {
        if (equalizer == null) {
            equalizer = new Equalizer(0,0);
        }
        if (loudnessEnhancer == null) {
            loudnessEnhancer = new LoudnessEnhancer(0);
        }

        if (audioManager.isWiredHeadsetOn()) {
            System.out.println("Wired headset.");
            equalizer.setBandLevel((short) 0, (short) -100);
            equalizer.setBandLevel((short) 1, (short) 0);
            equalizer.setBandLevel((short) 2, (short) -120);
            equalizer.setBandLevel((short) 3, (short) 150);
            equalizer.setBandLevel((short) 4, (short) 300);
            loudnessEnhancer.setTargetGain(950);
        } else if (audioManager.isBluetoothScoOn() || audioManager.isBluetoothA2dpOn()) {
            System.out.println("Bluetooth.");
            equalizer.setBandLevel((short) 0, (short) 100);
            equalizer.setBandLevel((short) 1, (short) 50);
            equalizer.setBandLevel((short) 2, (short) -120);
            equalizer.setBandLevel((short) 3, (short) 150);
            equalizer.setBandLevel((short) 4, (short) 300);
            loudnessEnhancer.setTargetGain(950);
        } else {
            System.out.println("Speaker.");
            equalizer.setBandLevel((short) 0, (short) 2000);
            equalizer.setBandLevel((short) 1, (short) 750);
            equalizer.setBandLevel((short) 2, (short) -250);
            equalizer.setBandLevel((short) 3, (short) 150);
            equalizer.setBandLevel((short) 4, (short) 300);
            loudnessEnhancer.setTargetGain(1750);
        }
    }
}
