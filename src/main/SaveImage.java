
package main; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import mail.Mail;
import mail.MailAccounts;
import timestamp.Timestamp;

/**
 * @author Sebastian
 * 
 * Programm zum Auslesen der Wetterstationskamerabilder.
 * Es werden IPs der Kameras sowie Zielspeicherorte/-namen angegeben.
 * 
 * Die hinterlegten Daten muessen anschliessend mit TurboFTP hochgeladen 
 * werden - dies uebernimmt das Programm nicht.
 *
 */
public class SaveImage {

	static int mastFehlversuche = 0;
	static int gelaendeFehlversuche = 0; 
	static String mastUrl = "http://192.168.2.180:1180/cgi-bin/view/image.jpg";
	static String gelaendeUrl = "http://192.168.2.181/cgi-bin/view/image?pro_1.jpg";
	static boolean mailMastIstRaus = false;
	static boolean mailGelaendeIstRaus = false;
	
/**
 * Main-Methode, die zum Programmstart die beiden Bildquellen setzt und
 * anschliessend in eine Endlosschleife uebergeht. In dieser werden alle 
 * FUENF Minuten (300.000 Millisekunden) die Speichermethoden fuer jede
 * Kamera aufgerufen. Die Exceptions (im catch-Block) sorgen dafuer, dass
 * das Programm beim Nicht-Erreichen der Kameras nicht abstuerzt und beim
 * Wiedererreichen dieser korrekt weiterarbeiten kann.
 * 
 * @param args ungenutz.
 * @throws Exception 
 */
public static void main(String[] args) throws Exception {
	
	PrintWriter pwriter = new PrintWriter(new FileWriter("Errorlog.txt"), true);
	pwriter.println("Automatisch generierter Fehlerlog.");
	
	GregorianCalendar now; 
	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
	
	System.out.print("CamReader wird gestartet");
	for(int i = 0; i < 5; i++) {
		Thread.sleep(1000); System.out.print(".");
	}
	System.out.println("\nCamReader erfolgreich gestartet.");
	
	
    while(true) {
    	
    	now = new GregorianCalendar();
    	
    	mailMastIstRaus = saveAll(mastUrl, pwriter, now, df, mailMastIstRaus, "den Mast", "mast.jpg", mastFehlversuche);
    	mailGelaendeIstRaus = saveAll(gelaendeUrl, pwriter, now, df, mailGelaendeIstRaus, "das Gelaende", "gelaende.jpg", gelaendeFehlversuche);
    	
    	System.out.println(df.format(now.getTime()) + ": Der CamReader laeuft.");
        Thread.sleep(300000);
        //Thread.sleep(10000);
    }
}

/**
 * 
 * @param url URL der enstprechenden Kamera
 * @param pwriter PrintWriter zum Schreiben des Logs
 * @param now GregorianischerKalender um im Log die aktuelle Zeit...
 * @param df ... im richtigen Datumsformat auszugeben
 * @param gesendet ob die mail fuer den entsprechenden Fall schon raus ist.
 * @param gibt an wie die Kamera tituliert wird
 * @param der resultierende eindeutige Dateiname fuer den Upload mit Turbo-FTP.
 */
private static boolean saveAll(String url, PrintWriter pwriter, GregorianCalendar now, DateFormat df, boolean gesendet, String cam, String bildname, int Fehlversuche) {
	try{
		saveImage((url), "Bilder/" + bildname);
		if(new File("Bilder/" + bildname).length() < 10000) {
			System.out.println(bildname + " konnte nicht erfolgreich generiert werden. Bitte Kamera neu starten.");
			setFehlversuche(cam, 1);
		} else {
			System.out.println(bildname + " erfolgreich generiert.");
			setFehlversuche(cam, 0);
		}
	} catch (Exception e) {
		pwriter.println(df.format(now.getTime()) + ": Das Bild der Kamera fuer" + cam + " konnte nicht ausgelesen werden.");
		System.out.println("Das Bild der Kamera fuer " + cam + " konnte nicht ausgelesen werden.");
		setFehlversuche(cam, 1);
		System.out.println("Anzahl Fehlversuche: " + Fehlversuche + " (Bei 3 Fehlversuchen in Folge wird eine Mail rausgeschickt.)");
		if(gesendet == false && Fehlversuche > 3) {
			try {				
				sendMail(cam);
    			return true;
			} catch( Exception ex) {
				pwriter.println(df.format(now.getTime()) + ": E-Mail konnte nicht versendet werden.");
				System.out.println("Mail konnte nicht versendet werden.");
			}
		}	
	}
	return gesendet;
}

/**
 * Methode zum Speichern des Bildes einer Kamera. 
 * 
 * @param imageUrl gibt an, wo das originale Kamera-Bild im Netzwerk zu finden ist.
 * @param destinationFile gibt an, wo das Bild hinterlegt werden soll.
 * @throws IOException
 */
private static void saveImage(String imageUrl, String destinationFile) throws IOException {
    URL url = new URL(imageUrl);
    InputStream is = url.openStream();
    OutputStream os = new FileOutputStream(destinationFile);

    byte[] b = new byte[2048];
    int length;

    while ((length = is.read(b)) != -1) {
        os.write(b, 0, length);
    }

    is.close();
    os.close();
    
    Timestamp t = new Timestamp(destinationFile);
}

/**
 * Methode zum Senden einer e-mail beim nicht Erreichen einer Kamera.
 * 
 * @param cam
 * @throws AddressException
 * @throws MessagingException
 */
private static void sendMail(String cam) throws AddressException, MessagingException {
    
    /**
     * Liste zum Speichern aller e-mail-Adressen, die ueber das nicht erreichen einer Kamera
     * informiert werden sollen.
     */
	List<String> empfaenger = new LinkedList<String>(); 
    
    empfaenger.add(new String("s.nikelski@tu-braunschweig.de"));
    //empfaenger.add(new String("d.nancekievill@tu-braunschweig.de"));
    //empfaenger.add(new String("s.schroedel@tu-braunschweig.de"));
    //empfaenger.add(new String("tim.mueller@tu-braunschweig.de"));
    
    String subject = "Die Kamera fuer " + cam + " funktioniert nicht";
    String text = "Hallo, \n die Kamera fuer " + cam + " funktioniert leider gerade nicht. \n" +
    			  "Da das Javaprogramm aber noch funktioniert, liegt das Problem wohl beim AP \n" +
    			  "oder natuerlich den Kameras selbst. Bitte umgehend ueberpruefen.\n" +
    			  "in letzter Zeit hat sich ein Klima-PC-Neustart oft bewaehrt. \n" +
    			  "Diese Mail wurde automatisch generiert, bitte nicht antworten.";
    
    /**
     * For-Each-Schleife.
     * Sprich: "Fuer jede Adresse in empfaenger sende eine mail an die entsprechende Person"
     */
    for(String s : empfaenger){
    	Mail.send(MailAccounts.TU, s, subject, text);    	
    }
    System.out.println(subject);
    
}

/**
 * Methode zum Setzen der Fehlversuche fuer eine Kamera
 * @param cam referenziert die entsprechende Kamera
 * @param typ was mit den Fehlversuchen gemacht werden soll:
 * 		  0: Die Fehlversuche werden resettet
 *        1: Die Anzahl der Fehlversuche wird um 1 erhoeht
 */
private static void setFehlversuche(String cam, int typ) {
	if(cam.contains("Mast")) {
		mastFehlversuche = addOrReset(mastFehlversuche, typ);
	} else if (cam.contains("Gel")){
		gelaendeFehlversuche = addOrReset(gelaendeFehlversuche, typ);
	}
}

private static int addOrReset(int Fehlversuche, int typ) {
	if(typ == 1) {
		return Fehlversuche++;
	} else if (typ == 0) {
		return Fehlversuche = 0;
	} else {
		return 0;
	}
}

}