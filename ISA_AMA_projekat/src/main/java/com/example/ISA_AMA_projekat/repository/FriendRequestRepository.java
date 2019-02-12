package com.example.ISA_AMA_projekat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.FriendRequest;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer>
{
	
	@Modifying
	@Transactional
	@Query("select friend_request from FriendRequest friend_request where friend_request.stanje=0 and (friend_request.prima.id = ?1 or friend_request.salje.id = ?1)")
	List<FriendRequest> findBySaljeOrPrima(Integer id);
	
	@Modifying
	@Transactional
	@Query("select friend_request from FriendRequest friend_request where friend_request.prima.id = ?1 or friend_request.salje.id = ?1")
	List<FriendRequest> findBySaljeOrPrimaAbstract(Integer id);

	@Modifying
	@Transactional
	@Query(value = "update friend_request h set h.stanje = ?1 where h.id = ?2", nativeQuery = true)
	public void updateRequest( Integer stanje, Integer id);
	
	@Query("select friend_request from FriendRequest friend_request where friend_request.stanje=0 and ((friend_request.prima.id = ?1 and friend_request.salje.id = ?2) or (friend_request.prima.id = ?2 and friend_request.salje.id = ?1))")
	Optional<FriendRequest> findByUsers(Integer userID1, Integer userID2);
	
	
	
}
