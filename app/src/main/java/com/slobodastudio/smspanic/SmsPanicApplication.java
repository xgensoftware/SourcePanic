package com.slobodastudio.smspanic;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.slobodastudio.smspanic.dao.DaoMaster;
import com.slobodastudio.smspanic.dao.DaoMaster.DevOpenHelper;
import com.slobodastudio.smspanic.dao.DaoSession;
import com.slobodastudio.smspanic.dao.EmailMessageRecord;
import com.slobodastudio.smspanic.dao.EmailMessageRecordDao;

@ReportsCrashes(formUri = "http://www.bugsense.com/api/acra?api_key=8ea62437", formKey="")
public class SmsPanicApplication extends Application{
	private DaoMaster daoMaster;
	private DevOpenHelper helper;
	private DaoSession daoSession;
	private EmailMessageRecordDao messageDao;
	private SQLiteDatabase db;
	

	public EmailMessageRecordDao getMessageDao() {
		return messageDao;
	}
	
	@Override
	public void onCreate() {
		ACRA.init(this);
		// TODO Auto-generated method stub
		helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		messageDao = daoSession.getEmailMessageRecordDao();
		super.onCreate();
	}
	
	public void addEmail(EmailMessageRecord m){
		messageDao.insert(m);
	}
	
}
