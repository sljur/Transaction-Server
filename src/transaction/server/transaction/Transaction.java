package transaction.server.transaction;

import transaction.server.TransactionServer;
import java.util.ArrayList;
import java.util.HashMap;


/**
 *
 * @author wolfdieterotte
 */
public class Transaction {

	// transaction ID and OCC specific transaction numbers
	int transactionID;
	int transactionNumber;
	int lastAssignedTransactionNumber;

	// the sets of tentative data
	ArrayList<Integer>         readSet = new ArrayList<>();
	HashMap<Integer, Integer> writeSet = new HashMap<>();

	StringBuffer log = new StringBuffer("");


	Transaction(int transactionID, int lastCommittedTransactionNumber)
        {
            // save the transactionID and lastAssignedTransactionNumber
            // the latter is at the very foundation to make the whole OCC work!
            this.transactionID = transactionID;
            this.lastAssignedTransactionNumber = lastCommittedTransactionNumber;
	}

        
	public int read(int accountNumber) {
        Integer balance;

        // check if value to be read was written by this transaction
        // i.e. is contained in the writeSet of this transaction
        // use get() on the writeSet
        // ...
        if (writeSet.containsKey(accountNumber)) {
            balance = writeSet.get(accountNumber);
        } 

        // if it is not in the writeSet, read the committed version of it from AccountManager
        // note: null and numerical zero are not the same thing!
        // ...
        else {
            balance = TransactionServer.accountManager.read(accountNumber);
        }

        // check if this account number is already in the readSet
        // and add it, if not
        // ...
        if (!readSet.contains(accountNumber)) {
            readSet.add(accountNumber);
        }

        return balance;
    }


	public int write(int accountNumber, int newBalance) {
        int oldBalance;

        // read (and return) old balance
        // ...
        if (writeSet.containsKey(accountNumber)) {
            oldBalance = writeSet.get(accountNumber);
        } else {
            oldBalance = TransactionServer.accountManager.read(accountNumber); 
        }

        // put <accountNumber, newBalance> in writeSet
        // possibly overwriting a prior write
        // ...
        writeSet.put(accountNumber, newBalance);

        return oldBalance;

	}


	public ArrayList<Integer> getReadSet() {
        return readSet;
    }


	public HashMap<Integer, Integer> getWriteSet() {
        return writeSet;
    }


	public int getTransactionID() 
        {
            return transactionID;
	}


	public int getTransactionNumber() 
        {
            return transactionNumber;
	}


	public void setTransactionNumber(int transactionNumber) 
        {
            this.transactionNumber = transactionNumber;

	}


	public int getLastAssignedTransactionNumber() 
        {
            return lastAssignedTransactionNumber;
	}


	public void log (String logString) 
        {
            int messageCount = TransactionServer.getMessageCount();

            log.append("\n").append(messageCount).append(" ").append(logString);

            if (!TransactionServer.transactionView) 
            {
                System.out.println(messageCount + " Transaction #" + transactionID + " " + logString);            
            }
	}

	public String getLog()
        {
            return log.toString();
	}
}
