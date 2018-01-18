package cn.edu.hznu.tallybook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper{
    public static final String CREATE_MONEY = "create table money("
            + "id integer primary key autoincrement,"
            + "type text,"
            + "notes text,"
            + "price double,"
            + "year text,"
            + "month text,"
            + "day text,"
            + "account text,"
            + "checkTypes integer)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_MONEY);   //建表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldversion,int newversion){}
}