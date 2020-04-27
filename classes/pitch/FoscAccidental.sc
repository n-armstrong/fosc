/* ------------------------------------------------------------------------------------------------------------
• FoscAccidental



a = FoscAccidental('b', arrow: 'up');
a.name;
------------------------------------------------------------------------------------------------------------ */
FoscAccidental : FoscObject {
	var <name, <arrow;
	classvar manager;
	*new { |val, arrow|
		var name;
        // if (#[nil, 'up', 'down'].includes(arrow).not) {
        //     throw("%:%: bad value for 'arrow': %.".format(this.species, thisMethod.name, arrow));
        // };
		manager = FoscPitchNameManager;
		name = case
		{ val.isNumber && { val.inclusivelyBetween(-2, 2) } } {
			manager.semitonesToAccidentalName(val.round(0.5));
		}
		{ val.asString.isAccidentalName } { val }
		{ val.asString.isLilyPondAccidentalName } { manager.lilypondAccidentalNameToAccidentalName(val) }
		{ val.asString.isEmpty } { "" }
		{ throw("Can not initialize % from value: %.".format(this.name, val)) };
		^super.new.init(name, arrow);
	}
	init { |argName, argArrow|
        name = manager.accidentalNameToLilypondAccidentalName(argName);
        if (argArrow.notNil) { this.arrow_(argArrow) };
        // name = manager.accidentalNameToLilypondAccidentalName(argName);
        // if (arrow.notNil) {
        //     switch(arrow, 
        //         'up', { name = name ++ "r" },
        //         'down', { name = name ++ "l" }
        //     );
        // };
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • arrow_
    -------------------------------------------------------------------------------------------------------- */
    arrow_ { |direction='up'|
        if (#['up', 'down'].includes(direction).not) {
            throw("%:%: bad value for 'arrow': %.".format(this.species, thisMethod.name, direction));
        };
        arrow = direction;
        switch(arrow, 
            'up', { name = name ++ "r" },
            'down', { name = name ++ "l" }
        );
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • inspect
    !!! DEPRECATE
    -------------------------------------------------------------------------------------------------------- */
    inspect {
		super.inspect(#[name, semitones, lpStr]);
	}
    /* --------------------------------------------------------------------------------------------------------
    • lpStr
    !!! DEPRECATE
    -------------------------------------------------------------------------------------------------------- */
	lpStr {
		^this.str;
	}
    /* --------------------------------------------------------------------------------------------------------
    • semitones

    
    FoscAccidental('s').name;

    FoscAccidental('s').semitones;

    FoscAccidental('s', arrow: 'up').semitones;

    FoscAccidental('f', arrow: 'down').semitones;

    FoscAccidental('', arrow: 'down').semitones;
    -------------------------------------------------------------------------------------------------------- */
	semitones {
        var result;
		result = manager.accidentalNameToSemitones(name);
        // if (arrow.notNil) {
        //     switch(arrow, 
        //         'up', { result = result + 0.25 },
        //         'down', { result = result - 0.25 }
        //     );
        // };
        ^result;
	}
    /* --------------------------------------------------------------------------------------------------------
    • str
    
    FoscAccidental('s').str;

    FoscAccidental('s', arrow: 'up').str;

    FoscAccidental('', arrow: 'down').str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^this.name;
    }
    /* --------------------------------------------------------------------------------------------------------
    • unabbreviatedName

    ! Temporary: used for arrow overrides in FoscPitch
    
    FoscAccidental("#").unabbreviatedName;
    -------------------------------------------------------------------------------------------------------- */
    unabbreviatedName {
        var abbreviationToName;
        abbreviationToName = (
            'ss':   "double sharp",
            'tqs':  "three-quarters sharp",
            's':    "sharp",
            'qs':   "quarter sharp",
            '':     "natural",
            'qf':   "quarter flat",
            'f':    "flat",
            'tqf':  "three-quarters flat",
            'ff':   "double flat"
        );
        ^abbreviationToName[manager.accidentalNameToLilypondAccidentalName(name).asSymbol];
    }
}
