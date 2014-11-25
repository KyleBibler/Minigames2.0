package com.example.kyle.minigames;

/**
 * Created by Kyle on 11/25/2014.
 */
public class Light {
    private int id = 0;
    private int red = 0;
    private int green = 0;
    private int blue = 0;
    private double intensity = 0;

    public Light(int id, int red, int green, int blue, double intensity) {
        this.id = id;
        this.red = red;

        this.green = green;
        this.blue = blue;
        this.intensity = intensity;
        validate();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    private void validate() throws IllegalArgumentException {
        if (id < 1 || id > 32)
            throw new IllegalArgumentException("ID out of range");
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255)
            throw new IllegalArgumentException("Color out of range");
        if (intensity < 0.0 || intensity > 1.0)
            throw new IllegalArgumentException("Intensity out of range");
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public double getIntensity() {
        return intensity;
    }

    public int getId() {

        return id;
    }
}
