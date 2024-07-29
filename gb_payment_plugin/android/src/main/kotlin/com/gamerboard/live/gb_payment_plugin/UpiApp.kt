package com.gamerboard.live.gb_payment_plugin

enum class UpiApp(val packageName: String) {
    GPAY("com.google.android.apps.nbu.paisa.user"),
    PAYTM("net.one97.paytm"),
    PHONEPE("com.phonepe.app"),
    AMAZON("in.amazon.mShop.android.shopping"),
    BHIM("in.org.npci.upiapp"),
}