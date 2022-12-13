package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SqLiteHandler.ACCOUNT_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SqLiteHandler.BALANCE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SqLiteHandler.BANK;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SqLiteHandler.NAME;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SqLiteHandler.TABLE_ACCOUNT;

public class PersistentAccountDAO implements AccountDAO {
    private final Map<String, Account> accounts;

    private final SqLiteHandler handler;
    private SQLiteDatabase sqLiteDatabase;

    public PersistentAccountDAO(Context context) {

        this.accounts = new HashMap<>();

        handler = new SqLiteHandler(context);
        sqLiteDatabase = handler.getReadableDatabase();
        String[] columns = {
                ACCOUNT_NO,
                BANK,
                NAME,
                BALANCE
        };
        Cursor cursor = sqLiteDatabase.query(
                TABLE_ACCOUNT,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {
            Account account = new Account(
                    cursor.getString(cursor.getColumnIndex(ACCOUNT_NO)),
                    cursor.getString(cursor.getColumnIndex(BANK)),
                    cursor.getString(cursor.getColumnIndex(NAME)),
                    cursor.getDouble(cursor.getColumnIndex(BALANCE))
            );
            this.accounts.put(account.getAccountNo(), account);
        }
        cursor.close();
    }

    @Override
    public List<String> getAccountNumbersList() {
        return new ArrayList<>(accounts.keySet());
    }

    @Override
    public List<Account> getAccountsList() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        if (accounts.containsKey(accountNo)) {
            return accounts.get(accountNo);
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {

        this.accounts.put(account.getAccountNo(), account);

        ContentValues contentValues = new ContentValues();

        contentValues.put(ACCOUNT_NO,account.getAccountNo());
        contentValues.put(BANK,account.getBankName());
        contentValues.put(NAME,account.getAccountHolderName());
        contentValues.put(BALANCE,account.getBalance());

        sqLiteDatabase.insert(TABLE_ACCOUNT,null,contentValues);
        sqLiteDatabase.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        accounts.remove(accountNo);

        sqLiteDatabase = handler.getWritableDatabase();
        String[] accountToDelete = {accountNo};
        sqLiteDatabase.delete(TABLE_ACCOUNT, ACCOUNT_NO + " = ?", accountToDelete);
        sqLiteDatabase.close();


    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        Account account = accounts.get(accountNo);
        // specific implementation based on the transaction type
        double balance = account.getBalance();
        switch (expenseType) {
            case EXPENSE:
                balance -= amount;
                break;
            case INCOME:
                balance += amount;
                break;
        }
        account.setBalance(balance);
        accounts.put(accountNo, account);

        ContentValues contentValues = new ContentValues();
        contentValues.put(BALANCE,account.getBalance());

        sqLiteDatabase = handler.getWritableDatabase();
        String[] projection = {
                BALANCE
        };

        String[] accountsToUpdate = { accountNo };
        sqLiteDatabase.update(TABLE_ACCOUNT, contentValues, ACCOUNT_NO + " = ?", accountsToUpdate);
        sqLiteDatabase.close();


    }
}
