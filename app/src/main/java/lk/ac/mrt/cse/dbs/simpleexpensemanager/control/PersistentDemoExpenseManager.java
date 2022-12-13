package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import java.text.ParseException;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

public class PersistentDemoExpenseManager extends ExpenseManager{


    @Override
    public void setup(Context context) throws ParseException {

        AccountDAO persistentAccountDAO = new PersistentAccountDAO(context);
        setAccountsDAO(persistentAccountDAO);


        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(context);
        setTransactionsDAO(persistentTransactionDAO);

    }

    public PersistentDemoExpenseManager(Context context) throws ParseException {
        super(context);
        setup(context);
    }
}
