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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class PieGraphWithText extends PieGraph
{
	protected String text="";

	public PieGraphWithText ( Context context, String text )
	{
		super( context );
		this.text = text;
	}
	public PieGraphWithText ( Context context, AttributeSet attrs )
	{
		super( context, attrs );
	}

	private void drawText ( Canvas canvas )
	{
		String minutes;
		String percentage = null;

		float textSizeMain = getResources().getDimension( R.dimen.graph_text_size );
		float textSizeMinor = textSizeMain * 0.7F;

		if ( !defaultChart )
		{
			minutes = valueUsed + "";
			percentage = defaultText;
			textSizeMinor = textSizeMinor * 0.7F;
			if ( text.equals( getResources().getString( R.string.tab_voice ) ) )
			{
				minutes += " min";
			}
			else if ( text.equals( getResources().getString( R.string.tab_data ) ) )
			{
				minutes += " MB";
			}
		}
		else if ( unlimited && valueLimit >= 44640 )
		{
			minutes = valueUsed + "";
			if ( text.equals( getResources().getString( R.string.tab_voice ) ) )
			{
				percentage = getResources().getString( R.string.chart_unlimited_usage_voice );
			}
			else
			{
				percentage = getResources().getString( R.string.chart_unlimited_usage );
			}
		}
		else
		{
			int x = ( valueLimit - valueUsed );
			minutes = ( x < 0 ? 0 : x ) + "/" + valueLimit;
			double perc = valueLimit == 0 ? valueLimit : Math.max( 0f,
					Math.floor( ( ( float ) ( valueLimit - valueUsed ) / ( float ) valueLimit ) * 100 ) );//( float ) ( valueLimit - valueUsed ) / ( float ) valueLimit ) * 100 );
			percentage = String.format( "%.0f%% ", perc );

			if ( text.equals( getResources().getString( R.string.tab_data ) ) )
			{
				if ( valueLimit < valueUsed )
				{
					percentage = ( valueUsed - valueLimit ) + " " + getResources().getString( R.string.chart_limit_over_data );
				}
				else
				{
					percentage += getResources().getString( R.string.chart_data_remaining_mb );
				}
			}
			else if ( text.equals( getResources().getString( R.string.tab_voice ) ) )
			{
				if ( valueLimit < valueUsed )
				{
					percentage = ( valueUsed - valueLimit ) + " " + getResources().getString( R.string.chart_limit_over_voice );
				}
				else
				{
					percentage += getResources().getString( R.string.chart_data_remaining );
				}
			}
			else
			{
				if ( valueLimit < valueUsed )
				{
					percentage = ( valueUsed - valueLimit ) + " " + getResources().getString( R.string.chart_limit_over );
				}
				else
				{
					percentage += getResources().getString( R.string.chart_data_remaining );
				}
			}
			percentage+=defaultText;
		}

		Paint paint = new Paint();
		paint.setColor( Color.parseColor( color ) );
		paint.setTextSkewX( -0.2f );
		paint.setTextSize( textSizeMain );
		paint.setTypeface( Typeface.DEFAULT_BOLD );

		float textWidth = paint.measureText( minutes );

		//first line
		canvas.drawText( minutes, getWidth() / 2 - textWidth / 2, getHeight() / 2 + textSizeMain / 8, paint );

		//sencond line
		paint.setTextSize( textSizeMinor );
		paint.setTypeface( Typeface.DEFAULT );
		if ( percentage == null )
		{
			//			textWidth = paint.measureText( text );
			//			canvas.drawText( text, getWidth() / 2 - textWidth / 2, getHeight() / 2 + textSizeMain, paint );
		}
		else
		{
			textWidth = paint.measureText( percentage );
			canvas.drawText( percentage, getWidth() / 2 - textWidth / 2, getHeight() / 2 + textSizeMain, paint );
			//canvas.drawText( percentage, getWidth() / 2 - paint.measureText( percentage ) - marginPx, getHeight() / 2 + textSizeMain, paint );
			//canvas.drawText( text, getWidth() / 2 + marginPx, getHeight() / 2 + textSizeMain, paint );
		}
	}

	@Override
	public void onDraw ( Canvas canvas )
	{
		super.onDraw( canvas );
		drawText( canvas );
	}
}