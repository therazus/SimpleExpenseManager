package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.xml.namespace.QName;

public class SqLiteHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "db.mini";
    private static final int VERSION = 1;

    public static final String TABLE_ACCOUNT = "Account";
    public static final String TABLE_LOGS = "log";

    public static final String ACCOUNT_NO = "AccNo";
    public static final String NAME = "Name";
    public static final String BANK = "Bank";
    public static final String BALANCE = "Balance";

    public static final String ID = "ID";
    public static final String DATE = "Date";
    public static final String TYPE = "Type";
    public static final String AMOUNT = "Amount";

    public SqLiteHandler(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String LOGS_TABLE_QUERY = "CREATE TABLE " + TABLE_LOGS + " "
                + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ACCOUNT_NO + " TEXT,"
                + DATE + " TEXT NOT NULL, "
                + TYPE + " TEXT NOT NULL, "
                + AMOUNT + " REAL NOT NULL, " + "FOREIGN KEY (" + ACCOUNT_NO + ") REFERENCES " + TABLE_ACCOUNT + "(" + ACCOUNT_NO + "))";

        sqLiteDatabase.execSQL(LOGS_TABLE_QUERY);

        String ACCOUNT_TABLE_QUERY = "CREATE TABLE " + TABLE_ACCOUNT + " "
                                    + "("
                                    + ACCOUNT_NO + " TEXT PRIMARY KEY, "
                                    + NAME + " TEXT NOT NULL, "
                                    + BANK + " TEXT NOT NULL, "
                                    + BALANCE + " REAL NOT NULL"
                                    + ");";

        sqLiteDatabase.execSQL(ACCOUNT_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldV, int newV) {

        String DROP_TABLE_ACC = "DROP TABLE IF EXISTS " + TABLE_ACCOUNT ;

        String DROP_TABLE_LOGS = "DROP TABLE IF EXISTS " + TABLE_LOGS ;

        sqLiteDatabase.execSQL(DROP_TABLE_ACC);
        sqLiteDatabase.execSQL(DROP_TABLE_LOGS);
        onCreate(sqLiteDatabase);
    }
}
