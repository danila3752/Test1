package com.example.test1;

import android.app.Activity;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.ubx.usdk.USDKManager;
import com.ubx.usdk.rfid.RfidManager;
import com.ubx.usdk.rfid.aidl.IRfidCallback;
import com.ubx.usdk.rfid.aidl.RfidDate;
import com.ubx.usdk.util.SoundTool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

//implements View.OnClickListener
public class ScanUrovo {
    //UROVO
    public static final String TAG = "usdk";
    public  boolean RFID_INIT_STATUS = false;
    private HashMap<String, TagScan> mapData;
    public List<String> mDataParents;
    public List<TagScan> tagScanSpinner;
    //private List<Fragment> fragments ;
    private List<TagScan> data;
    private int tagTotal = 0;
    public List<String> epcList ;
    public RfidManager mRfidManager;
    public int readerType = 0;
    private ScanCallback callback  ;


    private boolean booleanbBackScan;
    private boolean blSingleScan;                       //Параметр сканирования одной метки
    public Context context;
    private Bundle savedBundle;
    private Button scanStartBtn; //,btnStartL,btnConnect,btnDisConnect;


    private int mCount;
    private String[] mMarks = new String[100];

    void initRfidUrovo(Context сurContext) {
        // 在异步回调中拿到RFID实例 - Получите экземпляр rfid в асинхронном обратном вызове
        USDKManager.getInstance().init( сurContext,new USDKManager.InitListener() {
            @Override
            public void onStatus(USDKManager.STATUS status) {

                SoundTool.getInstance( сurContext );
                String sTemp = "Поднесите сканер к метке RFID..."; ///""Type: "+uhfModuleInfo.moduleType+"\n";

                if ( status == USDKManager.STATUS.SUCCESS) {
                    sTemp = "initRfid()  success.";
                    Log.d(TAG, sTemp);

                    mRfidManager =   USDKManager.getInstance().getRfidManager();
                    //((TagScanFragment)fragments.get(0)).setCallback();

                    //mRfidManager.setTrigger( true );
                    mRfidManager.setOutputPower((byte) 25);

                    RfidDate mRfidDate = mRfidManager.getFrequencyRegion();
                    //btRegion	byte	Spectrum regulation(0x01:FCC, 0x02:ETSI, 0x03:CHN).
                    int res=mRfidManager.setFrequencyRegion((byte)2,(byte)865,(byte)868);

                    //Gets the serial number
                    sTemp = " Rfid s/n: "+ mRfidManager.getDeviceId();
                    Log.d(TAG, sTemp);

                    //readerType =  mRfidManager.getReaderType();//80为短距，其他为长距
                    //80：the mode of short distance
                    //others：the mode of long distance mode

//                    if (blSingleScan)
//                        ReadOneTagUrovo();
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
                    //Toast.makeText(сurContext,"RFID ON",Toast.LENGTH_LONG).show();
                    //   ReadOneTagUrovo(сurContext);
                    Log.i("info", "RFID ON");
//                            //Toast.makeText(сurContext,"module："+readerType,Toast.LENGTH_LONG).show();
//                        }
//                    });

                    Log.d(TAG, "initRfid: GetReaderType() = " +readerType );
                }else {
                    sTemp = "initRfid  fail.";
                    Log.d(TAG, sTemp);
                }

            }
        });
    }


    String ReadOneTagUrovo(Context сurContext) {
        if (mRfidManager == null) {
            Log.e("error", "mRfidManager is null");
            return "error";
        }
        if (mRfidManager.isConnected()==false) {
            Log.e("error", "mRfidManager is not connected");
            return "error";
        }
        String epc = "";
        try {
            epc = mRfidManager.readTagOnce( (byte) 0, (byte) 0);
            if (epc!=null) {
                SoundTool.getInstance(сurContext).playBeep(1);
                mCount = 1;
                mMarks[mCount] = epc;
            }
            else {
                //   tvRes.setText("No tag data...");
                SoundTool.getInstance(сurContext).playBeep(2);
                Log.i("info", "No tag data...");
            }
        } catch (Exception e) {
            //  tvRes.setText("Error read RFID UROVO...");
            Log.e("error", e.getMessage());
        }

        return epc;
    }

    public List<String> ReadInventory(Context curContext,boolean isRead){
        if(isRead) {
            epcList = new ArrayList<>(); // Создаем список для хранения EPC мето
            context = curContext;
            setCallback();
            tagTotal = 0;
            Log.v(TAG, "--- startInventory()   ----");
            mRfidManager.startInventory((byte) 0);
        }else {
            mRfidManager.stopInventory();
            return epcList;
        }
        return epcList;
    }


    private void notiyDatas(final String epc, final String TID, final String strRSSI){
        String mapContainStr = null;

        SoundTool.getInstance(context).playBeep(1);
        Log.i("info", "Обнаружена метка: " + epc);
        mCount = 1;
        mMarks[mCount] = epc;
        epcList.add(epc);
        Log.i("TAG", "epcList: " + epcList);
    }
    class ScanCallback implements IRfidCallback  {

        @Override
        public void onInventoryTag(String EPC, final String TID, final String strRSSI) {

            notiyDatas(EPC,TID,strRSSI);

        }

        @Override
        public void onInventoryTagEnd()  {
            Log.d(TAG, "onInventoryTag()");
        }
    }

    public List<String> setCallback(){
        if (mRfidManager!=null) {

            if (callback == null){
                callback = new ScanCallback();
            }
            mRfidManager.registerCallback(callback);
        }
        return epcList;
    }


}



