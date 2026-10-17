package net.logicsquad.eogen.migration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.logicsquad.eogen.model.EOAttribute;
import net.logicsquad.eogen.model.EOFactoryMethodArgumentType;

/**
 * The calls that Project Wonder's {@code ERXMigrationTable} makes a column
 * with, which a migration uses for an attribute's column.
 * <p>
 * {@link #of(EOAttribute)} picks one as WOLips's Entity Modeler does when it
 * generates a migration, from the attribute's prototype, value class, value
 * type and external type, with its mismatches with Wonder's API fixed: Wonder
 * has no {@code newLargeStringColumn} with a default, no
 * {@code newBigIntegerColumn} with a precision, and an integer column's
 * precision isn't the first argument of {@code newIntegerColumn}; ERPrototypes'
 * {@code varcharLarge} is Wonder's large string, whose type and width depend on
 * the database; and the value classes that Entity Modeler had no call for, such
 * as {@code ERXMutableArray}, {@code Short} and Joda-Time's dates, have one.
 * But a string whose external type isn't a string's, such as PostgreSQL's
 * {@code uuid}, has none, since a string column isn't it.
 * </p>
 */
enum ColumnType {
	STRING("newStringColumn", Arguments.WIDTH, "VARCHAR"),
	LARGE_STRING("newLargeStringColumn", Arguments.NONE, null),
	INTEGER("newIntegerColumn", Arguments.NONE, "INTEGER"),
	SMALL_INTEGER("newSmallIntegerColumn", Arguments.NONE, "SMALLINT"),
	BIG_INTEGER("newBigIntegerColumn", Arguments.NONE, "BIGINT"),
	BIG_DECIMAL("newBigDecimalColumn", Arguments.PRECISION_SCALE, "DECIMAL"),
	DOUBLE("newDoubleColumn", Arguments.PRECISION_SCALE, "DOUBLE"),
	FLOAT("newFloatColumn", Arguments.PRECISION_SCALE, "FLOAT"),
	BOOLEAN("newBooleanColumn", Arguments.NONE, null),
	INT_BOOLEAN("newIntBooleanColumn", Arguments.NONE, null),
	FLAG_BOOLEAN("newFlagBooleanColumn", Arguments.NONE, null),
	TIMESTAMP("newTimestampColumn", Arguments.NONE, "TIMESTAMP"),
	DATE("newDateColumn", Arguments.NONE, "DATE"),
	TIME("newTimeColumn", Arguments.NONE, "TIME"),
	BLOB("newBlobColumn", Arguments.NONE, "BLOB"),
	IP_ADDRESS("newIpAddressColumn", Arguments.NONE, null);

	/**
	 * The arguments a call takes between the column's name and whether it
	 * allows null.
	 */
	enum Arguments {
		/**
		 * None.
		 */
		NONE,

		/**
		 * The width.
		 */
		WIDTH,

		/**
		 * The precision, then the scale.
		 */
		PRECISION_SCALE
	}

	/**
	 * The words that Wonder's {@code ERXTimestampUtilities.timestampForString}
	 * makes a timestamp of, which are all an attribute's {@code default} can
	 * be.
	 */
	private static final List<String> TIMESTAMP_DEFAULTS = Arrays.asList("now", "today", "yesterday", "tomorrow", "distantPast", "distantFuture");

	private final String method;

	private final Arguments arguments;

	private final String jdbcType;

	private ColumnType(String method, Arguments arguments, String jdbcType) {
		this.method = method;
		this.arguments = arguments;
		this.jdbcType = jdbcType;
	}

	/**
	 * Returns the {@code ERXMigrationTable} method.
	 *
	 * @return method name
	 */
	String getMethod() {
		return method;
	}

	/**
	 * Returns the arguments between the column's name and whether it allows
	 * null.
	 *
	 * @return arguments
	 */
	Arguments getArguments() {
		return arguments;
	}

	/**
	 * Returns the {@code java.sql.Types} constant for the type the call makes,
	 * which {@code ERXMigrationColumn.setDataType} changes a column to, or null
	 * if the type depends on the database, as the large string's does.
	 *
	 * @return constant name, such as {@code VARCHAR}, or null
	 */
	String getJdbcType() {
		return jdbcType;
	}

	/**
	 * Returns whether Wonder has an overload of the call with a default value
	 * for the rows that a table already has.
	 *
	 * @return {@code true} if it takes a default
	 */
	boolean takesDefault() {
		return this != TIME && this != BLOB;
	}

	/**
	 * The external types, without case, that a string's column can have, by
	 * the start of their names: character types, and large objects, as old
	 * models have long text in. A string whose external type is something
	 * else, such as PostgreSQL's {@code uuid} or {@code jsonb}, has no call
	 * that makes its column.
	 */
	private static final List<String> STRING_TYPES = Arrays.asList("char", "varchar", "character", "text", "clob", "nchar", "nvarchar", "ntext", "nclob", "long", "mediumtext", "tinytext",
			"citext", "string", "blob", "mediumblob", "tinyblob", "image");

	/**
	 * Returns the call for an attribute's column, or null if there is none.
	 *
	 * @param attribute an attribute
	 * @return the call, or null
	 */
	static ColumnType of(EOAttribute attribute) {
		ColumnType type = call(attribute);
		if ((type == STRING || type == LARGE_STRING) && !isStringType(attribute.getExternalType())) {
			return null;
		}
		return type;
	}

	/**
	 * Returns whether an external type can be a string's column's, or is
	 * unknown.
	 */
	private static boolean isStringType(String externalType) {
		if (externalType == null || externalType.trim().isEmpty()) {
			return true;
		}
		String type = externalType.trim().toLowerCase(Locale.ROOT);
		for (String string : STRING_TYPES) {
			if (type.startsWith(string)) {
				return true;
			}
		}
		return false;
	}

	private static ColumnType call(EOAttribute attribute) {
		String prototype = attribute.getPrototype() == null ? null : attribute.getPrototype().getName();
		String javaClassName = attribute.getJavaClassName();
		String valueClassName = attribute.getValueClassName();
		String valueType = attribute.getValueType();
		String externalType = attribute.getExternalType();
		Integer width = attribute.getWidth();
		if ("longText".equals(prototype) || "varcharLarge".equals(prototype)) {
			return LARGE_STRING;
		} else if ("ipAddress".equals(prototype)) {
			return IP_ADDRESS;
		} else if ("date".equals(prototype) || "jodaLocalDate".equals(prototype) || ("NSCalendarDate".equals(valueClassName) && "D".equals(valueType)) || "java.time.LocalDate".equals(javaClassName)) {
			return DATE;
		} else if ("String".equals(javaClassName)) {
			return width != null && width.intValue() > 0 ? STRING : LARGE_STRING;
		} else if ("java.math.BigDecimal".equals(javaClassName) || "BigDecimal".equals(javaClassName)) {
			return BIG_DECIMAL;
		} else if ("Integer".equals(javaClassName)) {
			return INTEGER;
		} else if ("Long".equals(javaClassName)) {
			return BIG_INTEGER;
		} else if ("Short".equals(javaClassName)) {
			return SMALL_INTEGER;
		} else if ("Double".equals(javaClassName)) {
			return DOUBLE;
		} else if ("Float".equals(javaClassName)) {
			return FLOAT;
		} else if ("Boolean".equals(javaClassName)) {
			if (width != null && width.intValue() == 5) {
				return BOOLEAN;
			}
			return "bool".equalsIgnoreCase(externalType) || "boolean".equalsIgnoreCase(externalType) ? FLAG_BOOLEAN : INT_BOOLEAN;
		} else if ("NSTimestamp".equals(javaClassName) || "java.time.LocalDateTime".equals(javaClassName) || "java.time.OffsetDateTime".equals(javaClassName)) {
			return TIMESTAMP;
		} else if ("java.time.LocalTime".equals(javaClassName)) {
			return TIME;
		} else if ("NSData".equals(javaClassName) || "er.extensions.foundation.ERXMutableDictionary".equals(javaClassName) || "ERXMutableDictionary".equals(javaClassName)
				|| "er.extensions.foundation.ERXMutableArray".equals(javaClassName) || "ERXMutableArray".equals(javaClassName)) {
			return BLOB;
		}
		EOFactoryMethodArgumentType argumentType = attribute.getFactoryMethodArgumentType();
		String argument = argumentType == null ? null : argumentType.getID();
		if (attribute.getAdaptorValueConversionMethodName() != null || argument != null) {
			if (EOFactoryMethodArgumentType.STRING.getID().equals(argument)) {
				return width != null && width.intValue() > 0 ? STRING : LARGE_STRING;
			} else if (EOFactoryMethodArgumentType.DATA.getID().equals(argument) || EOFactoryMethodArgumentType.BYTES.getID().equals(argument)) {
				return BLOB;
			} else if (EOFactoryMethodArgumentType.DATE.getID().equals(argument)) {
				return dateType(externalType, valueType);
			} else if (EOFactoryMethodArgumentType.NUMBER.getID().equals(argument)) {
				return numberType(valueType);
			}
		}
		return null;
	}

	/**
	 * Returns the call for a class that a date makes, such as Joda-Time's: from
	 * its external type, where that is one of SQL's, and otherwise its value
	 * type.
	 */
	private static ColumnType dateType(String externalType, String valueType) {
		String type = externalType == null ? "" : externalType.toUpperCase(Locale.ROOT);
		if (type.startsWith("TIMESTAMP") || type.equals("DATETIME")) {
			return TIMESTAMP;
		} else if (type.equals("DATE")) {
			return DATE;
		} else if (type.startsWith("TIME")) {
			return TIME;
		} else if ("D".equals(valueType)) {
			return DATE;
		} else if ("t".equals(valueType)) {
			return TIME;
		}
		return TIMESTAMP;
	}

	/**
	 * Returns the call for a class that a number makes, from its value type.
	 */
	private static ColumnType numberType(String valueType) {
		if ("B".equals(valueType)) {
			return BIG_DECIMAL;
		} else if ("l".equals(valueType)) {
			return BIG_INTEGER;
		} else if ("s".equals(valueType)) {
			return SMALL_INTEGER;
		} else if ("d".equals(valueType)) {
			return DOUBLE;
		} else if ("f".equals(valueType)) {
			return FLOAT;
		}
		return INTEGER;
	}

	/**
	 * Returns Java for a default value, as the call's last argument, or null if
	 * it isn't one the call can take: the {@code default} in an attribute's
	 * {@code userInfo}, which Wonder gives new objects.
	 *
	 * @param value the default, as the model has it
	 * @param imports where to add the classes that the Java uses
	 * @return Java, or null
	 */
	String defaultValue(String value, Set<String> imports) {
		if (value == null || !takesDefault()) {
			return null;
		}
		switch (this) {
		case STRING:
		case LARGE_STRING:
		case IP_ADDRESS:
			return MigrationWriter.literal(value);
		case INTEGER:
			return parses(() -> Integer.parseInt(value)) ? value : null;
		case SMALL_INTEGER:
			return parses(() -> Short.parseShort(value)) ? "(short) " + value : null;
		case BIG_INTEGER:
			return parses(() -> Long.parseLong(value)) ? value + "L" : null;
		case BIG_DECIMAL:
			if (!parses(() -> new BigDecimal(value))) {
				return null;
			}
			imports.add("java.math.BigDecimal");
			return "new BigDecimal(" + MigrationWriter.literal(value) + ")";
		case DOUBLE:
			return parses(() -> Double.parseDouble(value)) && !value.matches(".*[dDfF]$") ? value + "d" : null;
		case FLOAT:
			return parses(() -> Float.parseFloat(value)) && !value.matches(".*[dDfF]$") ? value + "f" : null;
		case BOOLEAN:
		case INT_BOOLEAN:
		case FLAG_BOOLEAN:
			String flag = value.trim().toLowerCase(Locale.ROOT);
			if (flag.equals("true") || flag.equals("yes") || flag.equals("y") || flag.equals("1")) {
				return "true";
			} else if (flag.equals("false") || flag.equals("no") || flag.equals("n") || flag.equals("0")) {
				return "false";
			}
			return null;
		case TIMESTAMP:
		case DATE:
			if (!TIMESTAMP_DEFAULTS.contains(value)) {
				return null;
			}
			imports.add("er.extensions.foundation.ERXTimestampUtilities");
			return "ERXTimestampUtilities.timestampForString(" + MigrationWriter.literal(value) + ")";
		default:
			return null;
		}
	}

	private static boolean parses(Runnable parse) {
		try {
			parse.run();
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
