/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hymas.motion.m6.clasifier;

/**
 *
 * @author Kiro
 */
class Speed {

    public Speed(){
        
    }
    
    public Speed(double speed, float accuracy, long time) {
        this.speed = speed;
        this.accuracy = accuracy;
        this.time = time;
    }
    
    double speed;
    float accuracy;
    long time;
}
