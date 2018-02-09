package com.b2wdigital.vegas.ingest.transform

import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException
import org.apache.spark.rdd.RDD


class Transformers(transformers : Seq[Transformer]) extends Transformer {


  override def transform(rdd : RDD[String]) : RDD[String] = {

    if (transformers.isEmpty) throw new WrongNumberArgsException("empty transformers list...")

    var result:RDD[String] = rdd
    transformers.foreach (t => result = t.transform(result))

    return result
  }

}
