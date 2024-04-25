package com.fms.repository;

import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fms.entity.Flight;

@Repository
@Transactional
public interface FlightRepository extends JpaRepository <Flight,Integer>{


	@Modifying
	@Query("UPDATE Flight f SET f.seats = :seat WHERE f.flightId = :flightId")
	public void updateSeat(int seat,int flightId);

	public List<Flight> findBySourceAndDestinationAndTraveldate(String src, String dest, Date d);
	

}