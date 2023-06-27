package com.example.test1;

import static com.ubx.usdk.util.ConvertUtils.hexStringToBytes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ubx.usdk.USDKManager;
import com.ubx.usdk.rfid.RfidManager;
import com.ubx.usdk.util.SoundTool;

public class WriteUrovo {
    public static final String TAG = "usdk";
    private String mEpc = "";

    public RfidManager mRfidManager;
    public int readerType = 0;

    private Context сurContext;
    EditText etTag;
    Button btnWriteTag;

    public void initRfidUrovo(Context сurContext) {
        // 在异步回调中拿到RFID实例 - Получите экземпляр rfid в асинхронном обратном вызове
        USDKManager.getInstance().init(сurContext, new USDKManager.InitListener() {
            @Override
            public void onStatus(USDKManager.STATUS status) {
                SoundTool.getInstance( сurContext );
                String sTemp = "Поднесите сканер к метке RFID..."; ///""Type: "+uhfModuleInfo.moduleType+"\n";

                if (status == USDKManager.STATUS.SUCCESS) {
                    sTemp = "initRfid()  success.";
                    Log.d(TAG, sTemp);

                    mRfidManager = USDKManager.getInstance().getRfidManager();

                    mRfidManager.setOutputPower((byte) 30);


                    sTemp = " Rfid s/n: " + mRfidManager.getDeviceId();
                    Log.d(TAG, sTemp);



                    Log.d(TAG, "initRfid: GetReaderType() = " + readerType);
                } else {
                    sTemp = "initRfid  fail.";
                    Log.d(TAG, sTemp);
                    Toast.makeText(сurContext, "ТСД RFID Urovo OFF:" + "\n" + sTemp, Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private String getPC(String epc) {
        String pc = "0000";
        int len = epc.length() / 4;
        int b = len << 11;
        String aHex = Integer.toHexString(b);
        if (aHex.length() == 3) {
            pc = "0" + aHex;
        } else {
            pc = aHex;
        }
        return pc;
    }

    public boolean WriteRFID(String RFIDtag) {
        if (mRfidManager == null)
            return false;
        if (mRfidManager.isConnected() == false)
            return false;


        String epc = RFIDtag;
        String pc = getPC(epc);
        String EPC_Actual = pc + epc;//Добавьте значение PC к записываемому контенту
        int EPC_Actual_Length = EPC_Actual.length() / 4;//Снова вычислите длину PC +EPC (фактическая длина, которая должна быть записана)
        byte cnt_Actual = (byte) EPC_Actual_Length;
        byte add_Actual = (byte) 1;//Чтобы изменить длину содержимого EPC, вам нужно записать с адреса 1
        byte[] btAryData = hexStringToBytes(EPC_Actual);//Фактический письменный контент равен значению PC+EPC

        byte btMemBank = 0x01;              //     * (0x00:RESERVED, 0x01:EPC, 0x02:TID, 0x03:USER)

        String strPwd = "00000000";
        byte[] pwd = hexStringToBytes(strPwd);
        mEpc = mRfidManager.readTagOnce( (byte) 0, (byte) 0);
        int res = mRfidManager.writeTag(mEpc, pwd, (byte) btMemBank, add_Actual, cnt_Actual, btAryData);


        if (res == 0) {
            //success write......
            String ResStr = "Метка записана: " + RFIDtag;
            return true;
        } else {
            Toast.makeText(сurContext, "Ошибка записи: " + RFIDtag
                    + " (res : " + String.valueOf(res) + ")", Toast.LENGTH_LONG).show();
            return false;
        }

    }
}
