package com.example.trimetable

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

        val action = intent!!.action ?: ""

        if (context != null && action == "refresh") {
            fetchData(context)
        }
    }

    override fun onEnabled(context: Context) {
        fetchData(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    // Update All Widgets
    private fun updateWidgets(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, javaClass))

        // Update every widget
        ids.forEach { id -> updateAppWidget(context, manager, id) }
    }

    private fun fetchData(context: Context) {
        val views = RemoteViews(context.packageName, R.layout.tridget)
        views.setTextViewText(R.id.loading, "Fetching Data...")
        val componentName = ComponentName(context, Tridget::class.java)
        AppWidgetManager.getInstance(context).updateAppWidget(componentName, views)

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.tfgm.com/odata/")
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getMetroData()



        retrofitData.enqueue(object : Callback<MetrolinkResponse?> {
            override fun onResponse(
                call: Call<MetrolinkResponse?>,
                response: Response<MetrolinkResponse?>
            ) {
                updateView(response.body(), context)
            }

            override fun onFailure(call: Call<MetrolinkResponse?>, t: Throwable) {
                Log.d("fetchfail", t.message!!)
                // Temp for testing
                views.setTextViewText(R.id.loading, t.message)
                AppWidgetManager.getInstance(context).updateAppWidget(componentName, views)
            }
        })
    }

    // Tutorial https://www.youtube.com/watch?v=5gFrXGbQsc8
    private fun updateView(body: MetrolinkResponse?, context: Context) {
        var data = body?.value ?: listOf()
        data = data.filter { x -> x.StationLocation == "Edge Lane" }

        val stringBuilder = StringBuilder()
        for (item in data) {
            if (item.Dest0 != "")
                stringBuilder.append(item.Dest0 + ": " + item.Wait0 + "mins\n")
            if (item.Dest1 != "")
                stringBuilder.append(item.Dest1 + ": " + item.Wait1 + "mins\n")
            if (item.Dest2 != "")
                stringBuilder.append(item.Dest2 + ": " + item.Wait2 + "mins\n")
            if (item.Dest3 != "")
                stringBuilder.append(item.Dest3 + ": " + item.Wait3 + "mins\n")
            stringBuilder.append("\n")
        }

        if (data[0].MessageBoard != "")
            stringBuilder.append(data[0].MessageBoard)

        val views = RemoteViews(context.packageName, R.layout.tridget)
        views.setTextViewText(R.id.data, stringBuilder.toString())
        views.setTextViewText(R.id.loading, "")
        val componentName = ComponentName(context, Tridget::class.java)
        AppWidgetManager.getInstance(context).updateAppWidget(componentName, views)
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
//        val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
//        val widgetText = prefs.getString("widgetText", "0") ?: "hello"
//        // Construct the RemoteViews object
//        views.setTextViewText(R.id.dest1, widgetText)


        // Launch a pending intent to increase the count
        val views = RemoteViews(context.packageName, R.layout.tridget)
        views.setOnClickPendingIntent(R.id.button, pendingIntent(context, "refresh"))

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
