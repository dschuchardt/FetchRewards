package com.fetch;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {
	
	Logger logger = LoggerFactory.getLogger(TransactionController.class);
	
	static final int ZERO = 0;
	private final UserTransactionRepository repository;

	TransactionController(UserTransactionRepository repository) {
		this.repository = repository;
	}


	// Aggregate root
	// tag::get-aggregate-root[]
	@GetMapping("/all")
	List<Transaction> allTransactions() {
		return repository.findAll();
	}
	// end::get-aggregate-root[]
	
	
	// Returns balances sorted by payer
	@GetMapping("/balances")
	HashMap<String,Integer> getPointsBalanceSortedByPayer() {
		List<Transaction> allTransactions = this.allTransactions();
		HashMap<String,Integer> balancesByPayer = new HashMap<String,Integer>();
		for(Transaction temp : allTransactions) {
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
	@PostMapping("/spend/{pointsToSpend}")
	ArrayList<Pair> spendPoints(@PathVariable int pointsToSpend){
		//Create resultSet
		ArrayList<Pair> spendPointsByPayer = new ArrayList<Pair>();
		
		//create counter for keeping track of points left to spend for this spend transaction
		int pointsLeftToSpend = pointsToSpend;
		
		//check if we are spending points and that the user has enough points to spend
		if(pointsToSpend > 0 && pointsToSpend <= getPointsBalance()) {
			while(pointsLeftToSpend > 0){
				
				
				//gets oldest transaction with points available from repository
				Transaction deductionTransaction = repository.findTopByPointsAvailableToSpendGreaterThanOrderByTimestampAsc(ZERO);
				
				//gets the total amount of points available for
				int pointsAvailable = deductionTransaction.getPointsAvailableToSpend();
				if(pointsLeftToSpend >= pointsAvailable) {
					pointsLeftToSpend = pointsLeftToSpend - pointsAvailable;
					Pair result = new Pair(deductionTransaction.getPayer(),-1 * pointsAvailable);
					spendPointsByPayer.add(result);
					logger.info("spendPointsByPayer " + spendPointsByPayer + " pointsLeftToSpend " + pointsLeftToSpend + " pointsAvailable "+ pointsAvailable);
					pointsAvailable = 0;
					deductionTransaction.setPointsAvailableToSpend(pointsAvailable);
					repository.save(deductionTransaction);
				} else if (pointsLeftToSpend < pointsAvailable) {
					pointsAvailable = pointsAvailable - pointsLeftToSpend;
					Pair result = new Pair(deductionTransaction.getPayer(),-1* pointsLeftToSpend);
					spendPointsByPayer.add(result);
					pointsLeftToSpend = 0;
					deductionTransaction.setPointsAvailableToSpend(pointsAvailable);
					repository.save(deductionTransaction);
				} else throw new RuntimeException("Error spending points");
				
				
			}
			for (int i = 0; i < spendPointsByPayer.size(); i++) {
				//get the first payer/points pair
				String payer = spendPointsByPayer.get(i).getPayer();
				int points = spendPointsByPayer.get(i).getPoints();
				
				//get current timestamp
				Instant instant = Instant.now();
				Transaction newTransaction = new Transaction(payer, points, instant);
				addTransactionToRepository(newTransaction);
			}
		} else if(pointsToSpend <= 0){
			throw new NotSpendException();
		} else if(pointsToSpend > getPointsBalance()){
			throw new InsufficientBalanceException();
		} else {
			throw new RuntimeException();
		}
		return spendPointsByPayer;
	}
	
	//Add Points
	@PostMapping("/addTransaction")
	public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction newTransaction){
		
        //Get all the newTransaction details
        //newTransaction.setTimestamp(Instant.now());
        String payer = newTransaction.getPayer();
        int points = newTransaction.getPoints();
        newTransaction.setPointsAvailableToSpend(points);
        
        if(points< 0) newTransaction.setPointsAvailableToSpend(ZERO);
        if(newTransaction.getTimestamp().compareTo(Instant.now()) > 0) throw new InvalidDateException(); 
        
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
    				Transaction deductionTransaction = repository.findTopByPointsAvailableToSpendGreaterThanAndPayerOrderByTimestampAsc(ZERO, payer);
    				
    				//gets the total amount of points available for
    				int pointsAvailable = deductionTransaction.getPointsAvailableToSpend();
    				if(pointsLeftToSpend >= pointsAvailable) {
    					pointsLeftToSpend = pointsLeftToSpend - pointsAvailable;
    					pointsAvailable = 0;
    					deductionTransaction.setPointsAvailableToSpend(pointsAvailable);
    					repository.save(deductionTransaction);
    				} else if (pointsLeftToSpend < pointsAvailable) {
    					pointsAvailable = pointsAvailable - pointsLeftToSpend; 
    					pointsLeftToSpend = 0;
    					deductionTransaction.setPointsAvailableToSpend(pointsAvailable);
    					repository.save(deductionTransaction);
    				} else {
    					throw new RuntimeException("Error spending points");
    				}
    				
    			}
    			logger.info("newTransaction: " + newTransaction);
    			addTransactionToRepository(newTransaction);
    			
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

