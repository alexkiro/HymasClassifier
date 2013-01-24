package hymas.motion.m6.clasifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

abstract class MotionClassifiersUtils {

    /**
     * Computes stops with 0 tolerance
     *
     * @param speeds list of speed measurements
     * @return
     */
    public static List<Long> computeStops(List<Speed> speeds) {
        return computeStops(speeds, 0);
    }

    /**
     * Computes stops according to tolerance
     *
     * @param speeds list of speed measurements
     * @param tolerance tolerance
     * @return
     */
    public static List<Long> computeStops(List<Speed> speeds, double tolerance) {
        List<Long> stops = new LinkedList<>();
        for (Speed speed : speeds) {
            if (speed.speed <= tolerance) {
                stops.add(speed.time);
            }
        }
        return stops;
    }

    /**
     * Metda care calculeaza frecventa intamplarii unui eveniment
     *
     * @param data lista de momente Unix Epoch Time
     * @param startTime inceputul intervalului
     * @param endTime sfarsitul intervalului
     * @return frecventa pe minut a intamplarii
     */
    public static float computeFrequency(List<Long> data, long startTime, long endTime) {
        float time = (endTime - startTime) / 60000.0f;
        return data.size() / time;
    }

    /**
     * Metoda care decide daca diferenta dintre doua bearing-uri este suficient
     * de mare pentru a insemna o schimbare de directie.
     *
     * @param _bearingThreshold
     * @param _accuracyThreshold
     * @param _previous
     * @param _current
     * @return
     * @throws BearingException
     */
    private static Bearing compareBearings(int _bearingThreshold, int _accuracyThreshold, Bearing _previous, Bearing _current) throws BearingException {
        if (_bearingThreshold < 0 || _bearingThreshold > 360) {
            throw new BearingException("INVALID_BEARING_THRESHOLD");
        }
        if (_accuracyThreshold <= 0) {
            throw new BearingException("INVALID_ACCURACY_THRESHOLD");
        }
        if (_previous == null) {
            throw new BearingException("INVALID_PREVIOUS_BEARING");
        }
        if (_current == null) {
            throw new BearingException("INVALID_CURRENT_BEARING");
        }

        if (_previous.getAccuracy() <= _accuracyThreshold && _current.getAccuracy() <= _accuracyThreshold) {
            if (_previous.getBearing() <= _bearingThreshold && _current.getBearing() >= (360 - _bearingThreshold)) {
                if ((_previous.getBearing() + (360 - _current.getBearing())) >= _bearingThreshold) {
                    return _current;
                } else {
                    return null;
                }
            } else if (_previous.getBearing() >= (360 - _bearingThreshold) && _current.getBearing() <= _bearingThreshold) {
                if (((360 - _previous.getBearing()) + _current.getBearing()) >= _bearingThreshold) {
                    return _current;
                } else {
                    return null;
                }
            } else if (Math.abs(_previous.getBearing() - _current.getBearing()) >= _bearingThreshold) {
                return _current;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Metoda principala, care filtreaza dintr-o lista de bearing-uri pe acelea
     * care inseamna schimbari de directie (conform threshold-urilor
     * specificate).
     *
     * @param _bearingThreshold filtru in grade
     * @param _accuracyThreshold
     * @param _data_list
     * @return
     * @throws BearingException
     */
    public static List<Long> computeDirectionChanges(int _bearingThreshold, int _accuracyThreshold, List<Bearing> _data_list) throws BearingException {
        if (_bearingThreshold < 0 || _bearingThreshold > 360) {
            throw new BearingException("INVALID_BEARING_THRESHOLD");
        }
        if (_accuracyThreshold <= 0) {
            throw new BearingException("INVALID_ACCURACY_THRESHOLD");
        }
        if (_data_list == null) {
            throw new BearingException("INVALID_DATA_LIST");
        }

        List<Long> filteredBearings = new LinkedList<>();

        if (_data_list.size() < 2) {
            filteredBearings.add(_data_list.get(0).getTimestamp());
        } else {
            for (int i = 1; i < _data_list.size(); i++) {
                if (compareBearings(_bearingThreshold, _accuracyThreshold, _data_list.get(i - 1), _data_list.get(i)) != null) {
                    filteredBearings.add(_data_list.get(i).getTimestamp());
                }
            }
        }
        return filteredBearings;
    }

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
        InputStream inputStreamContainer = null;
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

        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return isInCity;
    }

    /**
     * Metoda care calculeaza viteza cu care se deplaseaza intre 2 coordonate
     * date(Lat+long) intr-un interval de timp [time_1,time_2]. Calculam
     * distanta dintre cele doua coordonate folosind formula " Great-circle
     * distance" viteza = distanta transformata in m/diferenta dintre cei 2
     * timpi primiti ca parametru
     */
    public static double computeSpeed(double lat_1, double long_1, long time_1, double lat_2, double long_2, long time_2) {
        double speed; // speed->m/s
        double distanceKm;
        double distanceM;
        long time;
        double r = 6378.137;

        /* we have decided, that the acutal shape of the earth can be neglected ( "ellipsoid") and therefore use formulas based on the mathematical solutions for spheres.
         e = ARCCOS[ SIN(LAT1)*SIN(LAT2) + COS(LAT1)*COS(LAT2)*COS(LONG2-LONG1) ]
         e=e/180*pi
         Distance = e * r = e * 6378.137 km */

        double e = (double) Math.acos(Math.sin(lat_1) * Math.sin(lat_2) + Math.cos(lat_1) * Math.cos(lat_2) * Math.cos(long_2 - long_1));
        e = e / 180 * (double) Math.PI;
        distanceKm = e * r;
        distanceM = distanceKm * 1000;
        time = (time_2 - time_1) / 1000;
        speed = distanceM / time;

        return speed;
    }

    /**
     * Calculeaza deviatia maxima de la media pentru datele primite
     *
     * @param data
     * @return deviatia maxima de la medie
     */
    public static float computeFluctuations(List<Float> data) {
        float sum = 0f;
        int index = 0;
        float media = 0f;
        float max = 0f;

        if (data.isEmpty()){
            return 0f;
        }
        
        for (Float i : data) {
            sum = sum + i;
        }
        
        media = sum / data.size();

        for (Float i : data) {
            Float difference = Math.abs(media - i);
            if (max < difference) {
                max = difference;
            }
        }
        return max;
    }
}