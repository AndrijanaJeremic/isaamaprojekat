package com.example.ISA_AMA_projekat.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ISA_AMA_projekat.model.FriendRequest;
import com.example.ISA_AMA_projekat.repository.FriendRequestRepository;

@Service
public class FriendRequestService 
{
	@Autowired
	private FriendRequestRepository friendRequestRepository;
	
	public void updateRequest(Integer stanje, Integer id)
	{
		friendRequestRepository.updateRequest(stanje, id);
	}
	
	public void deleteRequest(Integer frID)
	{
		System.out.println("VRSI SE DELETE");
		friendRequestRepository.deleteById(frID);
	}
	
	public Optional<FriendRequest> findByUsers(Integer id1, Integer id2)
	{
		return friendRequestRepository.findByUsers(id1, id2);
	}
	
	public FriendRequest save(FriendRequest fr)
	{
		return friendRequestRepository.save(fr);
	}
}
