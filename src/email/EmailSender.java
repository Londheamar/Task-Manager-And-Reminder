package email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailSender {

    public static void sendEmail(String toEmail, String subject, String body) {

        // Set up properties for the SMTP server
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Use the SMTP server of your email provider
        props.put("mail.smtp.port", "587"); // Use the appropriate port for your email provider
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

        // Create a Session object to authenticate with the SMTP server
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("mail@gmail.com", "");
                    }
                });
        
        
        try {
            // Create a MimeMessage object
//        	session.setDebug(true);
            MimeMessage message = new MimeMessage(session);

            // Set the sender and recipient email addresses
            message.setFrom(new InternetAddress("enquiry.collegekatta@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            // Set the email subject and body
            message.setSubject(subject);
//            message.setText(body);
            message.setContent(body, "text/html");

            // Send the email
            Transport.send(message);

            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
