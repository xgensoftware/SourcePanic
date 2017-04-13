package com.slobodastudio.smspanic.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public final class EmailSenderService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return START_STICKY;
	}
}
