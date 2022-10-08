package com.misawabus.project.heartRate.Utils;

import org.apache.commons.math3.util.MathArrays;

public class Convolution {
    private double[] signal;
    private double[] kernel;
    private double[] output;

    /**
     * This constructor initialises the prerequisites required to perform convolution.
     * @param signal Signal to be convolved
     * @param window Kernel for convolution
     */
    public Convolution(double[] signal, double[] window) {
        this.signal = signal;
        this.kernel = window;
        this.output = null;
    }

    /**
     * This is the default discrete linear convolution procedure which works in full mode.
     * @return double[] The result of convolution.
     */
    public double[] convolve() {
        // Works in "full" mode
        this.output = MathArrays.convolve(this.signal, this.kernel);
        return this.output;
    }

    /**
     * This is the discrete linear convolution procedure which works in the specified mode.
     * @param mode Mode in which convolution will work. Can be 'full', 'same' or 'valid'
     * @throws java.lang.IllegalArgumentException if mode is not full, same or valid
     * @return double[] Result of convolution.
     */
    public double[] convolve(String mode) {
        double[] temp = MathArrays.convolve(this.signal, this.kernel);
        if (mode.equals("full")) {
            this.output = temp;
        }
        else if (mode.equals("same")) {
            this.output = new double[this.signal.length];
            int iterator = Math.abs(temp.length - this.signal.length)/2;
            for (int i=0; i<this.output.length; i++) {
                this.output[i] = temp[iterator];
                iterator++;
            }
        }
        else if (mode.equals("valid")) {
            this.output = new double[this.signal.length - this.kernel.length + 1];
            int iterator = this.kernel.length-1;;
            for (int i=0; i<this.output.length; i++) {
                this.output[i] = temp[iterator];
                iterator++;
            }
        }
        else {
            throw new IllegalArgumentException("convolve modes can only be full, same or valid");
        }
        return this.output;
    }

    private double[] convolve(double[] sig, double[] w) {
        // Works in "full" mode
        double[] output;
        output = MathArrays.convolve(sig,  w);
        return output;
    }

    /**
     * This method perform convolution using padding in different modes.
     * @param mode Mode in which convolution will work. Can be 'reflect', 'constant' or 'nearest', 'mirror' or 'wrap'
     * @throws java.lang.IllegalArgumentException if kernel size is greater than or equal to signal length
     * @return double[] Result of convolution with same length as input signal
     */
    public double[] convolve1d(String mode) {

        double[] output;
        double[] temp;

        if (mode.equals("reflect") || mode.equals("constant") || mode.equals("nearest") ||
                mode.equals("mirror") || mode.equals("wrap")) {
            int startVal = this.signal.length + this.kernel.length/2;
            double[] newSignal = UtilMethods.padSignal(this.signal, mode);
            temp = this.convolve(newSignal, this.kernel);
            output = UtilMethods.splitByIndex(temp, startVal, startVal+this.signal.length);
        }
        else  {
            throw new IllegalArgumentException("convolve1d modes can only be reflect, constant, nearest mirror, " +
                    "or wrap");
        }
        return output;
    }

    /**
     * This method perform default convolution using padding in 'reflect' modes.
     * @return double[] Result of convolution with same length as input signal
     */
    public double[] convolve1d() throws IllegalArgumentException {
        // Works in "reflect" mode
        double[] output;
        double[] temp;

        int startVal = this.signal.length + this.kernel.length/2;
        double[] newSignal = UtilMethods.padSignal(this.signal, "reflect");
        temp = this.convolve(newSignal, this.kernel);
        output = UtilMethods.splitByIndex(temp, startVal, startVal+this.signal.length);
        return output;
    }
}
