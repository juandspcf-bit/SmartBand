package com.misawabus.project.heartRate.Utils;

public class CrossCorrelation {

    private double[] signal;
    private double[] kernel;
    private double[] output;
    private boolean autocorr;

    /**
     * This constructor initialises the prerequisites required to perform cross-correlation.
     * @param signal Signal to be convolved
     * @param window Kernel for convolution
     */
    public CrossCorrelation(double[] signal, double[] window) {
        this.signal = signal;
        this.kernel = window;
        this.autocorr = false;
    }

    /**
     * This constructor initialises the prerequisites required to perform autocorrelation
     * @param s Signal to be convolved
     */
    public CrossCorrelation(double[] s) {
        this.signal = s;
        this.kernel = s;
        this.autocorr = true;
    }

    /**
     * This is the default cross-correlation procedure which works in "valid" mode.
     * @return double[] The result of correlation.
     */
    public double[] crossCorrelate() {
        //Works in "valid" mode
        String mode = "valid";
        if (autocorr) {
            mode = "full";
        }
        this.kernel = UtilMethods.reverse(this.kernel);
        Convolution c1 = new Convolution(this.signal, this.kernel);
        this.output = c1.convolve(mode);
        return this.output;
    }

    /**
     * This is the discrete linear convolution procedure which works in the specified mode.
     * @param mode Mode in which correlation will work. Can be 'full', 'same' or 'valid'
     * @return double[] Result of cross-correlation.
     */
    public double[] crossCorrelate(String mode) {
        this.kernel = UtilMethods.reverse(this.kernel);
        Convolution c1 = new Convolution(this.signal, this.kernel);
        this.output = c1.convolve(mode);
        return this.output;
    }
}

