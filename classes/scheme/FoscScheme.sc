/* ------------------------------------------------------------------------------------------------------------
• FoscScheme


• Example 1. A Scheme boolean value

a = FoscScheme(true);
a.format;


• Example 2. A nested Scheme expession
a = FoscScheme(['left', [1, 2, false]], ['right', [1, 2, 3.3]]);
a.format;


• Example 3. A variable-length argument

Scheme wraps nested variable-length arguments in a tuple.

a = FoscScheme(1, 2, 3);
b = FoscScheme([1, 2, 3]);
format(a) == format(b);


• Example 4. A quoted Scheme expression

a = FoscScheme([1, 2, 3]).quoting_("'#");
a.format;


• Example 5. A Scheme expression with forced quotes

a = FoscScheme('nospaces').forceQuotes_(true);
a.format;


• Example 6. A Scheme expression of LilyPond functions

Use this in certain override situations when LilyPond’s Scheme interpreter treats unquoted strings as symbols instead of strings. The string must contain no whitespace for this to work.

x = "tuplet-number::append-note-wrapper";
y = "tuplet-number::calc-denominator-text";
a = FoscScheme("4").forceQuotes_(true);
b = FoscScheme(x, y, a);
b.format;


• Example 7. A Scheme lambda expression of LilyPond function that takes a markup with a quoted string argument.

Setting verbatim to true causes the expression to format exactly as-is without modifying quotes or whitespace.

x = "(lambda (grob) (grob-interpret-markup grob' #{ \\markup \\musicglyph #\"noteheads.s0harmonic\" #}))";
a = FoscScheme(x).isVerbatim_(true)
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscScheme : Fosc {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <value, <quoting, <forceQuotes=false, <isVerbatim=false;
	*new { |... args|
		^super.new.init(args);
	}
	init { |args|
		if (args.size == 1) {
			value = if (args[0].isKindOf(FoscScheme)) { args[0].value } { args[0] };
		} {
			value = args;
		};
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC CLASS METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• *formatEmbeddedSchemeValue

	Formats `value` as an embedded Scheme value.
	
	FoscScheme.formatEmbeddedSchemeValue(FoscOrdinalConstant('x', -1, \left));
	-------------------------------------------------------------------------------------------------------- */
	*formatEmbeddedSchemeValue { |value, forceQuotes=false|
		var result;

		result = FoscScheme.formatSchemeValue(value, forceQuotes: true);
		
		if (
			value.isKindOf(Boolean)
			|| { value.isString && forceQuotes.not } 
			|| { value.isKindOf(FoscScheme) }
		) {
			result = "#" ++ result;
		};
		
		^result;
	}
	/* --------------------------------------------------------------------------------------------------------
	• *formatSchemeValue
	
	Formats value as Scheme would.

	Formats `value` as Scheme would.

	FoscScheme.formatSchemeValue(1);
	FoscScheme.formatSchemeValue("foo\"bar", isVerbatim: false);
	FoscScheme.formatSchemeValue("foo\"bar", isVerbatim: true);
	FoscScheme.formatSchemeValue("foobar");
	FoscScheme.formatSchemeValue('foo bar');
	FoscScheme.formatSchemeValue(true);
	FoscScheme.formatSchemeValue([\one, "two", \three, "four", 5, 7]);
	FoscScheme.formatSchemeValue();

	FoscScheme.formatSchemeValue("four", forceQuotes: true);

	FoscScheme.formatSchemeValue("#1-finger", isVerbatim: true);
	FoscScheme.formatSchemeValue("#1-finger", isVerbatim: false);

    m = FoscMarkup("\\bold { over pressure }");
    FoscScheme.formatEmbeddedSchemeValue(m);
	-------------------------------------------------------------------------------------------------------- */
	*formatSchemeValue { |value, forceQuotes=false, isVerbatim=false|
		^case
		{ [String, Symbol].includes(value.class) } {
			value = value.asString;
			
			if (isVerbatim.not) {
				value = value.replace("\"", "\\\"");
				if (forceQuotes || { value.includesAny([Char.space, $#]) }) { value = "\"%\"".format(value) };
				value;
			} {
				value;
			};
		}
		{ value.isNumber } {
			value.asString;
		}
		{ value.isKindOf(Boolean) } {
			if (value, "#t", "#f");
		}
		{ value.isKindOf(SequenceableCollection) } {
			value = value.collect { |each| FoscScheme.formatSchemeValue(each) }.join(" ");
			"(%)".format(value);
		}
		{ value.isKindOf(FoscScheme) } {
			if (value.quoting.notNil) {
				value.quoting ++ value.prGetFormattedValue;
			} {
				value.prGetFormattedValue;
			};
		}
		{ value.isNil } {
			"#f";
		}
		{ value.str };
	}
	/* --------------------------------------------------------------------------------------------------------
	• makeSpacingVector
    
    //!!! DEPRECATE
    //!!! use FoscSchemeSpacingVector instead

	a = FoscScheme.makeSpacingVector(0, 0, 12);
	a.format;
	-------------------------------------------------------------------------------------------------------- */
	*makeSpacingVector { |basicDistance=0, minimumDistance=0, padding=0, stretchability=0|
		^FoscSchemeVector(
			FoscSchemePair('basic-distance', basicDistance),
			FoscSchemePair('minimum-distance', minimumDistance),
			FoscSchemePair('padding', padding),
			FoscSchemePair('stretchability', stretchability),
		);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE PROPERTIES
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• forceQuotes_
	- Is true when quotes should be forced in output.
	-------------------------------------------------------------------------------------------------------- */
	forceQuotes_ { |bool|
		forceQuotes = bool;
	}
	/* --------------------------------------------------------------------------------------------------------
	• isVerbatim_
	- Is true when formatting should format value absolutely verbatim. Whitespace, quotes, and all other parts of value
	are left intact.
	-------------------------------------------------------------------------------------------------------- */
	isVerbatim_ { |bool|
		isVerbatim = bool;
	}
	/* --------------------------------------------------------------------------------------------------------
	• quoting_
	- Sets Scheme quoting string.
	-------------------------------------------------------------------------------------------------------- */
	quoting_ { |str|
		quoting = str.asString;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• format
	-------------------------------------------------------------------------------------------------------- */
	format {
		^this.prGetLilypondFormat;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE INSTANCE METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• prGetFormattedValue
	-------------------------------------------------------------------------------------------------------- */
	prGetFormattedValue {
		^FoscScheme.formatSchemeValue(value, forceQuotes, isVerbatim);
	}
	/* --------------------------------------------------------------------------------------------------------
	• prGetLilypondFormat
	-------------------------------------------------------------------------------------------------------- */
	prGetLilypondFormat {
		^if (quoting.notNil) {
			"%%%".format("#", quoting, this.prGetFormattedValue);
		} {
			"%%".format("#", this.prGetFormattedValue);
		};
	}
}
