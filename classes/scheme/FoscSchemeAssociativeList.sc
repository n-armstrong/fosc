/* ------------------------------------------------------------------------------------------------------------
• FoscSchemeAssociativeList

a = FoscSchemeAssociativeList(['space', 2], ['padding', 0.5]);
a.format;

a = FoscSchemeAssociativeList(['space', 2], FoscSchemePair('padding', 0.5));
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscSchemeAssociativeList : FoscScheme {
	*new { |... args|
		var argsAsPairs;
		argsAsPairs = [];
		args.do { |each|
			if ((each.isKindOf(SequenceableCollection) || { each.isKindOf(FoscSchemePair) }).not) {
				Error("%: args must be Array or Scheme pair: %.".format(this.name, each)).throw;
			};
			argsAsPairs = argsAsPairs.add(FoscSchemePair(*each));
		};
		^super.new(*argsAsPairs).quoting_("'");
	}
}