package mail;
 
public enum MailAccounts
{
	// Klappt so leider nicht, da wir erst mittels STARTTLS authentifizieren müssten. 
	GOOGLEMAIL("smtp.googlemail.com", 25, "Klimastation", "waterbuilding", "Klimastation"),
    TU("groupware.tu-braunschweig.de", 25, "i5101802", new String(String.valueOf(4030>>1) + (char)97 + (char)108 + (char)116 +
			 (char)65 + (char)76 + (char)84), "i5101802@tu-braunschweig.de"),
    ;
     
    private String smtpHost;
    private int port;
    private String username;
    private String password;
    private String email;
     
    /**
     * Setzt die notwendigen Attribute des MailAccounts
     * @param smtpHost - SMTP Host
     * @param port - Port
     * @param username - Benutzername
     * @param password - Passwort
     * @param email - Absender E-Mail
     */
    private MailAccounts(String smtpHost, int port, String username, String password, String email)
    {
        this.smtpHost = smtpHost;
        this.port = port;
        this.username = username;
        this.password = password;
        this.email = email;
    }
     
    public int getPort()
    {
        return port;
    }
     
    public String getSmtpHost()
    {
        return smtpHost;
    }
     
    public MailAuthenticator getPasswordAuthentication()
    {
        return new MailAuthenticator(username, password);
    }
     
    public String getEmail()
    {
        return email;
    }
}