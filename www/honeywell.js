var execute = require("cordova/exec");

var honeywell = {
    softwareTriggerStart: function () {
        return execute(null, null, 'HoneywellScannerPlugin', 'softwareTriggerStart', []);
    },
    softwareTriggerStop: function () {
        return execute(null, null, 'HoneywellScannerPlugin', 'softwareTriggerStop', []);
    },
    disableLaunchBrowser: function () {
        return execute(null, null, 'HoneywellScannerPlugin', 'disableLaunchBrowser', []);
    },
    enableLaunchBrowser: function () {
        return execute(null, null, 'HoneywellScannerPlugin', 'enableLaunchBrowser', []);
    },
    nativeListen: function (res, err) {
        return execute(res, err, 'HoneywellScannerPlugin', 'listen', []);
    },
    listen: function (res, err) { // DEPRECATION WARNING: This will be removed in the next major release, use nativeListen
        return execute(res, err, 'HoneywellScannerPlugin', 'listen', []);
    },
    nativeRelease: function () {
        return execute(null, null, 'HoneywellScannerPlugin', 'release', []);
    },
    release: function () { // DEPRECATION WARNING: This will be removed in the next major release, use nativeRelease
        return execute(null, null, 'HoneywellScannerPlugin', 'release', []);
    },
    nativeClaim: function () {
        return execute(null, null, 'HoneywellScannerPlugin', 'claim', []);
    },
    claim: function () { // DEPRECATION WARNING: This will be removed in the next major release, use nativeRelease
        return execute(null, null, 'HoneywellScannerPlugin', 'claim', []);
    },
    listConnectedBarcodeDevices: function (res, err) {
        return execute(res, err, 'HoneywellScannerPlugin', 'listConnectedBarcodeDevices', []);
    }
};

module.exports = honeywell;
