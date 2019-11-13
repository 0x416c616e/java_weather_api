//weather api demo
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        //setting up the url for making the api call
        String firstStringPart = "https://api.openweathermap.org/data/2.5/weather?q=";
        String location = "Chicago";
        String secondPart = "&appid=";
        String units = "&units=imperial"; //replace with metric if you want
        String apiKey = "";
        boolean failed = false;
        boolean fileDownloadCompleted = false;
        //getting api key from key.txt file
        //be sure to use .gitignore for the key
        //so it's not made public
        try {
            Scanner keyIn = new Scanner(new File("C:\\Users\\Alan\\IdeaProjects\\weatherApi\\src\\key.txt"));
            apiKey = keyIn.nextLine();
        } catch (FileNotFoundException ex) {
            System.out.print("oops");
            ex.getMessage();
            failed = true;
        }
        //proceeds if no file IO issues
        if (!failed) {
            String fullUrl = firstStringPart + location + secondPart + apiKey + units;
            //System.out.println(fullUrl);
            try {
                URL apiCall = new URL(fullUrl);
                try {
                    //note: this will always write over an existing weather.json file
                    //which is what I want
                    ReadableByteChannel myChannel = Channels.newChannel(apiCall.openStream());
                    FileOutputStream fout = new FileOutputStream("weather.json");
                    long max = Long.MAX_VALUE;
                    fout.getChannel().transferFrom(myChannel, 0, max);
                    //the file is now downloaded and saved as weather.json
                    fout.close();
                    myChannel.close();
                    fileDownloadCompleted = true;
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } catch (MalformedURLException m) {
                System.out.println(m.getMessage());
            }
        }
        //finished with file download stuff
        //now need to parse the JSON to show weather
        if (fileDownloadCompleted) {
            //System.out.println("File downloaded sucessfully. Proceeding...");
            try {
                //converting json file to a string
                Scanner scan = new Scanner(new File("weather.json"));
                String jsonString = "";
                while (scan.hasNextLine()) {
                    jsonString += scan.next();
                }
                //turning the string from the file to a JSON object
                JSONObject fileToObj = new JSONObject(jsonString);
                //System.out.println(fileToObj.toString());
                //getting the temperature
                JSONObject mainPart = fileToObj.getJSONObject("main");
                String tempString = mainPart.get("temp").toString();
                System.out.print("In " + location + ", it is currently ");
                System.out.print(tempString + " degrees. The weather is ");
                //getting the description
                //annoying due to JSON nesting
                JSONArray weatherArray = fileToObj.getJSONArray("weather");
                JSONObject arrayToObj = weatherArray.getJSONObject(0);
                String descString = arrayToObj.get("main").toString().toLowerCase();
                System.out.println(descString + ".");
            } catch (FileNotFoundException fnf) {
                fnf.printStackTrace();
            }
        }
    }
}
/* got some help from this source:
https://examples.javacodegeeks.com/core-java/nio/java-nio-download-file-url-example/
*/
