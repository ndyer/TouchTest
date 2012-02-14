package org.hermit.touchtest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.os.Environment;
import android.util.Log;

public class CsvFile
{
  /** Logging prefix */
  public static final String TAG = "CsvFile";
  
  /** The CSV file itself */
  private File _file;
  
  /** Buffer for writing to the file */
  private BufferedWriter _writer;
  
  /**
   * @param parentAcivity  Activity using the CSV file
   * @param filePrefix     String to prefix the filename with
   * Creates a new file whose name includes the file prefix and current date
   * and time. The file is stored in a directory named after the application of
   * the parent activity in the external storage.
   */
  public CsvFile()
  {
    Date timestamp = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    sdf.setTimeZone(TimeZone.getDefault());
    String filename = "Touches_" + sdf.format(timestamp) + ".csv";
    
    _file = new File(Environment.getExternalStorageDirectory(), filename);
    try
    {
      _writer = new BufferedWriter(new FileWriter(_file, true));
    }
    catch (IOException e)
    {
      Log.e(TAG, e.getMessage());
    }
  }
  
  /**
   * Used to check if the constructor was successful.
   * @return True if the CSV file exists and can be written to
   */
  public boolean isFileWritable()
  {
    if (_file != null && _file.exists() && _file.canWrite())
    {
      return true;
    }
    
    return false;
  }
  
  /**
   * Returns the pathname (directory and filename) of the CSV file.
   * @return Pathname of the file
   */
  public String getPathname()
  {
    return _file.getAbsolutePath();
  }
  
  /**
   * @param data  Data to be written on the line
   * Adds a new row with a timestamp in the first cell and the data in the
   * second.
   */
  public void addTimestampedLine(String data)
  {
    addTimestamp();
    addData(data);
    addNewLine();
  }
  
  /**
   * Adds a new cell containing a timestamp.
   */
  public void addTimestamp()
  {
    try
    {
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SSS");
      sdf.setTimeZone(TimeZone.getDefault());
      
      Date timestamp = new Date();
      _writer.write(sdf.format(timestamp) + ", ");
    }
    catch (IOException e)
    {
      Log.e(TAG, e.getMessage());
    }
  }
  
  /**
   * @param data  Data as a string 
   * Adds a new cell containing the data string. 
   */
  public void addData(String data)
  {
    try
    {
      _writer.write(data + ", ");
    }
    catch (IOException e)
    {
      Log.e(TAG, e.getMessage());
    }
  }
  
  public void addData(int i)
  {
    this.addData(Integer.toString(i));
  }
  
  public void addData(float f)
  {
	  this.addData(Float.toString(f));
  }
  
  /**
   * Starts a new row and flushes the write buffer.
   */
  public void addNewLine()
  {
    try
    {
      _writer.newLine();
      _writer.flush();
    }
    catch (IOException e)
    {
      Log.e(TAG, e.getMessage());
    }
  }
  
  /**
   * Closes the CSV file. Once closed the CSV file can no longer be written to.
   */
  public void close()
  {
    try
    {
      _writer.close();
    }
    catch (IOException e)
    {
      Log.e(TAG, e.getMessage());
    }
  }
}
