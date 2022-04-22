/* --------------------------------------------------------------------------------------------------------
• FoscPostscriptOperator

Postscript commands: http://www.math.ubc.ca/~cass/courses/ps.html

x = FoscPostscriptOperator('moveto', 1.0, 1.0);
x.name;
x.arguments;
x.format;
-------------------------------------------------------------------------------------------------------- */
FoscPostscriptOperator : Fosc {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <name, <arguments;
	*new { |name='stroke' ... arguments|
		^super.new.init(name, arguments);
	}
	init { |argName, argArguments|
		name = argName.asString;
		arguments = argArguments;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS: SPECIAL METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• asCompileString

	x = FoscPostscriptOperator('newpath');
	x.cs;

	x = FoscPostscriptOperator('moveto', 1.0, 1.0);
	x.cs;
	-------------------------------------------------------------------------------------------------------- */
	asCompileString {
		if (arguments.isEmpty) {
			^"FoscPostscriptOperator('%')".format(name);
		} {
			^"FoscPostscriptOperator('%', %)".format(name, arguments.join(", "));
		};
	}
	/* --------------------------------------------------------------------------------------------------------
	• format
	x = FoscPostscriptOperator('moveto', 1.0, 1.0);
	x.format;
	-------------------------------------------------------------------------------------------------------- */
	format {
		var parts, string;
		parts = [];
		arguments.do { |each| parts = parts.add(FoscPostscript.prFormatArgument(each)) };
		//arguments.do { |each| parts = parts.add(each.asString) };
		parts = parts.add(name);
		string = "".scatList(parts)[1..];
		^string;
	}
}
