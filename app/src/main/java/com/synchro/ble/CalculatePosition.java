package com.synchro.ble;

import android.content.Context;
import android.util.Log;

import com.synchro.fragment.TrainFreeStyleFragment;
import com.synchro.utils.AppMethods;
import com.synchro.utils.BleCallback;
import com.synchro.utils.SharedPref;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class CalculatePosition {
    private double[] M_measured_mat = {0, -0.0026659565, -0.0036969441, 0.0019696939, -0.0016783341, -0.0014730098, -0.0016897568, -0.00063328166};
    private double[] mat_x = {0.2, 0.065, 0, -0.6, 0.5, 0.85, -0.1, -0.63};
    private double[] mat_y = {-0.3, 0.5, -0.64, 0.87, 0, 0.42, 0.9, 0.1};
    private double[] mat_z = {-0.6, 0.9, 1.2, -0.6, 0.2, 0.6, 0.85, 0.2};

    private double[] output_mat = new double[5];
    private int number_of_data = 8;
    private int far_hit = 0;
    private static double X_value;
    private static double H_value;
    private static double V_value;
    private static double ALPHA_value;
    private double[] M_mat;
    private double[][] H_mat;
    private double[] x_mat;
    private double C = 340;
    private float[][] m_product1;
    private double[][] H_mat_T;
    private float[][] m_product2;
    private double[] res;
    private float[] m_product3;
    private BleCallback bleCallback;
    private Context context;

    public CalculatePosition(BleCallback bleCallback, double[] M_measured_mat, double c_value, Context context) {
        this.M_measured_mat = M_measured_mat;
        this.bleCallback = bleCallback;
        this.C = c_value;
        this.context = context;
//        mat_x = new double[]{0.2, 0.065, 0, -0.6, 0.5, 0.85, -0.1, -0.63};
//        mat_y = new double[]{-0.3, 0.5, -0.64, 0.87, 0, 0.42, 0.9, 0.1};
//        mat_z = new double[]{-0.6, 0.9, 1.2, -0.6, 0.2, 0.6, 0.85, 0.2};
        check_data();
    }

    private void check_data() {    //check the amount of relevant data
        int i;
        for (i = 4; i < 8; i += 1) {
            //only num 5-8 might be non relevant
            if (M_measured_mat[i] == 0) {
                number_of_data -= 1;
            }
        }

        if (number_of_data == 4) {//hit is far away from the target- no need to calculate hit position
            if (M_measured_mat[0] < M_measured_mat[3]) {

                far_hit = 1;    //the hit is in the far right
            } else {
                far_hit = -1;//the hit is in the far left
            }
        }

        if ((M_measured_mat[4]!=0) && (M_measured_mat[5]!=0) && (M_measured_mat[6]!=0) && (M_measured_mat[7]!=0)){
            Modify_Sensors(8);
        } else if ((M_measured_mat[4]!=0) && (M_measured_mat[5]!=0) && (M_measured_mat[6]!=0) && (M_measured_mat[7]==0)){
            Modify_Sensors(7);
        }else if ((M_measured_mat[4]!=0) && (M_measured_mat[5]!=0) && (M_measured_mat[6]==0) && (M_measured_mat[7]==0)){
            Modify_Sensors(6);
        }else if ((M_measured_mat[4]==0) && (M_measured_mat[5]==0) && (M_measured_mat[6]!=0) && (M_measured_mat[7]!=0)){
            Modify_Sensors(6);
        }else if ((M_measured_mat[4]==0) && (M_measured_mat[5]!=0) && (M_measured_mat[6]!=0) && (M_measured_mat[7]!=0)){
            Modify_Sensors(5);
        }else if (far_hit==0){
            change2sec();
        }else {
            doit();
        }

//        Modify_Sensors();
    }

    private void Modify_Sensors(int arraysize) {    // apply this function after the "check_data()" function, only if(number_of_data!=8)&&(far_hit!=0)
        double[] temp_M_measured_mat = new double[arraysize], temp_mat_x = new double[arraysize], temp_mat_y = new double[arraysize],
                temp_mat_z = new double[arraysize];
        int i, j = 0;
        for (i = 0; i < arraysize; i += 1) {
            if (i < 4) {    //for data 1-4, just copy
                temp_M_measured_mat[j] = M_measured_mat[i];
                temp_mat_x[j] = mat_x[i];
                temp_mat_y[j] = mat_y[i];
                temp_mat_z[j] = mat_z[i];
                j += 1;
            } else if (M_measured_mat[i] != 0) {        //for data 5-8, check relevancy before copy
                temp_M_measured_mat[j] = M_measured_mat[i];
                temp_mat_x[j] = mat_x[i];
                temp_mat_y[j] = mat_y[i];
                temp_mat_z[j] = mat_z[i];
                j += 1;
            }
        }
        //	overwrite the M_measured_mat, x_sensors, y_sensors, h_sensors
        M_measured_mat = temp_M_measured_mat;
        mat_x = temp_mat_x;
        mat_y = temp_mat_y;
        mat_z = temp_mat_z;
        //	kill temp arrays
        // temp_M_measured_mat, temp_mat_x, temp_mat_y, temp_mat_z
        change2sec();
    }
//end of the 1st step
// Move forward only if  (far_hit==0)

    //2nd step- change data units
    private void change2sec() {
        for (int i=0; i<M_measured_mat.length;i++){
            M_measured_mat[i] = M_measured_mat[i]/600000000;
        }

        doit();
        // device clock is 600MHz
    }

    //end of 2nd step

//3rd step- calc parameters
//Just do it...
//for an example- if M_measure_mat[0]={0,-0.0026659565,-0.0036969441,0.0019696939,-0.0016783341,-0.0014730098,-0.0016897568,-0.00063328166}


//then you should get:
//	variable/g X_value=0.25
//	variable/g H_value=0.95
//	variable/g V_value=850
//	variable/g ALPHA_value=0.05
 /*0.926735
         -0.0254842
         0.952219
         0.0027793
         0.94944
         -0.000558496
         0.949999
         -1.43051e-06
         0.95
         0*/

    private void doit() {
        single_iter(0, 1, 850, 0, 0, number_of_data);
        int i = 0;
        double threshold;
        if ((output_mat[0] > 5) || (output_mat[0] < -5) || (output_mat[1] < 0.2) || (output_mat[1] > 3) || (output_mat[2] > 1600) || (output_mat[2] < 400) || (output_mat[3] < -1) || (output_mat[3] > 1)) {
            X_value = 0;
        } else {
            do {
                threshold = output_mat[1];
                single_iter(output_mat[0], output_mat[1], output_mat[2], output_mat[3], output_mat[4], number_of_data);
                threshold -= output_mat[1];
                i += 1;
                if (i > 10) {
                    break;
                }
            } while (abs(threshold) > 0);
            X_value = output_mat[0];
            H_value = output_mat[1] - 1;
            V_value = output_mat[2];
            ALPHA_value = output_mat[3];

            SharedPref.setValue(context, SharedPref.VELOCITY,V_value+"");

            try
            {
                TrainFreeStyleFragment.tv_target_velocity.setText("Target Velocity: "+V_value);
            }
            catch(NullPointerException e)
            {
                e.printStackTrace();
            }
            AppMethods.writeDataTostrem(context, "Calculate Positon: " + X_value+", "+H_value);
            AppMethods.writeDataTostrem(context, "Velocity: " + V_value);
            double hCorrection =  CalculateHitCorrection.CalculateHitCorrection(M_measured_mat,H_value, context);
            if (abs(hCorrection)<0.05){
                H_value = H_value+hCorrection;
                AppMethods.writeDataTostrem(context, "Calculate Positon after correction: " + X_value+", "+H_value);
            }
            bleCallback.drawTrainDot((float) X_value, (float) H_value);
        }
    }

    private double calc_Q(double x, double x_k, double y_k, double h, double h_k, double V, double C, double alpha) {
        double Q = Math.pow((Math.pow(((x - x_k) * cos(alpha) + y_k * sin(alpha)), 2) + Math.pow((h - h_k), 2)), 0.5);
        return Q;
    }

    private double div_x(double x, double x_k, double y_k, double h, double h_k, double V, double C, double alpha, double Q_k) {
        double result = -sin(alpha) / V + 1 / Q_k * Math.pow((1 / Math.pow(C, 2) - 1 / Math.pow(V, 2)), 0.5) * ((x - x_k) * cos(alpha) + y_k * sin(alpha)) * cos(alpha);
        return result;
    }

    private double div_V(double x, double x_k, double y_k, double h, double h_k, double V, double C, double alpha, double Q_k) {
        double result = 1 / Math.pow(V, 2) * ((x - x_k) * sin(alpha) - y_k * cos(alpha)) + Q_k / (Math.pow(V, 3) * Math.pow((1 / Math.pow(C, 2) - 1 / Math.pow(V, 2)), 0.5));
        return result;
    }

    private double div_h(double x, double x_k, double y_k, double h, double h_k, double V, double C, double alpha, double Q_k) {
        double result = (h - h_k) / Q_k * Math.pow((1 / Math.pow(C, 2) - 1 / Math.pow(V, 2)), 0.5);
        return result;
    }

    private double div_alpha(double x, double x_k, double y_k, double h, double h_k, double V, double C, double alpha, double Q_k) {
        double result = -((x - x_k) * cos(alpha) + y_k * sin(alpha)) * (1 / V + 1 / Q_k * (Math.pow((1 / Math.pow(C, 2) - 1 / Math.pow(V, 2)), 0.5)) * ((x - x_k) * sin(alpha) - y_k * cos(alpha)));
        return result;
    }

    private double calc_TOA(double t_offset, double x, double x_k, double y_k, double h, double h_k, double V, double C, double alpha, double Q_k) {
        double result = t_offset - 1 / V * ((x - x_k) * sin(alpha) - y_k * cos(alpha)) + Q_k * Math.pow((1 / Math.pow(C, 2) - 1 / Math.pow(V, 2)), 0.5);
        return result;
    }

    private void make_mat(double x, double h, double V, double alpha, double t_offset, int num_of_data) {
        int i, j;
        x_mat = new double[]{x, h, V, alpha, t_offset};
        M_mat = new double[number_of_data];
        H_mat = new double[number_of_data][5];
        for (i = 0; i < num_of_data; i += 1) {
            double Q_k = calc_Q(x, mat_x[i], mat_y[i], h, mat_z[i], V, C, alpha);
            M_mat[i] = calc_TOA(t_offset, x, mat_x[i], mat_y[i], h, mat_z[i], V, C, alpha, Q_k);
            for (j = 0; j < 5; j += 1) {
                switch (j) {
                    case 0:
                        H_mat[i][j] = div_x(x, mat_x[i], mat_y[i], h, mat_z[i], V, C, alpha, Q_k);
                        break;
                    case 1:
                        H_mat[i][j] = div_h(x, mat_x[i], mat_y[i], h, mat_z[i], V, C, alpha, Q_k);
                        break;
                    case 2:
                        H_mat[i][j] = div_V(x, mat_x[i], mat_y[i], h, mat_z[i], V, C, alpha, Q_k);
                        break;
                    case 3:
                        H_mat[i][j] = div_alpha(x, mat_x[i], mat_y[i], h, mat_z[i], V, C, alpha, Q_k);
                        break;
                    case 4:
                        H_mat[i][j] = 1;
                        break;
                }
            }

        }
        transpose_matrix(H_mat, num_of_data);

        mat_multiple1(H_mat_T, H_mat, num_of_data);

        inv_mat(m_product1);

        mat_multiple2(m_product1, H_mat_T, num_of_data);
    }

    private void transpose_matrix(double[][] H_mat, int num_of_data) {
        H_mat_T = new double[5][num_of_data];
        int i, j;
        for (i = 0; i < 5; i += 1) {
            for (j = 0; j < num_of_data; j += 1) {
                H_mat_T[i][j] = H_mat[j][i];
            }
        }
    }

    private void mat_multiple1(double[][] H_mat_T, double[][] H_mat, int num_of_data) {
        m_product1 = new float[5][5];
        int i, j, k;
        for (k = 0; k < 5; k += 1) {
            for (i = 0; i < 5; i += 1) {
                for (j = 0; j < num_of_data; j += 1) {
                    m_product1[k][i] += H_mat_T[k][j] * H_mat[j][i];
                }
            }
        }
    }

    private void mat_multiple2(float[][] M_product1, double[][] H_mat_T, int num_of_data) {
        m_product2 = new float[5][num_of_data];
        int i, j, k;
        for (k = 0; k < 5; k += 1) {
            for (i = 0; i < num_of_data; i += 1) {
                for (j = 0; j < 5; j += 1) {
                    m_product2[k][i] += M_product1[k][j] * H_mat_T[j][i];
                }
            }
        }
    }

    private void mat_multiple3(float[][] M_product2, double[] res, int num_of_data) {
        m_product3 = new float[5];
        for (int i = 0; i < 5; i += 1) {
            for (int j = 0; j < num_of_data; j += 1) {
                m_product3[i] += (M_product2[i][j] * res[j]);
                Log.d("mat_multiple3:", M_product2[i][j] + "," + res[j] + "," + m_product3[i]);
            }
        }
    }

    private void single_iter(double x, double h, double V, double alpha, double t_offset, int num_of_data) {
        make_mat(x, h, V, alpha, t_offset, num_of_data);
        res = new double[num_of_data];
        for (int i = 0; i < num_of_data; i++) {
            res[i] = M_measured_mat[i] - M_mat[i];
        }
        mat_multiple3(m_product2, res, num_of_data);

        for (int i = 0; i < output_mat.length; i++) {
            output_mat[i] = x_mat[i] + m_product3[i];
        }
    }

    private void inv_mat(float[][] mat) {
        float[][] a = new float[5][10];
        float ratio;
        int i, j, k;
        for (i = 0; i < 5; i += 1) {
            for (j = 0; j < 5; j += 1) {
                a[i][j] = mat[i][j];
                if (i == j) {
                    a[i][j + 5] = 1;
                }
            }
        }

        for (i = 0; i < 5; i++) {
            for (j = 0; j < 5; j++) {
                if (i != j) {
                    ratio = a[j][i] / a[i][i];
                    for (k = 0; k < 10; k += 1) {
                        a[j][k] = a[j][k] - ratio * a[i][k];
                    }
                }
            }
        }
        for (i = 0; i < 5; i += 1) {
            for (j = 0; j < 5; j += 1) {
                mat[i][j] = a[i][j + 5] / a[i][i];
            }
        }
    }
}
