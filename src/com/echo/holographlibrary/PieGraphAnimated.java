/*
 * 	   Created by Daniel Nadeau
 * 	   daniel.nadeau01@gmail.com
 * 	   danielnadeau.blogspot.com
 * 
 * 		adapted by DroidsOnRoids
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

package com.echo.holographlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;

public class PieGraphAnimated extends PieGraph
{
	@SuppressWarnings ( "unused" )
	private static final String TAG = "PieGraphAnimated";

	private float animationSteps = 25;
	private float step = 0;

	private Handler h;

	private static final int FRAME_RATE = 50;

	public PieGraphAnimated ( Context context )
	{
		super( context );
		init();
	}

	public PieGraphAnimated ( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init();
	}

	private void init ()
	{
		h = new Handler();
	}

	private Runnable r = new Runnable()
	{
		@Override
		public void run ()
		{
			if ( targetUsedValue != 0 && targetUsedValue < targetLimitValue )
			{
				PieSlice sliceColor = null;
				PieSlice sliceUsed = null;
				float colorValue;
				float usedValue;
				for ( PieSlice slice : getSlices() )
				{
					if ( slice.getColor() == color )
					{
						sliceColor = slice;
					}
					else
					{
						sliceUsed = slice;
					}
				}

				usedValue = ( ( float ) targetUsedValue / ( float ) animationSteps * ( float ) step );
				colorValue = ( ( float ) ( targetLimitValue - usedValue ) );

				if ( sliceColor != null && sliceUsed != null )
				{
					sliceColor.setValue( colorValue );
					sliceUsed.setValue( usedValue );
				}
				invalidate();
			}
		}
	};

	@Override
	public void onDraw ( Canvas canvas )
	{
		super.onDraw( canvas );
		if ( animationStarted && valueUsed != 0 && step < animationSteps )
		{
			step += 1;
			h.postDelayed( r, FRAME_RATE );
		}
		else
		{
			animationStarted = false;
		}
	}

	private int targetUsedValue;
	private int targetLimitValue;
	private boolean animationStarted = false;

	@Override
	public void initChart ( int thickness, int valueUsed, int valueLimit, String color, boolean unlimited, boolean defaultChart,
			String defaultText )
	{
		super.initChart( thickness, valueUsed, valueLimit, color, unlimited, defaultChart, defaultText );
		this.targetLimitValue = valueLimit;
		this.targetUsedValue = valueUsed;
	}

	public void startAnimation ()
	{
		step = 0;
		animationStarted = true;
		invalidate();
	}

	public int getAnimationSteps ()
	{
		return ( int ) animationSteps;
	}

	public void setAnimationSteps ( int animationSteps )
	{
		this.animationSteps = animationSteps;
	}
}
