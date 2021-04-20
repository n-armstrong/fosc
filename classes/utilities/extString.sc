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
	• delimit

	x = "asd\nasdsad\nasdd";
	x.delimitBy("\\n");
	-------------------------------------------------------------------------------------------------------- */
	delimitBy { |string|
		^this.findRegexp("[^%]+".format(string)).flop[1];
	}
	/* --------------------------------------------------------------------------------------------------------
	• delimitByWhiteSpace

	"C#4 Ab Bb4 D5".delimitByWhiteSpace;
	-------------------------------------------------------------------------------------------------------- */
	delimitByWhiteSpace {
		^this.delimitBy("\\s");
	}
	/* --------------------------------------------------------------------------------------------------------
	• delimitWords

	"scale degrees 4 and 5.".delimitWords;
	"scale degrees 4and5.".delimitWords;
	"scaleDegrees4and5.".delimitWords;
	"ScaleDegrees4and 5.".delimitWords;
	"scale-degrees-4-and-5.".delimitWords;
	"SCALE_DEGREES_4_AND_5.".delimitWords;
	"one < two".delimitWords;
	"one! two!".delimitWords;
	-------------------------------------------------------------------------------------------------------- */
	delimitWords {
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
	• ditto

	"blah".ditto;
	"blah".ditto(3);
	-------------------------------------------------------------------------------------------------------- */
	ditto { |n=2|
		^this.dup(n).join;
	}
	/* --------------------------------------------------------------------------------------------------------
    • formatttt

    x = Array.geom(20, 1000, 7/10);
    x.do { |n| n.format(precision: 3, width: 8).postln };

    Returns a string.

    precision: number of decimal places
    width: column width
    -------------------------------------------------------------------------------------------------------- */
    formatttt { |precision=2, width=6|
        var characteristic, mantissa, result;
        characteristic = this.asInteger.asString;
        mantissa = this.frac.round(10 ** precision.neg).asString.drop(2).padRight(precision, "0");
        if (precision >= 1) {
            result = "%.%".format(characteristic, mantissa).padLeft(width, " ");
        } {
            result = "%".format(characteristic).padLeft(width, " ");   
        };
        ^result;
    }
	/* --------------------------------------------------------------------------------------------------------
	• formatColumn

	"".formatRow([1,2,3], 6, precision: 2);
	-------------------------------------------------------------------------------------------------------- */
	formatRow { |items, columnWidth=6, align='left', precision=2|
		var result, characteristic, mantissa, str;
		result = "";
		items = items.collect { |item|
			if (item.isNumber) {
				characteristic = this.asInteger.asString;
       		 	mantissa = this.frac.round(10 ** precision.neg).asString.drop(2).padRight(precision, "0");
				str = "%.%".format(characteristic, mantissa).padLeft(columnWidth, " ");
			} {
				str = item.asString;
				str = str.padLeft(columnWidth - str.size, " ");
			};
			result = result ++ str;
		};

		^result;
	}
	/* --------------------------------------------------------------------------------------------------------
	• partition
	-------------------------------------------------------------------------------------------------------- */
	partition { |string|
		^this.delimitBy(string);
	}
	/* --------------------------------------------------------------------------------------------------------
	• removeLeadingWhiteSpace

	Removes any whitespace between a newline and the first alphanumeric character, including tabs.

	a = "Gets forbidden note duration.\n\n    Returns duration or nil.";
	b = a.removeLeadingWhiteSpace;
	-------------------------------------------------------------------------------------------------------- */
	removeLeadingWhiteSpace {
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
		result = this.delimitWords;
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
		words = this.delimitWords;
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
		words = this.delimitWords;
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
		words = this.delimitWords;
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
		words = this.delimitWords;
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
		words = this.delimitWords;
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
		words = this.delimitWords;
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
		words = this.delimitWords;
		words = words.collect { |each| each.toLower.capitalizeFirst };
		result = words.join("_");
		^result;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// REGEX BODY ////////////////////////////////////////////////////////////////////////////////////////////////////
	accidentalNameRegexBody {
		^"#\\+|b~|[\\+~]|[#]{1,2}|[b]{1,2}";
	}
	diatonicPitchClassNameRegexBody {
        ^"[A-G]";
	}
	diatonicPitchNameRegexBody {
        ^(this.diatonicPitchClassNameRegexBody ++ this.octaveNameRegexBody);
	}
	octaveNameRegexBody {
        ^"[-]?[0-9]+";
	}
	/* --------------------------------------------------------------------------------------------------------
	• pitchClassNameRegexBody

	x ="Bb4 A#2 C3 Dbb1 A+7 A#+2 G~1 Gb~-3";
	m = x.findRegexp("".pitchClassNameRegexBody).flop[1];
	-------------------------------------------------------------------------------------------------------- */
	pitchClassNameRegexBody {
		^(this.diatonicPitchClassNameRegexBody ++ "(" ++ this.accidentalNameRegexBody ++ ")?");
		//^"[A-G]{1}(?:\\+|#\\+|~|b~|[#]{1,2}|[b]{1,2})?";
	}
	/* --------------------------------------------------------------------------------------------------------
	• pitchNameRegexBody

	x ="Bb4 A#2 C3 Dbb1 A+7 A#+2 G~1 Gb~-3";
	m = x.findRegexp("".pitchNameRegexBody).flop[1];

	x ="Bb4 A#2 <C3 Dbb1 A+7> A#+2 G~1 Gb~-3";
	m = x.findRegexp("<.*>").flop[1];
	-------------------------------------------------------------------------------------------------------- */
	pitchNameRegexBody {
		^(this.pitchClassNameRegexBody ++ "(" ++ this.octaveNameRegexBody ++ ")");
		//^"[A-G]{1}(?:\\+|#\\+|~|b~|[#]{1,2}|[b]{1,2})?-?[0-9]{1,2}";
	}

	// TESTING ////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	["A#4", "Bbb4", "B+4", "B~4", "B#+4", "Bb~4"].do { |each| each.isAccidentalName.postln };
	["#", "bb", "+", "~", "#+", "b~", ""].do { |each| each.isAccidentalName.postln };
	-------------------------------------------------------------------------------------------------------- */
	isAccidentalName {
		^("^(" ++ this.accidentalNameRegexBody ++ ")!?$").matchRegexp(this);
	}
	/* --------------------------------------------------------------------------------------------------------
	["A#4", "Bbb,", "B+-2", "B#+"].do { |each| each.isDiatonicPitchClassName.postln };
	["A4", "B", "F", "G-2", "E"].do { |each| each.isDiatonicPitchClassName.postln };
	-------------------------------------------------------------------------------------------------------- */
	isDiatonicPitchClassName {
		^("^(" ++ this.diatonicPitchClassNameRegexBody ++ ")!?$").matchRegexp(this);
	}
	/* --------------------------------------------------------------------------------------------------------
	!!!TODO: SHOULD PITCHES WITH EMPTY 8VE STRING RETURN false ?
	["A#4", "Bbb4", "B+4", "B~4", "B#+4", "Bb~4"].do { |each| each.isDiatonicPitchName.postln };
	["A4", "B3", "B-2", "F", "G"].do { |each| each.isDiatonicPitchName.postln };
	-------------------------------------------------------------------------------------------------------- */
	isDiatonicPitchName {
		^("^(" ++ this.diatonicPitchNameRegexBody ++ ")!?$").matchRegexp(this);
	}
	/* --------------------------------------------------------------------------------------------------------
	!!!TODO: SHOULD EMPTY STRING ("") RETURN false ?
	["A#4", "Bbb4", "B+4", "B~4", "B#+4", "Bb~4"].do { |each| each.isOctaveName.postln };
	["4", "-2", "0", "12", ""].do { |each| each.isOctaveName.postln };
	-------------------------------------------------------------------------------------------------------- */
	isOctaveName {
		^("^(" ++ this.octaveNameRegexBody ++ ")!?$").matchRegexp(this);
	}
	/* --------------------------------------------------------------------------------------------------------
	["A#4", "Bbb4", "B+4", "B~4", "B#+4", "Bb~4"].do { |each| each.isPitchClassName.postln };
	["A#", "Bbb", "B+", "B~", "B#+", "Bb~", "B"].do { |each| each.isPitchClassName.postln };
	-------------------------------------------------------------------------------------------------------- */
	isPitchClassName {
		^("^(" ++ this.pitchClassNameRegexBody ++ ")!?$").matchRegexp(this);
	}
	/* --------------------------------------------------------------------------------------------------------
	["A#4", "Bbb4", "B+4", "B~4", "B#+4", "Bb~4", "C"].do { |each| each.isPitchName.postln };
	["as,", "bff,", "bqs'", "bqf'''", "btqs"].do { |each| each.isPitchName.postln };
	-------------------------------------------------------------------------------------------------------- */
	isPitchName {
		^("^(" ++ this.pitchNameRegexBody ++ ")!?$").matchRegexp(this);
	}

	// MATCHING ///////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	["A#4", "Bbb4", "B+4", "B~4", "B#+4", "Bb~4"].do { |each| each.accidentalName.postln };
	-------------------------------------------------------------------------------------------------------- */
	accidentalName {
		var result;
		result = this.findRegexp(this.accidentalNameRegexBody);
		^if (result.notEmpty) { result[0][1] } { "" };
	}
	/* --------------------------------------------------------------------------------------------------------
	["C#4", "C#-14", "C", "Xb12"].do { |each| each.diatonicPitchClassName.postln };
	-------------------------------------------------------------------------------------------------------- */
	diatonicPitchClassName {
		var result;
		result = this.findRegexp(this.diatonicPitchClassNameRegexBody);
		^if (result.notEmpty) { result[0][1] } { "" };
	}
	/* --------------------------------------------------------------------------------------------------------
	!!!TODO: FALSE POSITIVE FOR "Xb12" ?
	["C#4", "C#-14", "C", "Xb12"].do { |each| each.diatonicPitchName.postln };
	-------------------------------------------------------------------------------------------------------- */
	diatonicPitchName {
		var result;
		result = this.findRegexp(this.diatonicPitchNameRegexBody);
		^if (result.notEmpty) { result[0][1] } { "" };
	}
	/* --------------------------------------------------------------------------------------------------------
	!!!TODO: SHOULD EMPTY STRING ("") IN RECEIVER RETURN 4 BY DEFAULT?
	["A#4", "Bbb0", "B+-3", "B~2", "B11", "F"].do { |each| each.octaveName.postln };
	-------------------------------------------------------------------------------------------------------- */
	octaveName {
		var result;
		result = this.findRegexp(this.octaveNameRegexBody);
		^if (result.notEmpty) { result[0][1] } { "" };
	}
	/* --------------------------------------------------------------------------------------------------------
	["A#4", "Bbb4", "B+4", "B~4", "B#+4", "Bb~4", "B", "F"].do { |each| each.pitchClassName.postln };
	-------------------------------------------------------------------------------------------------------- */
	pitchClassName {
		var result;
		result = this.findRegexp(this.pitchClassNameRegexBody);
		^if (result.notEmpty) { result[0][1] } { "" };
	}
	/* --------------------------------------------------------------------------------------------------------
	["A#4", "Bbb4", "B+4", "B~4", "B#+4", "Bb~4", "B", "F"].do { |each| each.pitchName.postln };
	-------------------------------------------------------------------------------------------------------- */
	pitchName {
		var result;
		result = this.findRegexp(this.pitchNameRegexBody);
		^if (result.notEmpty) { result[0][1] } { "" };
	}

	// LILYPOND: REGEX BODY ///////////////////////////////////////////////////////////////////////////////////////////
	lilypondAccidentalNameRegexBody {
		^"[s]{1,2}|[f]{1,2}|t?q?[fs]";
	}
	lilypondDiatonicPitchClassNameRegexBody {
        ^"[a-g]";
	}
	lilypondDiatonicPitchNameRegexBody {
        ^(this.lilypondDiatonicPitchClassNameRegexBody ++ "(" ++ this.lilypondOctaveNameRegexBody ++ ")?");
	}
	lilypondOctaveNameRegexBody {
        ^",+|'+";
	}
	lilypondPitchClassNameRegexBody {
		^(this.lilypondDiatonicPitchClassNameRegexBody ++ "(" ++ this.lilypondAccidentalNameRegexBody ++ ")?");
	}
	lilypondPitchNameRegexBody {
		^(this.lilypondPitchClassNameRegexBody ++ "(" ++ this.lilypondOctaveNameRegexBody ++ ")?");
	}

	// LILYPOND: TESTING //////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	["as,", "bff,", "bqs'", "bqf'''", "btqs"].do { |each| each.isLilyPondAccidentalName.postln };
	["s", "ff", "qs", "qf", "tqs"].do { |each| each.isLilyPondAccidentalName.postln };
	-------------------------------------------------------------------------------------------------------- */
	isLilyPondAccidentalName {
		^("^(" ++ this.lilypondAccidentalNameRegexBody ++ ")!?$").matchRegexp(this);
	}
	/* --------------------------------------------------------------------------------------------------------
	["as,", "bff,", "bqs'", "bqf'''", "btqs"].do { |each| each.isLilyPondDiatonicPitchClassName.postln };
	["a,", "b''", "f", "g", "e"].do { |each| each.isLilyPondDiatonicPitchClassName.postln };
	-------------------------------------------------------------------------------------------------------- */
	isLilyPondDiatonicPitchClassName {
		^("^(" ++ this.lilypondDiatonicPitchClassNameRegexBody ++ ")!?$").matchRegexp(this);
	}
	/* --------------------------------------------------------------------------------------------------------
	["A#4", "Bbb4", "B+4", "B~4", "B#+4", "Bb~4"].do { |each| each.isLilyPondDiatonicPitchName.postln };
	["as,", "bff,", "bqs'", "bqf'''", "btqs"].do { |each| each.isLilyPondDiatonicPitchName.postln };
	["a,", "b,", "b'", "b'''", "e"].do { |each| each.isLilyPondDiatonicPitchName.postln };
	-------------------------------------------------------------------------------------------------------- */
	isLilyPondDiatonicPitchName {
		^("^(" ++ this.lilypondDiatonicPitchNameRegexBody ++ ")!?$").matchRegexp(this);
	}
	/* --------------------------------------------------------------------------------------------------------
	//!!!TODO: SHOULD EMPTY STRING ("") RETURN false ?
	["as,", "bff,", "bqs'", "bqf'''", "btqs"].do { |each| each.isLilyPondOctaveName.postln };
	[",", ",,,", "'''''", ""].do { |each| each.isLilyPondOctaveName.postln };
	-------------------------------------------------------------------------------------------------------- */
	isLilyPondOctaveName {
		^(this.isEmpty || { ("^(" ++ this.lilypondOctaveNameRegexBody ++ ")!?$").matchRegexp(this) });
	}
	/* --------------------------------------------------------------------------------------------------------
	["as,", "bff,", "bqs'", "bqf'''", "btqs"].do { |each| each.isLilyPondPitchClassName.postln };
	["as", "bff", "bqs", "bqf", "btqs"].do { |each| each.isLilyPondPitchClassName.postln };
	-------------------------------------------------------------------------------------------------------- */
	isLilyPondPitchClassName {
		^("^(" ++ this.lilypondPitchClassNameRegexBody ++ ")!?$").matchRegexp(this);
	}
	/* --------------------------------------------------------------------------------------------------------
	["A#4", "Bbb4", "B+4", "B~4", "B#+4", "Bb~4"].do { |each| each.isLilyPondPitchName.postln };
	["as,", "bff,,", "bqs'", "bqf'''", "btqs"].do { |each| each.isLilyPondPitchName.postln };
	-------------------------------------------------------------------------------------------------------- */
	isLilyPondPitchName {
		^("^(" ++ this.lilypondPitchNameRegexBody ++ ")!?$").matchRegexp(this);
	}

	// LILYPOND: MATCHING /////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	["A#4", "Bbb4", "B+4", "B~4", "B#+4", "Bb~4"].do { |each| each.lilypondAccidentalName.postln };
	-------------------------------------------------------------------------------------------------------- */
	lilypondAccidentalName {
		var result;
		result = this.findRegexp(this.lilypondAccidentalNameRegexBody);
		^if (result.notEmpty) { result[0][1] } { "" };
	}
	/* --------------------------------------------------------------------------------------------------------
	["C#4", "C#-14", "C", "Xb12"].do { |each| each.lilypondDiatonicPitchClassName.postln };
	-------------------------------------------------------------------------------------------------------- */
	lilypondDiatonicPitchClassName {
		var result;
		result = this.findRegexp(this.lilypondDiatonicPitchClassNameRegexBody);
		^if (result.notEmpty) { result[0][1] } { "" };
	}
	/* --------------------------------------------------------------------------------------------------------
	!!!TODO: FALSE POSITIVE FOR "Xb12" ?
	["C#4", "C#-14", "C", "Xb12"].do { |each| each.lilypondDiatonicPitchName.postln };
	-------------------------------------------------------------------------------------------------------- */
	lilypondDiatonicPitchName {
		var result;
		result = this.findRegexp(this.lilypondDiatonicPitchNameRegexBody);
		^if (result.notEmpty) { result[0][1] } { "" };
	}
	/* --------------------------------------------------------------------------------------------------------
	!!!TODO: SHOULD EMPTY STRING ("") IN RECEIVER RETURN 4 BY DEFAULT?
	["A#4", "Bbb0", "B+-3", "B~2", "B11", "F"].do { |each| each.lilypondOctaveName.postln };
	-------------------------------------------------------------------------------------------------------- */
	lilypondOctaveName {
		var result;
		result = this.findRegexp(this.lilypondOctaveNameRegexBody);
		^if (result.notEmpty) { result[0][1] } { "" };
	}
	/* --------------------------------------------------------------------------------------------------------
	["A#4", "Bbb4", "B+4", "B~4", "B#+4", "Bb~4", "B", "F"].do { |each| each.lilypondPitchClassName.postln };
	-------------------------------------------------------------------------------------------------------- */
	lilypondPitchClassName {
		var result;
		result = this.findRegexp(this.lilypondPitchClassNameRegexBody);
		^if (result.notEmpty) { result[0][1] } { "" };
	}
	/* --------------------------------------------------------------------------------------------------------
	["A#4", "Bbb4", "B+4", "B~4", "B#+4", "Bb~4", "B", "F"].do { |each| each.lilypondPitchName.postln };
	-------------------------------------------------------------------------------------------------------- */
	lilypondPitchName {
		var result;
		result = this.findRegexp(this.lilypondPitchNameRegexBody);
		^if (result.notEmpty) { result[0][1] } { "" };
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
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
