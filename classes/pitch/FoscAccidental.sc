/* ------------------------------------------------------------------------------------------------------------
• FoscAccidental

a = FoscAccidental("s");
a.str;
a.semitones;


a = FoscAccidental(-1.5);
a.str;
a.semitones;
------------------------------------------------------------------------------------------------------------ */
FoscAccidental : Fosc {
	var <name;
	*new { |val|
		case
        { this.isAccidentalName(val) } {
            ^super.new.init(val);
        }
        { val.isNumber && { val.inclusivelyBetween(-2, 2) } } {
            //!!! TODO: get nearest match in FoscTuning:current ?
			val = FoscPitchManager.semitonesToAccidentalName(val);
            ^super.new.init(val);
		}
        { val.isKindOf(FoscAccidental) } {
            ^val;
        }
		{ ^throw("Can not initialize % from value: %.".format(this.name, val)) };
	}
	init { |argName|
        name = argName.asString;
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    a = FoscAccidental("s");
    b = FoscAccidental("");

    a == a;
    a == b;
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
        ^(this.semitones == FoscAccidental(expr).semitones);
    }
    /* --------------------------------------------------------------------------------------------------------
    • !=

    a = FoscAccidental("s");
    b = FoscAccidental("");

    a != a;
    a != b;
    -------------------------------------------------------------------------------------------------------- */
    != { |expr|
        ^(this == expr).not;
    }
    /* --------------------------------------------------------------------------------------------------------
    • <

    a = FoscAccidental("s");
    b = FoscAccidental("");

    a < b;
    b < a;
    -------------------------------------------------------------------------------------------------------- */
    < { |expr|
        ^(this.semitones < FoscAccidental(expr).semitones);
    }
    /* --------------------------------------------------------------------------------------------------------
    • >

    a = FoscAccidental("s");
    b = FoscAccidental("");

    a > b;
    b > a;
    -------------------------------------------------------------------------------------------------------- */
    > { |expr|
        ^(this < expr).not;
    }
    /* --------------------------------------------------------------------------------------------------------
    • <=

    a = FoscAccidental("s");
    b = FoscAccidental("");

    a <= a;
    a <= b;
    b <= a;
    -------------------------------------------------------------------------------------------------------- */
    <= { |expr|
        ^(this.semitones <= FoscAccidental(expr).semitones);
    }
    /* --------------------------------------------------------------------------------------------------------
    • >=

    a = FoscAccidental("s");
    b = FoscAccidental("");

    a >= a;
    a >= b;
    b >= a;
    -------------------------------------------------------------------------------------------------------- */
    >= { |expr|
        ^(this.semitones >= FoscAccidental(expr).semitones);
    }
    /* --------------------------------------------------------------------------------------------------------
    • add

    a = FoscAccidental("s");
    a = a + FoscAccidental("");
    a.cs;

    a = FoscAccidental("s");
    a = a + FoscAccidental("ff");
    a.cs;

    a = FoscAccidental("s");
    a = a + 1;
    a.cs;
    -------------------------------------------------------------------------------------------------------- */
    add { |expr|
        if (expr.isKindOf(FoscAccidental)) { expr = expr.semitones };
        ^this.species.new(this.semitones + expr);
    }
    /* --------------------------------------------------------------------------------------------------------
    • sub

    a = FoscAccidental("s");
    a = a - FoscAccidental("");
    a.cs;

    a = FoscAccidental("s");
    a = a - FoscAccidental("f");
    a.cs;

    a = FoscAccidental("s");
    a = a - 1;
    a.cs;
    -------------------------------------------------------------------------------------------------------- */
    sub { |expr|
        if (expr.isKindOf(FoscAccidental)) { expr = expr.semitones };
        ^this.species.new(this.semitones - expr);
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    a = FoscAccidental("s");
    a.asCompileString;

    a = FoscAccidental("");
    a.asCompileString;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^"FoscAccidental(%)".format(this.str.cs);
    }
    /* --------------------------------------------------------------------------------------------------------
    • str
    
    a = FoscAccidental("s");
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^this.name;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *isAccidentalName

    FoscAccidental.isAccidentalName("s");
    FoscAccidental.isAccidentalName("f");
    FoscAccidental.isAccidentalName("ss");
    FoscAccidental.isAccidentalName("qs");
    FoscAccidental.isAccidentalName("tqf");
    FoscAccidental.isAccidentalName("");
    -------------------------------------------------------------------------------------------------------- */
    *isAccidentalName { |name|
        ^FoscPitchManager.isAccidentalName(name);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • semitones

    FoscAccidental("s").semitones;
    FoscAccidental("f").semitones
    FoscAccidental("").semitones;
    FoscAccidental("ss").semitones;
    FoscAccidental("tqf").semitones
    -------------------------------------------------------------------------------------------------------- */
	semitones {
        ^FoscPitchManager.accidentalNameToSemitones(name);
	}
}
