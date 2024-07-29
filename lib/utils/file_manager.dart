import 'dart:convert';
import 'dart:io';

import 'package:path_provider/path_provider.dart';

class FileManager{


  static Future<void> saveJsonToFile(Map<String, dynamic> jsonData, String fileName) async {
    try {
      // Get the app's local directory
      Directory appDir = await getApplicationDocumentsDirectory();

      // Create a File object with the app's local directory and the provided file name
      File file = File('${appDir.path}/$fileName');

      // Convert the JSON data to a string
      String jsonString = json.encode(jsonData);

      // Write the JSON string to the file
      await file.writeAsString(jsonString);

      print('JSON saved to file: ${file.path}');
    } catch (e) {
      print('Error saving JSON to file: $e');
    }
  }
}