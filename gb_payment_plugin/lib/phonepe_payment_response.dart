class PhonePePaymentResponse {
  bool? result = false;
  PhonePeStatus status;

  PhonePePaymentResponse({this.result,required this.status});

  static PhonePePaymentResponse fromMap(Map<dynamic, dynamic> map) {
    return PhonePePaymentResponse(result: map["result"] as bool, status: mapStatus(map["Status"]));
  }

  static PhonePeStatus mapStatus(String? status){
    if(status == null){
      return PhonePeStatus.cancelled;
    }
    switch(status){
      case "Pending" :
        return PhonePeStatus.pending;
      case "Submitted" :
        return PhonePeStatus.pending;
      case "Success":
        return PhonePeStatus.success;
      case "Internal Error":
        return PhonePeStatus.internalError;
      case "Error":
        return PhonePeStatus.error;
      case "Time Out":
        return PhonePeStatus.timeOut;
      case "Failure":
        return PhonePeStatus.failure;
    }
    return PhonePeStatus.pending;
  }
}

enum PhonePeStatus { pending, success, submitted, failure, internalError, timeOut, error, cancelled }
