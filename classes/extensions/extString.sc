/* ------------------------------------------------------------------------------------------------------------
• String
------------------------------------------------------------------------------------------------------------ */
+ String {
	/* --------------------------------------------------------------------------------------------------------
	• capitalizeFirst

	"scale degrees 4 and 5.".capitalizeFirst;
	-------------------------------------------------------------------------------------------------------- */
	capitalizeFirst {
		^(this[0].toUpper ++ this[1..]);
	}
	/* --------------------------------------------------------------------------------------------------------
	• ditto

	!!! DEPRECATE - use 'wrapExtend'

	"blah".wrapExtend(12);

	"blah".ditto;
	"blah".wrapExtend(8);
	
	"blah".ditto(3);
	"blah".wrapExtend(12);
	-------------------------------------------------------------------------------------------------------- */
	ditto { |n=2|
		^this.dup(n).join;
	}
	/* --------------------------------------------------------------------------------------------------------
	• formatRow

	[
		[1, 2, 3.98783],
		[1, 11, 3.98783]
	].do { |e| "".formatRow(e, precision: 2, columnWidth: 8).postln };
	-------------------------------------------------------------------------------------------------------- */
	formatRow { |items, precision=2, columnWidth=6|
		var result, maxSize, characteristic, mantissa, str;
		
		result = "";
		//maxSize = 0;
		
		items = items.collect { |item|
			if (item.isNumber) {
				characteristic = item.asInteger.asString;
				//"characteristic: ".post; characteristic.postln;
       		 	
       		 	mantissa = item.frac.round(10 ** precision.neg);
       		 	mantissa = mantissa.asString.drop(2);
       		 	mantissa = mantissa.padRight(precision, "0");
				
				str = "%.%".format(characteristic, mantissa);
				//maxSize = [str.size, maxSize].maxItem;
				//columnWidth = maxSize + columnSpacing;

				str = str.padLeft(columnWidth, " ");
			} {
				str = item.asString;
				str = str.padLeft(columnWidth - str.size, " ");
			};

			result = result ++ str;
		};

		^result;
	}
	/* --------------------------------------------------------------------------------------------------------
	• stripLeadingWhiteSpace

	Removes any whitespace between a newline and the first alphanumeric character, including tabs.

	a = "Gets forbidden note duration.\n\n    Returns duration or nil.";
	b = a.stripLeadingWhiteSpace;
	-------------------------------------------------------------------------------------------------------- */
	stripLeadingWhiteSpace {
		var result, regexBody, res;
		
		result = [];
		regexBody = "\\s{2,}(.*)";

		this.splitLines.do { |str|
			res = str.findRegexp(regexBody);
			if (res.notEmpty) { str = res[1][1] };
			result = result.add(str);
		};

		result = result.join("\n");
		
		^result;
	}
	/* --------------------------------------------------------------------------------------------------------
	• removeTabs

	!!!TODO: rename 'stripTabs'

	Strips amy whitespace that appear after newline and before alphanumeric characters.

	a = "Gets forbidden note duration.\n\n\tReturns duration or nil.";
	a.removeTabs;
	-------------------------------------------------------------------------------------------------------- */
	removeTabs {
		^this.reject { |char| char == Char.tab };
	}
	/* --------------------------------------------------------------------------------------------------------
	• removeWhiteSpace

	!!! rename: stripWhiteSpace

	"scale degrees 4 and 5.".removeWhiteSpace;
	-------------------------------------------------------------------------------------------------------- */
	removeWhiteSpace {
		^this.reject { |char| char == Char.space };
	}
	/* --------------------------------------------------------------------------------------------------------
	• splitLines

	Identical to splitlines in python.

	"\none\ntwo\nthree".splitLines;
	"one\ntwo\nthree".splitLines;
	-------------------------------------------------------------------------------------------------------- */
    splitLines {
    	var result, string, indices;
    	result = [];
    	string = this.copy;
    	indices = string.findAllRegexp("\n");
    	indices = indices.add(string.size);
    	if (indices[0] != 0) { indices = [-1] ++ indices };
    	indices.doAdjacentPairs { |a, b| result = result.add(string.copyRange(a + 1, b - 1) ) };
    	^result;
    }
    /* --------------------------------------------------------------------------------------------------------
	• splitWhiteSpace

	Splits white space of any length, including tabs.

	"c' d' es'    f'\tg'".split WhiteSpace;
	-------------------------------------------------------------------------------------------------------- */
	splitWhiteSpace {
		^this.findRegexp("[^\\s]+").flop[1];
	}
	/* --------------------------------------------------------------------------------------------------------
	• splitWords

	"scale degrees 4 and 5.".splitWords;
	"scale degrees 4and5.".splitWords;
	"scaleDegrees4and5.".splitWords;
	"ScaleDegrees4and 5.".splitWords;
	"scale-degrees-4-and-5.".splitWords;
	"SCALE_DEGREES_4_AND_5.".splitWords;
	"one < two".splitWords;
	"one! two!".splitWords;
	-------------------------------------------------------------------------------------------------------- */
	splitWords {
		var wordLikeChars, result;
		
		result = this.copy;
		wordLikeChars = #[$<, $>, $!];
		
		result = result.separate { |a, b|
			a.isAlphaNum.not
			|| { b.isAlphaNum.not }
			|| { a.isLower && b.isUpper }
			|| { a.isAlpha && b.isDecDigit }
			|| { a.isDecDigit && b.isAlpha }
			|| { wordLikeChars.includes(b) }
		};
		
		result = result.collect { |each| each.removeWhiteSpace };
		
		result = result.collect { |each|
			each.reject { |char| char.isAlphaNum.not && wordLikeChars.includes(char).not };
		};
		
		result = result.select { |each| each.notEmpty };
		
		^result;
	}
	/* --------------------------------------------------------------------------------------------------------
	• strip

	Similar to strip in python.
	
	"__one_two__three___".strip($_);
	"__one_two__three___".strip("_");
	"   one  two   three  ".strip;
	-------------------------------------------------------------------------------------------------------- */
	strip { |char|
		var matchingIndices, nonMatchingIndices;
		char = char ? Char.space;
		matchingIndices = this.findAll(char.asString);
		if (matchingIndices.isNil) { ^this };
		nonMatchingIndices = (0..this.lastIndex).symmetricDifference(matchingIndices);
		^this[nonMatchingIndices.first .. nonMatchingIndices.last];
	}
	/* --------------------------------------------------------------------------------------------------------
	• title

	Capitalise the first letter of each word in string. Same as title in python.

	"one two three-and-four".title;
	-------------------------------------------------------------------------------------------------------- */
	title {
		var result;
		result = this.splitWords;
		result = result.collect { |str| str.capitalizeFirst };
		result = result.join(" ");
		^result;
	}
	/* --------------------------------------------------------------------------------------------------------
	• toDashCase

	"scaleDegrees4and5.".toDashCase;
	"SCALE_DEGREES_4_AND_5.".toDashCase;
	"SCALE DEGREES 4 AND 5.".toDashCase;
	-------------------------------------------------------------------------------------------------------- */
	toDashCase {
		var words, result;
		words = this.splitWords;
		result = words.join("-");
		^result;
	}
	/* --------------------------------------------------------------------------------------------------------
	• toLowerDashCase

	"scaleDegrees4and5.".toLowerDashCase;
	"SCALE_DEGREES_4_AND_5.".v;
	-------------------------------------------------------------------------------------------------------- */
	toLowerDashCase {
		var words, result;
		words = this.splitWords;
		words = words.collect { |each| each.toLower };
		result = words.join("-");
		^result;
	}
	/* --------------------------------------------------------------------------------------------------------
	• toLowerCamelCase

	"scale-degrees-4-and-5.".toLowerCamelCase;
	"SCALE_DEGREES_4_AND_5.".toLowerCamelCase;
	-------------------------------------------------------------------------------------------------------- */
	toLowerCamelCase {
		var words, result;
		words = this.splitWords;
		words = words.collect { |each| each.toLower };
		words = [words[0]] ++ words[1..].collect { |each| each.toUpperCamelCase };
		result = words.join;
		^result;
	}
	/* --------------------------------------------------------------------------------------------------------
	• toLowerSnakeCase

	"scaleDegrees4and5.".toLowerSnakeCase;
	"SCALE_DEGREES_4_AND_5.".toLowerSnakeCase;
	-------------------------------------------------------------------------------------------------------- */
	toLowerSnakeCase {
		var words, result;
		words = this.splitWords;
		words = words.collect { |each| each.toLower };
		result = words.join("_");
		^result;
	}
	/* --------------------------------------------------------------------------------------------------------
	• toUpperCamelCase

	"scale-degrees-4-and-5.".toUpperCamelCase;
	"SCALE_DEGREES_4_AND_5.".toUpperCamelCase;
	-------------------------------------------------------------------------------------------------------- */
	toUpperCamelCase {
		var words;
		words = this.splitWords;
		words = words.collect { |each| each.toLower.capitalizeFirst };
		^words.join;
	}
	/* --------------------------------------------------------------------------------------------------------
	• toUpperKebabCase

	"scale-degrees-4-and-5.".toUpperKebabCase;
	"SCALE_DEGREES_4_AND_5.".toUpperKebabCase;
	-------------------------------------------------------------------------------------------------------- */
	toUpperDashCase {
		var words, result;
		words = this.splitWords;
		words = words.collect { |each| each.toLower.capitalizeFirst };
		result = words.join("-");
		^result;
	}
	/* --------------------------------------------------------------------------------------------------------
	• toUpperSnakeCase

	"scale-degrees-4-and-5.".toUpperSnakeCase;
	"SCALE_DEGREES_4_AND_5.".toUpperSnakeCase;
	-------------------------------------------------------------------------------------------------------- */
	toUpperSnakeCase {
		var words, result;
		words = this.splitWords;
		words = words.collect { |each| each.toLower.capitalizeFirst };
		result = words.join("_");
		^result;
	}
	/* --------------------------------------------------------------------------------------------------------
	• toTridirectionalLilypondSymbol

	Changes receiver to tridirectional direction string.

	Returns string or nil.

	toTridirectionalDirectionString("^");
	toTridirectionalDirectionString("-");
	toTridirectionalDirectionString("_");
	toTridirectionalDirectionString("default");
    -------------------------------------------------------------------------------------------------------- */
    toTridirectionalLilypondSymbol {
    	^this.asSymbol.toTridirectionalLilypondSymbol;
    }
    /* --------------------------------------------------------------------------------------------------------
    • toTridirectionalDirectionalOrdinalConstant

    Changes receiver to tridirectional ordinal constant.

	Returns string or nil.

	toTridirectionalDirectionalOrdinalConstant("^");
	toTridirectionalDirectionalOrdinalConstant("-");
	toTridirectionalDirectionalOrdinalConstant("_");
	toTridirectionalDirectionalOrdinalConstant("default");
    -------------------------------------------------------------------------------------------------------- */
	toTridirectionalDirectionalOrdinalConstant {
    	^this.asSymbol.toTridirectionalDirectionalOrdinalConstant;
    }
}
