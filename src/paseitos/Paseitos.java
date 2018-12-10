
package paseitos;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;
import org.json.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.math.*;

public class Paseitos {

    public static void main(String[] args) throws MalformedURLException, IOException {
        String api_direction_key = "YOUR_KEY_HERE";
        String api_streetimage_key = "YOUR_KEY_HERE";
        
        String api_direction_url = generadorURLDirection(api_direction_key,"51.5426916, -0.1479199", "51.5284818, -0.1320276", "walking");
        
        URL url = new  URL(api_direction_url);
        System.out.println(url);
        
        String str;
        try (Scanner scan = new Scanner(url.openStream())) {
            str = new String();
            while (scan.hasNext())
                str += scan.nextLine();
        }
        
        JSONObject obj = new JSONObject(str);
        if (! obj.getString("status").equals("OK"))
            return;
        
        JSONObject legs = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
        JSONObject ini = legs.getJSONObject("start_location");
        Coordenada cord_ini = new Coordenada(ini.getDouble("lat"),ini.getDouble("lng"));
        JSONObject fin = legs.getJSONObject("end_location");
        Coordenada cord_fin = new Coordenada(fin.getDouble("lat"),fin.getDouble("lng"));
        
        JSONArray steps = legs.getJSONArray("steps");
        
        int nsteps = steps.length();
        ArrayList<Coordenada> listadepuntos = new ArrayList<>();
        for(int i=0; i<steps.length();i++){
            String a = steps.getJSONObject(i).getJSONObject("polyline").getString("points");
            System.out.println(a);
            listadepuntos.addAll(decodePoints(a));
        }
        
        for(int j=0;j<listadepuntos.size()-1;j++){
            Coordenada A = listadepuntos.get(j);
            Coordenada B = listadepuntos.get(j+1);
            //longitud=ejeX latitud=ejeY
            Double x1 = A.lng;
            Double y1 = A.lat;
            Double x2 = B.lng;
            Double y2 = B.lat;
            System.out.println(x1);
            System.out.println(y1);
            System.out.println(x2);
            System.out.println(y2);
            
            Double angulo = 0.0;
            
            Double y = Math.sin(x2-x1)* Math.cos(y2);
            Double x = Math.cos(y1)*Math.sin(y2) - Math.sin(y1)*Math.cos(y2)*Math.cos(x2-x1);
            angulo = Math.toDegrees(Math.atan2(y, x));
            angulo = (angulo+360)%360;
            
            if(Double.isNaN(angulo)){
                angulo = 0.0;
            };
            
            if (angulo != 0.0){ //si el angulo es 0 no hacer nada
                String strurl = generadorURLStreetImage(api_streetimage_key,"640x440", listadepuntos.get(j).getLatitud().toString(), listadepuntos.get(j).getLongitud().toString(),"90",Integer.toString(angulo.intValue()),"0");
                URL url_streetimage = new URL(strurl);
            
                System.out.println(url_streetimage);
          
                String filename = "C:\\Your\\Path\\Here\\src\\Imagenes\\img" + j + ".jpg" ;
           
                saveImage(url_streetimage.toString(), filename);
            }
        }

    }
    
    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
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
}
        
    private static String generadorURLDirection(String api_key, String origin,String destination,String mode) throws UnsupportedEncodingException{
            String api_direction_url = "https://maps.googleapis.com/maps/api/directions/json";
            api_direction_url = api_direction_url + "?origin=" + URLEncoder.encode(origin, "UTF-8");
            api_direction_url = api_direction_url + "&destination=" + URLEncoder.encode(destination, "UTF-8");
            api_direction_url = api_direction_url + "&mode=" + URLEncoder.encode(mode, "UTF-8");
            api_direction_url = api_direction_url + "&key=" + URLEncoder.encode(api_key, "UTF-8");
        return api_direction_url;
    }   
    
    private static String generadorURLStreetImage(String api_key, String size, String location_lat, String location_lng,String fov,String heading, String pitch) throws UnsupportedEncodingException{
            String api_streetview_url = "https://maps.googleapis.com/maps/api/streetview";
            api_streetview_url = api_streetview_url + "?size=" + URLEncoder.encode(size, "UTF-8");
            api_streetview_url = api_streetview_url + "&location=" + URLEncoder.encode(location_lat, "UTF-8") + "," + URLEncoder.encode(location_lng, "UTF-8");
            api_streetview_url = api_streetview_url + "&fov=" + URLEncoder.encode(fov, "UTF-8");
            api_streetview_url = api_streetview_url + "&heading=" + URLEncoder.encode(heading, "UTF-8");
            api_streetview_url = api_streetview_url + "&pitch=" + URLEncoder.encode(pitch, "UTF-8");
            api_streetview_url = api_streetview_url + "&key=" + URLEncoder.encode(api_key, "UTF-8");
        return api_streetview_url;
    }
    
    public static List <Coordenada> decodePoints(String encoded_points){
    int index = 0;
    int lat = 0;
    int lng = 0;
    List <Coordenada> out = new ArrayList<>();

    try {
        int shift;
        int result;
        while (index < encoded_points.length()) {
            shift = 0;
            result = 0;
            while (true) {
                int b = encoded_points.charAt(index++) - '?';
                result |= ((b & 31) << shift);
                shift += 5;
                if (b < 32)
                    break;
            }
            lat += ((result & 1) != 0 ? ~(result >> 1) : result >> 1);

            shift = 0;
            result = 0;
            while (true) {
                int b = encoded_points.charAt(index++) - '?';
                result |= ((b & 31) << shift);
                shift += 5;
                if (b < 32)
                    break;
            }
            lng += ((result & 1) != 0 ? ~(result >> 1) : result >> 1);
            /* Add the new Lat/Lng to the Array. */
            out.add(new Coordenada(lat*1.0/100000,lng*1.0/100000));
        }
        return out;
    }catch(Exception e) {
    }
    return out;
    }

}