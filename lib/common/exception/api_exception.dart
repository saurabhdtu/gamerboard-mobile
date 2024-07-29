class ApiException extends Error{
  String message;

  ApiException(this.message);
}