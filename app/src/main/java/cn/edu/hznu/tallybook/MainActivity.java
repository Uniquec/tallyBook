package cn.edu.hznu.tallybook;

import android.app.DatePickerDialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DetailFragment.FragmentInteraction {
    private static final String TAG = "MainActivity";
    private Button tab1Layout, tab2Layout, tab3Layout;
    private int index = 1;
    private FragmentManager fragmentManager;
    private Fragment tab1Fragment, tab2Fragment, tab3Fragment;
    private TextView expenditure;
    private TextView income;

    private LinearLayout month_select;
    private TextView set_month;
    private TextView set_year;

    private int year;
    private int month;
    private int day;

    private Dialog dialog;

    private Button sub_year;
    private Button add_year;
    private Button sub_month;
    private Button add_month;
    private TextView choose_year;
    private TextView choose_month;
    private Button cancel;
    private Button confirm;

    private String[] selectable_month = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private int temp = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        init();

        expenditure = (TextView) findViewById(R.id.expenditure);
        income = (TextView) findViewById(R.id.income);

        month_select = (LinearLayout) findViewById(R.id.month_select);
        set_month = (TextView) findViewById(R.id.month);
        set_year = (TextView) findViewById(R.id.year);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        set_year.setText(year + "年");
        if (month < 9) {
            set_month.setText("0" + (month + 1));
        } else {
            set_month.setText("" + (month + 1));
        }

        //年月选择的点击事件  因为网上找到的插件只能在模拟器上使用 在真机中会出现问题 所以自己写了一个简单的年月选择
        month_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setMonth(view);
                dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_month_picker);
                dialog.setTitle("Login:");
                WindowManager.LayoutParams a = dialog.getWindow().getAttributes();
                a.dimAmount = 0;
                dialog.getWindow().setAttributes(a);
                dialog.setCancelable(true);

                sub_year = (Button) dialog.findViewById(R.id.sub_yaer);
                add_year = (Button) dialog.findViewById(R.id.add_year);
                sub_month = (Button) dialog.findViewById(R.id.sub_month);
                add_month = (Button) dialog.findViewById(R.id.add_month);
                choose_year = (TextView) dialog.findViewById(R.id.choose_year);
                choose_month = (TextView) dialog.findViewById(R.id.choose_month);
                cancel = (Button) dialog.findViewById(R.id.cancel);
                confirm = (Button) dialog.findViewById(R.id.confirm);

                String str = set_year.getText().toString();
                choose_year.setText(str.substring(0, str.length() - 1));
                choose_month.setText(set_month.getText().toString());

                add_month.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        temp++;
                        choose_month.setText(selectable_month[temp % 12]);
                    }
                });

                sub_month.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        temp--;
                        if (temp < 0) {
                            temp += 12;
                        }
                        choose_month.setText(selectable_month[temp % 12]);
                    }
                });

                add_year.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int newyear = ++year;
                        choose_year.setText("" + newyear);
                    }
                });

                sub_year.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int newyear = --year;
                        choose_year.setText("" + newyear);
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        set_year.setText(choose_year.getText().toString() + "年");
                        set_month.setText(choose_month.getText().toString());
                        dialog.dismiss();

                    }
                });
                dialog.show();
            }
        });

        //只对month_select进行按钮点击事件 年月改变了但碎片里显示的内容不会发生改变
        //所以对年月的TextView进行事件监听
        set_month.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s);
                if (index == 1 && tab1Fragment instanceof DetailFragment) {
                    DetailFragment detailFragment = (DetailFragment) tab1Fragment;
                    detailFragment.setMonth_value(s.toString());
                    detailFragment.initLists();
                }

                if (index == 2 && tab2Fragment instanceof CategoryReportFragment) {
                    CategoryReportFragment categoryReportFragment = (CategoryReportFragment) tab2Fragment;
                    categoryReportFragment.setMonth_value(s.toString());
                    categoryReportFragment.initData();
                    categoryReportFragment.setData();
                }

                if (index == 3 && tab3Fragment instanceof AccountFragment) {
                    AccountFragment accountFragment = (AccountFragment) tab3Fragment;
                    accountFragment.setMonth_value(s.toString());
                    accountFragment.inits();
                    accountFragment.inits1();
                    accountFragment.inits2();
                    accountFragment.inits3();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { //屏蔽回车 中英文空格

            }
        });
        set_year.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s);
                if (index == 1 && tab1Fragment instanceof DetailFragment) {
                    DetailFragment detailFragment = (DetailFragment) tab1Fragment;
                    detailFragment.setYear_value(s.toString().substring(0,4));
                    detailFragment.initLists();
                }

                if (index == 2 && tab2Fragment instanceof CategoryReportFragment) {
                    CategoryReportFragment categoryReportFragment = (CategoryReportFragment) tab2Fragment;
                    categoryReportFragment.setYear_value(s.toString().substring(0,4));
                    categoryReportFragment.initData();
                    categoryReportFragment.setData();
                }

                if (index == 3 && tab3Fragment instanceof AccountFragment) {
                    AccountFragment accountFragment = (AccountFragment) tab3Fragment;
                    accountFragment.setYear_value(s.toString().substring(0,4));
                    accountFragment.inits();
                    accountFragment.inits1();
                    accountFragment.inits2();
                    accountFragment.inits3();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { //屏蔽回车 中英文空格

            }
        });


    }

    //因为支出总和和收入总和的布局是在MainActivity里面，碎片里不能直接进行操作 所以让碎片向活动传递数据
    //碎片定义了一个内部回调接口 活动实现这个接口并在接口里对传过来的数据进行操作
    @Override
    public void process(String str1, String str2) {
        if ((str1 != null) && (str2 != null)) {
            expenditure.setText(str1);
            income.setText(str2);
        }

    }

    private void init() {
        tab1Layout = (Button) findViewById(R.id.detail_btn);
        tab2Layout = (Button) findViewById(R.id.category_report_btn);
        tab3Layout = (Button) findViewById(R.id.account_btn);

        tab1Layout.setOnClickListener(this);
        tab2Layout.setOnClickListener(this);
        tab3Layout.setOnClickListener(this);

        tab1Layout.setBackgroundColor(getResources().getColor(R.color.tab_down));
        tab2Layout.setBackgroundColor(getResources().getColor(R.color.tab));
        tab3Layout.setBackgroundColor(getResources().getColor(R.color.tab));
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        tab1Fragment = new DetailFragment();
        transaction.replace(R.id.main_layout, tab1Fragment);
        transaction.commit();
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!newFragment.isAdded()) {
            transaction.replace(R.id.main_layout, newFragment);
            transaction.commit();
        } else {
            transaction.show(newFragment);
        }
    }

    private void clearStatus() {
        if (index == 1) {
            tab1Layout.setBackgroundColor(getResources().getColor(R.color.tab));
        } else if (index == 2) {
            tab2Layout.setBackgroundColor(getResources().getColor(R.color.tab));
        } else if (index == 3) {
            tab3Layout.setBackgroundColor(getResources().getColor(R.color.tab));
        }
    }

    @Override
    public void onClick(View v) {
        clearStatus();
        switch (v.getId()) {
            case R.id.detail_btn:
                if (tab1Fragment == null) {
                    tab1Fragment = new DetailFragment();
                }
                replaceFragment(tab1Fragment);
                tab1Layout.setBackgroundColor(getResources().getColor(R.color.tab_down));
                index = 1;
                break;
            case R.id.category_report_btn:
                if (tab2Fragment == null) {
                    tab2Fragment = new CategoryReportFragment();
                }
                replaceFragment(tab2Fragment);
                tab2Layout.setBackgroundColor(getResources().getColor(R.color.tab_down));
                index = 2;
                break;
            case R.id.account_btn:
                if (tab3Fragment == null) {
                    tab3Fragment = new AccountFragment();
                }
                replaceFragment(tab3Fragment);
                tab3Layout.setBackgroundColor(getResources().getColor(R.color.tab_down));
                index = 3;
                break;
        }
    }

    //月份选择
    public void setMonth(View view) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if (month < 9) {
                    set_month.setText("0" + (month + 1));
                } else {
                    set_month.setText("" + (month + 1));
                }
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Dialog, listener, year, month, day);
        // // TODO: 2017/12/15 0015  下面这行代码只在模拟器中有效
        dialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        dialog.show();
    }

    //将月份的值传递给DetailFragment 使列表显示对应月份的收支情况
    public String getMonth() {
        return set_month.getText().toString();
    }

    public String getYear() {
        String str = set_year.getText().toString();
        return str.substring(0, str.length() - 1);
//        return str;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }
}