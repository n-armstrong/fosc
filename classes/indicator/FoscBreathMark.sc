/* ------------------------------------------------------------------------------------------------------------
• FoscBreathMark (abjad 3.0)

Breath mark.


• Example 1

Attached to a single note.

a = FoscNote(60, 1/4);
m = FoscBreathMark();
a.attach(m);
a.show;


• Example 2

Attached to notes in a staff.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
a[..3].attach(FoscBeam());
a[4..].attach(FoscBeam());
a[3].attach(FoscBreathMark());
a[7].attach(FoscBreathMark());
a.show;


• Example 3

Breath mark can be tweaked.

a = FoscNote(60, 1/4);
m = FoscBreathMark(tweaks: #[['color', 'blue']]);
a.attach(m);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscBreathMark : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <tweaks;
    var <formatSlot='after', <timeOrientation='right';
    *new { |tweaks|
        ^super.new.init(tweaks);
    }
    init { |argTweaks|
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.


    • Example 1

    m = FoscBreathMark(tweaks: #[['color', 'blue']]);
    m.tweaks.cs;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • str

    Gets string representation of breath mark.
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^"\\breathe";
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    /////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        ^this.str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle { |component|
        var bundle, localTweaks;
        bundle = FoscLilyPondFormatBundle();
        if (tweaks.notNil) {
            localTweaks = tweaks.prListFormatContributions(directed: false);
            bundle.after.commands.addAll(localTweaks);
        };
        bundle.after.commands.add(this.prGetLilypondFormat);
        ^bundle;
    }
}
