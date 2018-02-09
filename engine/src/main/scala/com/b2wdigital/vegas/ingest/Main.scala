package com.b2wdigital.vegas.ingest

import Definitions._
import grizzled.slf4j.Logger



object Main {

  val usage = """
    Usage: spark-submit [--conf spark-params] [--job-id id] [--def-api api-server:port | --def-file filename] vegas-ingest.jar
  """

  type OptionMap = Map[Symbol, Any]

  def main(args: Array[String]) {

    if (args.length == 0) println(usage)
    val arglist = args.toList

    val options = nextOption(Map(),arglist)

    // triggers the ingestion
    IngestEngine.process(options)

  }

  def containsNoSpecialChars(string: String): Boolean = {
    val pattern = "^[0-9]*$".r
    return pattern.findAllIn(string).mkString.length == string.length
  }

  def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
      def isSwitch(s : String) = (s(0) == '-')
      list match {
        case Nil => map
        case "--job-id" :: value :: tail =>

                               if (!containsNoSpecialChars(value)) {
                                 println("Wrong option value "+value) 
                                 exit(1)
                               }
                               
                               nextOption(map ++ Map('jobid -> value.toInt), tail)

        case "--def-api" :: value :: tail =>
                               nextOption(map ++ Map('defapi -> value.toString), tail)
        case "--def-file" :: value :: tail =>
                               nextOption(map ++ Map('deffile -> value.toString), tail)
        case string :: opt2 :: tail if isSwitch(opt2) => 
                               nextOption(map ++ Map('infile -> string), list.tail)
        case string :: Nil =>  nextOption(map ++ Map('infile -> string), list.tail)
        case option :: tail => println("Unknown option "+option) 
                               exit(1) 
      }
  }
}


