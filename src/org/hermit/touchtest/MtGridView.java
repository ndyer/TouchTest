
/**
 * Touch Test: a multi-touch test app for Android.
 * <br>Copyright 2010 Ian Cameron Smith
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation (see COPYING).
 * 
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */


package org.hermit.touchtest;


import android.content.Context;
import android.view.MotionEvent;


/**
 * Multi-touch implementation of the touch test view.
 */
class MtGridView
    extends GridView
{

    // ******************************************************************** //
    // Constructor.
    // ******************************************************************** //

	/**
	 * Create a GridView instance.
	 * 
	 * @param	context  The application context we're running in.
	 */
    public MtGridView(Context context) {
        super(context);
    }

    
    // ******************************************************************** //
    // Input Handling.
    // ******************************************************************** //
    
    /**
	 * Handle touchscreen input.
	 * 
     * @param	event		The MotionEvent object that defines the action.
     * @return				True if the event was handled, false otherwise.
	 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final int npointers = event.getPointerCount();
        final int pact = event.getActionMasked();
        final int pid = event.getActionIndex();

        if (_saveData)
        {
          if (null == _file)
          {
            _file = new CsvFile();
            if (!_file.isFileWritable()) {
				//some kind of error
				_saveData = false;
				_file = null;
            }
          }
        }

        // Update the up/down state of this pointer.
        switch (pact) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_POINTER_DOWN:
            {
                Pointer rec = getPointer(pid);
                rec.seen = true;
                rec.down = true;
                rec.x = event.getX(pid);
                rec.y = event.getY(pid);
                rec.size = event.getSize(pid);

                rec.trailStart = 0;
                rec.trailLen = 0;

				if (_file != null)
				{
				    _file.addTimestamp();
					_file.addData(pid);
					_file.addData(1);
					_file.addData(rec.x);
					_file.addData(rec.y);
					_file.addData(rec.size);
					_file.addNewLine();
				}
            }
            break;
        case MotionEvent.ACTION_UP:
            for (int i = 0; i < MAX_POINTER_ID; ++i)
            {
                if (getPointer(i).down)
                {
	                if (_file != null)
	                {
			            _file.addTimestamp();
						_file.addData(i);
						_file.addData(0);
						_file.addNewLine();
	                }
                }

                getPointer(i).down = false;
            }
            break;
        case MotionEvent.ACTION_POINTER_UP:
            {
                Pointer rec = getPointer(pid);
                rec.down = false;

                if (_file != null)
                {
					_file.addTimestamp();
					_file.addData(pid);
					_file.addData(0);
					_file.addNewLine();
                }

                break;
            }
        case MotionEvent.ACTION_MOVE:
            for (int i = 0; i < npointers; ++i) {
                int p = event.getPointerId(i);
                Pointer rec = getPointer(p);
                
                rec.x = event.getX(i);
                rec.y = event.getY(i);
                rec.size = event.getSize(i);
                
                int nh = event.getHistorySize();
                for (int h = 0; h < nh; ++h) {
                    float hx = event.getHistoricalX(i, h);
                    float hy = event.getHistoricalY(i, h);
                    addPoint(rec, hx, hy);
                }
                addPoint(rec, rec.x, rec.y);

                if (_file != null)
                {
					_file.addTimestamp();
					_file.addData(p);
					_file.addData(1);
					_file.addData(rec.x);
					_file.addData(rec.y);
					_file.addData(rec.size);
					_file.addNewLine();
                }
            }
            break;
        }

        try
        {
          postUpdate();
        }
        catch (IllegalArgumentException e)
        {
          // this is thrown when the ticker is killed due an orientation change
        }
        
        return true;
    }

    private boolean _saveData = false;
    private CsvFile _file;

    public void setSaveData(boolean saveData) {
		this._saveData = saveData;
	}
}

