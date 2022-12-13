package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SqLiteHandler.ACCOUNT_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SqLiteHandler.AMOUNT;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SqLiteHandler.DATE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SqLiteHandler.TABLE_LOGS;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SqLiteHandler.TYPE;

public class PersistentTransactionDAO implements TransactionDAO {
    private final List<Transaction> transactions;

    private final SqLiteHandler handler;
    private SQLiteDatabase sqLiteDatabase;

    public PersistentTransactionDAO(Context context) throws ParseException {
        transactions = new LinkedList<>();
        handler  = new SqLiteHandler(context);

        sqLiteDatabase = handler.getReadableDatabase();


        String[] projection = {
                DATE,
                ACCOUNT_NO,
                TYPE,
                AMOUNT
        };

        Cursor cursor = sqLiteDatabase.query(
                TABLE_LOGS,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {

            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(cursor.getString(cursor.getColumnIndex(DATE)));
            ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex(TYPE)));

            Transaction transaction = new Transaction(
                    date,
                    cursor.getString(cursor.getColumnIndex(ACCOUNT_NO)),
                    expenseType,
                    cursor.getDouble(cursor.getColumnIndex(AMOUNT)));

            transactions.add(transaction);
        }
        cursor.close();
    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        transactions.add(transaction);

        sqLiteDatabase = handler.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        contentValues.put(DATE, formatter.format(date));
        contentValues.put(ACCOUNT_NO, accountNo);
        contentValues.put(TYPE, String.valueOf(expenseType));
        contentValues.put(AMOUNT,amount);

        sqLiteDatabase.insert(TABLE_LOGS,null,contentValues);
        sqLiteDatabase.close();
    }


    @Override
    public List<Transaction> getAllTransactionLogs() {
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
