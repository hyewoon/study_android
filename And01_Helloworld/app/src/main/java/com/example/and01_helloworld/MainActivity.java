package com.example.and01_helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //옵저버(observer)<= : 관찰자, 감시자
    //옵저버 패턴 : 인터페이스를 활용해서 각종 위젯(뷰)의 변경상태를 감지해서 개발자에게 알려주는 형식
    //xml에서 열린 태그는 반드시(대부분) 자바의 객체로 존재한다.
    //ConstraintLayout, Button

    Button btn; //<=1. null, 2.디자인에 있는 버튼?
                //객체를 선언만 한 상태(null)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //<=activity_main.xml java코드 연결

        btn =findViewById(R.id.btn); //new를 사용X , 디자인에 있는 위젯을 찾아서 연결(초기화식)
        //View.OnClickListener <=인터페이스 : 객체처럼 사용할 수 있다.
        //1. 인터페이스를 new로 바로 파라미터로 넘기는 방식
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        //2.인터페이스를 미리 new로 만들어 놓고 사용하는 방식
//           View.OnClickListener  clickListener = new View.OnClickListener() {
//               @Override
//               public void onClick(View v) {
//
//               }
//           };
//           btn.setOnClickListener(clickListener);
        //3. 상속을 받아서 나자신을 이용하는 방식
           btn.setOnClickListener(this);
        }

    @Override
    public void onClick(View v) {
        //Logd + enter: 자동완성

       // Log.d( "김혜운" , "버튼이 클릭 됨 ");
        //버튼을 클릭하면 1~50까지의 수중 홀수의 수를 로그로 출력하기

        for(int i = 1; i<=50 ; i++) {
             if(i%2 == 1)
            Log.d("홀수", i + "" ); //ctrl + p
        }
            //어떤 변수 + String Type = String
            //int(1) + int(2) + String = 3
            //int(1) + String + int(2) =12
        }

    }

