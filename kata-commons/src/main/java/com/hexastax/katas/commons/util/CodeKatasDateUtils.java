package com.hexastax.katas.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

/**
 * Contains date-related utilities.
 * 
 * @author dgoldenberg
 */
public class CodeKatasDateUtils {

  /**
   * The ISO-8601 date format: yyyy-MM-dd'T'HH:mm:ss.
   */
  public static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

  /**
   * The time zone for UTC.
   */
  public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  private static ThreadLocal<HashMap<String, SimpleDateFormat>> threadLocalFormatters = new ThreadLocal<HashMap<String, SimpleDateFormat>>() {
    @Override
    protected HashMap<String, SimpleDateFormat> initialValue() {
      return new HashMap<String, SimpleDateFormat>();
    }
  };

  private CodeKatasDateUtils() {
  }

  /**
   * Formats a date as ISO 8601, UTC, with a check for null input date value.
   * 
   * @param date
   *          the date value
   * @return the canonical string representation of the date, as UTC, or null if input date value is
   *         null
   */
  public static String safeDateToCanonical(Date date) {
    return ((date == null) ? null : CodeKatasDateUtils.formatDateAsCanonical(date));
  }

  /**
   * Formats a date as ISO 8601, UTC, per XML Schema Part 2
   * (http://www.w3.org/TR/xmlschema-2/#dateTime-canonical-representation). This date format is
   * compatible with Solr.
   * 
   * @param date
   *          the date
   * @return the canonical string representation of the date, as UTC
   */
  public static String formatDateAsCanonical(Date date) {
    return String.format("%sZ", formatDate(date, ISO_8601_DATE_FORMAT, UTC));
  }

  /**
   * Parses a date from a string which is expected to be ISO 8601, UTC, canonical representation per
   * XML Schema Part 2 (http://www.w3.org/TR/xmlschema-2/#dateTime-canonical-representation). This
   * date format is compatible with Solr.
   * 
   * @param strDate
   *          the string representation of a date
   * @return the parsed date
   * @throws ParseException
   */
  public static Date parseCanonicalDate(String strDate) throws ParseException {
    return parseDate(strDate, ISO_8601_DATE_FORMAT, UTC);
  }

  /**
   * Formats a date according to the specified format, using UTC.
   * 
   * @param d
   *          Date to format
   * @param format
   *          format string
   * @return String representation of Date according to format
   */
  public static String formatDate(final Date d, final String format) {
    return formatDate(d, format, UTC);
  }

  /**
   * Formats a date according to the specified format.
   * 
   * @param d
   *          Date to format
   * @param format
   *          format string
   * @param timeZone
   *          the TimeZone to use for formatting
   * @return String representation of Date according to format
   */
  public static String formatDate(final Date d, final String format, TimeZone timeZone) {
    return getFormatter(format, timeZone).format(d);
  }

  /**
   * Parses a date from string using the list of specified formats.
   * 
   * @param sDate
   * @param formats
   * @param timeZone
   * @return the parsed out date if at least one format allowed the method to parse the date, or
   *         null if none of the formats worked
   */
  public static Date parseDate(String sDate, List<String> formats, TimeZone timeZone) {
    Date date = null;

    for (String format : formats) {
      try {
        date = parseDate(sDate, format, timeZone);
      } catch (ParseException ex) {
        // Ignore.
      }
      if (date != null) {
        break;
      }
    }

    return date;
  }

  /**
   * Parses a date in the specified format, assuming that the input string is a date in UTC.
   * 
   * @param sDate
   *          the string date value to parse
   * @param format
   *          the format for the date
   * @return the parsed out date
   * @throws ParseException
   *           on a parse error
   */
  public static Date parseDate(String sDate, String format) throws ParseException {
    return parseDate(sDate, format, UTC);
  }

  /**
   * Parses a date in the specified format.
   * 
   * @param sDate
   *          the string date value to parse
   * @param format
   *          the format for the date
   * @param timeZone
   *          the timezone to parse the value as
   * @return the parsed out date
   * @throws ParseException
   *           on a parse error
   */
  public static Date parseDate(String sDate, String format, TimeZone timeZone) throws ParseException {
    return getFormatter(format, timeZone).parse(sDate);
  }

  private static SimpleDateFormat getFormatter(String format, TimeZone zone) {
    if (StringUtils.isEmpty(format)) {
      format = ISO_8601_DATE_FORMAT;
    }

    SimpleDateFormat formatter = threadLocalFormatters.get().get(format);
    if (formatter == null) {
      formatter = new SimpleDateFormat(format);
      threadLocalFormatters.get().put(format, formatter);
    }

    // Set the time zone
    if (zone == null) {
      formatter.setTimeZone(TimeZone.getDefault());
    } else {
      formatter.setTimeZone(zone);
    }

    return formatter;
  }

  public static void main(String[] args) throws ParseException {
    System.out.println(CodeKatasDateUtils.parseDate("2013-02-11 23:04:31", "yyyy-MM-dd HH:mm:ss"));
    System.out.println(CodeKatasDateUtils.safeDateToCanonical(new Date()));
  }
}
