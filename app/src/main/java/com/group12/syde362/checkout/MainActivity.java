package com.group12.syde362.checkout;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.util.Log;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;


import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;



public class MainActivity extends ActionBarActivity

    implements NavigationDrawerFragment.NavigationDrawerCallbacks,ProductFragment.OnFragmentInteractionListener,
        SingleProductFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener, SingleProductDescrFragment.OnFragmentInteractionListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    public Activity activity = this;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    private ProductFragment itemListFragment = new ProductFragment();

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AU2P2X1zPpKmOG3YQufkcq1tTO-mCF7j-s6CKz-yT_oJj2Xiadg2Q2c_ihyJZ8HA_CpsDYW1KsYU46Pq";
    private static final String CONFIG_RECEIVER_EMAIL = "terence.taehee.kim@gmail.com";


    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
                    // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Checked Out")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC and this device does not support it
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            //NFC is not enabled
            Toast.makeText(this, "Please enable NFC.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            //NFC is enabled, and its good to go
            Toast.makeText(this, "Tap an Item NFC Tag to Continue!", Toast.LENGTH_LONG).show();
        }

        handleIntent(getIntent());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //****************************************************** PAYPAL *************************
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);


    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link BaseActivity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     *
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                String scannedItemName = null;
                FragmentManager fragmentManager = getSupportFragmentManager();
                JSONObject obj = null;
                try {
                    obj = new JSONObject(result);
                    scannedItemName = obj.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //if this is a new item
                if ((getItemListFragment().findMatchingSingleProductFragment(fragmentManager.getFragments(), scannedItemName)) == null) {
                    SingleProductFragment newSingleProduct = SingleProductFragment.newInstance(result, "hi");
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,  newSingleProduct).addToBackStack("Item")
                            .commit();
                    //TextView newSingleProductTextView = (TextView) newSingleProduct.getView().findViewById(R.id.single_product_fragment_view);
                    //newSingleProductTextView.setText("Read content: " + result);
                } else { //if this item already exists
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(MainActivity.this);
                    adBuilder.setMessage("This item is already added!");
                    adBuilder.setCancelable(true);
                    final AlertDialog existsDialog = adBuilder.create();
                    existsDialog.show();
                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        public void run() {
                            existsDialog.dismiss(); // when the task active then close the dialog
                            t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                        }
                    }, 2000); // after 2 second (or 2000 miliseconds), the task will be active.

                }

            }
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (position+1 == 1){
            fragmentManager.beginTransaction()
                    .replace(R.id.container, itemListFragment, "List")
                    .commit();
        }
        else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, itemListFragment, "List")
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.e("Setting", "Setting button");
            FragmentManager fragmentManager = getSupportFragmentManager();
            SettingsFragment settingsFragment = new SettingsFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, settingsFragment).addToBackStack("Settings")
                        .commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(String id){

    }

    public void onFragmentInteraction(Uri uri){

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

    }

    public ProductFragment getItemListFragment(){
        return itemListFragment;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if (event.getAction()!=KeyEvent.ACTION_DOWN){
            return true;
        }

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            Fragment listFragment = fragmentManager.findFragmentByTag("List");
            if(listFragment.isVisible()){
                new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage("Exit CheckedOut?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //Stop the activity
                                    MainActivity.this.finish();
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();

                    return true;
            }
            Log.d("hi","j");

            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
        
    }

    public PayPalPayment getThingsToBuy(String paymentIntent, double totalPrice) {
        return new PayPalPayment(new BigDecimal(String.valueOf(totalPrice)), "CAD", "CheckedOut Total",
                paymentIntent);
    }

    public void startPayPalActivity(Double totalPrice){
        PayPalPayment thingToBuy = getThingsToBuy(PayPalPayment.PAYMENT_INTENT_SALE, totalPrice);

        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */

        Intent intent = new Intent(activity, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        //setResult(RESULT_OK, intent);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int request = requestCode;
        int result  = resultCode;
        Intent retrievedData = data;
        Log.d("Paypal", "man" );
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        System.out.println("Responseeee"+confirm);
                        Log.i("paymentExample", confirm.toJSONObject().toString());


                        JSONObject jsonObj=new JSONObject(confirm.toJSONObject().toString());

                        String paymentId=jsonObj.getJSONObject("response").getString("id");
                        System.out.println("payment id:-=="+paymentId);
                        Toast.makeText(getApplicationContext(), paymentId, Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment was submitted. Please see the docs.");
            }
        }


    }

}
