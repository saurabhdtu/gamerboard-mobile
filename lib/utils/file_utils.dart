import 'dart:io';

// import 'package:firebase_storage/firebase_storage.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';

////Created by saurabh.lahoti on 17/10/21

class DownloadUtils {
  static DownloadUtils? _instance;

  DownloadUtils._();

  static DownloadUtils get getInstance => _instance ??= DownloadUtils._();

  Future<Directory> _getDownloadDir() async {
    final List<Directory>? list =
        await getExternalStorageDirectories(type: StorageDirectory.downloads);
    Directory download;
    if (list != null && list.isNotEmpty) {
      download = list.first;
    } else {
      download = Directory("storage/emulated/0/Download");
    }
    return Future.value(download);
  }

  Future<int> downloadFileToDownloads(String url,
      {DownloadListener? downloadListener}) async {
    String fileName = url.split("/").last;
    Directory download = await _getDownloadDir();
    int value;
    File downloadFile = File("${download.path}/$fileName");
    bool fileExists = await downloadFile.exists();
    bool downloadNow = true;
    if (fileExists) {
      downloadNow = false;
    } else {
      fileName = "temp_$fileName";
      downloadFile = File("${download.path}/$fileName");
      downloadNow = true;
    }

    if (downloadNow) {
      deleteTempFiles();
      /*Reference ref = FirebaseStorage.instance.refFromURL(url);
      DownloadTask task = ref.writeToFile(downloadFile);
      task.snapshotEvents.listen((event) {
        double per = (event.bytesTransferred / (event.totalBytes));
        downloadListener?.downloadProgress(per);
      }, onError: (e) async {
        _deleteFile(downloadFile);
        downloadListener?.onError(e);
      });
      try {
        await task;
        fileName = fileName.replaceAll("temp_", "");
        await downloadFile.rename("${download.path}/$fileName");
        value = await downloadListener?.onDownloadComplete(downloadFile.path);
        return Future.value(value);
      } catch (ex) {
        return Future.value(-1000);
      }*/
      return Future.value(-1000);//to remove when above code is uncommented
    } else {
      value = await downloadListener?.onDownloadComplete(downloadFile.path);
      return Future.value(value);
    }
  }

  void _deleteFile(File file) async {
    try {
      await file.delete();
    } catch (e) {
      debugPrint(e.toString());
    }
  }

  void deleteUnusedApks() async {
    Directory download = await _getDownloadDir();
    download.list().forEach((element) {
      debugPrint(element.path + " .... " + p.extension(element.path));
      if (p.extension(element.path) == ".apk") {
        element.deleteSync();
      }
    });
  }

  void deleteTempFiles() async {
    Directory download = await _getDownloadDir();
    download.list().forEach((element) {
      debugPrint(element.path + " .... " + p.extension(element.path));
      if (element.path.contains("temp_")) element.deleteSync();
    });
  }
}

abstract class DownloadListener {
  void downloadProgress(double progress);

  Future<dynamic> onDownloadComplete(String path);

  void onError(Object? error);
}

class DownloadHandler implements DownloadListener {
  StateSetter? setState;
  double progress = 0;

  bool? downloadComplete;
  BuildContext context;
  bool openFile;

  DownloadHandler(this.context, {this.openFile = true});

  @override
  void downloadProgress(double progress) {
    setState?.call(() {
      this.progress = progress;
    });
  }

  @override
  Future<dynamic> onDownloadComplete(String path) async {
    downloadComplete = true;
    Navigator.of(context).pop();
    int val = -1000;
    if (openFile)
      val = await Constants.PLATFORM_CHANNEL
          .invokeMethod("open_file", {"path": path});
    return Future.value(val);
  }

  @override
  void onError(Object? error) {
    downloadComplete = false;
    Navigator.of(context).pop();
  }
}
