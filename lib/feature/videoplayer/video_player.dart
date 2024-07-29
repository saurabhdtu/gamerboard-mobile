import 'package:flutter/material.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:video_player/video_player.dart';

import '../../common/services/analytics/analytic_utils.dart';

////Created by saurabh.lahoti on 03/10/21
class VideoPlayerPage extends StatefulWidget {
  final String _videoUrl, pageTitle,source;

  const VideoPlayerPage(this._videoUrl, this.pageTitle,this.source);

  @override
  _VideoPlayerPageState createState() => _VideoPlayerPageState();
}

class _VideoPlayerPageState extends State<VideoPlayerPage> {
  late VideoPlayerController _controller;
  late Future<void> _initializeVideoPlayerFuture;

  @override
  void initState() {
    _controller = VideoPlayerController.network(
      widget._videoUrl,
    );
    _initializeVideoPlayerFuture = _controller.initialize();
    _controller.setLooping(true);
    _controller.play();
    super.initState();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: SafeArea(
            child: Stack(children: [
              FutureBuilder(
                  future: _initializeVideoPlayerFuture,
                  builder: (context, snapshot) {
                    if (snapshot.connectionState == ConnectionState.done) {
                      // If the VideoPlayerController has finished initialization, use
                      // the data it provides to limit the aspect ratio of the video.
                      return Center(
                          child: (AspectRatio(
                              aspectRatio: _controller.value.aspectRatio,
                              // Use the VideoPlayer widget to display the video.
                              child: VideoPlayer(_controller))));
                    } else {
                      // If the VideoPlayerController is still initializing, show a
                      // loading spinner.
                      return const Center(child: CircularProgressIndicator());
                    }
                  }),
              Align(
                  alignment: Alignment.topCenter,
                  child: Container(
                      color: Colors.black,
                      padding: EdgeInsets.symmetric(horizontal: 20.0),
                      child: Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            Expanded(
                                child: TitleText(widget.pageTitle)),
                            IconButton(
                                onPressed: () {
                                  double videoViewPercentage=(_controller.value.position.inSeconds/_controller.value.duration.inSeconds)*100;

                                  AnalyticService.getInstance().trackEvents(
                                      Events.INTRO_VIDEO_PLAY,
                                      properties: {
                                        "type": "intro",
                                        "source": widget.source,
                                        "duration": (!videoViewPercentage.isNaN) ? videoViewPercentage : 0
                                      });
                                  Navigator.of(context).pop();
                                },
                                icon: const Icon(Icons.close,
                                    color: Colors.white, size: 24.0))
                          ])))
            ])));
  }
}
