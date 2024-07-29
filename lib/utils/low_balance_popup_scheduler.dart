class LowBalancePopupScheduler{
  static bool? handled;

  static bool shouldShow(){
    return handled != true;
  }

  static void setHandled(){
    handled = true;
  }
}