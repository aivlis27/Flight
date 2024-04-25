package com.fms.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fms.entity.Booking;
import com.fms.entity.Flight;
import com.fms.service.BookingService;
import com.fms.service.FlightService;

@Controller
public class FlightController {
	
	@Autowired
	FlightService fservice;
	@Autowired
	BookingService bservice;
	
	

	
	@GetMapping("/")
	public String home() {
		return "home";
	}
	@GetMapping("/flights")
	public String flights(Model model) {
		List<Flight> flightDetails = fservice.getAllFlightDetails();
		model.addAttribute("fd", flightDetails);
		return "flights";
	}
	
	@GetMapping("/search")
	public String search(Model model) {
		return "search";
	}
	
	@GetMapping("/bookflight")
	public String booking(Model model) {
		model.addAttribute("idmango",true); 
		return "bookflight";
	}
	@GetMapping("/bookflight/{fid}")
	public String bookingwithid(Model model,@PathVariable("fid") String fid) {
	
			model.addAttribute("message",null);
			int flightid = Integer.parseInt(fid);
			model.addAttribute("idmango",false);
			model.addAttribute("fid",flightid);
		
		return "bookflight";
	}
	
	@GetMapping("/ticket")
	public String tickets(Model model) {
		return "ticket";
	}
	
	@GetMapping("/deletebooking/{combination}")
	public String cancelTicket(RedirectAttributes attri,@PathVariable("combination") String combination) {
		String[] arr = combination.split("-");
		int bid = Integer.parseInt(arr[0]);
		long umob = Long.parseLong(arr[1]);
		String upass = arr[2];
		int fid = Integer.parseInt(arr[3]);
		int seat = Integer.parseInt(arr[4]);
			
		//update flight seat
		Optional<Flight> opf = fservice.getAllFlightDetailsById(fid);
		Flight f = opf.get();
		int updateSeat = f.getSeats() + seat;
//		System.out.println(updateSeat);
//		System.out.println(seat);
		fservice.flightseatupdate(updateSeat, fid);
		//delete booking
		bservice.deleteTicket(bid,umob,upass);
		attri.addFlashAttribute("message","Ticket Cancelled ");
		return "redirect:/ticket";
	}
	
	@PostMapping("/searchdata")
	public String searchdata(@ModelAttribute Flight obj,RedirectAttributes attributes) {
		List<Flight> searchedFlights = fservice.findMyFlight(obj.getSource(),obj.getDestination(),obj.getTraveldate());
		attributes.addFlashAttribute("searchResult", searchedFlights);
		return "redirect:/search";
	}
	
	@PostMapping("/savebooking")
	public String makeBooking(@ModelAttribute Booking bobj,@Param("userFlightId") String userFlightId,RedirectAttributes reAttri) {
		//Check for usermobile and password combination
		List<Booking> checkUser = bservice.findmyuser(bobj.getUserMobile(),bobj.getUserPassword());
		if(checkUser.size()>0) {
			reAttri.addFlashAttribute("message","Change Your Number");
		}else {
			//First - Finding the flight with appropriate flightID
			Optional<Flight> fdet = fservice.getAllFlightDetailsById(Integer.parseInt(userFlightId));
			
			
			//If Flight is Not Found
			if(fdet.isEmpty()) {
				reAttri.addFlashAttribute("message", "No Such Flight Exists.");
			}else {
				//Check For Seat Availability
				if(fdet.get().getSeats() < bobj.getUserSeat()) {
					reAttri.addFlashAttribute("message", "Booking Seat Is More Than Availale Seats.");
				}else {
					//Calculate Total Price  and Set to the BookingObject
					double tp = bobj.getUserSeat() * fdet.get().getPrice();
					bobj.setTotalPrice(tp);
					
					//bobj.setFlightId(Integer.parseInt(userFlightId));
					bobj.setFlight(fdet.get());
					
					//Save The Booking Object
					bservice.savebooking(bobj);
					
					//Update The Flight Availability
					int updatedSeat = fdet.get().getSeats() - bobj.getUserSeat();
					fservice.flightseatupdate(updatedSeat,fdet.get().getFlightId());
					
					reAttri.addFlashAttribute("message", "Booked Successfully 👍");
				}
			}
		}
		
		return "redirect:/bookflight";
	}
	
	@PostMapping("/finduser")
	public String findUser(@ModelAttribute Booking bobj,RedirectAttributes reti) {
		List<Booking> res = bservice.findmyuser(bobj.getUserMobile(),bobj.getUserPassword());
	
		if(res.size() < 1) {
			reti.addFlashAttribute("message","Invalid Credentials");
		}else {
			reti.addFlashAttribute("ticketData",res);
		}
		
		return "redirect:/ticket";
	}
	
	

}