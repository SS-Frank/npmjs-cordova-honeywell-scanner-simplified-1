package com.icsfl.rfsmart.honeywell;

import android.content.Context;
import android.util.Log;

import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReaderInfo;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.ScannerNotClaimedException;

public class HoneywellScannerPlugin extends CordovaPlugin implements BarcodeReader.BarcodeListener {
    private static final String TAG = "HoneywellScanner";
    private static BarcodeReader barcodeReader;
    private AidcManager manager;
    private CallbackContext callbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {

        super.initialize(cordova, webView);

        Context context = cordova.getActivity().getApplicationContext();
        AidcManager.create(context, new CreatedCallback() {
            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                barcodeReader = manager.createBarcodeReader();

                if (barcodeReader != null) {
                    barcodeReader.addBarcodeListener(HoneywellScannerPlugin.this);
                    try {
                        barcodeReader.claim();
                    } catch (ScannerUnavailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext)
    throws JSONException {
        if (action.equals("softwareTriggerStart")) {
            if (barcodeReader != null) {
                try {
                    barcodeReader.softwareTrigger(true);
                } catch (ScannerNotClaimedException e) {
                    e.printStackTrace();
                    NotifyError("ScannerNotClaimedException");
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                 NotifyError("ScannerUnavailableException");
                }
            }
        } else if (action.equals("softwareTriggerStop")) {
            if (barcodeReader != null) {
                try {
                    barcodeReader.softwareTrigger(false);
                } catch (ScannerNotClaimedException e) {
                    e.printStackTrace();
                    NotifyError("ScannerNotClaimedException");
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                    NotifyError("ScannerUnavailableException");
                }
            }
        } else if (action.equals("disableLaunchBrowser")) {
            if (barcodeReader != null) {
                try {
                    barcodeReader.setProperty(BarcodeReader.PROPERTY_DATA_PROCESSOR_LAUNCH_BROWSER, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    NotifyError("PROPERTY_DATA_PROCESSOR_LAUNCH_BROWSER-EXCEPTION");
                }
            }
        } else if (action.equals("enableLaunchBrowser")) {
            if (barcodeReader != null) {
                try {
                    barcodeReader.setProperty(BarcodeReader.PROPERTY_DATA_PROCESSOR_LAUNCH_BROWSER, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    NotifyError("PROPERTY_DATA_PROCESSOR_LAUNCH_BROWSER-EXCEPTION");
                }
            }
        } else if (action.equals("listen") ) {
            this.callbackContext = callbackContext;
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
            this.callbackContext.sendPluginResult(result);
            if (barcodeReader != null) {
                try {
                   barcodeReader.softwareTrigger(false);
                } catch (ScannerNotClaimedException e) {
                    e.printStackTrace();
                    NotifyError("ScannerNotClaimedException2");
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                    NotifyError("ScannerUnavailableException2");
                }
            }
        } else if (action.equals("claim")) {
            if (barcodeReader != null) {
                try {
                    barcodeReader.claim();
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                    NotifyError("Scanner unavailable");
                }
            }
            if (barcodeReader != null) {
                try {
                   barcodeReader.softwareTrigger(false);
                } catch (ScannerNotClaimedException e) {
                    e.printStackTrace();
                    NotifyError("ScannerNotClaimedException2");
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                     NotifyError("ScannerUnavailableException2");
                }
            }
        } else if (action.equals("release")) {
            if (barcodeReader != null) {
                barcodeReader.release();
            }
            if (barcodeReader != null) {
                try {
                   barcodeReader.softwareTrigger(false);
                } catch (ScannerNotClaimedException e) {
                    e.printStackTrace();
                    NotifyError("ScannerNotClaimedException2");
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                     NotifyError("ScannerUnavailableException2");
                }
            }
         } else if ("listConnectedBarcodeDevices".equals(action)) {
            // Return the list of connected barcode Devices

            JSONObject obj = new JSONObject();
            JSONArray arrayDevices = new JSONArray();
            JSONObject objReader = new JSONObject();

            if (manager == null) {
                this.callbackContext = callbackContext;
                obj.put("devices", arrayDevices);
                PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
                result.setKeepCallback(true);
                this.callbackContext.sendPluginResult(result);
            } else {
                this.cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        List<BarcodeReaderInfo> list = manager.listConnectedBarcodeDevices();

                        try {
                            int index = 0;

                            for (BarcodeReaderInfo value : list) {
//                                JSONObject objReader = new JSONObject();
                                objReader.put("name", value.getFriendlyName());

                                objReader.put("id", value.getName());

                                arrayDevices.put(objReader);
                                index++;
                            }

                            obj.put("devices", arrayDevices);
                            callbackContext.success(obj);
                        } catch (Exception x) {
                            callbackContext.error(x.getMessage());
                        }
                    }
                });
            }
            return true;
         }
        return true;
    }

    @Override
    public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {
        String barcodeData = barcodeReadEvent.getBarcodeData();
        if (this.callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, barcodeData);
            result.setKeepCallback(true);
            this.callbackContext.sendPluginResult(result);
        }
        if (barcodeReader != null) {
            try {
                barcodeReader.softwareTrigger(false);
            } catch (ScannerNotClaimedException e) {
                e.printStackTrace();
                NotifyError("ScannerNotClaimedException2");
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                    NotifyError("ScannerUnavailableException2");
            }
        }
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        NotifyError("Scan has failed");
        if (barcodeReader != null) {
            try {
                barcodeReader.softwareTrigger(false);
            } catch (ScannerNotClaimedException e) {
                e.printStackTrace();
                NotifyError("ScannerNotClaimedException2");
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                    NotifyError("ScannerUnavailableException2");
            }
        }
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                NotifyError("The scanner is unavailable");
            }
        }
        if (barcodeReader != null) {
            try {
                barcodeReader.softwareTrigger(false);
            } catch (ScannerNotClaimedException e) {
                e.printStackTrace();
                NotifyError("ScannerNotClaimedException2");
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                    NotifyError("ScannerUnavailableException2");
            }
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        if (barcodeReader != null) {
            barcodeReader.release();
        }
        if (barcodeReader != null) {
            try {
                barcodeReader.softwareTrigger(false);
            } catch (ScannerNotClaimedException e) {
                e.printStackTrace();
                NotifyError("ScannerNotClaimedException2");
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                    NotifyError("ScannerUnavailableException2");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (barcodeReader != null) {
            barcodeReader.close();
            barcodeReader = null;
        }

        if (manager != null) {
            manager.close();
        }
    }

    private void NotifyError(String error) {
        if (this.callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, error);
            result.setKeepCallback(true);
            this.callbackContext.sendPluginResult(result);
        }
    }
}