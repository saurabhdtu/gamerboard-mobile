////Created by saurabh.lahoti on 14/12/21

class UserProfileRequest {
  String userName;
  String firstName;
  String lastName;
  DateTime? dateOfBirth;
  String? referralCode;

  UserProfileRequest(this.userName, this.firstName, this.lastName,
      this.dateOfBirth, this.referralCode);

  get name => "${firstName.trim()} ${lastName.trim()}";
}
