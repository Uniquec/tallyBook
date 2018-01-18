package cn.edu.hznu.tallybook;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;


//修改数据 更新数据库
public class ModifyDataActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private String modify_account_value;      //用于记录所选账户
    private int t = 0;                          //用于判断记录的是支出还是收入  默认0为支出 1为收入
    private String modify_type_value;

    private EditText modify_type_edit;
    private EditText modify_price_edit;
    private Button modify_date_button;
    private RadioButton modify_expenditure_radiobtn;
    private RadioButton modify_income_radiobtn;
    private RadioButton modify_alipay_radiobtn;
    private RadioButton modify_cash_radiobtn;
    private RadioButton modify_bankcard_radiobtn;
    private RadioButton modify_usual_type;
    private RadioButton modify_food_type;
    private RadioButton modify_shopping_type;

    private int modify_year;
    private int modify_month;
    private int modify_day;
    private String chosen_year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_data);
        dbHelper = new MyDatabaseHelper(this, "MoneyList.db", null, 1);

        //接收传递的数据
        Intent intent = getIntent();
        final String data = intent.getStringExtra("extra_data");

        //因为是修改数据 所以一开始之前所设定的所有信息都应该显示在界面上
        inits(data);

        //日期选择按钮事件  DatePickerDialog
        modify_date_button = (Button) findViewById(R.id.modify_date_button);
       modify_date_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                   @Override
                   public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                       if (month < 9 && day < 10){
                           modify_date_button.setText("0" + (month + 1) + "-0" + day);
                       }else if(month < 9 && day >= 10){
                           modify_date_button.setText("0" + (month + 1) + "-" + day);
                       }else if (month >= 9 && day < 10){
                           modify_date_button.setText((month + 1) + "-0" + day);
                       }else {
                           modify_date_button.setText((month + 1) + "-" + day);
                       }
                       chosen_year = "" + year;
                   }
               };
               DatePickerDialog dialog = new DatePickerDialog(ModifyDataActivity.this, 0, listener, modify_year, modify_month, modify_day);
               dialog.show();
           }
       });

        //修改按钮的点击事件 对数据库进行更新操作
        Button modifyData = (Button) findViewById(R.id.modifyData);
        modifyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                EditText type = (EditText) findViewById(R.id.modify_type_edit);
                EditText price = (EditText) findViewById(R.id.modify_price_edit);
                String n = type.getText().toString();
                String p = price.getText().toString();
                String d = modify_date_button.getText().toString();

                String month = d.split("-")[0];
                String day = d.split("-")[1];

                double price_num = Double.parseDouble(p);

                //数据库中关于金额的字段是double类型，根据radiobutton的选择来判断是输入还是支出 分别计算总和
                if (modify_expenditure_radiobtn.isChecked()) {
                    t = 0;
                } else {
                    t = 1;
                }

                //根据radiobutton的选择来判断账户
                if (modify_alipay_radiobtn.isChecked()){
                    modify_account_value = modify_alipay_radiobtn.getText().toString();
                }else if (modify_cash_radiobtn.isChecked()){
                    modify_account_value = modify_cash_radiobtn.getText().toString();
                }else {
                    modify_account_value = modify_bankcard_radiobtn.getText().toString();
                }

                //根据radiobutton的选择来判断类型
                if (modify_usual_type.isChecked()){
                    modify_type_value = modify_usual_type.getText().toString();
                }else if (modify_food_type.isChecked()){
                    modify_type_value = modify_food_type.getText().toString();
                }else {
                    modify_type_value = modify_shopping_type.getText().toString();
                }

                //数据库更新
                if ((!(n.equals(""))) && (!(p.equals(null))) && (!(d.equals("")))) {
                    values.put("type",modify_type_value);
                    values.put("notes", n);
                    values.put("price", price_num);
                    values.put("year",chosen_year);
                    values.put("month", month);
                    values.put("day",day);
                    values.put("checkTypes", t);
                    values.put("account", modify_account_value);
                }

                db.update("money", values, "id=?",new String[]{data});
                values.clear();
                finish();
            }
        });
    }

    //点击ListView中的item进入新的界面 所有值都是原先所拥有的
    //根据上一个活动传递过来的数据 找到特定的字段并显示在界面上
    private void inits(String data) {
        dbHelper = new MyDatabaseHelper(ModifyDataActivity.this, "MoneyList.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("money", null, "id = ?", new String[]{data}, null, null, null);
        if (cursor.moveToFirst()){
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String name = cursor.getString(cursor.getColumnIndex("notes"));
            String price = cursor.getString(cursor.getColumnIndex("price"));
            String year = cursor.getString(cursor.getColumnIndex("year"));
            String month = cursor.getString(cursor.getColumnIndex("month"));
            String day = cursor.getString(cursor.getColumnIndex("day"));
            int checkedTypes = cursor.getInt(cursor.getColumnIndex("checkTypes"));
            String account = cursor.getString(cursor.getColumnIndex("account"));

            String date = month + "-" + day;
            chosen_year = year;

            modify_year = Integer.parseInt(year);
            modify_month = Integer.parseInt(month);
            modify_day = Integer.parseInt(day);

            modify_type_edit = (EditText) findViewById(R.id.modify_type_edit);
            modify_price_edit = (EditText) findViewById(R.id.modify_price_edit);
            modify_type_edit.setText(name);
            modify_price_edit.setText(price);

            modify_date_button = (Button) findViewById(R.id.modify_date_button);
            modify_date_button.setText(date);


            modify_expenditure_radiobtn = (RadioButton) findViewById(R.id.modify_expenditure_radiobtn);
            modify_income_radiobtn = (RadioButton) findViewById(R.id.modify_income_radiobtn);
            if (checkedTypes == 1) {
                modify_expenditure_radiobtn.setChecked(false);
                modify_income_radiobtn.setChecked(true);
            } else {
                modify_expenditure_radiobtn.setChecked(true);
                modify_income_radiobtn.setChecked(false);
            }

            modify_alipay_radiobtn = (RadioButton) findViewById(R.id.modify_alipay_radiobtn);
            modify_cash_radiobtn = (RadioButton) findViewById(R.id.modify_cash_radiobtn);
            modify_bankcard_radiobtn = (RadioButton) findViewById(R.id.modify_bankcard_radiobtn);

            if (account.equals("支付宝")) {
                modify_alipay_radiobtn.setChecked(true);
                modify_cash_radiobtn.setChecked(false);
                modify_bankcard_radiobtn.setChecked(false);
            } else if (account.equals("现金")) {
                modify_alipay_radiobtn.setChecked(false);
                modify_cash_radiobtn.setChecked(true);
                modify_bankcard_radiobtn.setChecked(false);
            } else {
                modify_alipay_radiobtn.setChecked(false);
                modify_cash_radiobtn.setChecked(false);
                modify_bankcard_radiobtn.setChecked(true);
            }

            modify_usual_type = (RadioButton) findViewById(R.id.modify_usual_type);
            modify_food_type = (RadioButton) findViewById(R.id.modify_food_type);
            modify_shopping_type = (RadioButton) findViewById(R.id.modify_shopping_type);

            if (type.equals("其它")) {
                modify_usual_type.setChecked(true);
                modify_food_type.setChecked(false);
                modify_shopping_type.setChecked(false);
            } else if (type.equals("餐饮")) {
                modify_usual_type.setChecked(false);
                modify_food_type.setChecked(true);
                modify_shopping_type.setChecked(false);
            } else {
                modify_usual_type.setChecked(false);
                modify_food_type.setChecked(false);
                modify_shopping_type.setChecked(true);
            }
        }
    }
}