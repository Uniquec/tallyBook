package cn.edu.hznu.tallybook;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    private static final String TAG = "DetailFragment";
    private List<Money> moneyList = new ArrayList<>();
    private MyDatabaseHelper dbHelper;
    private MoneyAdapter adapter;
    private ListView listView;
    private double expenditure_total = 0;  //用于计算支出总和
    private double income_total = 0;       //用于计算收入总和

    private String month_value;
    private String year_value;


    //碎片向活动MainActivity传递数据
    //碎片定义了一个内部回调接口
    private FragmentInteraction listterner;

    public interface FragmentInteraction {
        void process(String str1, String str2);
    }

    //onAttach()方法获取接口的实例并赋值 检查activity是否实现了OnArticleSelectedListener接口
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        month_value = ((MainActivity) activity).getMonth();   ////通过强转成宿主activity，就可以获取到MainActivity传递过来的数据
        year_value = ((MainActivity) activity).getYear();
        Log.d(TAG, "onAttach: " + year_value + "  " + month_value);
        if (activity instanceof FragmentInteraction) {
            listterner = (FragmentInteraction) activity; // 获取到宿主activity并赋值
        } else {
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    //释放传递进来的Activity对象 不然会影响Activity的销毁
    @Override
    public void onDetach() {
        super.onDetach();
        listterner = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new MoneyAdapter(this.getActivity(), R.layout.money_list, moneyList);
        initLists();
        listView = (ListView) getActivity().findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        //listView的点击事件：进入新活动修改数据更新数据库
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //传递id并启动修改数据的活动
                Money money = moneyList.get(position);
                String data = "" + money.getId();
                Intent intent = new Intent(DetailFragment.this.getActivity(), ModifyDataActivity.class);
                intent.putExtra("extra_data", data);
                startActivity(intent);
            }
        });

        //listview长按事件：跳出对话框询问是否删除数据，点击确定删除数据点击取消回到原来界面
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //定义AlertDialog.Builder对象
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailFragment.this.getActivity());
                builder.setTitle("温馨提示");
                builder.setMessage("确定删除数据么？");

                //添加对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Money money = moneyList.get(position);
                        delete(money.getId());
                    }
                });

                //添加对象的setNegativeButton()方法
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
                return true;
            }
        });

        //添加数据按钮的点击事件：进入新活动增加数据
        Button add_btn = (Button) getActivity().findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailFragment.this.getActivity(), AddDataActivity.class);
                startActivity(intent);
            }
        });


    }

    //通过查询数据库绘制ListView
    public void initLists() {
        dbHelper = new MyDatabaseHelper(this.getActivity(), "MoneyList.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("money", null, "month = ? and year = ?", new String[]{"" + month_value, "" + year_value}, null, null, "day desc");
        Log.d(TAG, "initLists: " + year_value + "  " + month_value);
        moneyList.clear();
        if (cursor.moveToNext()) {
            income_total = 0.0;
            expenditure_total = 0.0;
            do {
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String name = cursor.getString(cursor.getColumnIndex("notes"));
                String price = cursor.getString(cursor.getColumnIndex("price"));
                String month = cursor.getString(cursor.getColumnIndex("month"));
                String day = cursor.getString(cursor.getColumnIndex("day"));
                int checkedTypes = cursor.getInt(cursor.getColumnIndex("checkTypes"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));

                String date = month + "-" + day;

                Money money = new Money();
                if (checkedTypes == 1) {
                    income_total += Double.parseDouble(price);
                    price = "+" + price;
                } else {
                    expenditure_total += Double.parseDouble(price);
                    price = "-" + price;
                }
                money.setType(type);
                money.setSource(name);
                money.setPrice(price);
                money.setDate(date);
                money.setId(id);
                moneyList.add(money);
            } while (cursor.moveToNext());
            adapter.notifyDataSetChanged();
            listterner.process("" + expenditure_total, "" + income_total);  // 碎片调用方法 把数据传给活动
        }else{
            income_total = 0.0;
            expenditure_total = 0.0;
            adapter.notifyDataSetChanged();
            listterner.process("" + expenditure_total, "" + income_total);  // 碎片调用方法 把数据传给活动

        }
        cursor.close();
    }

    //删除数据库的数据并重新绘制ListView
    private void delete(int id) {
        dbHelper = new MyDatabaseHelper(this.getActivity(), "MoneyList.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("money", "id=?", new String[]{"" + id});

        moneyList.clear();
        adapter = new MoneyAdapter(this.getActivity(), R.layout.money_list, moneyList);
        initLists();
        listView = (ListView) getActivity().findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    public void onResume() {
        super.onResume();
        moneyList.clear();
        adapter = new MoneyAdapter(this.getActivity(), R.layout.money_list, moneyList);
        initLists();
        listView = (ListView) getActivity().findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    public void setMonth_value(String month_value) {
        this.month_value = month_value;
    }

    public void setYear_value(String year_value) {
        this.year_value = year_value;
    }
}