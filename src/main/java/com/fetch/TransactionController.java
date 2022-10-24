package com.fetch;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

	private final UserTransactionRepository repository;

	TransactionController(UserTransactionRepository repository) {
		this.repository = repository;
	}


	// Aggregate root
	// tag::get-aggregate-root[]
	@GetMapping("/transactions")
	List<Transaction> allTransactions() {
		return repository.findAll();
	}
	// end::get-aggregate-root[]
	
	
	// Returns balances sorted by payer
	@GetMapping("/balances")
	HashMap<String,Integer> getPointsBalanceSortedByPayer() {
		List<Transaction> allnewTransaction = this.allTransactions();
		HashMap<String,Integer> balancesByPayer = new HashMap<String,Integer>();
		for(Transaction temp : allnewTransaction) {
			String payer = temp.getPayer();
			int points = temp.getPoints();
			if(balancesByPayer.get(payer) != null){
				points = points + balancesByPayer.get(payer).intValue();
				balancesByPayer.put(payer, points);
			} else {
				balancesByPayer.put(payer, points);
			}
		}
		return balancesByPayer;
	}
	
	/* Spends points based on 2 rules:
    1. Oldest points spent first (oldest based on timestamp, not the order they’re received)
    2. No payer's points can go negative.
    Inputs:
        spend_points - integer of points to spend
    Returns:
        HashMap<String,Integer> of points needed to be spent sorted by each different payer
        { payer -> points spent }
    NOTES: 
    	If attempting to spend more points than are available, transaction does not get added to repository
    	If attempting to "spend" negative points (aka add points to balance), transaction does not get added to repository 
     */
	@PostMapping("/Spend/{totalPointsToSpend}")
	HashMap<String,Integer> spendPoints(@RequestBody int totalPointsToSpend){
		//Create resultSet
		HashMap<String,Integer> spendPointsByPayer = new HashMap<String,Integer>();
		
		//create counter for keeping track of points left to spend for this spend transaction
		int pointsLeftToSpend = totalPointsToSpend;
		
		//check if we are spending points and that the user has enough points to spend
		if(totalPointsToSpend > 0 && totalPointsToSpend <= getPointsBalance()) {
			
			while(pointsLeftToSpend > 0){
				
				//gets oldest transaction with points available from repository
				Transaction deductionTransaction = repository.findFirstTransactionOrderByTimestamp();
				
				//gets the total amount of points available for
				int pointsAvailable = deductionTransaction.getPointsAvailableToSpend();
				if(pointsLeftToSpend >= pointsAvailable) {
					pointsLeftToSpend = pointsLeftToSpend - pointsAvailable;
					spendPointsByPayer.put(deductionTransaction.getPayer(),pointsAvailable);
					pointsAvailable = 0;
					deductionTransaction.setPointsAvailableToSpend(pointsAvailable);
				} else if (pointsLeftToSpend < pointsAvailable) {
					pointsAvailable = pointsAvailable - pointsLeftToSpend; 
					spendPointsByPayer.put(deductionTransaction.getPayer(),pointsLeftToSpend);
					pointsLeftToSpend = 0;
					deductionTransaction.setPointsAvailableToSpend(pointsAvailable);
				} else {
					throw new RuntimeException("Error spending points");
				}
				
			}
			for(String i : spendPointsByPayer.keySet()) {
				Instant instant = Instant.now();
				Transaction newTransaction = new Transaction(i,spendPointsByPayer.get(i), instant);
				addTransactionToRepository(newTransaction);
			}
		} else if(totalPointsToSpend >= 0){
			throw new NotSpendException();
		} else if(totalPointsToSpend > getPointsBalance()){
			throw new InsufficientBalanceException();
		} else {
			throw new RuntimeException();
		}
		return spendPointsByPayer;
	}
	
	//Add Points
	@PostMapping("/addTransaction")
	public ResponseEntity<Transaction> addTransaction(@PathVariable int id, @RequestBody Transaction newTransaction){

        //Get all the newTransaction details
        newTransaction.setTimestamp(Instant.now());
        String payer = newTransaction.getPayer();
        int points = newTransaction.getPoints();

        
        //if points are positive add to transactionRepository
        if(points>0) addTransactionToRepository(newTransaction);

        //If points to be added are negative call the spend route
        else if(points<0) {
        	
        	//flip points from negative to positive in order to call spend route
        	points = points * -1;
    		//create counter for keeping track of points left to spend for this spend transaction
    		int pointsLeftToSpend = points;
    		
    		//check if we are spending points and that the user has enough points to spend
    		if(points > 0 && points <= getPointsBalance()) {
    			
    			while(pointsLeftToSpend > 0){
    				
    				//gets oldest transaction with points available from repository
    				Transaction deductionTransaction = repository.findFirstTransactionByPayerOrderByTimestamp(payer);
    				
    				//gets the total amount of points available for
    				int pointsAvailable = deductionTransaction.getPointsAvailableToSpend();
    				if(pointsLeftToSpend >= pointsAvailable) {
    					pointsLeftToSpend = pointsLeftToSpend - pointsAvailable;
    					pointsAvailable = 0;
    					deductionTransaction.setPointsAvailableToSpend(pointsAvailable);
    				} else if (pointsLeftToSpend < pointsAvailable) {
    					pointsAvailable = pointsAvailable - pointsLeftToSpend; 
    					pointsLeftToSpend = 0;
    					deductionTransaction.setPointsAvailableToSpend(pointsAvailable);
    				} else {
    					throw new RuntimeException("Error spending points");
    				}
    				
    			}
    			
    			addTransactionToRepository(newTransaction);
    			
    		} else if(points >= 0){
    			throw new NotSpendException();
    		} else if(points > getPointsBalance()){
    			throw new InsufficientBalanceException();
    		} else {
    			throw new RuntimeException();
    		}
        }
        else throw new RuntimeException("Invalid transaction record");
        
        return ResponseEntity.ok().body(newTransaction);
    }

	@DeleteMapping("/newTransaction/{id}")
	void deleteTransaction(@PathVariable Long id) {
		repository.deleteById(id);
	}
	
	// Retrieves User's total balance
	private int getPointsBalance() {
		int total = 0;
		for (int i : getPointsBalanceSortedByPayer().values()) {
			total = i + total;
		}
		return total;
	}
	
	private Transaction addTransactionToRepository(Transaction newTransaction) {		
		return repository.save(newTransaction);
	}
}

