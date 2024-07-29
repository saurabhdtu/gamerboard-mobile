import 'package:dotted_border/dotted_border.dart';
import 'package:flutter/material.dart';
import 'package:gamerboard/resources/strings.dart';

class SelectableGameWidget extends StatelessWidget {
  const SelectableGameWidget({
    super.key,
    required this.size,
    required this.color,
    required this.dashPattern,
    required this.isSelected,
    required this.iconPath,
  });

  final double size;
  final Color color;
  final List<double> dashPattern;
  final bool isSelected;
  final String iconPath;

  @override
  Widget build(BuildContext context) {
    return Container(
      height: size,
      width: size,
      child: Stack(
        children: [
          Container(
            margin: const EdgeInsets.only(left: 8, right: 4, top: 4),
            child: GameSelectionIcon(
                color: color,
                dashPattern: dashPattern,
                isSelected: isSelected,
                size: size,
                iconPath: iconPath),
          ),
          Visibility(
            visible: isSelected,
            child: Padding(
              padding: const EdgeInsets.only(right: 4, bottom: 4),
              child: Image.asset(
                "${imageAssets}ic_ok.png",
                height: 16,
                width: 16,
              ),
            ),
          )
        ],
      ),
    );
  }
}

class GameSelectionIcon extends StatelessWidget {
  const GameSelectionIcon({
    super.key,
    required this.color,
    required this.dashPattern,
    required this.isSelected,
    required this.size,
    required this.iconPath,
  });

  final Color color;
  final List<double> dashPattern;
  final bool isSelected;
  final double size;
  final String iconPath;

  @override
  Widget build(BuildContext context) {
    return DottedBorder(
        padding: const EdgeInsets.all(2),
        color: color,
        strokeWidth: 2,
        dashPattern: dashPattern,
        strokeCap: isSelected ? StrokeCap.square : StrokeCap.butt,
        child: Stack(children: [
          Container(
              height: size - 20,
              width: size - 20,
              child: Center(
                  child: ColorFiltered(
                      colorFilter: ColorFilter.mode(
                        Colors.black.withOpacity(isSelected ? 0 : 0.75),
                        // 0 = Colored, 1 = Black & White
                        BlendMode.saturation,
                      ),
                      child: Image.asset(
                        iconPath,
                        color: Colors.white.withOpacity(isSelected ? 1 : 0.33),
                        colorBlendMode: BlendMode.modulate,
                      ))))
        ]));
  }
}
