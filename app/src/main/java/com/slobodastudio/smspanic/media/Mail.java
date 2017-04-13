package com.slobodastudio.smspanic.media;

import android.util.Log;

import java.util.Date;
import java.util.Properties;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mail extends javax.mail.Authenticator {

	private String _user;
	private String _pass;
	private String[] _to;
	private String _from;
	private String _port;
	private String _sport;
	private String _host;
	private String _subject;
	private String _body;

	/** @return the _user */
	public String getUser() {

		return _user;
	}

	/** @param _user
	 *            the _user to set */
	public void setUser(String _user) {

		this._user = _user;
	}

	/** @return the _pass */
	public String getPass() {

		return _pass;
	}

	/** @param _pass
	 *            the _pass to set */
	public void setPass(String _pass) {

		this._pass = _pass;
	}

	/** @return the _to */
	public String[] getTo() {

		return _to;
	}

	/** @param _to
	 *            the _to to set */
	public void setTo(String[] _to) {

		this._to = _to;
	}

	/** @return the _from */
	public String getFrom() {

		return _from;
	}

	/** @param _from
	 *            the _from to set */
	public void setFrom(String _from) {

		this._from = _from;
	}

	/** @return the _port */
	public String getPort() {

		return _port;
	}

	/** @param _port
	 *            the _port to set */
	public void setSmtpPort(String _port) {

		this._port = _port;
	}

	/** @return the _sport */
	public String getSport() {

		return _sport;
	}

	/** @param _sport
	 *            the _sport to set */
	public void setSport(String _sport) {

		this._sport = _sport;
	}

	/** @return the _host */
	public String getHost() {

		return _host;
	}

	/** @param _host
	 *            the smtp-server to set */
	public void setSmtpHost(String _host) {

		this._host = _host;
	}

	/** @return the _subject */
	public String getSubject() {

		return _subject;
	}

	/** @param _subject
	 *            the _subject to set */
	public void setSubject(String _subject) {

		this._subject = _subject;
	}

	/** @return the _auth */
	public boolean isAuth() {

		return _auth;
	}

	/** @param _auth
	 *            the _auth to set */
	public void setAuth(boolean _auth) {

		this._auth = _auth;
	}

	/** @return the _debuggable */
	public boolean isDebuggable() {

		return _debuggable;
	}

	/** @param _debuggable
	 *            the _debuggable to set */
	public void setDebuggable(boolean _debuggable) {

		this._debuggable = _debuggable;
	}

	/** @return the _multipart */
	public Multipart getMultipart() {

		return _multipart;
	}

	/** @param _multipart
	 *            the _multipart to set */
	public void setMultipart(Multipart _multipart) {

		this._multipart = _multipart;
	}

	private boolean _auth;
	private boolean _debuggable;
	private Multipart _multipart;

	public Mail() {

		_host = "smtp.gmail.com"; // default smtp server
		_port = "465"; // default smtp port
		_sport = "465"; // default socketfactory port
		_user = ""; // username
		_pass = ""; // password
		_from = ""; // email sent from
		_subject = ""; // email subject
		_body = ""; // email body
		_debuggable = false; // debug mode on or off - default off
		_auth = true; // smtp authentication - default on
		_multipart = new MimeMultipart();
		// There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed
		// part, so this bit needs to be added.
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);
	}

	public Mail(String user, String pass) {

		this();
		_user = user;
		_pass = pass;
	}

	public boolean send() throws Exception {

		Properties props = _setProperties();
		if (!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("")
				&& !_subject.equals("") && !_body.equals("")) {
			Session session = Session.getInstance(props, this);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(_from));
			InternetAddress[] addressTo = new InternetAddress[_to.length];
			for (int i = 0; i < _to.length; i++) {
				addressTo[i] = new InternetAddress(_to[i]);
			}
			msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
			msg.setSubject(_subject);
			msg.setSentDate(new Date());
			// setup message body
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(_body);
			_multipart.addBodyPart(messageBodyPart);
			// Put parts in message
			msg.setContent(_multipart);
			// send email
			Transport.send(msg);
            Log.d("Mail.class", "mail send successfully");
            return true;
		} else {
			return false;
		}
	}

	public void addAttachment(String filename) throws Exception {

		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		_multipart.addBodyPart(messageBodyPart);
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {

		return new PasswordAuthentication(_user, _pass);
	}

	private Properties _setProperties() {

		Properties props = new Properties();
		props.put("mail.smtp.host", _host);
		if (_debuggable) {
			props.put("mail.debug", "true");
		}
		if (_auth) {
			props.put("mail.smtp.auth", "true");
		}
        props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", _port);
		props.put("mail.smtp.socketFactory.port", _sport);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		return props;
	}

	// the getters and setters
	public String getBody() {

		return _body;
	}

	public void setBody(String _body) {

		this._body = _body;
	}
	// more of the getters and setters …..
}
