package cn.edu.hznu.tallybook;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AccountFragment extends Fragment {
    private MyDatabaseHelper dbHelper;
    private TextView alipay_expenditure;
    private TextView alipy_income;
    private TextView cash_expenditure;
    private TextView cash_income;
    private TextView bankcard_expenditure;
    private TextView bankcard_income;
    private double alipay_expenditure_total = 0.0;
    private double alipay_income_total = 0.0;
    private double cash_expenditure_total = 0.0;
    private double cash_income_total = 0.0;
    private double bankcard_expenditure_total = 0.0;
    private double bankcard_income_total = 0.0;

    private String month_value;
    private String year_value;

    private double expenditure_total = 0;  //用于计算支出总和
    private double income_total = 0;       //用于计算收入总和

    //碎片向活动MainActivity传递数据
    //碎片定义了一个内部回调接口
    private DetailFragment.FragmentInteraction listterner;

    public interface FragmentInteraction {
        void process(String str1, String str2);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        month_value = ((MainActivity) activity).getMonth();   ////通过强转成宿主activity，就可以获取到MainActivity传递过来的数据
        year_value = ((MainActivity)activity).getYear();
        if (activity instanceof DetailFragment.FragmentInteraction) {
            listterner = (DetailFragment.FragmentInteraction) activity; // 获取到宿主activity并赋值
        } else {
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        alipay_expenditure = (TextView) getActivity().findViewById(R.id.alipay_expenditure);
        alipy_income = (TextView) getActivity().findViewById(R.id.alipy_income);
        cash_expenditure = (TextView) getActivity().findViewById(R.id.cash_expenditure);
        cash_income = (TextView) getActivity().findViewById(R.id.cash_income);
        bankcard_expenditure = (TextView) getActivity().findViewById(R.id.bankcard_expenditure);
        bankcard_income = (TextView) getActivity().findViewById(R.id.bankcard_income);
        inits();
        inits1();
        inits2();
        inits3();
    }

    public void inits(){
        dbHelper = new MyDatabaseHelper(this.getActivity(), "MoneyList.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("money", null, "month = ? and year = ?", new String[]{"" + month_value, "" + year_value}, null, null,null);
        if (cursor.moveToNext()) {
            income_total = 0.0;
            expenditure_total = 0.0;
            do {
                String price = cursor.getString(cursor.getColumnIndex("price"));
                int checkedTypes = cursor.getInt(cursor.getColumnIndex("checkTypes"));
                if (checkedTypes == 1) {
                    income_total += Double.parseDouble(price);
                } else {
                    expenditure_total += Double.parseDouble(price);
                }

            } while (cursor.moveToNext());
            listterner.process("" + expenditure_total, "" + income_total);  // 碎片调用方法 把数据传给活动
        }else{
            income_total = 0.0;
            expenditure_total = 0.0;
            listterner.process("" + expenditure_total, "" + income_total);  // 碎片调用方法 把数据传给活动

        }
        cursor.close();
    }

    public void inits1() {
        dbHelper = new MyDatabaseHelper(this.getActivity(), "MoneyList.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("money", null, "account = ? and month = ? and year = ?", new String[]{"支付宝", "" + month_value,"" + year_value}, null, null, null);
        alipay_expenditure_total = 0.0;
        alipay_income_total = 0.0;
        if (cursor.moveToNext()) {
            alipay_expenditure_total = 0.0;
            alipay_income_total = 0.0;
            do {
                String price = cursor.getString(cursor.getColumnIndex("price"));
                int checkedTypes = cursor.getInt(cursor.getColumnIndex("checkTypes"));

                if (checkedTypes == 1) {
                    alipay_income_total += Double.parseDouble(price);
                } else {
                    alipay_expenditure_total += Double.parseDouble(price);
                }
            } while (cursor.moveToNext());
            alipay_expenditure.setText("-" + alipay_expenditure_total);
            alipy_income.setText("+" + alipay_income_total);
        } else {
            alipay_expenditure.setText("-" + alipay_expenditure_total);
            alipy_income.setText("+" + alipay_income_total);
        }

        cursor.close();
    }

    public void inits2() {
        dbHelper = new MyDatabaseHelper(this.getActivity(), "MoneyList.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("money", null, "account = ? and month = ? and year = ?", new String[]{"现金", "" + month_value,"" + year_value}, null, null, null);
        cash_expenditure_total = 0.0;
        cash_income_total = 0.0;
        if (cursor.moveToNext()) {
            cash_expenditure_total = 0.0;
            cash_income_total = 0.0;
            do {
                String price = cursor.getString(cursor.getColumnIndex("price"));
                int checkedTypes = cursor.getInt(cursor.getColumnIndex("checkTypes"));

                if (checkedTypes == 1) {
                    cash_income_total += Double.parseDouble(price);
                } else {
                    cash_expenditure_total += Double.parseDouble(price);
                }
            } while (cursor.moveToNext());
            cash_expenditure.setText("-" + cash_expenditure_total);
            cash_income.setText("+" + cash_income_total);
        } else {
            cash_expenditure.setText("-" + cash_expenditure_total);
            cash_income.setText("+" + cash_income_total);
        }
        cursor.close();
    }

    public void inits3() {
        dbHelper = new MyDatabaseHelper(this.getActivity(), "MoneyList.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("money", null, "account = ? and month = ? and year = ?", new String[]{"银行卡", "" + month_value,"" + year_value}, null, null, null);
        bankcard_expenditure_total = 0.0;
        bankcard_income_total = 0.0;
        if (cursor.moveToNext()) {
            bankcard_expenditure_total = 0.0;
            bankcard_income_total = 0.0;
            do {
                String price = cursor.getString(cursor.getColumnIndex("price"));
                int checkedTypes = cursor.getInt(cursor.getColumnIndex("checkTypes"));

                if (checkedTypes == 1) {
                    bankcard_income_total += Double.parseDouble(price);
                } else {
                    bankcard_expenditure_total += Double.parseDouble(price);
                }
            } while (cursor.moveToNext());
            bankcard_expenditure.setText("-" + bankcard_expenditure_total);
            bankcard_income.setText("+" + bankcard_income_total);
        } else {
            bankcard_expenditure.setText("-" + bankcard_expenditure_total);
            bankcard_income.setText("+" + bankcard_income_total);
        }
        cursor.close();
    }

    public void setMonth_value(String month_value) {
        this.month_value = month_value;
    }

    public void setYear_value(String year_value) {
        this.year_value = year_value;
    }
}