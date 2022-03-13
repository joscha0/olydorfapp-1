import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:hooks_riverpod/hooks_riverpod.dart';
import 'package:latlong2/latlong.dart';
import 'package:olydorf/views/map/custom_polygon_plugin.dart';

import 'custom_polygon_options.dart';

class MapView extends HookConsumerWidget {
  const MapView({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Center(
      child: FlutterMap(
        options: MapOptions(
            center: LatLng(48.17926, 11.55215),
            zoom: 18,
            plugins: [CustomPolygonPlugin()]),
        layers: [
          TileLayerOptions(
            urlTemplate: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
            subdomains: ['a', 'b', 'c'],
          ),
          CustomPolygonLayerOptions(polygons: [
            CustomPolygon(
                label: "B13",
                onTap: () {
                  showDialog(
                      context: context,
                      builder: (_) => const AlertDialog(
                            title: Text("B13"),
                          ));
                },
                points: [
                  LatLng(48.179139, 11.5538915),
                  LatLng(48.1791103, 11.5538916),
                  LatLng(48.1791104, 11.5539494),
                  LatLng(48.1791391, 11.5539493),
                ],
                color: Colors.black12,
                borderColor: Colors.black,
                borderStrokeWidth: 1),
          ]),
        ],
      ),
    );
  }
}
