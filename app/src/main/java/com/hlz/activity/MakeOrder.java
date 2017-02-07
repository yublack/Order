package com.hlz.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hlz.order.R;
import com.hlz.util.CharacterParser;
import com.hlz.util.ClearEditText;
import com.hlz.util.PinyinComparator;
import com.hlz.util.SideBar;
import com.hlz.util.SortAdapter;
import com.hlz.util.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MakeOrder extends Activity {
    /**
     * 必要变量
     */
    private RelativeLayout realative;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;
    private TextView sumSize;//点菜总数
    private TextView sumPrice;//总钱数
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);
        realative=(RelativeLayout)findViewById(R.id.cart);
        initViews();
    }
    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }
            }
        });

        sortListView = (ListView)findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                ((SortModel)adapter.getItem(position)).getName();
                //Toast.makeText(getActivity(), ((SortModel)adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        /**
         * 这个函数接受一个数组作为数据源
         * 菜单应该是Map类型，在这里获取Map类型的key集合，并转化为数组形式
         * 因为是点菜宝，因此不设定显示价格，只在购物车附近显示总价格与菜品数量
         */
        SourceDateList = filledData(getResources().getStringArray(R.array.datetest));
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        /**
         * 在这里获取购物车、总菜数、总钱数的图标
         */
        sumSize=(TextView)findViewById(R.id.sumSize);
        sumPrice=(TextView)findViewById(R.id.sumPrice);
        ImageView order_cart = (ImageView) findViewById(R.id.order_cart);
        adapter = new SortAdapter(MakeOrder.this, SourceDateList, order_cart,realative,sumSize,sumPrice);
        sortListView.setAdapter(adapter);
        mClearEditText = (ClearEditText)findViewById(R.id.filter_edit);
        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 为ListView填充数据
     * @param date 填充数组
     * @return List
     */
    private List<SortModel> filledData(String [] date){
        List<SortModel> mSortList = new ArrayList<>();

        for (String aDate : date) {
            SortModel sortModel = new SortModel();
            sortModel.setName(aDate);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(aDate);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * 添加数据
     */
    private void filterData(String filterStr){
        List<SortModel> filterDateList = new ArrayList<>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList = SourceDateList;
        }else{
            filterDateList.clear();
            for(SortModel sortModel : SourceDateList){
                String name = sortModel.getName();
                if(name.contains(filterStr) || characterParser.getSelling(name).startsWith(filterStr)){
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }
}
