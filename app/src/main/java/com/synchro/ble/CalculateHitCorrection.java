package com.synchro.ble;

import android.content.Context;

import com.synchro.utils.AppMethods;

public class CalculateHitCorrection {

    public static double CalculateHitCorrection(double[] res_array, double h_input, Context context) {
        double[] temp_res1 = new double[4];
        double[] temp_res2 = new double[4];
        double[] temp_res3 = new double[4];
        h_input = h_input + 1;

        temp_res1[0] = res_array[0];
        temp_res1[1] = res_array[1];
        temp_res1[2] = res_array[2];
        temp_res1[3] = res_array[3];

        temp_res2[0] = -1 * res_array[7];
        temp_res2[1] = -1 * res_array[6];
        temp_res2[2] = -1 * res_array[5];
        temp_res2[3] = -1 * res_array[4];

        temp_res3[0] = -1 * (temp_res1[0] + temp_res2[0]) / 2;
        temp_res3[1] = (temp_res1[1] + temp_res2[1]) / 2;
        temp_res3[2] = (temp_res1[2] + temp_res2[2]) / 2;
        temp_res3[3] = -1 * (temp_res1[3] + temp_res2[3]) / 2;
        double AVG_RES = (temp_res3[0] + temp_res3[1] + temp_res3[2] + temp_res3[3]) / 4;
        // Print the value of AVG_RES in the log
        double correction = AVG_RES * (-57654 + 1.8606e+005 * h_input - 1.7055e+005 * Math.pow(h_input , 2));
        // Print the value of correction in the log
        double new_h = correction;
        AppMethods.writeDataTostrem(context, "Correction to h value: " + new_h );
        return new_h;
    }
}
