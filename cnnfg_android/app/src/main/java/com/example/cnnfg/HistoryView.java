package com.example.cnnfg;
import android.content.Context; import android.view.View; import android.graphics.Canvas; import android.graphics.Paint; import android.graphics.Path; import java.util.ArrayList;
public class HistoryView extends View {
 private Paint p=new Paint(3); private ArrayList<Float> vals=new ArrayList<>();
 public HistoryView(Context c){super(c);} public void setValues(ArrayList<Float> v){vals=new ArrayList<>(v);invalidate();}
 @Override protected void onDraw(Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight();float left=12,right=w-12,top=12,bottom=h-24;
 p.setColor(0xFF253149);p.setStrokeWidth(1);p.setStyle(Paint.Style.STROKE); for(int i=0;i<=4;i++){float y=top+(bottom-top)*i/4;c.drawLine(left,y,right,y,p);}
 if(vals.size()<2){p.setStyle(Paint.Style.FILL);p.setColor(0xFF8D9BB5);p.setTextSize(13);c.drawText("暂无历史数据",left, h/2,p);return;}
 Path path=new Path();for(int i=0;i<vals.size();i++){float x=left+(right-left)*i/(vals.size()-1);float y=bottom-(bottom-top)*Math.max(0,Math.min(100,vals.get(i)))/100f;if(i==0)path.moveTo(x,y);else path.lineTo(x,y);}
 p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);p.setColor(0xFF45D6A5);c.drawPath(path,p);p.setStyle(Paint.Style.FILL);p.setColor(0xFF8D9BB5);p.setTextSize(11);c.drawText("0",left,bottom+15,p);c.drawText("100",left,top+10,p);
 }
}
