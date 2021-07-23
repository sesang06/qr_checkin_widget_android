package com.sesang06.qrcheckin

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*


/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {

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
        views.setTextViewText(R.id.last_update, "가져오기 실패")
        appWidgetManager.updateAppWidget(appWidgetId, views)

    }

    fun fetchQRImage(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.new_app_widget)

        val fetcher = QRImageFetcher(context)
        val success: (Bitmap) -> Unit = { bitmap: Bitmap ->
            views.setViewVisibility(R.id.checkin_image_view, View.VISIBLE)

            views.setImageViewBitmap(R.id.checkin_image_view, bitmap)
            appWidgetManager.updateAppWidget(appWidgetId, views)

            val date = Date()

            val dateformat = SimpleDateFormat("M/dd a h:mm", Locale.KOREAN)
            val text = dateformat.format(date)
            views.setTextViewText(R.id.last_update, "업데이트 ${text}")
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        val failure: () -> Unit = {
            showFailView(context, appWidgetManager, appWidgetId)
        }
        fetcher.fetch(success, failure)
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
        fetchQRImage(context, appWidgetManager, appWidgetId)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

}


