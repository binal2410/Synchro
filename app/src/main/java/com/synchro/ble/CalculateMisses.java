package com.synchro.ble;

import android.util.Log;

import com.synchro.utils.AppConstants;

import static com.synchro.activity.GlobalApplication.isMisses;
import static com.synchro.utils.AppConstants.Canvas_pixels_X;
import static com.synchro.utils.AppConstants.Canvas_pixels_Y;
import static com.synchro.utils.AppConstants.Image_pixels_X;
import static com.synchro.utils.AppConstants.Image_pixels_Y;
import static com.synchro.utils.AppConstants.offset_x;
import static com.synchro.utils.AppConstants.offset_y;

public class CalculateMisses {

    private static String TextEncoding = "MacRoman";
    private static int rtGlobals = 3;        // Use modern global access method and strict wave access.
// input parameters:
// canvas patameters: canvas_x_min, canvas_x_max, canvas_y_min, canvas_y_max
// image patameters: image_x_min, image_x_max, image_y_min, image_y_max
// the image can be with the same dimension as the canvas but in most cases it is smaller than the canvas.
//scaling parameters: Scale_x, Scale_y, x_center, y_center
//hit parameter: hit_x, hit_h in pixel units

    //output parameters:
// updated image patameters: image_x_min, image_x_max, image_y_min, image_y_max
// the image should be plot in smaller area of the canvas. The above parameters are the frame indexes (in pixel unit) of where the image should be ploted.
//update scaling parameters: Scale_x, Scale_y, x_center, y_center
// all previous hits should be rescale (from real-world cm to pixels in the canvas) according to this new scale parameters and upcoming hits as well.
    public static float image_x_min = 0, image_x_max = 0, image_y_min = 0, image_y_max = 0;
    public static float x_center, y_center;

    public static boolean check_miss(float hit_x, float hit_h) {
        image_x_min = 0;
        image_x_max = Image_pixels_X;
        image_y_min = 0;
        image_y_max = Image_pixels_Y;
        float canvas_x_min = 0, canvas_x_max = Image_pixels_X, canvas_y_min = 0, canvas_y_max = Image_pixels_Y;

        float RoL, ToB;
        float s_factor;
        if ((hit_x > canvas_x_min) && (hit_x < canvas_x_max) && (hit_h > canvas_y_min) && (hit_h < canvas_y_max)) {
            Log.d("ismiss==>",hit_x+", "+hit_h+", false");//check is it a miss
            return false;
        } else {
            Log.d("ismiss==>",hit_x+", "+hit_h+", true");
            //it is a miss
        }

        float temp_factor1 = 0;
        if (((hit_x > canvas_x_min) || (hit_x < canvas_x_max)) && ((hit_h > canvas_y_min) || (hit_h < canvas_y_max))) { //check is it missed in both direction or just single direction
            //missed in both direction
            RoL = check_dir_miss(hit_x, canvas_x_min, canvas_x_max);
            if (RoL == 0) {//miss to the right
                temp_factor1 = calc_s_factor(hit_x - canvas_x_max, canvas_x_max - canvas_x_min);
            } else {//miss to the left
                temp_factor1 = calc_s_factor(canvas_x_min - hit_x, canvas_x_max - canvas_x_min);
            }
        }
        float temp_factor2;
        ToB = check_dir_miss(hit_h, canvas_y_min, canvas_y_max);
        if (ToB == 0) {//miss to the top
            temp_factor2 = calc_s_factor(hit_h - canvas_y_max, canvas_y_max - canvas_y_min);
        } else { //miss to the bottom
            temp_factor2 = calc_s_factor(canvas_y_min - hit_h, canvas_y_max - canvas_y_min);
        }

        if (temp_factor1 > temp_factor2) {
            s_factor = temp_factor1;
        } else {
            s_factor = temp_factor2;
        }

        //missed in one direction
        if ((hit_x > canvas_x_min) || (hit_x < canvas_x_max)) { //is it x miss?
            // x miss
            RoL = check_dir_miss(hit_x, canvas_x_min, canvas_x_max);
            if (RoL == 0) {//miss to the right
                s_factor = calc_s_factor(hit_x - canvas_x_max, canvas_x_max - canvas_x_min);
            } else { //miss to the left
                s_factor = calc_s_factor(canvas_x_min - hit_x, canvas_x_max - canvas_x_min);
            }
        } else {
            //h miss
            ToB = check_dir_miss(hit_h, canvas_y_min, canvas_y_max);
            if (ToB == 0)//miss to the top
                s_factor = calc_s_factor(hit_h - canvas_y_max, canvas_y_max - canvas_y_min);
            else //miss to the bottom
                s_factor = calc_s_factor(canvas_y_min - hit_h, canvas_y_max - canvas_y_min);
        }

        find_new_image_para(s_factor);
        find_new_scale_para(s_factor);
        return true;
    }

    private static int check_dir_miss(float num1, float num2, float num3) {
        int re = 1;
        if (num1 > num3) {
            re = 0;
        }
        return re;
    }

    private static float calc_s_factor(float num1, float num2) {
        float re = (2 * num1 + num2) / num2;
        re *= 1.1;
        return re;
    }

    private static void find_new_image_para(float scale_num) {
        float image_length = (image_y_max - image_y_min) / 2;
        float image_width = (image_x_max - image_x_min) / 2;
        float image_center_x = (image_x_max + image_x_min) / 2;
        float image_center_y = (image_y_max + image_y_min) / 2;
        //the target should be plot according to the following parameters:
        image_x_max = image_center_x + image_width / scale_num;
        image_x_min = image_center_x - image_width / scale_num;
        image_y_max = image_center_y + image_length / scale_num;
        image_y_min = image_center_y - image_length / scale_num;
        //now the app should plot the image according to these new pixel's parameters
    }

    private static void find_new_scale_para(float s_factor) {
        float Scale_x = 0, Scale_y = 0;
        Scale_x /= s_factor;
        Scale_y /= s_factor;
        //remember the following command from the original scaling function.
        x_center = (Canvas_pixels_X) / 2 - offset_x * Scale_x;
        y_center = (Canvas_pixels_Y) / 2 + offset_y * Scale_y;
        //Now rescale (cm to pixels) all hits again with the new parameters.
    }

}
