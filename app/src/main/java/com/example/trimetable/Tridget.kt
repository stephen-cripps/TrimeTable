package com.example.trimetable

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class Tridget : AppWidgetProvider() {
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

    override fun onReceive(context: Context?, intent: Intent?) {
        // Handle intent broadcasts
        super.onReceive(context, intent)

        val action = intent!!.action?: ""

        if(context!= null && action == "increase"){
            // Update preferences
            val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            prefs.edit().putString("widgetText", (prefs.getString("widgetText", "0")!!.toInt() + 1).toString()).apply()
            updateWidgets(context)
        }
    }

    override fun onEnabled(context: Context) {
        val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        prefs.edit().putString("widgetText", "0").apply()
        updateWidgets(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    // Update All Widgets
    private fun updateWidgets(context: Context){
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, javaClass))

        // Update every widget
        ids.forEach { id -> updateAppWidget(context, manager, id) }
    }

    private fun pendingIntent(
        context: Context?,
        action: String
    ): PendingIntent? {
        val intent = Intent(context, javaClass)
        intent.action = action

        return PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val widgetText = prefs.getString("widgetText", "0") ?: "hello"
        Log.d("UpdateWidget", widgetText)
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.tridget)
        views.setTextViewText(R.id.textView, widgetText)

        // Launch a pending intent to increase the count
        views.setOnClickPendingIntent(R.id.button, pendingIntent(context,"increase"))

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

