package com.weather.stringtosuccessrate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    static String WholeJson;
    static String Code="M35Y5325";
    int Score=0;
    static boolean stateFound=false;
    int length_RTO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WholeJson=loadJSONfromAsset();

        Toast.makeText(this, String.valueOf(scoreOfNumberPlateAccuracy(Code,WholeJson)), Toast.LENGTH_SHORT).show();
    }


    private String loadJSONfromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("RTO.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }




    private int scoreOfNumberPlateAccuracy(String Code,String WholeJson)
    { String substituteCode;

        Code=Code.replaceAll("[\\\\\\-\\.\\(\\)'+]","");

        if(Code.substring(Code.length()-1).equalsIgnoreCase("I") && Character.isDigit(Code.charAt(Code.length()-5)) && Code.length()>10)
             {
                 Code=Code.substring(0,Code.length()-1);
                 Log.d("main",Code);
             }



              if(  Code.substring(Code.length()-4,Code.length()).contains("O") ||
                   Code.substring(Code.length()-4,Code.length()).contains("I") ||
                   Code.substring(Code.length()-4,Code.length()).contains("T"))
              {
                      String NewCode=Code;
                      Code = NewCode.substring(0,Code.length()-4)+Code.substring(Code.length() - 4, Code.length()).replaceAll("O", "0");
                      NewCode=Code;
                      Code = NewCode.substring(0,Code.length()-4)+Code.substring(Code.length() - 4, Code.length()).replaceAll("I", "1");
                      NewCode=Code;
                      Code = NewCode.substring(0,Code.length()-4)+Code.substring(Code.length() - 4, Code.length()).replaceAll("T", "1");

                  Log.d("Replace",Code);

              }



        if(Code.length()<7 || Code.length()>13)
        {
            Toast.makeText(this, "Not a Number Plate", Toast.LENGTH_SHORT).show();
            return Score;
        }
        for(int i = Code.length()-4; i < Code.length(); i++)
        {
            Log.d("digit", String.valueOf(Code.charAt(i)));

            if(Character.isDigit(Code.charAt(i)))
            {
                Score=Score+10;
            }
        }


        try {
            JSONArray  jsonArray=new JSONArray(WholeJson);

            int j;
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String State=jsonObject.getString("state");
                if(State.equalsIgnoreCase(Code.substring(0,2)))
                {
                    Score=Score+20;
                    stateFound=true;
                    String vehicleCode=jsonObject.getString("vehicle_code");
                    Log.d("vehicle",vehicleCode);

                    if(vehicleCode.equalsIgnoreCase("False"))
                    {
                        JSONArray rtoArray=jsonObject.getJSONArray("rto");
                        Log.d("rtoArray",String.valueOf(rtoArray.length()));

                        String lastIndexedRto=rtoArray.getString(rtoArray.length()-1);
                        int max_length_rto=lastIndexedRto.length();
                        length_RTO=max_length_rto;
                        for(int k=0;k<rtoArray.length();k++)
                        {
                            String kthRto=rtoArray.getString(k);

                            if(Code.substring(0,max_length_rto).equalsIgnoreCase(kthRto))
                            {
                                Score=Score+max_length_rto*10;
                            }
//                            else if(Code.substring(0,max_length_rto-1).equalsIgnoreCase(kthRto))
//
//                            {
//                                Score=Score+10;
//                            }


                        }

                    }

                    else
                     {
                         JSONArray rtoArray=jsonObject.getJSONArray("rto");

                         Log.d("rtoArray",String.valueOf(rtoArray.length()));
                         String lastIndexedRto=rtoArray.getString(rtoArray.length()-1);
                         int max_length_rto=lastIndexedRto.length();
                         length_RTO=max_length_rto;
                         for(int k=0;k<rtoArray.length();k++)
                         {
                             String kthRto=rtoArray.getString(k);

                             if(Code.substring(0,max_length_rto).equalsIgnoreCase(kthRto))
                             {
                                 Score=Score+max_length_rto*10;
                             }
//                             else if(Code.substring(0,max_length_rto-1).equalsIgnoreCase(kthRto))
//
//                             {
//                                 max_length_rto-=1;
//                                 Score=Score+10;
//                             }


                         }

                         JSONArray codesrray=jsonObject.getJSONArray("codes");
                         String lastIndexedCode=codesrray.getString(codesrray.length()-1);
                         int max_length_code=lastIndexedCode.length();

                         for(int k=0;k<codesrray.length();k++)
                         {
                             String kthCode=codesrray.getString(k);

                             if(Code.substring(max_length_rto,max_length_rto+max_length_code).equalsIgnoreCase(kthCode))
                             {
                                 Score=Score+max_length_rto*10+max_length_code*10;
                             }
//                             else if(Code.substring(max_length_rto,max_length_rto+max_length_code+1).equalsIgnoreCase(kthCode))
//
//                             {
//                                 Score=Score+10;
//                                 Toast.makeText(this, Code.substring(max_length_rto,max_length_rto+max_length_code+1), Toast.LENGTH_SHORT).show();
//
//                             }


                         }

                     }

                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!stateFound)
        {
            Log.d("score",String.valueOf(Score));
            return Score;
        }
        if(Code.length()>8)
        {
         if(Code.substring(length_RTO,Code.length()-4).length()>2)
         {
             Score=Score-30;
             Log.d("score",String.valueOf(Score));

         }

        }
        else
        {
         return Score;
        }

        Log.d("Aftermain",Code);
        return Score;

    }
}