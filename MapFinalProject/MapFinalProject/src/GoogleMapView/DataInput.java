package GoogleMapView;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Ver 1.0 Author : Piyali Mukherjee pm2678@columbia.edu Date : May second
 * fortnight, 2014 Project : To write a prototype for Prof. Feniosky Pena-Mora
 * and Prof. Charles Shen to assist them in integrating a BIM object on to a
 * Google Map object. Description of this module : This module is the main()
 * module. It comprises of definition of a Xbim object that is actually an
 * extended Xbim object. We augment the standard Xbim object that is represented
 * by an IFC type file, whose structure is dynamic depending on what object does
 * the underlying Xbim object represents - is it a complete building,m or a
 * wall, or any specific sub-component. A very large number of IFC file
 * structures have been defined and these definitions have been made consistent
 * with requirements of the AutoCAD and other such tools.
 *
 * The module also receives from user the longitude/latitude and reference data,
 * so that the given object can be placed on the Google Map.
 *
 * Version Control : any changes to the following must be discussed and
 * explained to the Prof. Feniosky Pena-Mora (feniosky@columbia.edu) and Prof.
 * Charles Shen (charles@cs.columbia.edu), and their concurrence taken.
 *
 * Compilation Instructions : The whole project is a 64 bit project, so it
 * requires that the JDK installed is 64 bit version, and the running
 * environment is a 64 bit Windows.
 */
public class DataInput {

    static double center_lat = 0.0;    //To store the latitude of the map centralizing coordinate
    static double center_long = 0.0;   //To store the longitude of the map centralizing coordinate
    public static ArrayList<BIMObject> array_of_BIM = new ArrayList<BIMObject>();           //Defines the array of the BIM Objects that will be placed on the map.

    /**
     * This module is an HTML file generator. This produces a text file in html
     * format containing necessary javascript lines to create a Google Maps API
     * v3.0 compliant html file.
     *
     * @throws FileNotFoundException
     */
    public static void createHTMLFile() throws FileNotFoundException {
        String html_file_name = "temp.html";
        try ( //To store the current file name (includes the file path) for HTML rendering
                PrintWriter writer = new PrintWriter(html_file_name); //To create the HTML file
                ) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("\t<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>");
            writer.println("\t<style type=\"text/css\">");
            writer.println("\t\thtml { height: 100% }");
            writer.println("\t\tbody { height: 100%; margin: 0; padding: 0 }");
            writer.println("\t\t#map-canvas { height: 100%}");
            writer.println("\t</style>");
            writer.println("\t<title>Google Map View</title>");
            writer.println("\t<script type=\"text/javascript\" src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyDsMRv7WekHwFOUztNBkSA6Rbaz5LrUCnc&amp;sensor=false\">");
            writer.println("\t</script>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("<script type=\"text/javascript\">");
//Now we write the Javascript objects. This will require repeated iterations on array_of_BIMobjects
            writer.print("var displayStrings = [");
            for (int i = 0; i < array_of_BIM.size(); i++) { //Write displayString array
                BIMObject d = array_of_BIM.get(i);
                writer.println("'<div id=\"content\">'+");
                writer.println("'<div id=\"siteNotice\">'+");
                writer.println("'</div>'+");
                writer.println("'<p style=\"font-size:16px\"><h1 id=\"firstHeading\" class=\"firstHeading\">" + d.title + "</h></p>'+");
                writer.println("'<div id=\"bodyContent\">'+");
                writer.println("'<p style=\"font-size:12px\">" + d.info + "</p>'+");
                //Since the strings IFC File Header, etc contain many unacceptable characters, we move these characters to another StringBuilder object
                //StringBuilder sb_hfn = new StringBuilder("IFC Header File Name: ");
                StringBuilder sb_hfn = new StringBuilder("<p style=\"font-size:10px\">IFC Header File Name: ");
                String s_hfn = d.header_file_name;      //We need to replcae all single quote marks with a double quote marks to enable display.
                for (int j = 0; j < s_hfn.length(); j++){
                    char c = s_hfn.charAt(j);
                    if (c == 39) c = 34;    //The c0ompilers tend to behave irrationally in presence of single quotes, so we replace through a brute force algorithm
                    sb_hfn.append(c);
                }
                writer.println("'"+sb_hfn+ "</p>'+");

                StringBuilder sb_hfd = new StringBuilder("<p style=\"font-size:10px\">IFC Header File Descriptor: ");
                String s_hfd = d.header_file_desc;      //We need to replcae all single quote marks with a double quote marks to enable display.
                for (int j = 0; j < s_hfd.length(); j++){
                    char c = s_hfd.charAt(j);
                    if (c == 39) c = 34;    //The c0ompilers tend to behave irrationally in presence of single quotes, so we replace through a brute force algorithm
                    sb_hfd.append(c);
                }
                writer.println("'"+sb_hfd+ "</p>'+");

                StringBuilder sb_hfs = new StringBuilder("<p style=\"font-size:10px\">IFC Header File Schema: ");
                String s_hfs = d.header_file_schema;      //We need to replcae all single quote marks with a double quote marks to enable display.
                for (int j = 0; j < s_hfs.length(); j++){
                    char c = s_hfs.charAt(j);
                    if (c == 39) c = 34;    //The c0ompilers tend to behave irrationally in presence of single quotes, so we replace through a brute force algorithm
                    sb_hfs.append(c);
                }
                writer.println("'"+sb_hfs+ "</p>'+");
                
                writer.println("'<p style=\"font-size:10px\"><a href=\"" + d.additional_hyperlink + "\"><style=font-size:5px>'+");
                writer.println("'" + d.additional_hyperlink + "</a></p>'+");
                writer.println("'</div>'+");
                writer.print("'</div>'");
                writer.println((i == array_of_BIM.size() - 1) ? "];" : ",");
            }
            StringBuilder sb_loc = new StringBuilder("var locations = [");
            for (int i = 0; i < array_of_BIM.size(); i++) { //Write displayString array
                BIMObject d = array_of_BIM.get(i);
                sb_loc.append("[");
                sb_loc.append(d.longitude);
                sb_loc.append(",");
                sb_loc.append(d.latitude);
                sb_loc.append(",");
                sb_loc.append(i + 1);
                sb_loc.append("]");
                sb_loc.append((i == array_of_BIM.size() - 1) ? "];" : ",");
            }
            writer.println(sb_loc.toString());
            StringBuilder sb_icons = new StringBuilder("var imageIcons = [");
            for (int i = 0; i < array_of_BIM.size(); i++) { //Write displayString array
                BIMObject d = array_of_BIM.get(i);
                sb_icons.append("'building.png'");
                sb_icons.append((i == array_of_BIM.size() - 1) ? "];" : ",");
            }
            writer.println(sb_icons.toString());
            writer.println("var info = new google.maps.InfoWindow();");
            StringBuilder sb_titles = new StringBuilder("var titles = [");
            for (int i = 0; i < array_of_BIM.size(); i++) { //Write displayString array
                BIMObject d = array_of_BIM.get(i);
                sb_titles.append("'");
                sb_titles.append(d.title);
                sb_titles.append("'");
                sb_titles.append((i == array_of_BIM.size() - 1) ? "];" : ",");
            }
            writer.println(sb_titles.toString());
            writer.println("function setMarkers(map,locations,imageIcons,titles) {");
            writer.println("\tfor (i = 0; i < locations.length; i++) {");
            writer.println("\t\tmarker = new google.maps.Marker({ ");
            writer.println("\t\t\tposition: new google.maps.LatLng(locations[i][0], locations[i][1]),");
            writer.println("\t\t\ticon: imageIcons[i],");
            writer.println("\t\t\tmap: map,");
            writer.println("\t\t\ttitle: titles[i],");
            writer.println("\t\t\tzindex: locations[i][2]");
            writer.println("\t\t});");
            writer.println("\t\tgoogle.maps.event.addListener(marker, 'click', (function(marker, i) {");
            writer.println("\t\t\treturn function() {");
            writer.println("\t\t\t\tinfo.setContent(displayStrings[i]);");
            writer.println("\t\t\t\tinfo.open(map, marker);");
            writer.println("\t\t\t};");
            writer.println("\t\t}) (marker,i));");
            writer.println("\t} //End of for loop");
            writer.println("} //End of setMarkers function");
            writer.println("function initialize() {");
            writer.println("\tvar centreLatlng = new google.maps.LatLng(" + center_long + "," + center_lat + ");");
            writer.println("\tvar mapOptions = {");
            writer.println("\t\tzoom: 12,");
            writer.println("\t\tcenter: centreLatlng");
            writer.println("\t};");
            writer.println("\tvar map = new google.maps.Map(document.getElementById('map-canvas'),mapOptions); ");
            writer.println("\tsetMarkers(map,locations,imageIcons,titles);");
            writer.println("} //End of initialize() function");
            writer.println("google.maps.event.addDomListener(window, 'load', initialize);");
            writer.println("</script>");
            writer.println("\t<div id=\"map-canvas\"></div>");
            writer.println("</body>");
            writer.println("</html>");
            writer.close();
        } //End of writing loop        
    } //End of createHTMLFile

    /**
     *
     */
    public static void GoogleMapSample() throws IOException, URISyntaxException {
        java.awt.Desktop.getDesktop().browse(new URI("temp.html"));
    }

    /**
     *
     * @param args
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, URISyntaxException {

        int no_of_records;
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Welcome to Prototype Integration Program between Xbim object files and Google Maps");
        System.out.println("Do you want to load an existing file? [Y] to load an existing data file and append to it.... ");
        System.out.println("[N] to create a new data file...");
        String response = stdin.readLine().toUpperCase();
        if (response.equals("Y")) {
            System.out.println("Please enter the data filename...");
            String filename = stdin.readLine();
            File fn = new File(filename);
            try {
                Scanner in = new Scanner(fn);
                no_of_records = Integer.parseInt(in.nextLine());
                for (int i = 0; i < no_of_records; i++) {
                    BIMObject temp = new BIMObject();
                    // Read ther first line that contains the title of the record
                    temp.title = in.nextLine();
                    temp.longitude = Double.parseDouble(in.nextLine());
                    temp.latitude = Double.parseDouble(in.nextLine());
                    temp.file_name = in.nextLine();
                    temp.header_file_name = in.nextLine();
                    temp.header_file_desc = in.nextLine();
                    temp.header_file_schema = in.nextLine();
                    temp.info = in.nextLine();
                    temp.additional_hyperlink = in.nextLine();
                    array_of_BIM.add(temp);
                } //End of for
            } catch (FileNotFoundException e) {
                System.err.println("No such file ....");
                System.exit(-1);
            }
            System.out.println("Would you like to add additional records to this file? [Y/N]...");
            String ans_for_more = stdin.readLine().toUpperCase();
            if (ans_for_more.equals("Y")) {
                System.out.println("Please enter the number of records you wish to add.");
                int new_records = Integer.parseInt(stdin.readLine());
                int i = 0;
                do {
                    BIMObject temp = new BIMObject();
                    System.out.println("For rec[" + (i + 1) + "] Please enter the IFC file name ...");
                    temp.file_name = stdin.readLine();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(temp.file_name));
                        try {
                            String line = br.readLine();
                            while (!(line.equals("ENDSEC;"))) {
                                if (line.contains("FILE_DESCRIPTION")) {
                                    temp.header_file_desc = line.substring(line.lastIndexOf("FILE_DESCRIPTION"));
                                } else if (line.contains("FILE_NAME")) {
                                    temp.header_file_name = line.substring(line.lastIndexOf("FILE_NAME"));
                                } else if (line.contains("FILE_SCHEMA")) {
                                    temp.header_file_schema = line.substring(line.lastIndexOf("FILE_SCHEMA"));
                                }
                                line = br.readLine();
                            }
                        } finally {
                            br.close();
                        }
                    } catch (FileNotFoundException e) {
                        System.err.println("File : File Not found error for filer : " + temp.file_name);
                        System.err.println("Please retry ...");
                        continue;
                    }
                    System.out.println("For rec[" + (i + 1) + "] Please enter the title ...");
                    temp.title = stdin.readLine();
                    System.out.println("For rec[" + (i + 1) + "] Please enter the latitude ...");
                    temp.latitude = Double.parseDouble(stdin.readLine());
                    System.out.println("For rec[" + (i + 1) + "] Please enter the longitude ...");
                    temp.longitude = Double.parseDouble(stdin.readLine());
                    System.out.println("For rec[" + (i + 1) + "] Please enter one line of information ...");
                    temp.info = stdin.readLine();
                    System.out.println("For rec[" + (i + 1) + "] Please enter a complete hyperlink to gain more info  ...");
                    temp.additional_hyperlink = stdin.readLine();
                    array_of_BIM.add(temp);
                } while (++i < new_records);
                //Since we have added more records, we update the source file, and then we save and close it
                //As a matter of safe practice, we rename the file to old_<original_filename>.txt
                fn.delete();
                fn.createNewFile();
                try (PrintWriter pw = new PrintWriter(fn)) {
                    pw.println(array_of_BIM.size());                    //the first number is count of records in the file
                    for (int j = 0; j < array_of_BIM.size(); j++) {
                        BIMObject d = array_of_BIM.get(j);
                        pw.println(d.title);
                        pw.println(d.longitude);
                        pw.println(d.latitude);
                        pw.println(d.file_name);
                        pw.println(d.header_file_name);
                        pw.println(d.header_file_desc);
                        pw.println(d.header_file_schema);
                        pw.println(d.info);
                        pw.println(d.additional_hyperlink);
                        //System.out.println(d.title + " " + d.longitude + " " + d.latitude + " " + d.file_name + " " + d.info + " " + d.additional_hyperlink);
                    }
                } //finish saving the file
            } //end of ans_for_more == "Y"

        } //If response == "Y"
        else {
            System.out.println("Please enter the number of records you would like to place in the file...");
            int new_records = Integer.parseInt(stdin.readLine());
            int i = 0;
            do {
                BIMObject temp = new BIMObject();
                System.out.println("For rec[" + (i + 1) + "] Please enter the IFC file name ...");
                temp.file_name = stdin.readLine();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(temp.file_name));
                    try {
                        String line = br.readLine();
                        while (!(line.equals("ENDSEC;"))) {
                            if (line.contains("FILE_DESCRIPTION")) {
                                temp.header_file_desc = line.substring(line.lastIndexOf("FILE_DESCRIPTION"));
                            } else if (line.contains("FILE_NAME")) {
                                temp.header_file_name = line.substring(line.lastIndexOf("FILE_NAME"));
                            } else if (line.contains("FILE_SCHEMA")) {
                                temp.header_file_schema = line.substring(line.lastIndexOf("FILE_SCHEMA"));
                            }
                            line = br.readLine();
                        }
                    } finally {
                        br.close();
                    }
                } catch (FileNotFoundException e) {
                    System.err.println("File : File Not found error for filer : " + temp.file_name);
                    System.err.println("Please retry ...");
                    continue;
                }
                System.out.println("For rec[" + (i + 1) + "] Please enter the title ...");
                temp.title = stdin.readLine();
                System.out.println("For rec[" + (i + 1) + "] Please enter the latitude ...");
                temp.latitude = Double.parseDouble(stdin.readLine());
                System.out.println("For rec[" + (i + 1) + "] Please enter the longitude ...");
                temp.longitude = Double.parseDouble(stdin.readLine());
                System.out.println("For rec[" + (i + 1) + "] Please enter one line of information ...");
                temp.info = stdin.readLine();
                System.out.println("For rec[" + (i + 1) + "] Please enter a complete hyperlink to gain more info ...");
                temp.additional_hyperlink = stdin.readLine();
                array_of_BIM.add(temp);
            } while (++i < new_records);
            System.out.println("Please enter the file name to store this data...");
            String fn = stdin.readLine();
            try (PrintWriter pw = new PrintWriter(fn)) {
                pw.println(array_of_BIM.size());                    //the first number is count of records in the file
                for (int j = 0; j < array_of_BIM.size(); j++) {
                    BIMObject d = array_of_BIM.get(j);
                    pw.println(d.title);
                    pw.println(d.longitude);
                    pw.println(d.latitude);
                    pw.println(d.file_name);
                    pw.println(d.header_file_name);
                    pw.println(d.header_file_desc);
                    pw.println(d.header_file_schema);
                    pw.println(d.info);
                    pw.println(d.additional_hyperlink);
                    //System.out.println("Debug: " + d.title + " " + d.longitude + " " + d.latitude + " " + d.file_name + " " + d.info + " " + d.additional_hyperlink);
                }
            } //finish saving the file
        }//end of else response == "N"
        //Purely for debugging
//        for (int i = 0; i < array_of_BIM.size(); i++) {
//            BIMObject d = array_of_BIM.get(i);
//            System.out.println(d.title + " " + d.longitude + " " + d.latitude + " " + d.file_name + " " + d.info + " " + d.additional_hyperlink);
//        }
        //Compute the map center by computing the average of all the positioned objects
        center_lat = 0.0;
        for (int i = 0; i < array_of_BIM.size(); i++) {
            BIMObject d = array_of_BIM.get(i);
            center_lat += d.latitude;
            center_long += d.longitude;
        }
        center_lat /= array_of_BIM.size();
        center_long /= array_of_BIM.size();
        createHTMLFile();
        GoogleMapSample();
    } //End of main

    //DataInput.file_name  = (new File(".").getCanonicalPath().concat("\\"));
} //End of class Data Input

