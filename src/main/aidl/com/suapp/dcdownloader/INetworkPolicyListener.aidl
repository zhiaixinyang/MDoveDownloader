// INetworkPolicyListener.aidl
package com.suapp.dcdownloader;

// Declare any non-default types here with import statements

interface INetworkPolicyListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onUidRulesChanged(int uid, int uidRules);
    void onMeteredIfacesChanged(in String[] meteredIfaces);
    void onRestrictBackgroundChanged(boolean restrictBackground);
}
