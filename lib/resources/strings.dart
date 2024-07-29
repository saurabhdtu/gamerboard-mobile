import 'package:flutter/src/widgets/framework.dart';

import '../common/constants.dart';

////Created by saurabh.lahoti on 02/08/21
abstract class AppStrings {
  static const String installNow = 'Install now';
  static const String later = 'Later';
  static const String yes = 'Yes';
  static const String no = 'No';
  static const String skip = 'Skip';
  static const String retry = 'Retry';
  static const String whatsNew = "What's new:";
  static const String createAccount = 'Create account';
  static const String continueText = 'Continue';
  static const String rupeeSymbol = '₹';
  static const String signIn = 'Sign in';
  static const String enterMobileNumber = 'Enter mobile number';
  static const String mobileNumber = 'Mobile number';
  static const String dateFormat = 'DD / MM / YYYY';
  static const String dateOfBirth = 'Date of birth';
  static const String getOtp = 'Get OTP';
  static const String enterOTP = 'Enter OTP';
  static const String createGBAccount = 'Create gamerboard account';
  static const String username = 'Gamerboard username';
  static const String joinDiscordServer = 'Join our discord server';
  static const String inviteCodeOptionalField = 'Invite code (Optional)';
  static const String inviteCode = 'Invite code';
  static const String firstName = 'First name';
  static const String lastName = 'Last name';
  static const String alreadyHaveAccount = 'Already have an account?';
  static const String dontHaveAccount = 'Don’t have an account?';
  static const String receiveOTP = 'Didn’t receive OTP?';
  static const String sendAgain = 'Send again';
  static const String verify = 'Verify';
  static const String cancel = 'Cancel';
  static const String profileRegistered =
      'Congratulations for completing your placement match.\n\nYou now have access to all leaderboards.';
  static const String learnMore = 'Learn more';
  static const String welcomeGamerboard = 'Welcome to Gamerboard';
  static const String editTier = 'Edit ';
  static const String edit = 'Edit';
  static const String tapToEditTier =
      'We’ll automatically update your tier after you play game.';
  static const String selectTier = 'Select your current';
  static const String tierWarning =
      'Warning: If your selected tier does not match your in-game tier, your game(s) will not count until corrected. You can edit any time.';
  static const String submit = 'Submit';
  static const String depositWinningBonus = 'Deposit, winnings, bonus';
  static const String joinToStart = 'Join a tournament to start playing';
  static const String bonusCash = 'Bonus cash has been added to your wallet!';
  static const String squadTeamGuidelines = 'Squad team guidelines';
  static const String squadGuidelines =
      ' Please make sure that you only play with your below squad mates to get the match counted.';

  static const String useIfNowToPlay =
      'Use it now to play in Premium Tournaments';
  static const String play = 'Play';
  static const String total = 'Total';
  static const String wallet = 'Wallet';
  static const String needHelp = 'Need help?';

  static const String rewardReceived = 'Reward Received';
  static const String history = 'History';
  static const String topLeaderboards = 'Top Leaderboards';
  static const String solo = 'Solo';
  static const String duo = 'Duo';
  static const String squad = 'Squad';
  static const String topGamer = 'Top Gamer';

  static const String news = 'News';
  static const String signOut = 'Sign Out';
  static const String signOutMessage = 'Are you sure you want to sign out?';
  static const String yourLeaderboards = 'Your leaderboards';
  static const String joined = 'Joined';
  static const String createJoin = 'Create/Join team';
  static const String pointsBreakDown = 'Points';
  static const String reward = 'Reward';
  static const String inviteFriend = 'Invite friend get rewards';
  static const String manageWallet = 'Manage wallet';
  static const String tournamentEnded = 'Tournament ended';
  static const String points = 'points';
  static const String rankPoints = 'Rank points';
  static const String killPoints = 'Kill points';
  static const String totalPoints = 'Total points';
  static const String pointsBehind = 'Points behind 1st';
  static const String top15text = 'Top %s players in a game get rank points';
  static const String killText = '1 point for each kill';
  static const String bestGames = 'Best %s games.';
  static const String totalText =
      'Your best %s games with the highest combined point total';
  static const String playerName = 'Player name';
  static const String gamesPlayed = 'Games played';
  static const String yourStats = 'Your stats';
  static const String yourRank = 'Your rank';
  static const String gameHistory = 'Game history';
  static const String topXGames = 'Top %s games';
  static const String totalPlayed = 'Total games played';
  static const String myWallet = 'My wallet';

  static const String deposit = 'Deposit';
  static const String withdraw = 'Withdraw';
  static const String winnings = 'Winnings';
  static const String bonus = 'Bonus';
  static const String bonusDesc =
      'Bonus money can be used for playing tournaments, but cannot be withdrawn';
  static const String amountToAdd = 'Amount to add';
  static const String amountWithdraw = 'Amount to withdraw';
  static const String amountDeposit = 'Amount to deposit';
  static const String currentTotal = 'Current total';
  static const String fundsToDeposit = 'Funds to deposit';
  static const String availableBalance = 'Avail. balance';
  static const String totalAfterDeposit = 'Total after deposit';
  static const String afterWithdrawal = 'After withdrawal';
  static const String enterUpiId = 'Enter UPI Id';
  static const String next = 'Next';
  static const String done = 'Done';
  static const String finish = 'Finish';
  static const String joiningConformation = 'Join Tournament';

  static const String withdrawalConfirmation = 'Withdrawal confirmation';
  static const String depositConfirmation = 'Deposit confirmation';
  static const String withdrawalConfirmationDesc =
      'Do you wish to confirm the %s of $rupeeSymbol%s ?';

  static const String withdrawalSuccess = 'Successfully initiated withdrawal.';
  static const String depositSuccess = 'Successfully deposited';

  static const String createATeam = 'Create a team';
  static const String createTeam = 'Create team';
  static const String joinTeam = 'Join team';
  static const String enterGroupCode = 'Enter invite code';
  static const String createOrJoinTitle = 'Create or Join a team (Beta)';
  static const String joinExistingSquad = 'Join an existing team';
  static const String enterTeamCreationCode =
      'Enter your Gamerboard teams invite code ';
  static const String enterTeamInviteCode =
      'Enter invite code provided by the team creator';
  static const String inviteCodeForSquad = 'Create a team';
  static const String createStep1 = 'Choose a team name and invite %s players';
  static const String createStep2a = 'Each team member pays %s on joining.';
  static const String createStep2b = 'This tournament is free to join.';
  static const String gamerboardCode =
      'Gamerboard team invites are ${Constants
      .INVITE_CODE} digit alpha-numeral codes';

  static const String registerTeam = 'Register team name';
  static const String teamName = 'Team name';
  static const String enterTeamName = 'Enter team name';
  static const String inviteTeammates = 'Invite teammates';
  static const String payLater = 'Pay later';
  static const String invitePlayersNow = 'Invite teammates now';
  static const String chooseSquadName =
      'Choose a unique team name for this tournament. This will be displayed in the leaderboard.';
  static const String sendInvite =
      'You can send as many invites as you like. The first 3 players to accept will be placed in your team.';
  static const String inviteLater = 'You can also invite players later';
  static const String payment = 'Payment';
  static const String payForYourself = 'Pay for yourself';
  static const String payWithGB = 'Pay with GB wallet';
  static const String addFunds = 'Add funds to GB wallet';
  static const String payYourShare = 'Each team-mate pays their share';
  static const String payNow = 'Pay now';
  static const String currentBalance = 'Current balance';
  static const String backToTournament = 'Back to tournament';
  static const String ready = 'Ready';
  static const String paymentPending = 'Payment pending';
  static const String notPaid = 'Not paid';
  static const String teamMembers = 'Team members';
  static const String paymentIncomplete = 'Payment incomplete';
  static const String paymentShare =
      'Each player must pay their own share before playing';
  static const String successfullyPaid = 'Successfully joined the tournament';

  static const String useCodeBelow =
      'Use the code below to manually invite squad mates.';
  static const String linkShareText =
      'Download gamerboard, play in BGMI tournaments and win cash prizes';
  static const String appShareText =
      'Download gamerboard using this link %s, play BGMI tournaments and win loads of prizes. You can also play with me as a team in duo or squad mode and participate in free tournaments.';
  static const String inviteTitle =
      '%s has invited you to %s tournament on Gamerboard.';

  static const String whenDoYouPlay =
      "When do you play?";
  static const String youCanChooseOneOption =
      "You can only choose 1 option";
  static const String whatTimeYouPlayInWeek =
      "What time you are playing in week?";

  static const String whatIsPreferredRole = "What is your preferred gaming role?";
  static const String notMoreThanTwo = "Not more than 2";
  static const String whyDoYouPlay = "Why do you play?";
  static const String youCannotSelectMoreThanTwo = "You can not select more than 2 options";
  static const String yourProfileIsReady = "Hurray! Your Profile is ready";
  static const String youHaveCompletedYourProfile = "You have completed your profile \ndetails! You can now match with \nplayers to team up. \nGame on!";
  static const String completeYourProfile = 'Complete your profile to connect \nwith ';
  static const String newTeam = 'New Team';

  static const String inviteCodeTitle =
      "Play BGMI tournaments with me and win loads of cash prizes everyday. Download Gamerboard using this using this link %s now and get *$rupeeSymbol${Constants
      .REFERRAL_AMOUNT}* joining bonus. Please enter my referral %s when you sign up.";
  static const String inviteVariant =
      "Join Gamerboard with my referral code *%s* and get *$rupeeSymbol${Constants
      .REFERRAL_AMOUNT}*. Bonus Cash to play BGMI tournaments. %s";
  static const String squadShareText = """
     $inviteTitle
    -> *Maps*: %s
    -> *Mode*: %s
    -> *Tier*: %s
    -> *Entry fee*: $rupeeSymbol%s
    -> *Starts on*: %s
    -> *Duration*: %s
    To join, enter code *'%s'* or tap on the link %s
      """;
  static const String sendCode = 'Send code';
  static const String copy = 'Copy';
  static const String share = 'Share';
  static const String apply = 'Apply';
  static const String roles ='Roles';
  static const String playingTimeOfWeek = "Playing time in week";
  static const String playingTimeOfDay ="Playing time in day";
  static const String findPlayerToPlayNextMatch ="Find players to play in the next match";
  static const String noUserFound ="No user found";
  static const String bgmiUid ='BGMI UID: ';
  static const String sayHiToOnWhatsapp ="Say Hi on WhatsApp";
  static const String kdRatio ="KD Ratio";
  static const String last50Games = "Last 50 games";
  static const String classic = "Classic";
  static const String role = 'Role: ';
  static const String custom="Custom";

  static const String whatsapp = 'Whatsapp';
  static const String recentPlayers = 'Recent players';
  static const String searchPlayers = 'Search players';
  static const String players = 'players';
  static const String teams = 'teams';
  static const String noticeInvite =
      'Notice: Invitees without Gamerboard accounts will be asked to install Gamerboard App first';
  static const String searchBy =
      'Search by Gamerboard Name, mobile, or BGMI ID';
  static const String search = 'Search';
  static const String inviteSent = 'Invite sent!';
  static const String inviteToTeam = 'Invite to team';
  static const String youCanPlayWithIncompleteTeam =
      'You can play with less than %s members, but games in %s will only be counted when playing with teammates registered on Gamerboard app.';
  static const String inviteToGB = 'Invite to Gamerboard';
  static const String searchByBGMID = 'Search by BGMI ID';
  static const String searchByMob = 'Search by mobile number';
  static const String searchByUsername = 'Search by gamerboard username';
  static const String startSearch = 'Start search for your team mate.';
  static const String notOnGamerboard = 'Not on gamerboard';
  static const String acceptInvite = 'Accept invite';
  static const String joinedTeam = 'Joined team';
  static const String dot = '•';

  static const String errorTier = 'Please choose your tier first';
  static const String errorTeamThis = 'You are already part of this team';
  static const String errorTeamOther =
      "You are already part of %s team. Do you want to leave the current team and accept %s team's invite?";
  static const String tournamentNotAvailable = 'Tournament not available';

  static const String accountCreationCompleted = 'Account creation completed';
  static const String oneGameSubmissionReward = 'First game submitted';
  static const String threeKillReward = 'First hat-tick on GB';

  static const String gbTeamRules = 'GB Team Rules';
  static const String from = 'From';

  static const String playWithCompleteTeam =
      'Play with all your GB registered teammates.\n\nDo NOT play with auto-matching teams.';

  static const String paymentMessageTitle =
      'Your amount will be credited within 24 hours into Gamerboard wallet';
  static const String paymentMessageDesc =
      'If the amount is not deposited, the amount will be refunded back to your account.';

  static const String choosePaymentMethod = 'Choose UPI app';

  static const String transactionCancelled = 'Transaction is cancelled.';

  static const String unableToProcessPayment =
      'Unable to process payment at this time. Please try again later';

  static var paymentRequestTimeout =
      'Payment request timeout. Please check the transaction status after some time.';

  static const String congratulations = 'Congratulations';

  static const String slotNumber = 'Slot';

  static const String registrationClosed = 'Registration Closed';

  static const String titleInviteOnly = 'Invite Only';
  static const String hintInviteOnlyCode = 'Invite Code';

  static const String errorInviteCodeIsRequired = 'Invite Code is required';

  static const String descriptionInviteOnly =
      'Enter your invite code to join the tournament';

  static const String locationShareTitle =
      'You’ll need to give your location in order to play with people nearby';

  static var hintManualAddress = 'Enter your location';

  static const String back = 'Back';

  static const String manualLocationTitle =
      'Enter your location in order to play with people nearby';

  static const String selectAtLeastOnRole = "Please select at-least one role";

  static const String getMyLocationButtonText = 'Get my location';
  static const String addManualLocationButtonText = 'Add location manually';

  static const String locationServiceRationaleDescription =
      'To enhance your experience and let you connect with nearby players, we require your location. Please enable location services so we can help you find and play with others in your area.';
  static const String locationServiceRationaleTitle = 'Please enable location service';

  static const String goToLocationSettings = 'Enable Location';

  static const String locationPermissionDeniedTitle = 'Location permission is required';
  static const String locationPermissionDeniedDescription = 'Location permission has been permanently denied. Please access the app settings to grant permission for location.';
  static const String goToAppSettings = 'Open Settings';

  static const String changeLocationButtonText = 'CHANGE';

  static const String someErrorOccurred = 'Some error occurred. Please try again';

  static const String allowedTiers = 'Allowed tiers';

  static const String rules = 'Rules';

  static const String scoring = 'Scoring';

  static const String support = 'Support';

  static const String disqualified = '(Disqualified)';

  static String doYouWantToPay(int fee) =>
      'Do you want to pay ' + rupeeSymbol + fee.toString() + '?';

  static const String amountAndJoin = ' amount And join ';

  static const String tournamentRule1 =
      "Tournament gets cancelled if %s don't submit games";

  static const String selectGame = 'Select the game';

  static const String confirm = 'Confirm';

  static String joinString(int fee) =>
      "Join for ${fee == 0 ? 'free' : AppStrings.rupeeSymbol + fee.toString()}";
  static const String tournamentFull = 'Tournament full';
  static const String firstSelectYourCurrent = 'First, select your current';
  static const String howToFindMyTier = ' How can I find my tier?';
  static const String whatsappNotAvailable="WhatsApp is not installed on the device";

  static String findYourGameTier(String gameName) =>
      'Find your ${gameName} tier';

  static const String findYourBgmiTier = 'Find your BGMI tier';

  static String submitYourTier(String gameName) =>
      'Submit your current ${gameName} tier to compete against players at your level. You can find it on your ${gameName} profile';

  static const String moreInstruction = 'More instruction';
  static const String inviteYourFriendToGetRewards =
      'Invite your friends to get rewards';
  static const String youWillReceive = 'You will receive ';
  static const String bonusCaseByFollowing =
      ' bonus cash by followings 3 simple steps';
  static const String youInviteYourFriend = '1. You invite your friend';

  static const String yourFriendInstalls =
      '2. Your friend installs GB and enters invite code';
  static const String theyJoinTournament =
      '3. They join a tournament and submit a game';
  static const String useTheCodeBelow =
      'Use the code below to manually invite squadmates.';
  static const String code = 'CODE ';
  static const String didGamerboardJustRestart = 'Did gamerboard just restart';

  static const String selectedTierDoesNotMatch =
      'If your selected tier does not match your in-game tier, your game(s) will not count until corrected. You can edit any time.';
  static const String paymentConfirmDialogTitle =
      'After making the payment please confirm';
  static const String notAbleToMakePayment = 'Not able to make the payment?';
  static const String useTheQrCode = 'Use the UPI id or QR code';
  static const String download = 'Download';

  static const String close = 'Close';
  static const String vpaAddress = 'gamerboard@ybl';

  static const String runningLowBalance = 'Running low on your wallet balance!';
  static const String addMoneyToWallet =
      "Don't worry its very easy to add money to your wallet";
  static const String addMoney = 'Add Money';
  static const String or = 'or';
  static const String msgPendingTransaction =
      'Your payment is currently being processed. Please note that if the payment is not successfully processed, it will be refunded to your account. Thank you for your patience.';
  static const String processingPayment = 'Processing payment.';
  static const String noUpiAppsAvailable =
      'No upi app available. We are currently supporting upi payments.';
  static const String exclusiveLeaderboard = ' Exclusive Leaderboard';
  static const String congratulationsYouAreEligible =
      'Congratulations!!\nYou are eligible to join this.';
  static const String gotIt = 'Got It';
  static const String join = 'Join';
  static const String exclusiveTournaments = ' Exclusive Tournament';

  static const String viewLeaderboard = 'View leaderboard';
  static const String seeDetails = 'See Details';
  static const String gamesPlayedRecently = 'Games played recently';
  static const String tournamentsPlayedRecently = 'Tournaments played recently';
  static const String youDontQualify = "You don't qualify";
  static const String yourRankedRecently = 'Your ranked recently';
  static const String daysSinceSignup = 'days since sign-up';
  static const String customRoom = 'Custom Room';
  static const String useCredentialsCustomRoom =
      'Use this credentials to join custom room';
  static const String roomId = 'Room Id';
  static const String roomPassword = 'Room Password';
  static const String howToJoinCustomRoom = 'How to join GB Custom Rooms >';
  static const String joinDiscord = 'Join Discord';
  static const String gameStartIn = 'Game starts in';
  static const String registrationClose = 'Registration Close';
  static const String gameIsLive = 'Game is Live';
  static const String clickToWatch = 'Click to watch';
  static const String showPassword = 'Show Password';
  static const String passwordShareInMovement =
      'Admin will share the IDP in a few mins';

  static const String registrationCloseIn = 'Registration close in';
  static const String passwordWillBeSharedIn = 'Password will be shared in';
  static const String welcomeAboard = 'Welcome Aboard';
  static const String submitYourGameTier =
      'Submit your Game Tier and \nearn a reward of ₹20';
  static const String joinTournament = 'Join Tournament';
  static const String customRoomGameRules = 'Custom room rules';
  static const String findAndJoinTournament =
      'Find and Join a tournament \non homepage.';
  static const String playTournament = 'Play Tournament';
  static const String playTournamentAndEarn =
      'Play a Tournament and earn \na bonus reward of ₹10';

  static const String gbCustom = 'GB Custom';
  static const String exclusive = 'Exclusive';
  static const String inviteOnly = 'Invite Only';
  static const String loginUsingWhatsapp = 'Login with Whatsapp';
  static const String loginUsingOtp = 'Login with OTP';

  static const String signupUsingWhatsapp = 'Signup with Whatsapp';
  static const String signupUsingOtp = 'Signup with OTP';
  static const String codeIsSentTo = 'Code is sent to +91-';

  static String getNumGameQualifyTitle(String qualificationNumber,
      String difference) =>
      'Only for gamers who played $qualificationNumber+ games in the last $difference days';

  static String getNumTournamentQualifyTitle(String qualificationNumber,
      String difference) =>
      'Only for gamers with $qualificationNumber+ tournaments in the last $difference days';

  static String getRankByTournamentQualifyTitle(
      String qualificationNumber, String tournamentName) =>
      'Only for winners who secured top $qualificationNumber rank in $tournamentName';

  static String getOnlyWinnersWhoQualifyTitle(String qualificationNumber,
      String numberOfDays) =>
      'Only for winners who got top ${qualificationNumber} rank in the last ${numberOfDays} days';

  static String getOnlyVeteransQualifyTitle(String qualificationNumber) =>
      'Only for veterans who have been on GB for $qualificationNumber+ days';
}

class ErrorMessages {
  static const String ERROR_INTERNET_CONNECTIVITY =
      'Please check your internet connectivity';
  static const String ERROR_REQUEST_TIMED_OUT =
      'Request timed out. You might be on a slow connection.';
  static const String ERROR_SOME = 'Some error occurred';
  static const String ERROR_SESSION_EXPIRED =
      'Your session has expired. Please login again.';
  static const String ERROR_UNAUTHORIZED = 'Unauthorized';
  static const String ERROR_BAD_REQUEST = 'Bad request. Try again.';
  static const String ERROR_UNREACHABLE =
      'Unable to reach host. Please try again or check your internet connectivity.';
}

String imageAssets = 'assets/images/';
String lottieAssets = 'assets/lottie/';
