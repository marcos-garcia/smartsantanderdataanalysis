package com.marcosgarciacasado.ssscrapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Scanner;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * Runnable class which downloads every x seconds the content of a webpage and
 * stores it into a kafka queue.
 * 
 * @author Marcos García Casado
 *
 */
public class URLPeriodicDownload implements Runnable {

	private Producer<String, String> producer = null;
	private URL url;

	public URLPeriodicDownload(URL url) {
		this.url = url;
	}

	private synchronized Producer<String, String> getProducer() {
		if (producer == null) {
			// Producer initialization. Implements the singleton pattern
			Properties props = new Properties();
			props.put("metadata.broker.list", SSScrapper.kafkaLocation);
			props.put("serializer.class", "kafka.serializer.StringEncoder");
			props.put("request.required.acks", "1");

			ProducerConfig config = new ProducerConfig(props);

			producer = new Producer<String, String>(config);
		}
		return producer;
	};

	public void run() {
		while (true) {
			URLConnection conn;
			InputStream contentStream = null;
			String content = "";
			// Opens the connection
			try {
				conn = url.openConnection();
				conn.connect();
				contentStream = (InputStream) conn.getContent();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Receives the web content line by line
			Scanner s = new Scanner(contentStream);
			while (s.hasNext()) {
				content = content + s.nextLine();
			}
			// Closes the conection
			s.close();
			// Attachment of the actual date
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			System.out.println(dateFormat.format(cal.getTime())+": Sending data from " + url.toString());
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(
					"ssdata", url.toString()+";"+dateFormat.format(cal.getTime()), dateFormat.format(cal.getTime()) + "~" + content);
			// Sends it to the synchronized process that saves the content into the kafka queue
			registerDownload(data);
			try {
				Thread.sleep(SSScrapper.timeInterval * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void registerDownload(KeyedMessage<String, String> data) {
		// Sends the data into the kafka queue
		getProducer().send(data);
	}

}
