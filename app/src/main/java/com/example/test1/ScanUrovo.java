package com.example.test1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ubx.usdk.USDKManager;
import com.ubx.usdk.rfid.RfidManager;
import com.ubx.usdk.rfid.aidl.IRfidCallback;
import com.ubx.usdk.rfid.aidl.RfidDate;
import com.ubx.usdk.util.SoundTool;

import java.util.HashMap;
import java.util.List;

public class ScanUrovo extends Activity implements View.OnClickListener {
    //UROVO
    public static final String TAG = "usdk";
    public  boolean RFID_INIT_STATUS = false;
    private HashMap<String, TagScan> mapData;
    public List<String> mDataParents;
    public List<TagScan> tagScanSpinner;
    //private List<Fragment> fragments ;
    private List<TagScan> data;

    private int tagTotal = 0;

    public RfidManager mRfidManager;
    public int readerType = 0;
    private ScanCallback callback  ;


    private boolean booleanbBackScan;
    private boolean blSingleScan;                       //Параметр сканирования одной метки

    private Bundle savedBundle;
    private Context сurContext;
    private Button scanStartBtn; //,btnStartL,btnConnect,btnDisConnect;


    private int mCount;
    private String[] mMarks = new String[100];

    private void initRfidUrovo(Context  сurContext ) {
        // 在异步回调中拿到RFID实例 - Получите экземпляр rfid в асинхронном обратном вызове
        USDKManager.getInstance().init( сurContext,new USDKManager.InitListener() {
            @Override
            public void onStatus(USDKManager.STATUS status) {
                String sTemp = "Поднесите сканер к метке RFID..."; ///""Type: "+uhfModuleInfo.moduleType+"\n";
                TextView tvRes = (TextView) findViewById(R.id.tvScanVer);

                if ( status == USDKManager.STATUS.SUCCESS) {
                    sTemp = "initRfid()  success.";
                    Log.d(TAG, sTemp);

                    mRfidManager =   USDKManager.getInstance().getRfidManager();
                    //((TagScanFragment)fragments.get(0)).setCallback();
                    tvRes.setText( "ТСД RFID Urovo ON:" +"\n"+ sTemp );

                    //mRfidManager.setTrigger( true );
                    mRfidManager.setOutputPower((byte) 25);

                    RfidDate mRfidDate = mRfidManager.getFrequencyRegion();
                    //btRegion	byte	Spectrum regulation(0x01:FCC, 0x02:ETSI, 0x03:CHN).
                    int res=mRfidManager.setFrequencyRegion((byte)2,(byte)865,(byte)868);

                    //Gets the serial number
                    sTemp = " Rfid s/n: "+ mRfidManager.getDeviceId();
                    Log.d(TAG, sTemp);
                    tvRes.setText( "ТСД RFID Urovo ON:" +"\n"+ sTemp );

                    //readerType =  mRfidManager.getReaderType();//80为短距，其他为长距
                    //80：the mode of short distance
                    //others：the mode of long distance mode

                    if (blSingleScan)
                        ReadOneTagUrovo();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(сurContext,"RFID ON",Toast.LENGTH_LONG).show();
                            //Toast.makeText(сurContext,"module："+readerType,Toast.LENGTH_LONG).show();
                        }
                    });

                    Log.d(TAG, "initRfid: GetReaderType() = " +readerType );
                }else {
                    sTemp = "initRfid  fail.";
                    Log.d(TAG, sTemp);
                    tvRes.setText( "ТСД RFID Urovo OFF:" +"\n"+ sTemp );
                }

            }
        });
    }

    private void ReadOneTagUrovo() {
        if (mRfidManager == null)
            return;
        if (mRfidManager.isConnected()==false)
            return;
        TextView tvRes = (TextView) findViewById(R.id.tvScanVer);
        try {
            String epc = mRfidManager.readTagOnce( (byte) 0, (byte) 0);
            if (epc!=null) {
                tvRes.setText("Scan tag: \n" + epc);
                Toast.makeText(getApplicationContext(), "Обнаружена метка: " + epc, Toast.LENGTH_LONG).show();
                SoundTool.getInstance(сurContext).playBeep(1);

                mCount = 1;
                mMarks[mCount] = epc;
                sendMessage1С("", epc);

//            Intent intent = new Intent();
//            intent.putExtra("RFID", epc);
//            setResult( RESULT_OK, intent);
            }
            else {
                tvRes.setText("No tag data...");
                SoundTool.getInstance(сurContext).playBeep(2);
            }
        } catch (Exception e) {
            tvRes.setText("Error read RFID UROVO...");
        }


    }

    private void ReadTagUrovo() {
        String epc="";
        byte btMemBank=1;   //btMemBank	byte	Tag memory bank(0x00:RESERVED, 0x01:EPC, 0x02:TID, 0x03:USER)
        byte btWordAdd=0;   //btWordAdd	Byte[	Read start address,please see the tag's spec for more information.
        byte btWordCnt=5;   //Read data length,Data length in WORD(16bits) unit. Please see the tag's spec for more information.

        int startAddr = 0;//starting address from block 0th
        int blkcnt = 5 ;// read two blocks, that is to read 4 bytes.
        String hexAccesspasswd = "00000000"; //$ "00000001";//access password（hexadecimal string）, if no access password entered, set null.

        //byte[] rdata = mUHFMgr.GetTagData(bank, startAddr, blkcnt, null); //sHexPasswd);
        TextView tvRes = (TextView) findViewById(R.id.tvScanVer);

        if (mRfidManager.isConnected()) {
            //"00000000".getBytes());

            epc = mRfidManager.readTagOnce( (byte) 0, (byte) 0);
            //epc = mRfidManager.readTag(epc, btMemBank, btWordAdd, btWordCnt, "".getBytes());

//            byte cnt = Integer.valueOf(manageCntEdit.getText().toString()).byteValue();
//            byte address = Integer.valueOf(manageAddressEdit.getText().toString()).byteValue();
//            String strPwd = managePasswordEdit.getText().toString();
//            byte[] pwd = hexStringToBytes(strPwd);
//            //Log.d(TAG, "initEvents: cnt = " + cnt + ", address = " + address +", strPwd = " + Arrays.toString(hexStringToBytes(strPwd))+", pwd" + Arrays.toString(pwd));
//            //formInfo = new ManageFormInfo(cnt, address, (byte) btMemBank, pwd, null);
//            String dataRead = mActivity.mRfidManager.readTag(mEpc, (byte) btMemBank, address, cnt, pwd);


            if (epc!=null) {
                tvRes.setText("Scan tag: \n" + epc);
                Toast.makeText(getApplicationContext(), "Обнаружена метка: " + epc, Toast.LENGTH_LONG).show();
                SoundTool.getInstance(сurContext).playBeep(1);

                Intent intent = new Intent();
                intent.putExtra("RFID", epc);
                setResult( RESULT_OK, intent);
            }
            else {
                tvRes.setText("No tag data...");
                SoundTool.getInstance(сurContext).playBeep(2);
            }
        }
        else
            tvRes.setText("Not connected...");

    }

    @Override
    public void onStart() {
        super.onStart();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRfidManager!=null) {
                    Log.v(TAG,"--- getFirmwareVersion()   ----");
                    RFID_INIT_STATUS = true;
                    String firmware =  mRfidManager.getFirmwareVersion();
                    //textFirmware.setText(getString(R.string.firmware)+firmware);
                }else {
                    Log.v(TAG,"onStart()  --- getFirmwareVersion()   ----  mActivity.mRfidManager == null");
                }
            }
        }, 5000);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnInitRFID:
                initRfidUrovo( сurContext );
                break;

            case R.id.btnStartScan:
                ReadOneTagUrovo();
                mRfidManager.startInventory((byte) 1);
                break;
            case R.id.btnStopScan:
                returnMarks();
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_scan_ver);
        savedBundle = savedInstanceState;
        сurContext = getApplicationContext();   //BaseApplication.getContext()

        //Получим переданную извне параметр SingleScan
        Intent InIntent = getIntent();
        blSingleScan = InIntent.getBooleanExtra("SingleScan", false);

        Button btnInitRFID = (Button) findViewById(R.id.btnInitRFID);
        btnInitRFID.setOnClickListener(this);

        Button btnStartScan = (Button) findViewById(R.id.btnStartScan);
        btnStartScan.setOnClickListener(this);

        Button btnStoptScan = (Button) findViewById(R.id.btnStopScan);
        btnStoptScan.setOnClickListener(this);

        TextView tvRes = (TextView) findViewById(R.id.tvScanVer);
        tvRes.setText("UROVO");

        //Инициализируем модуль RFID и SoundTool
        SoundTool.getInstance( сurContext );
        initRfidUrovo( сurContext );

        scanStartBtn = findViewById(R.id.scan_start_btn);
        scanStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {

                    if (RFID_INIT_STATUS) {

                        if (scanStartBtn.getText().equals("Inventory")) {
                            setCallback();
                            scanStartBtn.setText("Stop Inventory");
                            setScanStatus(true);
                        } else {
                            scanStartBtn.setText("Inventory");
                            setScanStatus(false);
                        }
                    }else {
                        Log.d(TAG, "scanStartBtn  RFID  Not initialized"  );
                        Toast.makeText(сurContext,"RFID Not initialized",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private void setScanStatus(boolean isScan) {

        if (isScan) {
            tagTotal = 0;
//            if (mapData!=null){
//                mapData.clear();
//            }
//            if (mActivity.mDataParents != null){
//                mActivity.mDataParents.clear();
//            }
//            if (mActivity.tagScanSpinner != null){
//                mActivity.tagScanSpinner.clear();
//            }
//            if (data!=null) {
//                data.clear();
//                scanListAdapterRv.setData(data);
//            }

            Log.v(TAG,"--- startInventory()   ----");
            //handlerUpdateUI();
            mRfidManager.startInventory( (byte) 0);
            //Для инвентаризации рекомендуется использовать небольшое количество этикеток：0；
            // Инвентарная этикетка превышает 100-200 建议使用：1.
        } else {
            Log.v(TAG,"--- stopInventory()   ----");
            mRfidManager.stopInventory();
            //handlerStopUI();
        }
    }

    private void handlerUpdateUI(){
//        if (mHandler!=null){
//            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_UI,1000);
//        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        SoundTool.getInstance(сurContext).release();
/*
        RFID_INIT_STATUS = false;
        if (mRfidManager != null) {
            mRfidManager.disConnect();
            mRfidManager.release();

            Log.d(TAG, "onDestroyView: rfid close");
//            System.exit(0);
        }
*/
    }

    private void sendMessage1С(String title,String text) {
        Intent intent = new Intent();
        intent.setAction("ru.w0rm.action.ScanCode");
        intent.putExtra("text", text); //Основной текст сообщения
        intent.putExtra("base", "");
        intent.putExtra("title", title); //Будем использовать для определения действия
        sendBroadcast(intent);
    }

    public void startScan(View view)  {
//        UHFReader.READER_STATE er3 = mUHFMgr.startTagInventory();
//        if( er3 != UHFReader.READER_STATE. OK_ERR) //success
//        {
//            //fail start inventory
//            TextView tvRes = (TextView) findViewById(R.id.tvScanVer);
//            tvRes.setText( "RFID Error start inventory" );
//            return;
//        }
//        //Register and listen
//        IntentFilter iFilter = new IntentFilter("nlscan.intent.action.uhf.ACTION_RESULT");
//        registerReceiver(mResultReceiver, iFilter);
    }

    public void stopScan(View view) {
        //unregisterReceiver(mResultReceiver);
        returnMarks();
    }

    public void returnMarks() {
        if (mRfidManager == null)
            return;

        //mRfidManager.stopInventory();

        String AllMarks="";
        for (int i = 1; i <= mCount; i++)
            AllMarks = AllMarks +  mMarks[i] + ";" ;

        Intent intent = new Intent();
        intent.putExtra("RFID", AllMarks);
        setResult( RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //finish();

        //super.onActivityResult(requestCode, resultCode, data);
        if (1 == 1) {
            //Toast.makeText(context, "Обнаружена метка: " +Mark, Toast.LENGTH_LONG).show();
            return;
        }

        if (data == null) {return;}
        String Mark = data.getStringExtra("RFID");

        Intent intent = new Intent();
        intent.putExtra("RFID", Mark);
        setResult(RESULT_OK, intent);
        finish();

        //tvName.setText("Ваше имя:  " + name);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        int res;
//        res = event.getKeyCode();
//        Toast.makeText(getApplicationContext(), "Кнопка : " + String.valueOf(res), Toast.LENGTH_LONG).show();

        if(event.getKeyCode() == 523 &&  event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0){
            //TODO 按下
            ReadOneTagUrovo();
            return true;
        }else if (event.getKeyCode() == 523 &&  event.getAction() == KeyEvent.ACTION_UP && event.getRepeatCount() == 0){
            //TODO 抬起

            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    class Callback implements IRfidCallback {

        @Override
        public void onInventoryTag(String EPC, String TID, String strRSSI) {
            final String s2 = EPC.replace(" ","");
            Log.e(TAG, "onInventoryTag: "+EPC );
            //temp.add(new TempBean(EPC,strRSSI));

            TextView tvRes = (TextView) findViewById(R.id.tvScanVer);
            tvRes.setText("Scan tag: \n" + EPC);
            Toast.makeText(getApplicationContext(), "Обнаружена метка: " + EPC, Toast.LENGTH_LONG).show();
            SoundTool.getInstance(сurContext).playBeep(1);

            Intent intent = new Intent();
            intent.putExtra("RFID", EPC);
            setResult( RESULT_OK, intent);

        }

        @Override
        public void onInventoryTagEnd()  {
        }

    }

    private void notiyDatas(final String epc, final String TID, final String strRSSI){
        String mapContainStr = null;
//        if (!TextUtils.isEmpty(TID)){
//            mapContainStr = TID;
//        }else {
//            mapContainStr = s2;
//        }

        //mapContainStr = s2;
        //final String mapContainStrFinal = mapContainStr;

        Log.d(TAG, "onInventoryTag: EPC: " + epc);
        SoundTool.getInstance(сurContext).playBeep(1);

        //getActivity().
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                {

                    Toast.makeText(сurContext, "Обнаружена метка: " + epc, Toast.LENGTH_LONG).show();

                    TextView tvRes = (TextView) findViewById(R.id.tvScanVer);
                    tvRes.setText("Scan tag: \n" + epc);

                    mCount = 1;
                    mMarks[mCount] = epc;

//                    if (mapData.containsKey(mapContainStrFinal)) {
//                        TagScan tagScan = mapData.get(mapContainStrFinal);
//                        tagScan.setCount(mapData.get(mapContainStrFinal).getCount() + 1);
////                    tagScan.setTid(TID);
//                        tagScan.setRssi(strRSSI);
//                        mapData.put(mapContainStrFinal, tagScan);
//                    } else {
//                        mDataParents.add(s2);
//
//                        TagScan tagScan = new TagScan(s2, TID,strRSSI, 1);
//                        mapData.put(mapContainStrFinal, tagScan);
//                        tagScanSpinner.add(tagScan);
//                    }

                    //scanTotalText.setText(++tagTotal + "");

                    //Toast.makeText(curContext, "Обнаружена метка: " +Mark, Toast.LENGTH_LONG).show();

                    //long nowTime = System.currentTimeMillis();
//                    if ((nowTime - time)>1000){
//                        data = new ArrayList<>(mapData.values());
//                        Toast.makeText(сurContext, "Обнаружена метка: "
//                                +  Arrays.toString(data.toArray()), Toast.LENGTH_LONG).show();
//
//                        time = nowTime;
//                        data = new ArrayList<>(mapData.values());
//                        Log.d(TAG, "onInventoryTag: data = " + Arrays.toString(data.toArray()));
//                        scanListAdapterRv.setData(data);
//                        scanCountText.setText(mapData.keySet().size() + "");
//
//                    }


                }
            }
        });
    }

    class ScanCallback implements IRfidCallback  {

        @Override
        public void onInventoryTag(String EPC, final String TID, final String strRSSI) {

            notiyDatas(EPC,TID,strRSSI);

        }

        /**
         * 盘存结束回调(Inventory Command Operate End)
         */
        @Override
        public void onInventoryTagEnd()  {
            Log.d(TAG, "onInventoryTag()");
        }
    }

    public void setCallback(){
        if (mRfidManager!=null) {

            if (callback == null){
                callback = new ScanCallback();
            }
            mRfidManager.registerCallback(callback);
        }
    }


}