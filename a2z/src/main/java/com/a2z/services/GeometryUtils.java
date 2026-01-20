package com.a2z.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.a2z.data.GPS;

@Service
public class GeometryUtils {

	private static double Earth_Radius_KM = 6378.137;
	public static Map<String , Double> getSquareOfTolerance(GPS center , double radius){
		Map<String , Double> mapDetails = new HashMap<String,Double>();
		/* radius in kms */
		if(center == null) {
			//return null;
		}
		else if (radius <= 0.0) {
			//return null;
		}
		else {
			double deltaLongitude = radius/(getCircleOfLatitudeLength(center.getDecimalLatitude())/360.0);
			/* each degree of latitude is about 111.111 km or 111,111 metres */
			double deltaLatitude = radius/111.11;
			double lat1 = fixOverLappedLatitude(center.getDecimalLatitude()-deltaLatitude);
			double lon1 = fixOverLappedLongitude(center.getDecimalLongitude()-deltaLongitude);
			double lat2 = fixOverLappedLatitude(center.getDecimalLatitude()+deltaLatitude);
			double lon2 = fixOverLappedLongitude(center.getDecimalLongitude()+deltaLongitude);
			mapDetails.put("lat1", lat1);
			mapDetails.put("lon1", lon1);
			mapDetails.put("lat2", lat2);
			mapDetails.put("lon2", lon2);
			
			
		}
		return mapDetails;
	}
	
	private static double getCircleOfLatitudeLength(double latitude) {
		double rads = Math.abs(latitude) * Math.PI/180.0; //converting latitude  in to  radians
		return 2* Math.PI * Earth_Radius_KM * Math.cos(rads); //arc length at latitude 2*pi*R * cos(alpha)
	}
	
	protected static double fixOverLappedLatitude(double latitude) {
		if(latitude > 90.0) {
			return 90.0;
		}
		else {
			return latitude < -90.0 ? -90.0 : latitude;
		}
	}
	
	protected static double fixOverLappedLongitude(double longitude) {
		if(longitude < -180.0) {
			return 360.0+longitude;
		}
		else {
			return longitude > 180.0 ? -360.0+ longitude : longitude;
		}
	}
}
