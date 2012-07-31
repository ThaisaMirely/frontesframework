package br.ufc.ubiparkingclient_using_frontes.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceStarter extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent2 = new Intent(context, LocationCheckService.class);
		context.startService(intent2);
	}

}
