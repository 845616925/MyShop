package xin.mcosmos.utils;

import java.security.Security;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class MailUtils {
	/**
	 * @param email 收件人邮箱
	 * @param emailMsg 邮件信息
	 */
	public static void sendMail(String email, String emailMsg)
			throws AddressException, MessagingException {
	
		try {
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
			//设置邮件会话参数
			Properties props = new Properties();
			//邮箱的发送服务器地址
			props.put("mail.smtp.host", "smtp.163.com");
			props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
			props.put("mail.smtp.socketFactory.fallback", "false");
			//邮箱发送服务器端口,这里设置为465端口
			props.put("mail.smtp.port", "465");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.auth", "true");

			//获取到邮箱会话,利用匿名内部类的方式,将发送者邮箱用户名和密码授权给jvm
			Session session = Session.getDefaultInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("15536002863", "luomo2018");
				}
			});
			//通过会话,得到一个邮件,用于发送
			Message msg = new MimeMessage(session);
			//设置发件人
			msg.setFrom(new InternetAddress("15536002863@163.com"));

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));

			msg.setSubject("用户激活");
			//设置邮件消息
			msg.setContent(emailMsg, "text/html;charset=utf-8");
			//调用Transport的send方法去发送邮件
			Transport.send(msg);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
