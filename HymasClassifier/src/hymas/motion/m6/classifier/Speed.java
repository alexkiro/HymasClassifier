package hymas.motion.m6.classifier;

/**
 *
 * @author Chirila Alexandru
 */
class Speed {

    public Speed(){
        
    }
    
    public Speed(double speed, double accuracy, long time) {
        this.speed = speed;
        this.accuracy = accuracy;
        this.time = time;
    }
    
    double speed;
    double accuracy;
    long time;
}
