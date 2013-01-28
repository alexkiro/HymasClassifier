package hymas.motion.m6.classifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

abstract class ReverseGeocoding {

    /**
     * Folosim serviciul oferite de Nominatim pentru a afla daca coordonatele
     * pasate ca parametrii se afla in interiorul unui oras. Datele sunt primite
     * de la serviciu in xml, nu sunt stocate local ci procesate prin
     * stream-uri.
     *
     *
     * @param latitude
     * @param longitude
     * @return True daca se afla in interiorul unui oras
     */
    public static boolean checkIfInCity(double latitude, double longitude) {

        boolean isInCity = false;
        String restEndPoint = "http://nominatim.openstreetmap.org/reverse?format=xml&lat="
                + latitude + "&lon=" + longitude + "&zoom=18&addressdetails=1";
        String line;
        InputStream inputStreamContainer;
        StringBuilder queryResult = new StringBuilder();

        try {

            URL url = new URL(restEndPoint);

            inputStreamContainer = url.openStream();
            URLConnection urlc = url.openConnection();
            urlc.setDoOutput(true);
            urlc.setAllowUserInteraction(false);
            try (BufferedReader queryStream = new BufferedReader(new InputStreamReader(inputStreamContainer))) {
                while ((line = queryStream.readLine()) != null) {
                    queryResult.append(line);
                    queryResult.append("\n");
                }

                inputStreamContainer = url.openStream();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                Document doc = builder.parse(inputStreamContainer);

                NodeList listOfCityTags = doc.getElementsByTagName("city");

                if (listOfCityTags.getLength() > 0) {
                    isInCity = true;
                } else {
                    return isInCity;
                }
            }

        } catch (IOException | ParserConfigurationException | SAXException ex) {
            Logger.getLogger(MotionClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isInCity;
    }
}
