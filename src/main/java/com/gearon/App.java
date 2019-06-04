package com.gearon;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;


/**
 * Web Crawler 4 Pexels
 *
 */
public class App 
{
	static String defaultStorageFolder = System.getProperty("user.home") + "/" + "pexels";

    public static void main( String[] args ) throws Exception
    {
    	String FOLDER = "folder";
    	String TOPICS = "topics";
    	String SHARPNESS = "sharpness";
    	String THUMBNAIL = "thumbnail";
    	String CLEARER = "clearer";
    	String PERFECT = "perfect";
    	String CLEAREST = "clearest";
    	
    	String regex_thumbnail = ".*src=\"(.*?pexels-photo-[\\d]+\\.([\\w]+).*?)\".*";
		String regex_clearer = ".*\\s(.*pexels-photo-[\\d]+\\.([\\w]+)\\?.*dpr=2.*)\\s.*";
		String regex_perfect = ".*data-large-src=\"(.*?pexels-photo-[\\d]+\\.([\\w]+).*?)\".*";
		String regex_clearest = ".*data-big-src=\"(.*?pexels-photo-[\\d]+\\.([\\w]+).*?)\".*";
		
		HashMap<String, String> sharpnessMap = new HashMap<String, String>();
		sharpnessMap.put(THUMBNAIL, regex_thumbnail);
		sharpnessMap.put(CLEARER, regex_clearer);
		sharpnessMap.put(PERFECT, regex_perfect);
		sharpnessMap.put(CLEAREST, regex_clearest);

		Option topicsOption = OptionBuilder.hasArgs()
    			.withDescription("Specify a list of topic to be searched")
    			.create(TOPICS);
    	
		Options options = new Options();
		options.addOption(topicsOption);
		options.addOption(FOLDER, true, "Specify the folder to store images");
		options.addOption(SHARPNESS, true, "Specify the sharpness of crawled images. Can be 'thumbnail', 'clearer', 'pefect' and 'clearest'");
		
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "Crawler 4 Pexels", options );

    	CommandLineParser parser = new PosixParser();
    	CommandLine cli = parser.parse(options, args);
    	
    	String topics[] = null;
    	String folder = null;
    	String sharpness = null;
    	topics = cli.getOptionValues(TOPICS);
    	folder = cli.getOptionValue(FOLDER);
    	if(topics == null){
    		print("No topics specified. Pleasee specify at least one topic.");
    		return;
    	}
    	if(folder == null){
    		print("No folder specified. Default folder to user.home will be used.");
    		folder = defaultStorageFolder; 
    	}
    	if(sharpness == null){
    		print("No sharpness specified. Clearest images will be downloaded by default.");
    		sharpness = CLEAREST;
    	}
    	String regex = sharpnessMap.get(sharpness) ;
    	if(regex == null){
    		regex = sharpnessMap.get(CLEAREST);
    	}
    		
    	crawl(folder, topics, regex);
    }
    
    public static void print(String info){
    	System.out.println(info);
    }
    
    /**
     * Crawl images based on provided topic list and 
     * @param storageFolder
     * @param topics
     * @param regex
     * @throws Exception
     */
    public static void crawl(String storageFolder, String[] topics, String regex) throws Exception{
		print("Hi, this is Crawler whose name is Eugene");
		String not_found = "Sorry, no pictures found!";
		
		for(String topic: topics){
			File file = new File(storageFolder + "/" + topic);
			if(!file.exists()){
				file.mkdirs();
			}
			HttpClient client = HttpClientBuilder.create().build();
			int page_no = 0;
			outer: while(true){
				page_no++;
				print("" + page_no);
				String url = "https://www.pexels.com/search/" + topic + "/" + "?page=" + page_no;
				HttpGet request = new HttpGet(url);
				request.addHeader("User-Agent", "Mozilla/5.0");
				HttpResponse response = client.execute(request);
				print("Status code: " + response.getStatusLine().getStatusCode());
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));

				StringBuffer result = new StringBuffer();
				String line = "";
				Pattern p = Pattern.compile(regex);
				System.setProperty("http.agent", "Mozilla/5.0"); 
				while ((line = rd.readLine()) != null) {
					if(line.contains(not_found)){
						break outer;
					}
					Matcher matcher = p.matcher(line);
					if(matcher.matches()){
						print(matcher.group(1));
						BufferedImage image = ImageIO.read(new URL(matcher.group(1)));
						// get a unique name for storing this image
						String extension = matcher.group(2);
						String hashedName = UUID.randomUUID() + "." + extension;

						// store image
						String filename = file.getAbsolutePath() + "/" + hashedName;
						ImageIO.write(image, extension, new File(filename));
					}
					result.append(line + "\n");
				}
				request.releaseConnection();
			}
		}
    }
    
    /**
     * For text usage
     * @throws Exception
     */
    public static void printPage() throws Exception{
    	String url = "https://www.pexels.com/search/beauty/?page=10000";
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", "Mozilla/5.0");
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line + "\n");
		}
		print(result.toString());
    }
}
