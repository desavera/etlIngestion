package com.b2wdigital.vegas.ingest

import org.apache.spark.sql.types._
import com.b2wdigital.vegas.ingest.exception._
import play.api.libs.json._
import scala.util.control.Exception._
import org.apache.spark.sql.catalyst.util.DateTimeUtils

object Validations {

  def validateSchema(value: String, tableSchema: StructType) = {

    val json = Json.parse(value).asInstanceOf[JsObject]

    tableSchema.foreach{ structField =>
      val fieldValue = (json \ structField.name).getOrElse(null)

      // tableSchema is always retrieved with all nullable=true in all fields (bug in spark?)
      //      if (fieldValue == null && !structField.nullable)
      //        throw new JsonSchemaException(structField.name + " cannot be null")

      if (fieldValue != null && Utils.cast(fieldValue.toString, structField.dataType) == null)
        throw new JsonSchemaException("Wrong data type for field: " + structField.name)
    }

  }

}




