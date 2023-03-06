/* ------------------------------------------------------------------------------------------------------------
• FoscSchemeMoment

a = FoscSchemeMoment(1, 64);
a.format;

a = FoscSchemeMoment([1, 64]);
a.format;

a = FoscSchemeMoment();
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscSchemeMoment : FoscScheme {
	*new { |... args|
		case
		{ args.size == 1 && { args[0].isKindOf(FoscSchemeMoment) } } {
			args = args[0].duration;
		}
		{ args.size == 1 && { args[0].isKindOf(SequenceableCollection) } } {
			args = FoscDuration(*args[0]);
		}
		{ args.size == 1 && { args[0].isKindOf(Number) } } {
			args = FoscDuration(*args[0]);
		}
		{ args.size == 2 && { args[0].isKindOf(Integer) } && { args[1].isKindOf(Integer) } } {
			args = FoscDuration(*args);
		}
		{ args.size == 0 } {
			args = FoscDuration(1, 4);
		}
		{ ^throw("Can not initialize % from: %.".format(this.name, args)) };
		^super.new(args);
	}

	// PRIVATE ////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• prGetFormattedValue
	-------------------------------------------------------------------------------------------------------- */
	prGetFormattedValue {
		// numerator, denominator = self._value.numerator, self._value.denominator
        ^"(ly:make-moment % %)".format(value.numerator, value.denominator);
	}

	// PUBLIC /////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• duration
	- Duration of scheme moment.

	a = FoscSchemeMoment(7, 8);
	a.duration.format;
	-------------------------------------------------------------------------------------------------------- */
	duration {
        ^value;
	}
}