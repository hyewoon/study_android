package com.example.lastproject.member;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.conn.CommonMethod;
import com.example.lastproject.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JoinActivity extends AppCompatActivity {
    Button btn_join , btn_cancel ;
    EditText edt_email , edt_pw , edt_name ;
    ImageView imgv_profile ;
    String img_path; // 이미지의 경로를 저장하기위한 변수.

    public final int CAMERA_CODE = 1000;
    public final int GALLERY_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        checkDangerousPermissions();


        btn_join = findViewById(R.id.btn_join);
        btn_cancel = findViewById(R.id.btn_cancel);
        edt_email = findViewById(R.id.edt_email);
        edt_pw = findViewById(R.id.edt_pw);
        edt_name = findViewById(R.id.edt_name);
        imgv_profile = findViewById(R.id.imgv_profile);

        //회원가입 버튼
        btn_join.setOnClickListener(v -> {
            MemberVO vo = new MemberVO();
            vo.setEmail(edt_email.getText()+"");
            vo.setGender("여");
            vo.setName(edt_name.getText()+"");
            vo.setPw(edt_pw.getText()+"");

            new CommonMethod().
                    setParams("param" , vo).sendPostFile("join.me",img_path, (isResult, data) -> {

                    });
        });

        //취소버튼
        btn_cancel.setOnClickListener(v -> {
            finish(); // 종료
        });

        imgv_profile.setOnClickListener(v -> {
            // 카메라로 사진을 업데이트 할건지 ( 실시간으로 사진을 찍기 ) => provider
            // 갤러리로 사진을 찍어놓은걸 사용할건지 ( 저장된것을 사용 ) => fakepath => realpath (cursor)
            showDialog();
        });
    }

    String[] dialog_item = {"카메라" , "갤러리"};
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(" 방법 선택 ")
                .setSingleChoiceItems(dialog_item, -1, (dialog, i) -> {
                    if (dialog_item[i].equals("카메라")) {
                        //카메라를 선택함
                        cameraMethod();

                    } else {
                        //갤러리 처리
                        galleryMethod();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void galleryMethod(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"select picture"), GALLERY_CODE);
        // onActivityResult GALLERY_CODE<-코드가 나오면 갤러리 액티비티 종료시점을 알수있음.
    }

    public void cameraMethod(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new CommonMethod().createFile(this);// 임시 파일 만들어오기 <- 프로바이더사용시 필요함.
        img_path = file.getAbsolutePath();
        if(file != null){
            Uri imgUri = FileProvider.getUriForFile(this ,
                    getPackageName() +".fileprovider" , file);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){ //API24부터 Provider를 이용하게바뀜
                intent.putExtra(MediaStore.EXTRA_OUTPUT , imgUri);
            }else{
                intent.putExtra(MediaStore.EXTRA_OUTPUT , Uri.fromFile(file));//24이전에는 파일만든것만보내면됐음.
            }
        }
        //startActivity(intent); ==X 결과를 받아와야할떄는 startActivityForResult
        // startActivityForResult로 액티비티가 실행이되고 종료가되면 하나의 메소드인 onActivityResult가 결과를
        //다받게됨. => 이때 어떤 액티비티가 종료 되었는지 구분하기위한 코드== RequestCode
        startActivityForResult(intent,CAMERA_CODE);
    }

    // 어떤 인텐트로 startActivityForResult를 실행을 하든 그 결과는 무조건 ↓
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_CODE && resultCode == RESULT_OK){
            // img_path라는 변수에는 이미지 경로가 담겨있게됨 => 임시파일이 용량을 가지고 실제 이미지파일로됨.
            Glide.with(this).load(img_path).into(imgv_profile);
            imgv_profile.setScaleType(ImageView.ScaleType.FIT_XY);
        }else if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            Log.d("로그", "onActivityResult: " + data.getData());
            Log.d("로그", "onActivityResult: " + data.getData().getPath());
            img_path = new CommonMethod().getRealPath(data.getData() , this);//가짜 URI주소로 실제 물리적인 사진파일 위치를 받아옴.
            Glide.with(this).load(img_path).into(imgv_profile);
            //storage<=실제 물리적 주소. ↑ 갤러리가 끝나고나서 우리에게 제공되는정보가
            //실제 물리적 주소일지??
        }

    }




    // 카메라로 찍은 사진을 우리가 만든 임시파일로 가져오기위한 처리


    // 권한레벨 - 낮음 : 인터넷 - 사용하겠다고 메니페스트에 명시만하면 OK
    // 권한레벨 - 중간 : 유튜브 - 사용하겠다고 메니페스트에 명시 후 queries 로 재명시 해줘야함.
    // 권한레벨 - 높음 : 위치 , 카메라 , 갤러리(파일저장소) - 사용하겠다고 메니페스트에 명시 후 , 쿼리스로도 명시 후 사용자 동의.
    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            for (int i = 0 ; i< permissions.length ; i++){
                if ( grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Log.d("로그", "권한 승인 됨: " + permissions[i]);
                }else{
                    Log.d("로그", "권한 승인 안됨: " + permissions[i]);
                }
            }
        }
    }



}