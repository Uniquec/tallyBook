package cn.edu.hznu.tallybook;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CategoryReportFragment extends Fragment {
    private static final String TAG = "CategoryReportFragment";

    private PieChart mChart;
    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    private Map<String, Float> pieData = new HashMap<>();

    private String month_value;
    private String year_value;
    private MyDatabaseHelper dbHelper;

    private List<Money> moneyList = new ArrayList<>();
    private MoneyAdapter adapter;
    private ListView listView;

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
        year_value = ((MainActivity) activity).getYear();
        if (activity instanceof DetailFragment.FragmentInteraction) {
            listterner = (DetailFragment.FragmentInteraction) activity; // 获取到宿主activity并赋值
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 设置字体
        mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mChart = (PieChart) getActivity().findViewById(R.id.report_pie_chart);

        // 初始设置图表
        initPieChart();

        // 图表项选择事件
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: " + e);
                Log.d(TAG, "onValueSelected: " + h);
            }

            @Override
            public void onNothingSelected() {
                Log.d(TAG, "onNothingSelected");
            }
        });

        //修改数据
        initData();

        //设置数据
        setData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_report, container, false);
        return view;
    }

    private void initPieChart() {
        // 设置 pieChart 图表基本属性
        mChart.setUsePercentValues(true);            //使用百分比显示
        mChart.getDescription().setEnabled(false);    //设置pieChart图表的描述
        mChart.setBackgroundColor(Color.WHITE);      //设置pieChart图表背景色
        mChart.setExtraOffsets(-80, 10, 0, 5);        //设置pieChart图表上下左右的偏移，类似于外边距
        mChart.setDragDecelerationFrictionCoef(0.95f);//设置pieChart图表转动阻力摩擦系数[0,1]
        mChart.setRotationAngle(0);                   //设置pieChart图表起始角度
        mChart.setRotationEnabled(true);              //设置pieChart图表是否可以手动旋转
        mChart.setHighlightPerTapEnabled(true);       //设置piecahrt图表点击Item高亮是否可用
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);// 设置pieChart图表展示动画效果

        // 设置 pieChart 图表Item文本属性
        mChart.setDrawEntryLabels(false);             //设置pieChart是否只显示饼图上百分比不显示文字（true：下面属性才有效果）
        mChart.setEntryLabelColor(Color.BLACK);       //设置pieChart图表文本字体颜色
        mChart.setEntryLabelTypeface(mTfRegular);    //设置pieChart图表文本字体样式
        mChart.setEntryLabelTextSize(12f);            //设置pieChart图表文本字体大小

        // 设置 pieChart 内部圆环属性
        mChart.setDrawHoleEnabled(true);              //是否显示PieChart内部圆环(true:下面属性才有意义)
        mChart.setHoleRadius(58f);                    //设置PieChart内部圆的半径(这里设置28.0f)
        mChart.setTransparentCircleRadius(61f);       //设置PieChart内部透明圆的半径(这里设置31.0f)
        mChart.setTransparentCircleColor(Color.WHITE);//设置PieChart内部透明圆与内部圆间距(31f-28f)填充颜色
        mChart.setTransparentCircleAlpha(110);         //设置PieChart内部透明圆与内部圆间距(31f-28f)透明度[0~255]数值越小越透明
        mChart.setHoleColor(Color.WHITE);             //设置PieChart内部圆的颜色
        mChart.setDrawCenterText(true);               //是否绘制PieChart内部中心文本（true：下面属性才有意义）
        mChart.setCenterTextTypeface(mTfLight);       //设置PieChart内部圆文字的字体样式
        mChart.setCenterText(generateCenterSpannableText());                 //设置PieChart内部圆文字的内容
        mChart.setCenterTextSize(10f);                //设置PieChart内部圆文字的大小
        mChart.setCenterTextColor(Color.RED);         //设置PieChart内部圆文字的颜色

        // 获取pieCahrt图列
        Legend l = mChart.getLegend();
        l.setEnabled(true);                    //是否启用图列（true：下面属性才有意义）
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setForm(Legend.LegendForm.DEFAULT); //设置图例的形状
        l.setFormSize(10);                    //设置图例的大小
        l.setFormToTextSpace(2f);            //设置每个图例实体中标签和形状之间的间距
        l.setDrawInside(true);
        l.setWordWrapEnabled(true);           //设置图列换行(注意使用影响性能,仅适用legend位于图表下面)
        l.setXEntrySpace(0f);                //设置图例实体之间延X轴的间距（setOrientation = HORIZONTAL有效）
        l.setYEntrySpace(0f);                 //设置图例实体之间延Y轴的间距（setOrientation = VERTICAL 有效）
        l.setXOffset(0f);                     //设置比例块X轴偏移量
        l.setYOffset(0f);                     //设置比例块Y轴偏移量
        l.setTextSize(12f);                   //设置图例标签文本的大小
        l.setTextColor(Color.parseColor("#ff9933"));//设置图例标签文本的颜色
    }

    public void initData() {
        Map<String, Float> temp = new HashMap<>();
        dbHelper = new MyDatabaseHelper(this.getActivity(), "MoneyList.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("money", null, "month = ? and year = ?", new String[]{"" + month_value, "" + year_value}, null, null, null);
        if (cursor.moveToNext()) {
            income_total = 0.0;
            expenditure_total = 0.0;
            do {
                int checkedTypes = cursor.getInt(cursor.getColumnIndex("checkTypes"));
                if (checkedTypes == 0) {
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    float price = Float.parseFloat(cursor.getString(cursor.getColumnIndex("price")));
                    if (temp.containsKey(type)) {
                        float f = temp.get(type);
                        temp.put(type, f + price);
                    } else {
                        temp.put(type, price);
                    }
                    expenditure_total += price;
                }else {
                    float price = Float.parseFloat(cursor.getString(cursor.getColumnIndex("price")));
                    income_total += price;
                }
            } while (cursor.moveToNext());
            listterner.process("" + expenditure_total, "" + income_total);  // 碎片调用方法 把数据传给活动
        }else{
            income_total = 0.0;
            expenditure_total = 0.0;
            listterner.process("" + expenditure_total, "" + income_total);  // 碎片调用方法 把数据传给活动

        }
        cursor.close();

        pieData.clear();
        for (Map.Entry<String, Float> entry : temp.entrySet()) {
            pieData.put(String.format("%s ￥%.2f", entry.getKey(), entry.getValue()), entry.getValue());
        }

    }

    public void setData() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (Map.Entry<String, Float> entry : pieData.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey(), R.drawable.star));
        }

        PieDataSet dataSet = new PieDataSet(entries, "支出类型");

        dataSet.setDrawIcons(true);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(mTfLight);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("月度报表");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), s.length(), s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), s.length(), s.length(), 0);
        s.setSpan(new RelativeSizeSpan(.8f), s.length(), s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length(), s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length(), s.length(), 0);
        return s;
    }

    public void setMonth_value(String month_value) {
        this.month_value = month_value;
    }

    public void setYear_value(String year_value) {
        this.year_value = year_value;
    }
}