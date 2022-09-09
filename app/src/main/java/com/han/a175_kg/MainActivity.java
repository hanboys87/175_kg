package com.han.a175_kg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ListAdapter.Listener{

    private PermissionSupport permission;
    boolean ring_flag=true;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    // Bluetooth
    public String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBTConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Thread workerThread;
    byte[] generalBuffer;
    int generalBufferPosition;
    volatile boolean stopWorker;
    String full = "";
    //
    Context mContext;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    ArrayList<String> time = new ArrayList<>();
    ArrayList<String> kg = new ArrayList<>();

    int water=30;
    int up_temp=20,down_temp=13;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //permissionCheck();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionCheck();
        } else {
            permissionCheck11();
        }

        mContext=this;
        ButterKnife.bind(this);

        Intent newInt = getIntent();
        address = newInt.getStringExtra(DeviceList.EXTRA_ADDRESS); // MAC address of the chosen device
        Log.e("HAN","address"+address);
        if(address!=null) {
            new ConnectBT().execute(); // Connection class
            Toast.makeText(mContext,"null",Toast.LENGTH_SHORT).show();
        }


        //

        for (int i=0; i<data.getInstance(mContext).getSIZE(); i++) {
            time.add(data.getInstance(mContext).getTime(i+1));
            kg.add(data.getInstance(mContext).getKg(i+1));
        }
        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recycler.setLayoutManager(new LinearLayoutManager(mContext)) ;
        ListAdapter adapter = new ListAdapter(mContext,kg,time,this) ;
        recycler.setAdapter(adapter) ;
        //startActivity(new Intent(mContext,DeviceList.class));
    }

    private void permissionCheck11() {

        // PermissionSupport.java 클래스 객체 생성
        permission = new PermissionSupport(this, this);

        // 권한 체크 후 리턴이 false로 들어오면
        if (!permission.checkPermission11()){
            //권한 요청
            permission.requestPermission();
            Log.e("HAN","권한 실패");
        }else{
            Log.e("HAN","권한 성공");
            //startActivity(new Intent(Splash.this,DeviceList.class));
        }
    }

    @Override
    public void onItemSelected(int position) {
        Toast.makeText(mContext,"position"+position,Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.tv_connect) void click(){
        Toast.makeText(mContext,"test",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(mContext,DeviceList.class));
    }

    @OnClick(R.id.tv_test) void tv_test(){
        ReceiveModule("s50e");

    }



//
//    public void NotificationSomethings(String str) {
//
//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(),notification);
//        rt.play();
//
//        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//
////        Intent notificationIntent = new Intent(this, ResultActivity.class);
////        notificationIntent.putExtra("notificationId", 0); //전달할 값
////        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
////        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
//                .setContentTitle(str)
////                .setContentText("물이부족합니다.")
//                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
//                //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                //.setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
//                .setAutoCancel(true);
//
//        //OREO API 26 이상에서는 채널 필요
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
//            CharSequence channelName  = "노티페케이션 채널";
//            String description = "오레오 이상을 위한 것임";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//
//            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
//            channel.setDescription(description);
//
//            // 노티피케이션 채널을 시스템에 등록
//            assert notificationManager != null;
//            notificationManager.createNotificationChannel(channel);
//
//        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
//
//        assert notificationManager != null;
//        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
//
//    }


    private class ConnectBT extends AsyncTask<Void, Void, Void> { // UI thread

        private boolean connectionSuccess = true;

        @Override
        protected void onPreExecute() {
            Log.e("HAN","ConnectBT");
            progress = ProgressDialog.show(mContext, "Connecting...", "Please wait!"); // Connection loading dialog
        }

        @Override
        protected Void doInBackground(Void... devices) { // Connect with bluetooth socket
            Log.e("HAN","doInBackground");
            try {
                if (btSocket == null || !isBTConnected) { // If socket is not taken or device not connected
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = myBluetooth.getRemoteDevice(address); // Connect to the chosen MAC address
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID); // This connection is not secure (mitm attacks)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery(); // Discovery process is heavy
                    btSocket.connect();
                    Log.e("HAN","connect");
                }
            }
            catch (IOException e) {
                connectionSuccess = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) { // After doInBackground
            super.onPostExecute(result);

            if (!connectionSuccess) {

                finish();
            }
            else {
                beginListenForData();
                isBTConnected = true;
            }
            progress.dismiss();
        }
    }

    public void beginListenForData() {
        final Handler handler = new Handler(); // Interacts between this thread and UI thread
        final byte delimiter = 35; // ASCII code for (#) end of transmission
        Log.e("HAN","beginListenForData");
        stopWorker = false;
        generalBufferPosition = 0;
        generalBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                Log.e("HAN","Thread");
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = btSocket.getInputStream().available(); // Received bytes by bluetooth module
                        //Log.e("HAN","bytesAvailable: "+bytesAvailable);
                        if (bytesAvailable > 0) {
                            byte[] packet = new byte[bytesAvailable];
                            btSocket.getInputStream().read(packet);
                            //Log.e("HAN","read");
                            for (int i=0; i<bytesAvailable; i++) {
                                byte b = packet[i];
                                //if (b == delimiter) { // If found a # print on screen
                                if (true) { // If found a # print on screen
                                    byte[] arrivedBytes = new byte[generalBufferPosition];
                                    System.arraycopy(generalBuffer, 0, arrivedBytes, 0, arrivedBytes.length);
                                    final String data = new String(arrivedBytes, "US-ASCII"); // Decode from bytes to string
                                    generalBufferPosition = 0;
                                    char ch = (char)b;
                                    Log.e("HAN","data: "+ch);
                                    Log.e("HAN","data: "+b);
                                    full += ch;
                                    handler.post(new Runnable() {
                                        public void run() {
                                            // TODO: 여기가 뿌려주는곳
                                            Log.e("HAN","char: "+full);

                                            ReceiveModule(full);
                                            if(ch=='e'){
                                                full="";
                                            }
                                            //dataTW.setText(full); // Print on screen
                                        }
                                    });
                                }
                                else { // If there is no # add bytes to buffer
                                    generalBuffer[generalBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void ReceiveModule(String full){
        if(full.contains("e")){
            full=full.replace("e","");
            full=full.replace("s","");


            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd a HH:mm:ss");
            String getTime = sdf.format(date);

            int current_size = data.getInstance(mContext).getSIZE();
            current_size=current_size+1;
            data.getInstance(mContext).setSIZE(current_size);
            data.getInstance(mContext).setKg(full,current_size);
            data.getInstance(mContext).setTime(getTime,current_size);

            // 리사이클러뷰에 LinearLayoutManager 객체 지정.
            kg.add(full);
            time.add(getTime);

            recycler.setLayoutManager(new LinearLayoutManager(mContext)) ;
            ListAdapter adapter = new ListAdapter(mContext,kg,time,this) ;
            recycler.setAdapter(adapter) ;

        }
    }


    // 권한 체크
    private void permissionCheck() {

        // PermissionSupport.java 클래스 객체 생성
        permission = new PermissionSupport(this, this);

        // 권한 체크 후 리턴이 false로 들어오면
        if (!permission.checkPermission()){
            //권한 요청
            permission.requestPermission();
            Log.e("HAN","권한 실패");
        }else{
            //TODO: 블루투스 연결
            Log.e("HAN","권한 성공");
            //startActivity(new Intent(Splash.this,DeviceList.class));
        }
    }

    // Request Permission에 대한 결과 값 받아와
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //여기서도 리턴이 false로 들어온다면 (사용자가 권한 허용 거부)
        if (!permission.permissionResult(requestCode, permissions, grantResults)) {
            // 다시 permission 요청
            permission.requestPermission();
        }else{
            //TODO: 블루투스 연결
            //startActivity(new Intent(Splash.this,DeviceList.class));
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}