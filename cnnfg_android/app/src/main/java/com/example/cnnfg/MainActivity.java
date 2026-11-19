package com.example.cnnfg;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class MainActivity extends Activity {
    private final String API = "https://production.dataviz.cnn.io/index/fearandgreed/graphdata/2021-02-01";
    private LinearLayout root, content;
    private TextView status, scoreText, ratingText, updatedText;
    private GaugeView gauge;
    private HistoryView history;
    private TextView[] comparisons;
    private final Handler handler = new Handler();
    private final ArrayList<Float> historyValues = new ArrayList<>();
    private Runnable autoRefresh;
    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        buildUI();
        refresh();
        autoRefresh = new Runnable() { @Override public void run() { refresh(); handler.postDelayed(this, 30 * 60 * 1000L); } };
        handler.postDelayed(autoRefresh, 30 * 60 * 1000L);
    }
    @Override protected void onDestroy() { handler.removeCallbacks(autoRefresh); networkExecutor.shutdownNow(); super.onDestroy(); }


    private void buildUI() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.rgb(11,16,32));
        ScrollView sc = new ScrollView(this); sc.setFillViewport(true);
        content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(20),dp(18),dp(20),dp(26));
        sc.addView(content); root.addView(sc); setContentView(root);
        TextView title = text("CNN FEAR & GREED", 20, Color.WHITE, true); title.setLetterSpacing(.04f); content.addView(title);
        TextView subtitle = text("美股市场情绪监控", 13, 0xFF8D9BB5, false); content.addView(subtitle);
        gauge = new GaugeView(this); LinearLayout.LayoutParams gp = new LinearLayout.LayoutParams(-1,dp(230)); gp.topMargin=dp(12); content.addView(gauge,gp);
        scoreText = text("--",54,Color.WHITE,true); scoreText.setGravity(Gravity.CENTER); content.addView(scoreText);
        ratingText = text("等待数据…",20,0xFF45D6A5,true); ratingText.setGravity(Gravity.CENTER); content.addView(ratingText);
        status = text("正在连接 CNN 数据源",12,0xFF8D9BB5,false); status.setGravity(Gravity.CENTER); content.addView(status);
        updatedText = text("",12,0xFF65738F,false); updatedText.setGravity(Gravity.CENTER); content.addView(updatedText);
        TextView compareTitle = text("情绪历史对比",17,Color.WHITE,true); compareTitle.setPadding(0,dp(24),0,dp(8)); content.addView(compareTitle);
        comparisons = new TextView[4]; String[] labels={"昨日收盘","一周前","一月前","一年前"};
        for(int i=0;i<4;i++){ comparisons[i]=text(labels[i]+"     --",14,0xFFCBD5E1,false); comparisons[i].setPadding(dp(4),dp(9),dp(4),dp(9)); content.addView(comparisons[i]); }
        TextView trendTitle = text("历史走势",17,Color.WHITE,true); trendTitle.setPadding(0,dp(24),0,dp(8)); content.addView(trendTitle);
        history = new HistoryView(this); content.addView(history,new LinearLayout.LayoutParams(-1,dp(180)));
        Button refreshBtn = new Button(this); refreshBtn.setText("刷新数据"); refreshBtn.setTextColor(Color.WHITE); refreshBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF263650)); refreshBtn.setOnClickListener(v->refresh()); content.addView(refreshBtn);
        TextView disclaimer = text("数据来自 CNN 官方页面接口（非官方 API）；仅供参考，不构成投资建议。",11,0xFF63728D,false); disclaimer.setPadding(0,dp(18),0,0); content.addView(disclaimer);
    }
    private TextView text(String s,int size,int color,boolean bold){ TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return t; }
    private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    private void refresh(){ status.setText("正在更新…"); networkExecutor.execute(()->{
        HttpURLConnection c=null;
        try {
            c=(HttpURLConnection)new URL(API).openConnection(); c.setConnectTimeout(15000); c.setReadTimeout(20000); c.setRequestProperty("User-Agent","Mozilla/5.0 (Android) CNNFearGreed/1.0"); c.setRequestProperty("Referer","https://www.cnn.com/markets/fear-and-greed"); c.setRequestProperty("Accept","application/json");
            int code=c.getResponseCode(); if(code<200||code>=300) throw new Exception("HTTP "+code);
            BufferedReader r=new BufferedReader(new InputStreamReader(c.getInputStream(),"UTF-8")); StringBuilder sb=new StringBuilder(); String line; while((line=r.readLine())!=null)sb.append(line); r.close(); JSONObject json=new JSONObject(sb.toString());
            JSONObject current=json.optJSONObject("fear_and_greed"); if(current==null)throw new Exception("CNN 返回格式发生变化");
            final double score=current.optDouble("score",Double.NaN); final String rating=current.optString("rating",ratingFor(score));
            final String timestamp=current.optString("timestamp",""); final double prev=current.optDouble("previous_close",Double.NaN), week=current.optDouble("previous_1_week",Double.NaN), month=current.optDouble("previous_1_month",Double.NaN), year=current.optDouble("previous_1_year",Double.NaN);
            ArrayList<Float> values=new ArrayList<>(); JSONObject hist=json.optJSONObject("fear_and_greed_historical"); if(hist!=null){JSONArray data=hist.optJSONArray("data"); if(data!=null){for(int i=Math.max(0,data.length()-90);i<data.length();i++){Object item=data.opt(i); if(item instanceof JSONObject){JSONObject p=(JSONObject)item; double val=p.optDouble("y",p.optDouble("score",p.optDouble("value",Double.NaN))); if(!Double.isNaN(val))values.add((float)val);} else if(item instanceof JSONArray){JSONArray pair=(JSONArray)item; if(pair.length()>1){double val=pair.optDouble(1,Double.NaN); if(!Double.isNaN(val))values.add((float)val);}}}} }
            runOnUiThread(()->{ scoreText.setText(String.format(java.util.Locale.US,"%.0f",score)); ratingText.setText(toChinese(rating)); ratingText.setTextColor(colorFor(score)); gauge.setScore((float)score); updatedText.setText(timestamp.isEmpty()?"":("更新："+timestamp)); status.setText("数据更新成功"); comparisons[0].setText("昨日收盘      "+fmt(prev)); comparisons[1].setText("一周前        "+fmt(week)); comparisons[2].setText("一月前        "+fmt(month)); comparisons[3].setText("一年前        "+fmt(year)); history.setValues(values); });
        } catch(Exception e){runOnUiThread(()->status.setText("获取失败："+e.getMessage()+"（点击刷新重试）"));} finally {if(c!=null)c.disconnect();}
    }); }
    private String fmt(double v){return Double.isNaN(v)?"--":String.format(java.util.Locale.US,"%.1f",v);}
    static String ratingFor(double v){if(v<25)return "extreme fear";if(v<45)return "fear";if(v<55)return "neutral";if(v<75)return "greed";return "extreme greed";}
    static String toChinese(String r){String s=r.toLowerCase(); if(s.contains("extreme fear"))return "极度恐慌";if(s.equals("fear")||s.contains(" fear"))return "恐慌";if(s.contains("neutral"))return "中性";if(s.contains("extreme greed"))return "极度贪婪";if(s.contains("greed"))return "贪婪";return r;}
    static int colorFor(double v){if(v<25)return 0xFFFF5361;if(v<45)return 0xFFFF9955;if(v<55)return 0xFFE5C451;if(v<75)return 0xFF85C77A;return 0xFF36C98B;}
}
