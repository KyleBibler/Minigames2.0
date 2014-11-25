package com.example.kyle.minigames;

/**
 * Created by Kyle on 11/25/2014.
 */

import java.util.ArrayList;


/**
 * Created by Alex on 11/2/2014.
 */
public class LightModel {
    private ArrayList<Light> lights = null;
    private boolean propagate = false;

    public LightModel(ArrayList<Light> lights, boolean propagate) {
        this.lights = lights;
        this.propagate = propagate;
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder("{\"lights\": [");
        for (Light l : lights) {
            builder.append("{\"lightId\": " + l.getId() + ",");
            builder.append("\"red\": " + l.getRed() + ",");
            builder.append("\"green\": " + l.getGreen() + ",");
            builder.append("\"blue\": " + l.getBlue() + ",");
            builder.append("\"intensity\": " + l.getIntensity() + "},");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("],\"propagate\": " + propagate + "}");

        return builder.toString();
    }

    public ArrayList<Light> getLights() {
        return lights;
    }

    public boolean isPropagate() {
        return propagate;
    }
}