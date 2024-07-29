class UpiPaymentRequest {
  String redirectUrl;
  String packageName;

  UpiPaymentRequest(this.redirectUrl, this.packageName);

  Map<String, dynamic> toMap(){
    return {
      "redirectUrl" : redirectUrl,
      "packageName" : packageName
    };
  }
}

