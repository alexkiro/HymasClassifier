package hymas.motion.m6.clasifier;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clasa singleton ce colecteaza si prelucreaza date, care, impreuna cu
 * clasificatorul generat de Weka (WekaClassifier). <br/> <br/> Deasemeni
 * permite atasarea unor ChangeMotionListener pentru a notifica alte aplicatii
 * de schimbare situatiei de miscare <br/><br/> Utilizare:
 * <pre> {@code
 * MotionClassifier.getInstance().registerChangeMotionListener(new ChangeMotionListener() {
 *
 * @Override public void onChangeMotion(Label oldMotion, Label newMotion) {
 * //foloseste newMotion } });
 * MotionClassifier.getInstance().startClassifying(); //pornire clasificator
 *
 *     //... //adaugare de date colectate de la senzori
 * MotionClassifier.getInstance().addGpsData(47.17410181649029,
 * 27.575028548017144, 5.0, 62.0, 1359032216000l); // ...
 * MotionClassifier.getInstance().stopClassifying(); }</pre>
 * @author Chirila Alexandru
 */
public class MotionClassifier {

    // parametri fixati
    private final int T = 180; //nr de secunde
    private final double acuracyThreshold = 30.0;
    private final double bearingThreshold = 15.0;
    private final double tolerance = 0.833333; //3kph
    private LinkedList<Bearing> bearings = new LinkedList<>();
    private LinkedList<Speed> speeds = new LinkedList<>();
    private LinkedList<Boolean> revGeos = new LinkedList<>();
    private double[] lastGpsData = null;
    private long lastGpsTime = 0;
    private Label lastKnownMotion = null;
    private long startTime;
    private long endTime;
    //scheduling
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> handle;
    private List<ChangeMotionListener> onChangeEvents = new LinkedList<>();
    final Runnable classify = new Runnable() {
        @Override
        public void run() {
            synchronized (MotionClassifier.getInstance()) {
                endTime = System.currentTimeMillis();
                try {
                    List<Double> speedLst = MotionClassifiersUtils.toList(speeds);
                    Label newMotion =
                            WekaClassifier.classify(
                            MotionClassifiersUtils.computeAverage(speeds, acuracyThreshold),
                            MotionClassifiersUtils.computeFluctuations(speedLst),
                            MotionClassifiersUtils.computeFrequency(
                            MotionClassifiersUtils.computeDirectionChanges(bearingThreshold, acuracyThreshold, bearings),
                            startTime, endTime),
                            MotionClassifiersUtils.computeFrequency(
                            MotionClassifiersUtils.computeStops(speeds, tolerance),
                            startTime, endTime),
                            MotionClassifiersUtils.computeAverage(revGeos));
                    if (newMotion != lastKnownMotion) {
                        notifyChangeEvents(lastKnownMotion, newMotion);
                        lastKnownMotion = newMotion;
                    }

                } catch (Exception ex) {
                    Logger.getLogger(MotionClassifier.class.getName()).log(Level.SEVERE, null, ex);
                }
                startTime = endTime;
                speeds.clear();
                bearings.clear();
                revGeos.clear();
            }
        }
    };

    private MotionClassifier() {
    }

    public static MotionClassifier getInstance() {
        return ClassifierHolder.INSTANCE;
    }

    private void notifyChangeEvents(final Label oldMotion, final Label newMotion) {
        for (ChangeMotionListener changeMotionListener : onChangeEvents) {
            changeMotionListener.onChangeMotion(oldMotion, newMotion);
        }
    }

    /**
     * Incepe procesul de clasificare la intervale regulate a situatiei de
     * miscare
     */
    public void startClassifying() {
        startTime = System.currentTimeMillis();
        handle = scheduler.scheduleWithFixedDelay(classify, T, T, TimeUnit.SECONDS);
    }

    /**
     * Opreste procesul de clasificare la intervale regulate a situatiei de
     * miscare
     */
    public void stopClassifying() {
        handle.cancel(false);
    }

    /**
     * Adauga date primite de la gps la listele de date ce vor fi analizate
     *
     * @param lat latitudine
     * @param lng longitudine
     * @param acu accuracy
     * @param bea bearing
     * @param timestamp timpul milisecunde Unix standard epoch time
     */
    public void addGpsData(final double lat, final double lng, final double acu, final double bea, final long timestamp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (MotionClassifier.getInstance()) {
                    try {
                        if (lastGpsData == null) {
                            lastGpsData = new double[]{lat, lng};
                            lastGpsTime = timestamp;
                        } else {
                            double speed = MotionClassifiersUtils.computeSpeed(
                                    lat, lng, timestamp, lastGpsData[0], lastGpsData[1], lastGpsTime);
                            speeds.add(new Speed(speed, acu, timestamp));
                            lastGpsData[0] = lat;
                            lastGpsData[1] = lng;
                            lastGpsTime = timestamp;
                        }
                        bearings.add(new Bearing(bea, acu, timestamp));
                        revGeos.add(ReverseGeocoding.checkIfInCity(lat, lng));
                    } catch (BearingException ex) {
                        Logger.getLogger(MotionClassifier.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();

    }

    /**
     * Intoarce rezultatul ultimei clasificari
     *
     * @return
     */
    public Label getLastKnownMotion() {
        return lastKnownMotion;
    }

    /**
     * Inregistreaza un listner ce va fi apelat atunci cand se schimba situatia
     * de miscare
     *
     * @param cml
     */
    public void registerChangeMotionListener(ChangeMotionListener cml) {
        onChangeEvents.add(cml);
    }

    /**
     * Sterge un listener din lista ce va fi apelata atunci cand se schimba
     * situatia de miscare
     *
     * @param cml
     */
    public void unregisterChangeMotionListenter(ChangeMotionListener cml) {
        onChangeEvents.remove(cml);
    }

    private static class ClassifierHolder {

        private static final MotionClassifier INSTANCE = new MotionClassifier();
    }
}
