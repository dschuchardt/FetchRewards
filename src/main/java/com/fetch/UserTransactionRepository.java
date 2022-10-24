package com.fetch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//contains all successful transactions for a given user
interface UserTransactionRepository extends JpaRepository<Transaction, Long> {
	@Query("SELECT t from Transaction t where t.pointsAvailable > 0 ORDER BY t.timestamp ASC")
	Transaction findFirstTransactionOrderByTimestamp();
	
	@Query("SELECT t from Transaction t where t.pointsAvailable > 0 AND t.payer = :payer ORDER BY t.timestamp ASC")
	Transaction findFirstTransactionByPayerOrderByTimestamp(@Param("payer")String payer);
}
