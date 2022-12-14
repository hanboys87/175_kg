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
        // ????????????????????? LinearLayoutManager ?????? ??????.
        recycler.setLayoutManager(new LinearLayoutManager(mContext)) ;
        ListAdapter adapter = new ListAdapter(mContext,kg,time,this) ;
        recycler.setAdapter(adapter) ;
        //startActivity(new Intent(mContext,DeviceList.class));
    }

    private void permissionCheck11() {

        // PermissionSupport.java ????????? ?????? ??????
        permission = new PermissionSupport(this, this);

        // ?????? ?????? ??? ????????? false??? ????????????
        if (!permission.checkPermission11()){
            //?????? ??????
            permission.requestPermission();
            Log.e("HAN","?????? ??????");
        }else{
            Log.e("HAN","?????? ??????");
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
////        notificationIntent.putExtra("notificationId", 0); //????????? ???
////        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
////        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap ????????? ??????
//                .setContentTitle(str)
////                .setContentText("?????????????????????.")
//                // ??? ?????? ??????????????? ????????? ???????????? ?????? ?????? ?????? ????????? ???????????? setContentText??? ?????? ????????? ?????? ?????? ???????????? ?????????
//                //.setStyle(new NotificationCompat.BigTextStyle().bigText("??? ?????? ????????? ???????????? ?????? ??????..."))
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                //.setContentIntent(pendingIntent) // ???????????? ????????????????????? ?????? ResultActivity??? ??????????????? ??????
//                .setAutoCancel(true);
//
//        //OREO API 26 ??????????????? ?????? ??????
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap ????????? Oreo ???????????? ????????? UI ?????????
//            CharSequence channelName  = "?????????????????? ??????";
//            String description = "????????? ????????? ?????? ??????";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//
//            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
//            channel.setDescription(description);
//
//            // ?????????????????? ????????? ???????????? ??????
//            assert notificationManager != null;
//            notificationManager.createNotificationChannel(channel);
//
//        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo ???????????? mipmap ???????????? ????????? Couldn't create icon: StatusBarIcon ?????????
//
//        assert notificationManager != null;
//        notificationManager.notify(1234, builder.build()); // ??????????????? ?????????????????? ????????????
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
                                            // TODO: ????????? ???????????????
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

            // ????????????????????? LinearLayoutManager ?????? ??????.
            kg.add(full);
            time.add(getTime);

            recycler.setLayoutManager(new LinearLayoutManager(mContext)) ;
            ListAdapter adapter = new ListAdapter(mContext,kg,time,this) ;
            recycler.setAdapter(adapter) ;

        }
    }


    // ?????? ??????
    private void permissionCheck() {

        // PermissionSupport.java ????????? ?????? ??????
        permission = new PermissionSupport(this, this);

        // ?????? ?????? ??? ????????? false??? ????????????
        if (!permission.checkPermission()){
            //?????? ??????
            permission.requestPermission();
            Log.e("HAN","?????? ??????");
        }else{
            //TODO: ???????????? ??????
            Log.e("HAN","?????? ??????");
            //startActivity(new Intent(Splash.this,DeviceList.class));
        }
    }

    // Request Permission??? ?????? ?????? ??? ?????????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //???????????? ????????? false??? ??????????????? (???????????? ?????? ?????? ??????)
        if (!permission.permissionResult(requestCode, permissions, grantResults)) {
            // ?????? permission ??????
            permission.requestPermission();
        }else{
            //TODO: ???????????? ??????
            //startActivity(new Intent(Splash.this,DeviceList.class));
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}