package hymas.motion.m6.classifier;

class Bearing {

    private double bearing;
    private double accuracy;
    private long timestamp;

    /**
     * Container pentru bearing-uri
     *
     * @param _bearing
     * @param _accuracy
     * @param _timestamp
     * @throws BearingException
     */
    public Bearing(double bearing, double accuracy, long timestamp) throws BearingException {
        if (bearing < 0 || bearing > 360) {
            throw new BearingException("INVALID_BEARING");
        }
        if (accuracy <= 0) {
            throw new BearingException("INVALID_ACCURACY");
        }
        if (timestamp <= 0) {
            throw new BearingException("INVALID_TIMESTAMP");
        }

        this.bearing = bearing;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
    }

    public double getBearing() {
        return bearing;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
