package org.samcrow.differentialgps;

class FixStatus {

    /**
     * A fix status object that represents no fix
     */
    public static final FixStatus NO_FIX = new FixStatus(Float.NaN);

    /**
     * Horizontal dilution of precision (lower is better)
     *
     * Implementation detail: NaN represents no connection
     */
    private final float mHdp;

    private FixStatus(float hdp) {
        mHdp = hdp;
    }

    public static FixStatus withHdp(float hdp) {
        if (Float.isNaN(hdp)) {
            throw new IllegalArgumentException("hdp must not be NaN");
        }
        return new FixStatus(hdp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FixStatus fixStatus = (FixStatus) o;

        return Float.compare(fixStatus.mHdp, mHdp) == 0;
    }

    @Override
    public int hashCode() {
        return (mHdp != +0.0f ? Float.floatToIntBits(mHdp) : 0);
    }

    @Override
    public String toString() {
        if (Float.isNaN(mHdp)) {
            return "No fix";
        } else {
            return "Fix with HDP " + mHdp;
        }
    }

    public float getHdp() {
        return mHdp;
    }
}
