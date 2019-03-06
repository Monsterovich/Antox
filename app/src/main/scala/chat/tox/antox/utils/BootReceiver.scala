package chat.tox.antox.utils

import android.content.{BroadcastReceiver, Context, Intent, SharedPreferences}
import android.preference.PreferenceManager
import chat.tox.antox.tox.ToxService

/**
  * This Broadcast Receiver will pick up the phone booting up and will proceed to start the tox service
  */
class BootReceiver extends BroadcastReceiver {
  override def onReceive(context: Context, intent: Intent): Unit = {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    if (intent.getAction.equals(Intent.ACTION_BOOT_COMPLETED) && preferences.getBoolean("autostart", true)) {
      val startTox = new Intent(context, classOf[ToxService])
      context.startService(startTox)
    }
  }
}
