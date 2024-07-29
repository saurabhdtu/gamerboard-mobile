import 'package:gamerboard/graphql/query.dart';

extension WalletExtension on WalletMixin{
  double total(){
    return deposit + winning + bonus;
  }
}