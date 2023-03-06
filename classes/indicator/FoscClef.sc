/* ------------------------------------------------------------------------------------------------------------
• FoscClef (abjad 3.0)

Clef.


• Example 1

Some available clefs.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
c = #['treble','alto','bass','treble^8','bass_8','tenor','bass^15','percussion'];
c.do { |name, i| a[i].attach(FoscClef(name)) };
a.show;



• Example 2 !!!TODO: tags not yet implemented

Clefs can be tagged.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
m = FoscClef('bass');
a[0].attach(m);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscClef : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar <clefNameToMiddleCPosition, <clefNameToStaffPositionZero, <toWidth;
    var <hide, <middleCPosition, <name;
    var <context='Staff', <formatSlot='opening', <parameter=true, <redraw=true;
    *initClass {
    	clefNameToMiddleCPosition = (
    		'treble': -6,
	        'alto': 0,
	        'tenor': 2,
	        'bass': 6,
	        'french': -8,
	        'soprano': -4,
	        'mezzosoprano': -2,
	        'baritone': 4,
	        'varbaritone': 4,
	        'percussion': 0,
        	'tab': 0
    	);

        toWidth = (
            'alto': 2.75,
            'bass': 2.75,
            'percussion': 2.5,
            'tenor': 2.75,
            'treble': 2.5
        );

  //   	clefNameToStaffPositionZero = (
  //           'treble': FoscPitch('B4'),
  //           'alto': FoscPitch('C4'),
  //           'tenor': FoscPitch('A3'),
  //           'bass': FoscPitch('D3'),
  //           'french': FoscPitch('D5'),
  //           'soprano': FoscPitch('G4'),
  //           'mezzosoprano': FoscPitch('E4'),
  //           'baritone': FoscPitch('F3'),
  //           'varbaritone': FoscPitch('F3'),
  //           'percussion': nil,
  //           'tab': nil,
		// );
    }
    *new { |name='treble', hide=false|
    	if (name.isKindOf(FoscClef)) { name = name.name };
    	^super.new.init(name, hide);
    }
    init { |argName, argHide|
        assert(
            [Symbol, String].any { |type| argName.isKindOf(type) },
            "Can't initialize FoscClef from: %.".format(argName);
        );
    	name = argName.asString;
        hide = argHide;
    	middleCPosition = this.prCalculateMiddleCPosition(name);
        middleCPosition = FoscStaffPosition(middleCPosition);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • context

    Gets context. 'Staff' by default.
    

    • Example 1   

    a = FoscClef('treble');
    a.context;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • hide

    Is true when clef should not appear in output (but should still determine effective clef).
    

    • Example 1

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    m = FoscClef('treble', hide: true);
    a[0].attach(m);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • middleCPosition

    !!!TODO: not yet implemented

    Gets middle C position of clef.


    • Example 1   

    a = FoscClef('treble');
    a.middleCPosition.cs;


    • Example 2   

    a = FoscClef('alto');
    a.middleCPosition.cs;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • name

    Gets name of clef.


    • Example 1   

    a = FoscClef('treble');
    a.name;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • parameter

    Is true.


    • Example 1   

    a = FoscClef('treble');
    a.parameter;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • redraw

    Is true.


    • Example 1   

    a = FoscClef('treble');
    a.redraw;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Tweaks are not implemented on clef.

    The LilyPond '\clef' command refuses tweaks.

    Override the LilyPond 'Clef' grob instead.
    -------------------------------------------------------------------------------------------------------- */
    tweaks {
        // pass
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats clef.

    Returns string.


    • Example 1

    a = FoscClef('treble');
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    format {
    	^this.prGetLilypondFormat;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prCalculateMiddleCPosition
    -------------------------------------------------------------------------------------------------------- */
    prCalculateMiddleCPosition { |clefName|
        var alteration, baseName, suffix;
        alteration = 0;
        case
        { name.includes($_) } {
            # baseName, suffix = clefName.split($_);

            case 
            { suffix == "8" } { alteration = 7 }
            { suffix == "15" } { alteration = 13 }
            {
                ^throw("%:%: bad clef alteration suffix: %.".format(this.species, thisMethod.name, suffix));
            }
        }
        { name.includes($^) } {
            # baseName, suffix = clefName.split($^);
            
            case 
            { suffix == "8" } { alteration = -7 }
            { suffix == "15" } { alteration = -13 }
            {
                suffix.class.postln;
                ^throw("%:%: bad clef alteration suffix: %.".format(this.species, thisMethod.name, suffix));
            };
        }
        {
            baseName = clefName;
        };
        ^FoscClef.clefNameToMiddleCPosition[clefName.asSymbol] + alteration;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prClefNameToStaffPositionZero
    
	a = FoscClef('treble');
	a.prClefNameToStaffPositionZero('soprano').pitchName;
    -------------------------------------------------------------------------------------------------------- */
    prClefNameToStaffPositionZero { |clefName|
    	^FoscClef.clefNameToStaffPositionZero[clefName];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    
    a = FoscClef('treble');
    a.prGetLilypondFormat;
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        ^"\\clef %".format(name.quote);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle {
    	var bundle;
        bundle = FoscLilyPondFormatBundle();
        if (hide.not) {
            bundle.opening.commands.add(this.prGetLilypondFormat);
        };
        ^bundle;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *clefNames

    Array of all clef names.
    

    • Example 1

	FoscClef.clefNames;
    -------------------------------------------------------------------------------------------------------- */
    *clefNames {
    	var clefNames;
        clefNames = clefNameToMiddleCPosition.asSortedArray.collect { |each| each[0] };
        ^clefNames;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *fromSelection

    !!!TODO
    -------------------------------------------------------------------------------------------------------- */
    *fromSelection {
    	^this.notYetImplemented(thisMethod);
    }
}
