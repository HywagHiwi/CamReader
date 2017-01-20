package timestamp;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

/**
 * Klasse zur Erzeugung eines Timestamps fuer die vom CamReader ausgelesenen Bilder.
 * @author snikelski
 */
public class Timestamp {
	
	/**
	 * 
	 * @param path
	 */
	public Timestamp(String path){
		File input = new File(path);
		try {
			BufferedImage bi = ImageIO.read(input);
			Graphics2D graphics = bi.createGraphics();
			Font font = new Font("ARIAL",Font.PLAIN, 20);
			graphics.setFont(font);
			graphics.drawString(getTime(), 10, 30);
			bi.flush();
			ImageIO.write(bi, "jpg", input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return gibt das aktuelle Datum (im ordentlichen Format) samt Uhrzeit als String zurueck
	 */
	private String getTime(){
		
		LocalDate date = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String text = date.format(formatter);

		return new String(text + " " + Time.valueOf(LocalTime.now()).toString());
	}
	
}
