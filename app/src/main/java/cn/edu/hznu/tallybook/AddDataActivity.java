package cn.edu.hznu.tallybook;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

public class AddDataActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private String account_value;      //用于记录所选账户
    private int t = 0;                   //用于判断记录的是支出还是收入  默认0为支出 1为收入
    private String type_value;

    private Button date_button;
    private int year;
    private int month;
    private int day;
    private int chosen_year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        dbHelper = new MyDatabaseHelper(this, "MoneyList.db", null, 1);
        Button addData = (Button) findViewById(R.id.addData);

        //在xml文件中设置radiobutton 默认选中支出选项
        final RadioButton expenditure_radiobtn = (RadioButton) findViewById(R.id.expenditure_radiobtn);
        final RadioButton income_radiobtn = (RadioButton) findViewById(R.id.income_radiobtn);

        //因为在xml文件中设置了默认选中项，所以当点击收入选项的时候支出选项的checked状态改为false
        income_radiobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenditure_radiobtn.setChecked(false);
                income_radiobtn.setChecked(true);
            }
        });

        //在xml文件中设置radiobutton默认选中类型为一般选项
        final RadioButton usual_type = (RadioButton) findViewById(R.id.usual_type);
        final RadioButton food_type = (RadioButton) findViewById(R.id.food_type);
        final RadioButton shopping_type = (RadioButton) findViewById(R.id.shopping_type);

        type_value = usual_type.getText().toString();

        food_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usual_type.setChecked(false);
                food_type.setChecked(true);
                shopping_type.setChecked(false);
                type_value = food_type.getText().toString();
            }
        });

        shopping_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usual_type.setChecked(false);
                food_type.setChecked(false);
                shopping_type.setChecked(true);
                type_value = shopping_type.getText().toString();
            }
        });

        //在xml文件中设置radiobutton默认选中账户为支付宝选项
        final RadioButton alipay_radiobtn = (RadioButton) findViewById(R.id.alipay_radiobtn);
        final RadioButton cash_radiobtn = (RadioButton) findViewById(R.id.cash_radiobtn);
        final RadioButton bankcard_radiobtn = (RadioButton) findViewById(R.id.bankcard_radiobtn);

        account_value = alipay_radiobtn.getText().toString();

        cash_radiobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alipay_radiobtn.setChecked(false);
                cash_radiobtn.setChecked(true);
                bankcard_radiobtn.setChecked(false);
                account_value = cash_radiobtn.getText().toString();
            }
        });

        bankcard_radiobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alipay_radiobtn.setChecked(false);
                cash_radiobtn.setChecked(false);
                bankcard_radiobtn.setChecked(true);
                account_value = bankcard_radiobtn.getText().toString();
            }
        });

        //日期选择按钮事件  DatePickerDialog
        date_button = (Button) findViewById(R.id.date_button);
        //一开始显示当前日期
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        chosen_year = year;
        date_button.setText((month + 1) + "-" + day);
        //按钮事件：点击按钮跳出选择时间的对话框 选择日期后点击确定所选择的日期会显示在按钮上
        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        if (month < 9 && day < 10){
                            date_button.setText("0" + (month + 1) + "-0" + day);
                        }else if(month < 9 && day >= 10){
                            date_button.setText("0" + (month + 1) + "-" + day);
                        }else if (month >= 9 && day < 10){
                            date_button.setText((month + 1) + "-0" + day);
                        }else {
                            date_button.setText((month + 1) + "-" + day);
                        }
                        chosen_year = year;
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(AddDataActivity.this, 0, listener, year, month, day);
                dialog.show();
            }
        });

        //添加按钮的点击事件：对数据库进行增加数据的操作
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                EditText type = (EditText) findViewById(R.id.type_edit);
                EditText price = (EditText) findViewById(R.id.price_edit);
                Button date_btn = (Button) findViewById(R.id.date_button);
                String n = type.getText().toString();
                String p = price.getText().toString();
                String d = date_btn.getText().toString();

                String month = d.split("-")[0];
                String day = d.split("-")[1];

                double price_num = Double.parseDouble(p);

                //数据库中关于金额的字段是int类型，根据radiobutton的选择来判断是输入还是支出 分别计算总和
                if (expenditure_radiobtn.isChecked()) {
                    t = 0;
                } else {
                    t = 1;
                }

                if ((!(n.equals(""))) && (!(p.equals(null))) && (!(d.equals("")))) {
                    values.put("type",type_value);
                    values.put("notes", n);
                    values.put("price", price_num);
                    values.put("year",chosen_year);
                    values.put("month", month);
                    values.put("day",day);
                    values.put("checkTypes", t);
                    values.put("account", account_value);
                }

                db.insert("money", null, values);
                values.clear();
                finish();
            }
        });
    }
}