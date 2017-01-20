package mail;
 
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
 
/**
 * Liefert eine Authentifizierungsobjekt 
 * (muss die Klasse um "Authenticator" erweitern)
 */
public class MailAuthenticator extends Authenticator
{
    private String user;
    private String password;
     
    public MailAuthenticator(String user, String password)
    {
        this.user = user;
        this.password = password;
    }
     
    /**
     * getPasswordAuthentication() wird automatisch aufgerufen
     * sobald der Benutzernamen + Passwort verlangt wird
     */
    public PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(user, password);
    }
}