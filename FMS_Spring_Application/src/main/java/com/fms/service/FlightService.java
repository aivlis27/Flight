package com.fms.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fms.entity.Flight;
import com.fms.repository.FlightRepository;

@Service
public class FlightService {
	@Autowired
	FlightRepository frepo;

	public List<Flight> getAllFlightDetails() {
		return frepo.findAll();
	}

	public List<Flight> findMyFlight(String src,String dest,Date d) {
		return frepo.findBySourceAndDestinationAndTraveldate(src,dest,d);
	}

	public Optional<Flight> getAllFlightDetailsById(int userFlightId) {
		return frepo.findById(userFlightId);
	}

	public void flightseatupdate(int seat,int flightId) {
		frepo.updateSeat(seat,flightId);
	}

}