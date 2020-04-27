/* ------------------------------------------------------------------------------------------------------------
• FoscSchemeVector

Fosc model of Scheme vector.

// Example 1: Scheme vector of boolean values:
a = FoscSchemeVector(true, true, false)
a.format;

// Example 2: Scheme vector of symbols:
a = FoscSchemeVector('foo', 'bar', 'blah');
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscSchemeVector : FoscScheme {
	*new { |... args|
		^super.new(*args).quoting_("'");
	}
}

/* ------------------------------------------------------------------------------------------------------------
• FoscSchemeVectorConstant

Fosc model of Scheme vector constant.

// Example 1: Scheme vector of boolean values:
a = FoscSchemeVectorConstant(true, true, false)
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscSchemeVectorConstant : FoscScheme {
	*new { |... args|
		^super.new(*args).quoting_("'#");
	}
}