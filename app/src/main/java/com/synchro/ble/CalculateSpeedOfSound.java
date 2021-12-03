package com.synchro.ble;

import com.synchro.activity.GlobalApplication;

public class CalculateSpeedOfSound {
    public static double CalculateSpeedOfSound(double temperature, double humidity, double pressure) {
        GlobalApplication.last_metrology_time = System.currentTimeMillis();
        pressure *= 100;
        double Kelvin = 273.15;
        double T = temperature - Kelvin;
        double pi = 3.14;
        double ENH = pi * Math.pow(10, (-8)) * pressure + 1.00062 + Math.pow(T, 2) * 5.6 * Math.pow(10, (-7));
        double PSV1 = Math.pow(temperature, 2) * 1.2378847 * Math.pow(10, (-5)) - 1.9121316 * Math.pow(10, (-2)) * temperature;
        double PSV2 = 33.93711047 - 6.3431645 * Math.pow(10, (3)) / temperature;
        double PSV = Math.exp(PSV1) * Math.exp(PSV2);
        double H = humidity * ENH * PSV / pressure;
        double Xw = H / 100.0;
        double Xc = 400.0 * Math.pow(10, (-6));
        double C1 = 0.603055 * T + 331.5024 - Math.pow(T, 2) * 5.28 * Math.pow(10, (-4)) + (0.1495874 * T + 51.471935 - Math.pow(T, 2) * 7.82 * Math.pow(10, (-4))) * Xw;
        double C2 = (-1.82 * Math.pow(10, (-7)) + 3.73 * Math.pow(10, (-8)) * T - Math.pow(T, 2) * 2.93 * Math.pow(10, (-10))) * pressure + (-85.20931 - 0.228525 * T + Math.pow(T, 2) * 5.91 * Math.pow(10, (-5))) * Xc;
        double C3 = Math.pow(Xw, 2) * 2.835149 + Math.pow(pressure, 2) * 2.15 * Math.pow(10, (-13)) - Math.pow(Xc, 2) * 29.179762 - 4.86 * Math.pow(10, (-4)) * Xw * pressure * Xc;
        double C = C1 + C2 - C3;
        return C;
    }

    public static double calculateSpeedOfSoundFromTemperarure(double value1) {
        GlobalApplication.last_metrology_time = System.currentTimeMillis();
        return (20.05 * Math.pow((273.16 + value1 / 100), 0.5));
    }

}
