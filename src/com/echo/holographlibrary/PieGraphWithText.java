package com.echo.holographlibrary;

/*
 * 	   Created by Daniel Nadeau
 * 	   daniel.nadeau01@gmail.com
 * 	   danielnadeau.blogspot.com
 * 
 * 	   Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.view.View;

public class PieGraphWithText extends View
{

	private ArrayList< PieSlice > slices = new ArrayList< PieSlice >();
	private Paint paint = new Paint();
	private Path path = new Path();

	private int indexSelected = -1;
	private int thickness = 50;
	private OnSliceClickedListener listener;

	private int valueUsed;
	private int valueLimit;
	private String text;
	private String color;
	private String colorOverrun;
	private int colorNotUsed = Color.parseColor( "#d8d8d8" );
	private boolean unlimited;

	public PieGraphWithText ( Context context, int valueUsed, int valueLimit, String text, String color, String colorOverrun, boolean unlimited )
	{
		super( context );
		this.valueUsed = valueUsed;
		this.valueLimit = valueLimit;
		this.text = text;
		this.color = color;
		this.colorOverrun = colorOverrun;
		this.unlimited = unlimited;
		init();
	}

	private void init ()
	{
		PieSlice slice;

		if ( unlimited && valueLimit != 99999 && valueUsed != 0 )
		{
			slice = new PieSlice();
			slice.setColor( Color.parseColor( color ) );
			slice.setValue( valueLimit - valueUsed );
			addSlice( slice );

			slice = new PieSlice();
			slice.setColor( colorNotUsed );
			slice.setValue( valueUsed );
			addSlice( slice );
		}
		else if ( valueUsed == valueLimit || unlimited && valueLimit == 99999 )
		{
			slice = new PieSlice();
			slice.setColor( Color.parseColor( color ) );
			slice.setValue( 1 );
			addSlice( slice );
			addSlice( slice );
		}
		else if ( valueUsed == 0 )
		{
			slice = new PieSlice();
			if ( unlimited )
			{
				slice.setColor( Color.parseColor( color ) );
			}
			else
			{
				slice.setColor( colorNotUsed );
			}
			slice.setValue( 1 ); //fake full circle
			addSlice( slice );
			addSlice( slice );
		}
		else if ( valueUsed < valueLimit )
		{
			slice = new PieSlice();
			slice.setColor( Color.parseColor( color ) );
			slice.setValue( valueUsed );
			addSlice( slice );

			slice = new PieSlice();
			slice.setColor( colorNotUsed );
			slice.setValue( valueLimit - valueUsed );
			addSlice( slice );
		}
		else if ( valueUsed >= 2 * valueLimit )
		{
			slice = new PieSlice();
			slice.setColor( Color.parseColor( colorOverrun ) );
			slice.setValue( valueUsed );
			addSlice( slice );
			addSlice( slice );
		}
		else if ( valueUsed > valueLimit )
		{
			slice = new PieSlice();
			slice.setColor( Color.parseColor( colorOverrun ) );
			slice.setValue( valueUsed - valueLimit );
			addSlice( slice );

			slice = new PieSlice();
			slice.setColor( Color.parseColor( color ) );
			slice.setValue( valueLimit - ( valueUsed - valueLimit ) );
			addSlice( slice );
		}
	}

	private void drawText ( Canvas canvas )
	{
		String usedLimit = unlimited ? getContext().getResources().getString( R.string.choose_pricing_unlimited ) : valueUsed + "/" + valueLimit;
		String percentage = String.format( "%.0f%%", Math.floor( ( ( float ) valueUsed / ( float ) valueLimit ) * 100 ) );
		float textSizeMain = 50f;
		int marginPx = 5;

		Paint paint = new Paint();
		paint.setColor( Color.parseColor( color ) );
		paint.setTextSkewX( -0.2f );
		paint.setTextSize( textSizeMain );
		paint.setTypeface( Typeface.DEFAULT_BOLD );

		float textWidth = paint.measureText( usedLimit );

		canvas.drawText( usedLimit, getWidth() / 2 - textWidth / 2, getHeight() / 2 + textSizeMain / 8, paint );

		if ( !unlimited )
		{
			paint.setTextSize( textSizeMain * 0.7f );
			paint.setTypeface( Typeface.DEFAULT );

			canvas.drawText( percentage, getWidth() / 2 - paint.measureText( percentage ) - marginPx, getHeight() / 2 + textSizeMain, paint );

			canvas.drawText( text, getWidth() / 2 + marginPx, getHeight() / 2 + textSizeMain, paint );
		}
	}

	public void onDraw ( Canvas canvas )
	{
		canvas.drawColor( Color.TRANSPARENT );
		paint.reset();
		paint.setAntiAlias( true );
		float midX, midY, radius, innerRadius;
		path.reset();

		float currentAngle = 270;
		float currentSweep = 0;
		int totalValue = 0;
		float padding = 0;//2;

		midX = getWidth() / 2;
		midY = getHeight() / 2;
		if ( midX < midY )
		{
			radius = midX;
		}
		else
		{
			radius = midY;
		}
		radius -= padding;
		innerRadius = radius - thickness;

		for ( PieSlice slice : slices )
		{
			totalValue += slice.getValue();
		}

		int count = 0;
		for ( PieSlice slice : slices )
		{
			Path p = new Path();
			paint.setColor( slice.getColor() );
			currentSweep = ( slice.getValue() / totalValue ) * ( 360 );
			p.arcTo( new RectF( midX - radius, midY - radius, midX + radius, midY + radius ), currentAngle + padding, currentSweep - padding );
			p.arcTo( new RectF( midX - innerRadius, midY - innerRadius, midX + innerRadius, midY + innerRadius ), ( currentAngle + padding )
					+ ( currentSweep - padding ), -( currentSweep - padding ) );
			p.close();

			slice.setPath( p );
			slice.setRegion( new Region( ( int ) ( midX - radius ), ( int ) ( midY - radius ), ( int ) ( midX + radius ),
					( int ) ( midY + radius ) ) );
			canvas.drawPath( p, paint );

			//			if ( indexSelected == count && listener != null )
			//			{
			//				path.reset();
			//				paint.setColor( slice.getColor() );
			//				paint.setColor( Color.parseColor( "#33B5E5" ) );
			//				paint.setAlpha( 100 );
			//
			//				if ( slices.size() > 1 )
			//				{
			//					path.arcTo( new RectF( midX - radius - ( padding * 2 ), midY - radius - ( padding * 2 ), midX + radius + ( padding * 2 ),
			//							midY + radius + ( padding * 2 ) ), currentAngle, currentSweep + padding );
			//					path.arcTo( new RectF( midX - innerRadius + ( padding * 2 ), midY - innerRadius + ( padding * 2 ), midX + innerRadius
			//							- ( padding * 2 ), midY + innerRadius - ( padding * 2 ) ), currentAngle + currentSweep + padding,
			//							-( currentSweep + padding ) );
			//					path.close();
			//				}
			//				else
			//				{
			//					path.addCircle( midX, midY, radius + padding, Direction.CW );
			//				}
			//
			//				canvas.drawPath( path, paint );
			//				paint.setAlpha( 255 );
			//			}

			currentAngle = currentAngle + currentSweep;

			count++;
		}

		drawText( canvas );

	}

	//	@Override
	//	public boolean onTouchEvent ( MotionEvent event )
	//	{
	//
	//		Point point = new Point();
	//		point.x = ( int ) event.getX();
	//		point.y = ( int ) event.getY();
	//
	//		int count = 0;
	//		for ( PieSlice slice : slices )
	//		{
	//			Region r = new Region();
	//			r.setPath( slice.getPath(), slice.getRegion() );
	//			if ( r.contains( ( int ) point.x, ( int ) point.y ) && event.getAction() == MotionEvent.ACTION_DOWN )
	//			{
	//				indexSelected = count;
	//			}
	//			else if ( event.getAction() == MotionEvent.ACTION_UP )
	//			{
	//				if ( r.contains( ( int ) point.x, ( int ) point.y ) && listener != null )
	//				{
	//					if ( indexSelected > -1 )
	//					{
	//						listener.onClick( indexSelected );
	//					}
	//					indexSelected = -1;
	//				}
	//
	//			}
	//			count++;
	//		}
	//
	//		if ( event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP )
	//		{
	//			postInvalidate();
	//		}
	//
	//		return true;
	//	}

	public ArrayList< PieSlice > getSlices ()
	{
		return slices;
	}

	public void setSlices ( ArrayList< PieSlice > slices )
	{
		this.slices = slices;
		postInvalidate();
	}

	public PieSlice getSlice ( int index )
	{
		return slices.get( index );
	}

	public void addSlice ( PieSlice slice )
	{
		this.slices.add( slice );
		postInvalidate();
	}

	public void setOnSliceClickedListener ( OnSliceClickedListener listener )
	{
		this.listener = listener;
	}

	public int getThickness ()
	{
		return thickness;
	}

	public void setThickness ( int thickness )
	{
		this.thickness = thickness;
		postInvalidate();
	}

	public void removeSlices ()
	{
		for ( int i = slices.size() - 1; i >= 0; i-- )
		{
			slices.remove( i );
		}
		postInvalidate();
	}

	public static interface OnSliceClickedListener
	{
		public abstract void onClick ( int index );
	}

}