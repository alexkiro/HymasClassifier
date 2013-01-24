package hymas.motion.m6.clasifier;

class Bearing {

    private int bearing;
    private int accuracy;
    private long timestamp;

    /**
     * Container pentru bearing-uri
     *
     * @param _bearing
     * @param _accuracy
     * @param _timestamp
     * @throws BearingException
     */
    public Bearing(int bearing, int accuracy, long timestamp) throws BearingException {
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

    public int getBearing() {
        return bearing;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
