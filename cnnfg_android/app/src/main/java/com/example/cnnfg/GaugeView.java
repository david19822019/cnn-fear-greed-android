package com.example.cnnfg;
import android.content.Context; import android.view.View; import android.graphics.Canvas; import android.graphics.Paint; import android.graphics.RectF;
public class GaugeView extends View {
 private Paint p=new Paint(3); private float score=50; private final int[] colors={0xFFFF5361,0xFFFF9955,0xFFE5C451,0xFF85C77A,0xFF36C98B};
 public GaugeView(Context c){super(c);} public void setScore(float s){score=Math.max(0,Math.min(100,s));invalidate();}
 @Override protected void onDraw(Canvas c){super.onDraw(c); float w=getWidth(),h=getHeight(); float cx=w/2,cy=h*.78f,rad=Math.min(w*.40f,h*.66f); RectF r=new RectF(cx-rad,cy-rad,cx+rad,cy+rad);
 p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(22);p.setStrokeCap(Paint.Cap.ROUND);
 for(int i=0;i<5;i++){p.setColor(colors[i]); c.drawArc(r,135+i*54,48,false,p);}
 p.setStyle(Paint.Style.FILL);p.setColor(0xFFDFE7F3);p.setTextSize(13);p.setTextAlign(Paint.Align.CENTER); c.drawText("0",cx-rad*.95f,cy+rad*.22f,p);c.drawText("100",cx+rad*.95f,cy+rad*.22f,p);
 float angle=(float)Math.toRadians(135+score*2.7); float nx=cx+(float)Math.cos(angle)*(rad*.78f),ny=cy+(float)Math.sin(angle)*(rad*.78f);
 p.setColor(0xFFEAF0FA);p.setStrokeWidth(4);c.drawLine(cx,cy,nx,ny,p);p.setColor(0xFF45D6A5);c.drawCircle(cx,cy,9,p);
 }
}
