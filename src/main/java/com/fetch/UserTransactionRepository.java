package com.fetch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//contains all successful transactions for a given user
interface UserTransactionRepository extends JpaRepository<Transaction, Long> {
	
	Transaction findTopByPointsAvailableToSpendGreaterThanOrderByTimestampAsc(@Param("pointsAvailableToSpend")int pointsAvailableToSpend);
	
	
	Transaction findTopByPointsAvailableToSpendGreaterThanAndPayerOrderByTimestampAsc(@Param("pointsAvailableToSpend")int pointsAvailableToSpend, @Param("payer")String payer);
}
