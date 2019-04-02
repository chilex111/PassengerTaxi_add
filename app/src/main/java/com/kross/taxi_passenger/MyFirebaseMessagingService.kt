package com.kross.taxi_passenger

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kross.taxi_passenger.data.repository.server.socket.Parser
import com.kross.taxi_passenger.presentation.screen.main.MainActivity
import com.kross.taxi_passenger.utils.saveToSharedPreference
import org.jetbrains.anko.runOnUiThread
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val type: Int = remoteMessage?.data?.get(getString(R.string.type_key))?.toInt()!!
        val body = remoteMessage.data?.get(getString(R.string.body_key))
        val title = remoteMessage.data?.get(getString(R.string.title_key))
        applicationContext.runOnUiThread { liveDataNotificationEvent.value = type }
        Log.d("Socket Communicator: ", "order push type $type")
        if (type == 3) {
            val date = remoteMessage.data?.get(getString(R.string.date_key))

            if (date != null) {
                saveDriverArrivedDate(date)
            } else {
                saveToSharedPreference(R.string.arrived_key, Calendar.getInstance().timeInMillis)
            }
        }
        if (title != null && body != null) {
            sendNotification(body, title, type)
        }
    }

    private fun saveDriverArrivedDate(date: String) {
        saveToSharedPreference(R.string.arrived_key, date.toLong())
    }

    override fun onNewToken(token: String?) {
        token?.let { saveToSharedPreference(R.string.PUSH_TOKEN, it) }
    }


    private fun sendNotification(message: String, title: String, type: Int) {
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_app_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(getIntentByType(type))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    private val NOT_FOUND_STATUS_PUSH = 5

    private fun getIntentByType(type: Int): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        if (type == NOT_FOUND_STATUS_PUSH) {
            intent.putExtra(getString(R.string.PUSH_TYPE), 5)
        }
        return PendingIntent.getActivity(this, 0, intent, 0)

    }

    companion object {

        val liveDataNotificationEvent = MutableLiveData<Int>()

        private const val TAG = "MyFirebaseMsgService"
    }
}