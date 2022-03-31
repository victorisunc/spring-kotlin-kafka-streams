package com.demo.sse.engine

class CalcUtils {

    object Geo {
        fun speed(distance: Double, timeInSec: Long): Double {
            val secondsInHour = (timeInSec * 60 * 60).toDouble()
            return distance / (1 / secondsInHour)
        }

        // http://www.movable-type.co.uk/scripts/latlong.html
        fun distanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val theta = lon1 - lon2
            var distInMiles = (Math.sin(deg2rad(lat1))
                    * Math.sin(deg2rad(lat2))
                    + (Math.cos(deg2rad(lat1))
                    * Math.cos(deg2rad(lat2))
                    * Math.cos(deg2rad(theta))))
            distInMiles = Math.acos(distInMiles)
            distInMiles = rad2deg(distInMiles)
            distInMiles *= 60 * 1.1515
            val distInKm = distInMiles / 0.62137
            return distInKm
        }

        private fun deg2rad(deg: Double): Double {
            return deg * Math.PI / 180.0
        }

        private fun rad2deg(rad: Double): Double {
            return rad * 180.0 / Math.PI
        }
    }
}