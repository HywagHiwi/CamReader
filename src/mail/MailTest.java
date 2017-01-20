package mail;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class MailTest {

    public static void main(String[] args) throws AddressException, MessagingException
    {
        String recipient = "s.nikelski@web.de";
        String subject = "Kamera-Problem";
        String text = "Hallo, ich (die Kamera) funktioniere nicht.";
 
        Mail.send(MailAccounts.TU, recipient, subject, text);      
    }
	
}
