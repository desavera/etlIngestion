package com.b2wdigital.vegas.ingest

import org.apache.spark.sql.catalyst.util.DateTimeUtils
import org.apache.spark.sql.types._

import scala.util.control.Exception.allCatch

/**
  * Created by zhang on 05/09/17.
  */
object Utils {


  def cast(value: String, dataType: DataType): Object = {

    if(value == null || value.isEmpty || dataType == null)
      return null;

    dataType match {
      case IntegerType => allCatch.opt(Integer.valueOf(value)).getOrElse(null)
      case LongType => allCatch.opt(java.lang.Long.valueOf(value)).getOrElse(null)
      case _: DecimalType => allCatch.opt(BigDecimal(value)).getOrElse(null)
      case DoubleType => allCatch.opt(java.lang.Double.valueOf(value)).getOrElse(null)
      case TimestampType => allCatch.opt(DateTimeUtils.stringToTime(value)).getOrElse(null)
      case BooleanType => {
        if (value.toLowerCase.equals("true") || value.toLowerCase.equals("false"))
          java.lang.Boolean.valueOf(value) // Boolean.valueOf doesn't throws exceptions
        else
          null
      }
      case StringType => value
      case other => null
    }

  }

}
