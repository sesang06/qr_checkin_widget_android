package com.sesang06.qrcheckin

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.CountDownTimer
import android.util.Base64
import android.view.View
import android.widget.RemoteViews
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.HashMap


/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {
    companion object {
        var countDownTimer: CountDownTimer? = null
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisAppWidget = ComponentName(
            context!!.packageName,
            NewAppWidget::class.java.getName()
        )
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
        onUpdate(context!!, appWidgetManager, appWidgetIds)
    }


    fun showFailView(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.new_app_widget)

        views.setViewVisibility(R.id.checkin_image_view, View.GONE)
        views.setViewVisibility(R.id.refresh_button, View.VISIBLE)
        views.setTextViewText(R.id.last_update, "0초")
        appWidgetManager.updateAppWidget(appWidgetId, views)

    }

    fun fetchQRImage(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        repeatCount: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.new_app_widget)

        val fetcher = QRImageFetcher(context)
        val success: (Bitmap) -> Unit = { bitmap: Bitmap ->
            views.setViewVisibility(R.id.checkin_image_view, View.VISIBLE)
            views.setViewVisibility(R.id.refresh_button, View.GONE)

            views.setImageViewBitmap(R.id.checkin_image_view, bitmap)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            makeCountDown(context, appWidgetManager, appWidgetId, repeatCount)
        }
        val failure: () -> Unit = {
            showFailView(context, appWidgetManager, appWidgetId)
        }
        fetcher.fetch(success, failure)
    }


    fun makeCountDown(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        repeatCount: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.new_app_widget)
        val mMilliseconds: Long = 1000 * 15
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(mMilliseconds, 1000) {
            override fun onFinish() {
                if (repeatCount == 0) {

                    showFailView(context, appWidgetManager, appWidgetId)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } else {
                    fetchQRImage(context, appWidgetManager, appWidgetId, repeatCount-1)
                }
            }

            override fun onTick(millisUntilFinished: Long) {

                val currentSecond = millisUntilFinished / 1000

                views.setTextViewText(R.id.last_update, "${currentSecond}초")
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
        countDownTimer?.start()
    }

    fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.new_app_widget)


        val intent = Intent(context, NewAppWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val pendingSync = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.refresh_button, pendingSync)
        fetchQRImage(context, appWidgetManager, appWidgetId, 3)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

}


