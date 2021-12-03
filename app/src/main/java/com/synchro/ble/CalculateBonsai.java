package com.synchro.ble;

import android.content.Context;
import android.util.Log;

import com.synchro.utils.SharedPref;

import java.util.ArrayList;

public class CalculateBonsai {
    public static int bonsai_per_hit(float x, float h) {
        Double radial_dist = Math.pow((Math.pow(x , 2) + Math.pow(h , 2)) , 0.5);
        radial_dist = radial_dist*100;
        int Score;
        if(radial_dist < 1) {
            Score = 10;
        }else if(radial_dist < 3) {
            Score = 9;
        }else if(radial_dist < 5) {
            Score = 8;
        } else if(radial_dist < 7) {
            Score = 7;
        } else if(radial_dist < 9) {
            Score = 6;
        } else if(radial_dist < 11) {
            Score = 5;
        } else if(radial_dist < 13) {
            Score = 4;
        } else if(radial_dist < 15) {
            Score = 3;
        } else if(radial_dist < 17) {
            Score = 2;
        } else if(radial_dist < 19) {
            Score = 1;
        } else {
            Score = 0;
        }
        return Score;
    }

    public static int bonsai_per_hit_new(Context mContext, float x, float h){
        String value=SharedPref.getValue(mContext,SharedPref.SCORE_VALUE,"");
        int score=0;
        if(!value.equals(""))
        {
            String[] rowdata = value.split("\n");
            ArrayList<Integer> arrayList1=new ArrayList<>();
            ArrayList<Integer>arrayList2=new ArrayList<>();
            ArrayList<Integer>arrayList3=new ArrayList<>();
            ArrayList<Integer>arrayList4=new ArrayList<>();
            ArrayList<Integer>arrayList5=new ArrayList<>();
            ArrayList<Integer>arrayList6=new ArrayList<>();
            ArrayList<Integer>arrayList7=new ArrayList<>();
            if (rowdata.length > 0) {

                for (int i = 0; i < rowdata.length; i++) {
                    String[] columndata = rowdata[i].trim().split("\t");
                    Log.e("rowData",rowdata[i]);

                    //  Log.e("rowData",rowdata[i]);
                    if (columndata.length >= 5) {
                        arrayList1.add(Integer.parseInt(columndata[0]));
                        arrayList2.add(Integer.parseInt(columndata[1]));
                        arrayList3.add(Integer.parseInt(columndata[2]));
                        arrayList4.add(Integer.parseInt(columndata[3]));
                        arrayList5.add(Integer.parseInt(columndata[4]));
                        arrayList6.add(Integer.parseInt(columndata[5]));
                        arrayList7.add(Integer.parseInt(columndata[6]));
                    }
                }
            }
            for(int i=0;i<arrayList1.size();i+=1)
            {
                if((arrayList1.get(i)==arrayList5.get(i))&&(arrayList2.get(i)==arrayList6.get(i)))
                {
                    score=calc_score1(x, h,arrayList1.get(i),arrayList2.get(i),arrayList3.get(i),arrayList4.get(i));
                   // the_score*=Score_row_7[i]
                    score=(score*arrayList7.get(i));
                }
                else
                {
                    score=calc_score2(x, h,arrayList1.get(i),arrayList2.get(i),arrayList3.get(i),arrayList4.get(i),arrayList5.get(i),arrayList6.get(i));
                  //  the_score*=Score_row_7[i]
                    score=(score*arrayList7.get(i));                }

                if(score!=0)
                    break;


            }
            return score;

        }
        return score;
       // return 0;
    }

    public static int calc_score1(float result_x,float result_h,int Score_parameter_1,int Score_parameter_2,int Score_parameter_3,int Score_parameter_4)
    {
        int x= (int) (result_x-Score_parameter_1);
        int h= (int)(result_h-Score_parameter_2);
        double temp_dist=Math.pow((Math.pow(x , 2) + Math.pow(h , 2)) , 0.5);

        if(temp_dist>Score_parameter_3)
        {
            return 0;

        }
        else if(Score_parameter_4==0)
        {
            return 1;

        }
        else if((Score_parameter_4==1)&&(result_h>Score_parameter_2))
        {
            return 1;

        }
        else if((Score_parameter_4==2)&&(result_x>Score_parameter_1))
        {
            return 1;

        }
        else if((Score_parameter_4==3)&&(result_h<Score_parameter_2))
        {
            return 1;

        }
        else if((Score_parameter_4==4)&&(result_x<Score_parameter_1))
        {
            return 1;

        }
        else if((Score_parameter_4==5)&&(result_h>Score_parameter_2)&&(result_x>Score_parameter_1))
        {
            return 1;

        }
        else if((Score_parameter_4==6)&&(result_x>Score_parameter_1)&&(result_h<Score_parameter_2))
        {
            return 1;

        }
        else if((Score_parameter_4==7)&&(result_h<Score_parameter_2)&&(result_x<Score_parameter_1))
        {
            return 1;

        }
        else if((Score_parameter_4==8)&&(result_x<Score_parameter_1)&&(result_h>Score_parameter_2))
        {
            return 1;

        }
        else
        {
            return 0;

        }

    }
    public static int calc_score2(float result_x, float result_h,int Score_parameter_1,int Score_parameter_2,int Score_parameter_3,int Score_parameter_4,int Score_parameter_5,int Score_parameter_6)
    {
        int slope1, slope2, shape1, shape2;


        if(Score_parameter_1==Score_parameter_3)
        {
            if((result_x>Score_parameter_1)||(result_x<Score_parameter_5))
                return 0;
        }
        else
        {
            slope1=(Score_parameter_2-Score_parameter_4)/(Score_parameter_1-Score_parameter_3);
            shape1=check_slope(result_x, result_h,slope1,Score_parameter_1,Score_parameter_2,Score_parameter_5,Score_parameter_6);
            if(shape1==0)
                return 0;
        }
        slope2=(Score_parameter_4-Score_parameter_6)/(Score_parameter_3-Score_parameter_5);
        shape2=check_slope(result_x, result_h,slope2,Score_parameter_1,Score_parameter_2,Score_parameter_5,Score_parameter_6);
        if(shape2==0)
            return 0;
        else
            return 1;

    }

    public static int  check_slope(float result_x, float result_h,int slope,int num1,int num2,int num3,int num4)
    {
        double inter1=num2-num1*slope;
        double inter2=num4-num3*slope;
        double inter3=result_h-result_x*slope;
        if(inter1>inter2)
        {
            if((inter3<inter1)&&(inter3>inter2))
                return 1;
            else
                return 0;
        }
        else
        {
            if((inter3<inter2)&&(inter3>inter1))
            {
                return 1;

            }
            else
            {
                return 0;

            }
        }

    }
}
