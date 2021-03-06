package rspl_so.astix.rspl_so;

/**
 * Created by Sunil on 2/16/2018.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class WebViewManageDSRActivity extends Activity
{

  DBAdapterLtFoods dbengine=new DBAdapterLtFoods(this);

    String ImageUrl;


    String distId,dsrId,outletId;
    int nid=22;
    int ntype=150;

    ImageView btn_bck;



    ProgressDialog progressDialog;

    public Timer timer;
    public	MyTimerTask myTimerTask;

    public int Retry=0;

    public WebView webView;

    public TextView txtVw_name;
    public TextView txtVw_contact;

    public String imei;

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return true;
        }
        if(keyCode==KeyEvent.KEYCODE_HOME){
            return true;
        }
        if(keyCode==KeyEvent.KEYCODE_MENU){
            return true;
        }
        if(keyCode==KeyEvent.KEYCODE_SEARCH){
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    public void customHeader()
    {
        TextView tv_heading=(TextView) findViewById(R.id.tv_heading);
        tv_heading.setText(getText(R.string.ManageDSRMsg));
        ImageView imgVw_next=(ImageView) findViewById(R.id.imgVw_next);
        imgVw_next.setVisibility(View.GONE);
        ImageView imgVw_back=(ImageView) findViewById(R.id.imgVw_back);
        imgVw_back.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = tManager.getDeviceId();


        if(CommonInfo.imei.trim().equals(null) || CommonInfo.imei.trim().equals(""))
        {
            imei = tManager.getDeviceId();
            CommonInfo.imei=imei;
        }
        else
        {
            imei= CommonInfo.imei.trim();
        }


      /*  String SONodeIdAndNodeType= dbengine.fnGetPersonNodeIDAndPersonNodeTypeForSO();

        nid=Integer.parseInt(SONodeIdAndNodeType.split(Pattern.quote("^"))[0]);
        ntype=Integer.parseInt(SONodeIdAndNodeType.split(Pattern.quote("^"))[1]);*/

        customHeader();
        /*String UserNameandContact=dbengine.fnGetUserNameContact();

        String userName=UserNameandContact.split(Pattern.quote("^"))[0];
        String ContactNo=UserNameandContact.split(Pattern.quote("^"))[1];

        txtVw_name=(TextView) findViewById(R.id.txtVw_name);
        txtVw_name.setText(userName);
        txtVw_contact=(TextView) findViewById(R.id.txtVw_contact);
        txtVw_contact.setText(ContactNo);*/

        if(Retry==0);
        {
            Intent intentData=getIntent();

            // distId=intentData.getStringExtra("Distid");
            //dsrId=intentData.getStringExtra("DSRid");
            //outletId=intentData.getStringExtra("storeId");
        }

        if(isOnline())
        {

            if (timer!=null)
            {
                timer.cancel();
                timer = null;
            }

            timer = new Timer();
            myTimerTask = new MyTimerTask();

            timer.schedule(myTimerTask,30000);

            try
            {
                progressDialog = new ProgressDialog(WebViewManageDSRActivity.this);
                progressDialog.setMessage("Please Wait...");
                progressDialog.setCancelable(false);

                dbengine.open();
                String getServerDate = dbengine.fnGetServerDate();
                dbengine.close();
                if(getServerDate.equals(""))
                {
                    long  syncTIMESTAMP = System.currentTimeMillis();
                    Date dateobj = new Date(syncTIMESTAMP);
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                    getServerDate= df.format(dateobj);
                }

                int ApplicationID= CommonInfo.Application_TypeID;

              //  http://103.20.212.194/LTACE_test/pda/frmIMEImanagement.aspx?imei=351976082609146&date=09-Feb-2018

                ImageUrl= CommonInfo.WebManageDSRUrl.trim();
                ImageUrl=ImageUrl+"?imei="+imei+"&date="+getServerDate;

                //ImageUrl=ImageUrl+"?nid="+nid+"&ntype="+ntype;

                webView=(WebView) findViewById(R.id.webView);
                webView.setWebViewClient(new MyBrowser(progressDialog));
                webView.getSettings().setLoadsImagesAutomatically(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.loadUrl(ImageUrl);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        }
        else
        {
            showNoConnAlert();
        }








    }
    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
        {
            return true;
        }
        return false;
    }
    public void showNoConnAlert()
    {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(WebViewManageDSRActivity.this);
        alertDialogNoConn.setTitle(R.string.genTermNoDataConnection);
        alertDialogNoConn.setMessage(R.string.genTermNoDataConnectionFullMsg);
        alertDialogNoConn.setCancelable(false);
        alertDialogNoConn.setNeutralButton(getText(R.string.txtOk),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();

                    }
                });
        alertDialogNoConn.setIcon(R.drawable.error_ico);
        AlertDialog alert = alertDialogNoConn.create();
        alert.show();

    }


    class MyTimerTask extends TimerTask
    {

        @Override
        public void run()
        {

            runOnUiThread(new Runnable()
            {

                @Override
                public void run()
                {

                    if(progressDialog.isShowing())
                    {
                        //  progressDialog.cancel();
                        //  webView.setVisibility(View.GONE);
                        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(WebViewManageDSRActivity.this);
                        alertDialogNoConn.setTitle("Internet issue");
                        //alertDialogNoConn.setMessage(getText(R.string.syncAlertErrMsggg));
                        alertDialogNoConn.setMessage(getText(R.string.internetslowMsggg));
                        alertDialogNoConn.setCancelable(false);
                        alertDialogNoConn.setPositiveButton("OK",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.dismiss();

                                    }
                                });
                        alertDialogNoConn.setNegativeButton("Abort/Cancle",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        progressDialog.cancel();
                                        dialog.dismiss();
                                        Retry=1;
                                        Bundle bundle=new Bundle();
                                        onCreate(bundle);

                                    }
                                });
                        alertDialogNoConn.setIcon(R.drawable.error_info_ico);
                        AlertDialog alert = alertDialogNoConn.create();
                        alert.show();

                    }



                }});
        }

    }


}


