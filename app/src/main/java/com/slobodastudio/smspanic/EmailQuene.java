package com.slobodastudio.smspanic;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.net.Uri;

import com.slobodastudio.smspanic.dao.EmailMessageRecord;
import com.slobodastudio.smspanic.dao.EmailMessageRecordDao;

public final class EmailQuene {
	
	Queue<EmailMessageRecord> mQuene = new LinkedList<EmailMessageRecord>();
	EmailMessageRecordDao mDao;
	
	public EmailQuene(EmailMessageRecordDao dao){
		this.mDao = dao;
	}
	
	void load(){
		mQuene = new LinkedList<EmailMessageRecord>(mDao.loadAll());
	}
	
	public EmailMessageRecord getEmailToSend(){
		return mQuene.peek();
	}
	
	public Long addEmailMessage(EmailMessageRecord message){
		return mDao.insert(message);
	}
	
	public boolean RemoveEmailMessage(EmailMessageRecord message){
		mDao.delete(message);
		return mQuene.remove(message);
	}
}
