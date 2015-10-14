package com.example.administrator.myapplication2._4_Detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.myapplication2.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by GIL on 2015-10-08.
 */
public class graphActivity extends Activity implements View.OnClickListener{
    private ArrayList<HashMap<String,String>> listItem;
    Button minus_btn,plus_btn;

    GraphView graph1,graph2;
    DataPoint[] kal,ldistance,kal_show,ldistance_show;
    ArrayList<String> kal2,ldistance2;
    String[] ldate,ldate_show;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout._4_graph);

        Intent myIntent = getIntent();
        if (myIntent != null) {
            listItem = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("listItem");
            Log.i("boogill", listItem.toString());
            if (listItem != null) {
            } else {
                Log.i("boogil", "사이즈 0이래");
            }
        }


        listLintUp(listItem); //seq순서대로 정렬하기

        graph1 = (GraphView) findViewById(R.id.graph1);
        graph2 = (GraphView) findViewById(R.id.graph2);

        Button plus_btn=(Button)findViewById(R.id.plus_btn);
        Button minus_btn=(Button)findViewById(R.id.minus_btn);
        plus_btn.setOnClickListener(this);minus_btn.setOnClickListener(this);

        kal2=new ArrayList<>();
        ldistance2=new ArrayList<>();



        for (int i = 0; i < listItem.size(); i++) {
            kal2.add(listItem.get(i).get("kal"));
            ldistance2.add(listItem.get(i).get("ldistance"));
        }



            if(listItem.size()>0) {
       /*특정일에 운동을 여러번했을때 칼로리와 이동거리를 합쳐주는 로직*/
            int i = 0;
            while (kal2.size() != (i + 1)) {
                if (kal2.size() == i + 1) break;
                if (listItem.get(i).get("ldate").substring(5, 10).equals(listItem.get(i + 1).get("ldate").substring(5, 10))) {
                    kal2.set(i, String.valueOf(Integer.parseInt(kal2.get(i)) + Integer.parseInt(kal2.get(i + 1))));
                    ldistance2.set(i, String.valueOf(Double.parseDouble(ldistance2.get(i)) + Double.parseDouble(ldistance2.get(i + 1))));
                    kal2.remove(i + 1);
                    ldistance2.remove(i + 1);
                    listItem.remove(i + 1);
                    Log.i("boogil", "하하22:" + kal2.size());
                    i--;
                }
                i++;
            }

            Log.i("boogil", "하하33:" + kal2.size());
            Log.i("boogil", "하하33:" + ldistance2.size());
            Log.i("boogil", "하하33:" + listItem.size());

            }

        /*걸러낸 정보 시작*/

            ldistance = new DataPoint[listItem.size()];
            kal = new DataPoint[listItem.size()];
            ldate = new String[listItem.size()];

            for (int a = 0; a < listItem.size(); a++) {
                ldistance[a] = new DataPoint(a, Double.parseDouble(ldistance2.get(a)));
                kal[a] = new DataPoint(a, Double.parseDouble(kal2.get(a)));
                ldate[a] = listItem.get(a).get("ldate").substring(8, 9).equals("0") ?
                        listItem.get(a).get("ldate").substring(9, 10) + "일" : listItem.get(a).get("ldate").substring(8, 10) + "일";
            }



        ldistance_show = new DataPoint[7];
        kal_show = new DataPoint[7];
        ldate_show = new String[7];

        if(listItem.size()>=7) {
            int c=0;
            for (int a = listItem.size()-7; a < listItem.size(); a++) {
                ldistance_show[c]=ldistance[a];
                kal_show[c]=kal[a];
                ldate_show[c]=ldate[a];
                c++;
            }
        }

        if(ldate.length<=1) {
            Toast.makeText(getApplicationContext(),"분석할 데이터가 없습니다.",Toast.LENGTH_SHORT);

        }else if(listItem.size()<=7 && ldate.length>=2) {
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph1);
            staticLabelsFormatter.setHorizontalLabels(ldate);
            StaticLabelsFormatter staticLabelsFormatter2 = new StaticLabelsFormatter(graph2);
            staticLabelsFormatter2.setHorizontalLabels(ldate);
            graph1.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter2);

            LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>(kal);
            series1.setDrawDataPoints(true);

            graph1.addSeries(series1);
            //graph1.addSeries(point1);

            BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(kal);
            series.setSpacing(60);
            // draw values on top
            series.setDrawValuesOnTop(true);
            series.setValuesOnTopColor(Color.RED);
            //series.setValuesOnTopSize(50);
            graph1.addSeries(series);


            LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(ldistance);
            series2.setDrawDataPoints(true);

            graph2.addSeries(series2);

            BarGraphSeries<DataPoint> series2_2 = new BarGraphSeries<DataPoint>(ldistance);
            series2_2.setSpacing(60);
            // draw values on top
            series2_2.setDrawValuesOnTop(true);
            series2_2.setValuesOnTopColor(Color.RED);
            //series.setValuesOnTopSize(50);
            graph2.addSeries(series2_2);
        }else{
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph1);
            staticLabelsFormatter.setHorizontalLabels(ldate_show);
            StaticLabelsFormatter staticLabelsFormatter2 = new StaticLabelsFormatter(graph2);
            staticLabelsFormatter2.setHorizontalLabels(ldate_show);
            graph1.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter2);

            LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>(kal_show);
            series1.setDrawDataPoints(true);

            graph1.addSeries(series1);
            //graph1.addSeries(point1);

            BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(kal_show);
            series.setSpacing(60);
            // draw values on top
            series.setDrawValuesOnTop(true);
            series.setValuesOnTopColor(Color.RED);
            //series.setValuesOnTopSize(50);
            graph1.addSeries(series);


            LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(ldistance_show);
            series2.setDrawDataPoints(true);

            graph2.addSeries(series2);

            BarGraphSeries<DataPoint> series2_2 = new BarGraphSeries<DataPoint>(ldistance_show);
            series2_2.setSpacing(60);
            // draw values on top
            series2_2.setDrawValuesOnTop(true);
            series2_2.setValuesOnTopColor(Color.RED);
            //series.setValuesOnTopSize(50);
            graph2.addSeries(series2_2);
        }


      }


    public void listLintUp(ArrayList<HashMap<String,String>> listItem){
        for(int i=0;i<listItem.size()-1;i++){
            for(int j=i+1;j<listItem.size();j++){
                int a= Integer.parseInt(listItem.get(i).get("seq"));
                int b= Integer.parseInt(listItem.get(j).get("seq"));
                HashMap tmp=new HashMap<String,String>();
                if(a>b){
                    tmp=listItem.get(i);
                    listItem.set(i,listItem.get(j));
                    listItem.set(j,tmp);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.plus_btn:
                if(listItem.size()>=7) {
                    graph1.removeAllSeries();
                    graph2.removeAllSeries();
                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph1);
                    staticLabelsFormatter.setHorizontalLabels(ldate_show);
                    StaticLabelsFormatter staticLabelsFormatter2 = new StaticLabelsFormatter(graph2);
                    staticLabelsFormatter2.setHorizontalLabels(ldate_show);
                    graph1.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
                    graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter2);

                    LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>(kal_show);
                    series1.setDrawDataPoints(true);

                    graph1.addSeries(series1);
                    //graph1.addSeries(point1);

                    BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(kal_show);
                    series.setSpacing(60);
                    // draw values on top
                    series.setDrawValuesOnTop(true);
                    series.setValuesOnTopColor(Color.RED);
                    //series.setValuesOnTopSize(50);
                    graph1.addSeries(series);


                    LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(ldistance_show);
                    series2.setDrawDataPoints(true);

                    graph2.addSeries(series2);

                    BarGraphSeries<DataPoint> series2_2 = new BarGraphSeries<DataPoint>(ldistance_show);
                    series2_2.setSpacing(60);
                    // draw values on top
                    series2_2.setDrawValuesOnTop(true);
                    series2_2.setValuesOnTopColor(Color.RED);
                    graph2.addSeries(series2_2);
                }
                break;

            case R.id.minus_btn:
                if(ldate.length<=1) {
                    Toast.makeText(getApplicationContext(), "분석할 데이터가 없습니다.", Toast.LENGTH_SHORT);
                }else {
                    graph1.removeAllSeries();
                    graph2.removeAllSeries();
                    StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph1);
                    int a = (ldate.length - 1) / 4, b = (ldate.length - 1) / 2, c = (ldate.length - 1) * 3 / 4, d = ldate.length - 1;
                    for (int i = 0; i < ldate.length; i++) {
                        if (!(i == a || i == b || i == c || i == d || i == 0))
                            ldate[i] = "";
                    }
                    staticLabelsFormatter.setHorizontalLabels(ldate);
                    StaticLabelsFormatter staticLabelsFormatter2 = new StaticLabelsFormatter(graph2);
                    staticLabelsFormatter2.setHorizontalLabels(ldate);
                    graph1.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
                    graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter2);

                    LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>(kal);
                    //series1.setDrawDataPoints(true);

                    graph1.addSeries(series1);
                    //graph1.addSeries(point1);

                    BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(kal);
                    series.setSpacing(60);
                    // draw values on top
                    series.setDrawValuesOnTop(true);
                    series.setValuesOnTopColor(Color.RED);
                    series.setValuesOnTopSize(35);
                    graph1.addSeries(series);


                    LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(ldistance);
                    //series2.setDrawDataPoints(true);

                    graph2.addSeries(series2);

                    BarGraphSeries<DataPoint> series2_2 = new BarGraphSeries<DataPoint>(ldistance);
                    series2_2.setSpacing(60);
                    // draw values on top
                    series2_2.setDrawValuesOnTop(true);
                    series2_2.setValuesOnTopColor(Color.RED);
                    series2_2.setValuesOnTopSize(35);
                    graph2.addSeries(series2_2);
                    break;
                }
        }
    }
}
