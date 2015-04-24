package com.example.adedayoadelowokan.assignment03;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;

/**
 * Created by adedayoadelowokan on 18/04/15.
 */
public class TraceView extends View
{

    int width;
    int height;

    float minSpeed, maxSpeed, averageSpeed, currentAverageSpeed;

    ArrayList<Location> locations = new ArrayList<Location>();
    ArrayList<Point> trace = new ArrayList<Point>();

    Paint black;
    Paint green, grey, orange, red;
    float scaleSpeed = 35;
    float scaleDistance = 15;
    float minDistance;
    float maxDistance;
    double scale = 1.05;

    public TraceView(Context c)
    {
        super(c);
        init();
    }

    // constructor that takes in a context and also a list of attributes
    // that were set through XML
    public TraceView(Context c, AttributeSet as)
    {
        super(c, as);
        init();
    }

    // constructor that take in a context, attribute set and also a default
    // style in case the view is to be styled in a certian way
    public TraceView(Context c, AttributeSet as, int default_style)
    {
        super(c, as, default_style);
        init();
    }

    // refactored init method as most of this code is shared by all the constructors
    private void init()
    {
        black = new Paint(Paint.ANTI_ALIAS_FLAG);
        black.setColor(Color.BLACK);
        green = new Paint(Paint.ANTI_ALIAS_FLAG);
        green.setColor(Color.GREEN);
        grey= new Paint(Paint.ANTI_ALIAS_FLAG);
        grey.setColor(Color.GRAY);
        orange = new Paint(Paint.ANTI_ALIAS_FLAG);
        red = new Paint(Paint.ANTI_ALIAS_FLAG);
        orange.setColor(Color.rgb(255, 102, 0));
        red.setColor(Color.RED);
    }

    // makes sure that the board is a square
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.translate(0,canvas.getHeight());   // reset where 0,0 is located
        canvas.scale(1,-1);
        canvas.drawPaint(black);
        for(int i = 1; (10 * scaleSpeed) * i < height; i++)
        {
            canvas.drawLine(0, 10 * scaleSpeed * i, canvas.getWidth(), 10 * scaleSpeed * i, grey);
        }
        canvas.drawLine(0, currentAverageSpeed*scaleSpeed, canvas.getWidth(), currentAverageSpeed*scaleSpeed, orange);
        canvas.drawLine(0, averageSpeed*scaleSpeed, canvas.getWidth(), averageSpeed*scaleSpeed, red);

        for(int i = 0; i < trace.size(); i++)
        {
            float cx = trace.get(i).x * scaleDistance;
            float cy = trace.get(i).y * scaleSpeed;
            canvas.drawCircle(cx, cy, 5.0f, green);

            if(i > 0)
            {
                float cx1 = trace.get(i-1).x * scaleDistance;
                float cy1 = trace.get(i-1).y * scaleSpeed;
                canvas.drawLine(cx1, cy1, cx, cy, green);
            }
        }

    }

    public void setTrace(ArrayList<Location> locations)
    {
        this.locations = locations;
        trace.clear();
        int previousDistance = 0;
        for(int i = 0; i < locations.size(); i++)
        {
            Location location = locations.get(i);
            if(i > 0)
            {
                int distance = (int) (locations.get(i-1).distanceTo(location) * 3.16);
                previousDistance += distance;
                // add * 10000 to time for testing with Geny Motion
                double time = 0.000277778;
                int speed = (int) (distance/time);
                trace.add(new Point(previousDistance, speed));
            }
            else
            {
                trace.add(new Point(0, 0));
            }
        }
        invalidate();
    }

    public void updateOverallAverageSpeed()
    {
        float speed = 0.0f;
        for(Point point : trace)
        {
            speed += point.y;
        }
        averageSpeed = speed/trace.size();
    }

    public void updateCurrentAverage()
    {
        float speed = 0.0f;
        int count = 0;
        for(int i = trace.size() - 1; i > trace.size() - 20; i--)
        {
            if(i < 0)
                return;
            speed += trace.get(i).y;
            count++;
        }
        currentAverageSpeed = speed/count;
    }

    public void update()
    {
        if (trace.size() == 0)
            return;
        minSpeed = trace.get(0).y;
        maxSpeed = trace.get(0).y;
        minDistance = trace.get(0).x;
        maxDistance = trace.get(0).x;
        for(int i = 0; i < trace.size(); i++)
        {
            int speed = trace.get(i).y;
            if(speed > maxSpeed)
                maxSpeed = speed;
            else if(speed < minSpeed)
                minSpeed = speed;

            int distance = trace.get(i).x;
            if(distance > maxDistance)
                maxDistance = distance;
            else if(distance < minDistance)
                minDistance = distance;
        }
        if(scaleSpeed > ( height / ( maxSpeed * scale ) ) ) {
            scaleSpeed = (float) (height / (maxSpeed * scale));
        }

        if(scaleDistance > ( width / ( maxDistance * scale ) )) {
            scaleDistance = (float) (width / (maxDistance * scale));
        }
        updateCurrentAverage();
        updateOverallAverageSpeed();
        invalidate();
    }

    public void reset()
    {
        locations = new ArrayList<>();
        trace = new ArrayList<>();
        scaleSpeed = 35;
        scaleDistance = 1;
        averageSpeed = 0;
        currentAverageSpeed = 0;
        invalidate();
    }
}
