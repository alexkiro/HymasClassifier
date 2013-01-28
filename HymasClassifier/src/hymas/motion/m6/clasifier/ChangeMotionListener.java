package hymas.motion.m6.clasifier;

/**
 * Listner ce va fi apelat cand se schimba situatia de miscare trebuie inregistrat
 * in lista clasei MotionClassifier
 * @author Chirila Alexandru
 */
public interface ChangeMotionListener {
    void onChangeMotion(Label oldMotion, Label newMotion);
}
