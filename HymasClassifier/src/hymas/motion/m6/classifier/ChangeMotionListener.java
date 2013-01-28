package hymas.motion.m6.classifier;

/**
 * Listner ce va fi apelat cand se schimba situatia de miscare trebuie inregistrat
 * in lista clasei MotionClassifier
 * @author Chirila Alexandru
 */
public interface ChangeMotionListener {
    /**
     * Va fi apelat cand se schimba situatia de miscare. Atentie! Pentru prima
     * situatie de miscare dedusa oldMotion va fi null.
     * @param oldMotion
     * @param newMotion 
     */
    void onChangeMotion(Label oldMotion, Label newMotion);
}
