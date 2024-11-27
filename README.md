# HubeRSoundX-Lite
## 针对扬声器、耳机的音频输出快捷优化<br><br>Optimized audio output for both speakers and headphones(Streamlined)
## 全局常驻音频处理服务，兼容安卓11及以上版本<br><br>Global Persistent Audio Processing Framework,Compatible with Android 11+
<br>
<br>

### _安装/激活 (Install/Activate)_
<br>

#### 安装APK后前往系统控制中心添加磁贴开关，磁贴开关如图
After installing the APK, go to the system control center to add the tile switch.<br>
![](https://huberhayu.github.io/HubeRSoundX-Lite/image/hsxlite.png)
<br>
<br>

### 部分开源内容(Partially open-sourced content)

#### _引用的权限Manifest(Manifest Permission)_

```ruby
  <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
  <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

#### _音效处理API(Audio Processing API)_<br><br>Officially open-sourced by Google for Android.

```ruby
  equalizer = new Equalizer(0,0);
  loudnessEnhancer = new LoudnessEnhancer(0);
```

#### _后台服务(Service)_

```ruby
  public class TileS extends TileService {
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
  }
```
