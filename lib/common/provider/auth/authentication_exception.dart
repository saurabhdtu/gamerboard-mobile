class AuthenticationException extends Error{
  String message;
  AuthenticationException(this.message);
}